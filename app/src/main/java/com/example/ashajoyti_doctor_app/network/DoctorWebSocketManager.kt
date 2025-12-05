package com.example.ashajoyti_doctor_app.network

import android.content.Context
import android.util.Log
import okhttp3.*
import okio.ByteString
import org.json.JSONObject
import org.webrtc.*

/**
 * Manages WebSocket connection and WebRTC for doctor receiving calls.
 */
class DoctorWebSocketManager(
    private val context: Context,
    private val localVideoView: SurfaceViewRenderer?,
    private val remoteVideoView: SurfaceViewRenderer?,
    private val eglBaseContext: EglBase.Context?
) {

    companion object {
        private const val TAG = "DoctorWebSocketManager"
        private const val SOCKET_URL = "wss://ashartc.onrender.com"
    }

    // WebSocket
    private var client: OkHttpClient? = null
    private var webSocket: WebSocket? = null
    private var isConnected = false

    // WebRTC
    private var peerConnectionFactory: PeerConnectionFactory? = null
    private var peerConnection: PeerConnection? = null
    private var localVideoTrack: VideoTrack? = null
    private var localAudioTrack: AudioTrack? = null
    private var videoCapturer: CameraVideoCapturer? = null
    private var isWebRtcInitialized = false

    // ADDED: persist SurfaceTextureHelper so it is not GC'd and camera works reliably
    private var surfaceTextureHelper: SurfaceTextureHelper? = null

    // Call state
    private var doctorId: String = ""
    private var level: String = ""
    private var currentPatientId: String? = null

    // Callbacks
    var onIncomingCall: ((patientId: String) -> Unit)? = null
    var onCallConnected: (() -> Unit)? = null
    var onCallEnded: ((reason: String) -> Unit)? = null
    var onError: ((error: String) -> Unit)? = null

    private fun initializePeerConnectionFactory() {
        if (isWebRtcInitialized) return

        try {
            if (eglBaseContext == null) {
                Log.w(TAG, "EglBase context is null, WebRTC video will not work")
                return
            }

            val options = PeerConnectionFactory.InitializationOptions.builder(context)
                .setEnableInternalTracer(true)
                .createInitializationOptions()
            PeerConnectionFactory.initialize(options)

            peerConnectionFactory = PeerConnectionFactory.builder()
                .setVideoEncoderFactory(DefaultVideoEncoderFactory(eglBaseContext, true, true))
                .setVideoDecoderFactory(DefaultVideoDecoderFactory(eglBaseContext))
                .createPeerConnectionFactory()

            isWebRtcInitialized = true
            Log.d(TAG, "WebRTC initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize WebRTC: ${e.message}", e)
            onError?.invoke("WebRTC initialization failed: ${e.message}")
        }
    }

    fun connect(doctorId: String, level: String) {
        if (isConnected) {
            Log.d(TAG, "Already connected")
            return
        }

        this.doctorId = doctorId
        this.level = level

        client = OkHttpClient()
        val request = Request.Builder().url(SOCKET_URL).build()

        webSocket = client!!.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(ws: WebSocket, response: Response) {
                Log.d(TAG, "WebSocket connected for doctor $doctorId ($level)")
                isConnected = true

                // Register as doctor
                val registerJson = """
                    {
                        "type": "register",
                        "id": "$doctorId",
                        "role": "doctor",
                        "level": "$level"
                    }
                """.trimIndent()
                ws.send(registerJson)
            }

            override fun onMessage(ws: WebSocket, text: String) {
                Log.d(TAG, "Received: $text")
                handleSignalingMessage(text)
            }

            override fun onMessage(ws: WebSocket, bytes: ByteString) {
                Log.d(TAG, "Received binary: ${bytes.hex()}")
            }

            override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
                Log.e(TAG, "WebSocket failure: ${t.message}")
                isConnected = false
                onError?.invoke("Connection failed: ${t.message}")
            }

            override fun onClosed(ws: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WebSocket closed: $reason")
                isConnected = false
            }
        })
    }

    private fun handleSignalingMessage(message: String) {
        try {
            val json = JSONObject(message)
            val type = json.optString("type")

            when (type) {
                "socket-id", "registered" -> {
                    Log.d(TAG, "Registration confirmed")
                }
                "incoming-call" -> {
                    // Try multiple possible field names for patient ID
                    var patientId = json.optString("from")
                    if (patientId.isEmpty()) {
                        patientId = json.optString("fromPatientID")
                    }
                    if (patientId.isEmpty()) {
                        patientId = json.optString("patient_id")
                    }
                    if (patientId.isEmpty()) {
                        patientId = json.optString("patientId")
                    }

                    currentPatientId = patientId
                    Log.d(TAG, "Incoming call from patient: $patientId (raw JSON: $message)")

                    if (patientId.isNotEmpty()) {
                        onIncomingCall?.invoke(patientId)
                    } else {
                        Log.e(TAG, "Could not extract patient ID from incoming call")
                        onError?.invoke("Invalid incoming call - no patient ID")
                    }
                }
                "offer" -> {
                    val sdp = json.optString("sdp")
                    val patientId = json.optString("from")
                    if (patientId.isNotEmpty() && sdp.isNotEmpty()) {
                        // Ensure WebRTC factory / media / pc exist BEFORE setting remote description
                        // ADDED: initialize factory if needed (fix case where offer arrives before accept)
                        initializePeerConnectionFactory() // ADDED: ensure factory exists

                        // ADDED: if local media not set up yet, set it up so local tracks exist
                        if (localVideoTrack == null || localAudioTrack == null) {
                            try {
                                setupMedia()
                            } catch (e: Exception) {
                                Log.e(TAG, "setupMedia failed while handling offer: ${e.message}")
                            }
                        }

                        // ADDED: ensure peerConnection exists before setting remote description
                        if (peerConnection == null) {
                            createPeerConnection()
                        }

                        // Now set remote description
                        handleOffer(sdp, patientId)
                    } else {
                        Log.e(TAG, "Invalid offer - missing sdp or patientId")
                    }
                }
                "ice" -> {
                    val candidate = json.optString("candidate")
                    val sdpMid = json.optString("sdpMid")
                    val sdpMLineIndex = json.optInt("sdpMLineIndex")
                    if (candidate.isNotEmpty()) {
                        handleIceCandidate(candidate, sdpMid, sdpMLineIndex)
                    }
                }
                "call-ended" -> {
                    val reason = json.optString("reason", "Call ended by patient")
                    endCall(reason)
                }
                else -> {
                    Log.d(TAG, "Unhandled message type: $type")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing message: ${e.message}", e)
            Log.e(TAG, "Raw message: $message")
        }
    }

    private fun handleOffer(sdpString: String, patientId: String) {
        Log.d(TAG, "Handling offer from patient: $patientId")
        Log.d(TAG, "Offer SDP: ${sdpString.take(100)}...") // Log first 100 chars

        // ADDED: store current patient id when offer arrives
        currentPatientId = patientId

        val sdp = SessionDescription(SessionDescription.Type.OFFER, sdpString)

        // Make sure peerConnection is available
        if (peerConnection == null) {
            Log.w(TAG, "PeerConnection is null when handling offer — creating one")
            createPeerConnection()
        }

        peerConnection?.setRemoteDescription(object : SdpObserver {
            override fun onSetSuccess() {
                Log.d(TAG, "✅ Remote description set successfully, creating answer...")
                createAnswer()
            }

            override fun onSetFailure(error: String?) {
                Log.e(TAG, "❌ Failed to set remote description: $error")
                onError?.invoke("Failed to process offer")
            }

            override fun onCreateSuccess(sdp: SessionDescription?) {}
            override fun onCreateFailure(error: String?) {}
        }, sdp)
    }

    private fun createAnswer() {
        Log.d(TAG, "Creating answer for patient: $currentPatientId")

        val constraints = MediaConstraints().apply {
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
        }

        peerConnection?.createAnswer(object : SdpObserver {
            override fun onCreateSuccess(sdp: SessionDescription?) {
                sdp?.let {
                    Log.d(TAG, "✅ Answer created successfully")
                    Log.d(TAG, "Answer SDP type: ${it.type}")
                    Log.d(TAG, "Answer SDP: ${it.description.take(100)}...") // Log first 100 chars

                    peerConnection?.setLocalDescription(object : SdpObserver {
                        override fun onSetSuccess() {
                            Log.d(TAG, "✅ Local description set successfully, sending answer...")
                            sendAnswer(it)
                        }

                        override fun onSetFailure(error: String?) {
                            Log.e(TAG, "❌ Failed to set local description: $error")
                        }

                        override fun onCreateSuccess(sdp: SessionDescription?) {}
                        override fun onCreateFailure(error: String?) {}
                    }, it)
                }
            }

            override fun onCreateFailure(error: String?) {
                Log.e(TAG, "❌ Failed to create answer: $error")
                onError?.invoke("Failed to create answer")
            }

            override fun onSetSuccess() {}
            override fun onSetFailure(error: String?) {}
        }, constraints)
    }

    private fun sendAnswer(sdp: SessionDescription) {
        val answerJson = """
            {
                "type": "answer",
                "to": "$currentPatientId",
                "from": "$doctorId",
                "sdp": "${sdp.description}"
            }
        """.trimIndent()

        Log.d(TAG, "📤 Sending answer to patient: $currentPatientId")
        Log.d(TAG, "Answer JSON length: ${answerJson.length} bytes")

        val sent = webSocket?.send(answerJson) ?: false
        if (sent) {
            Log.d(TAG, "✅ Answer sent successfully")
        } else {
            Log.e(TAG, "❌ Failed to send answer - WebSocket might be closed")
            onError?.invoke("Failed to send answer to patient")
        }
    }

    private fun sendIceCandidate(candidate: IceCandidate) {
        Log.d(TAG, "📤 Sending ICE candidate to patient: $currentPatientId")
        Log.d(TAG, "ICE candidate - sdpMid: ${candidate.sdpMid}, sdpMLineIndex: ${candidate.sdpMLineIndex}")
        Log.d(TAG, "ICE candidate SDP: ${candidate.sdp}")

        val iceJson = """
            {
                "type": "ice",
                "to": "$currentPatientId",
                "from": "$doctorId",
                "candidate": "${candidate.sdp}",
                "sdpMid": "${candidate.sdpMid}",
                "sdpMLineIndex": ${candidate.sdpMLineIndex}
            }
        """.trimIndent()

        val sent = webSocket?.send(iceJson) ?: false
        if (sent) {
            Log.d(TAG, "✅ ICE candidate sent successfully")
        } else {
            Log.e(TAG, "❌ Failed to send ICE candidate - WebSocket might be closed")
        }
    }

    private fun handleIceCandidate(candidate: String, sdpMid: String, sdpMLineIndex: Int) {
        Log.d(TAG, "📥 Received ICE candidate from patient: $currentPatientId")
        Log.d(TAG, "ICE candidate - sdpMid: $sdpMid, sdpMLineIndex: $sdpMLineIndex")
        Log.d(TAG, "ICE candidate SDP: $candidate")

        try {
            val iceCandidate = IceCandidate(sdpMid, sdpMLineIndex, candidate)
            val added = peerConnection?.addIceCandidate(iceCandidate) ?: false
            if (added) {
                Log.d(TAG, "✅ ICE candidate added successfully")
            } else {
                Log.w(TAG, "⚠ Failed to add ICE candidate to peer connection")
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error adding ICE candidate: ${e.message}", e)
        }
    }

    fun acceptCall() {
        if (currentPatientId == null) {
            Log.e(TAG, "❌ No incoming call to accept")
            return
        }

        Log.d(TAG, "📞 Accepting call from patient: $currentPatientId")

        initializePeerConnectionFactory()

        if (!isWebRtcInitialized) {
            Log.e(TAG, "❌ Cannot accept call: WebRTC not initialized")
            onError?.invoke("Cannot accept call: WebRTC not initialized")
            return
        }

        Log.d(TAG, "Setting up media...")
        setupMedia()

        Log.d(TAG, "Creating peer connection...")
        createPeerConnection()

        val acceptJson = """
            {
                "type": "call-accepted",
                "to": "$currentPatientId",
                "from": "$doctorId"
            }
        """.trimIndent()

        Log.d(TAG, "📤 Sending call-accepted notification")
        val sent = webSocket?.send(acceptJson) ?: false
        if (sent) {
            Log.d(TAG, "✅ Call acceptance sent successfully")
        } else {
            Log.e(TAG, "❌ Failed to send call acceptance")
        }
    }

    fun rejectCall(reason: String = "Doctor declined") {
        if (currentPatientId == null) return

        val rejectJson = """
            {
                "type": "call-rejected",
                "to": "$currentPatientId",
                "from": "$doctorId",
                "reason": "$reason"
            }
        """.trimIndent()
        webSocket?.send(rejectJson)

        currentPatientId = null
    }

    private fun setupMedia() {
        try {
            // Initialize video views
            localVideoView?.init(eglBaseContext, null)
            remoteVideoView?.init(eglBaseContext, null)

            // Create local video track
            videoCapturer = createCameraVideoCapturer()
            val videoSource = peerConnectionFactory!!.createVideoSource(videoCapturer!!.isScreencast)

            // ADDED: persist SurfaceTextureHelper to avoid GC / camera issues
            surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", eglBaseContext)

            videoCapturer!!.initialize(
                surfaceTextureHelper, // ADDED: use persisted helper
                context,
                videoSource.capturerObserver
            )
            videoCapturer!!.startCapture(720, 480, 30)

            localVideoTrack = peerConnectionFactory!!.createVideoTrack("local_video", videoSource)
            localVideoView?.let { localVideoTrack!!.addSink(it) }

            // Create local audio track
            val audioConstraints = MediaConstraints().apply {
                mandatory.add(MediaConstraints.KeyValuePair("googEchoCancellation", "true"))
                mandatory.add(MediaConstraints.KeyValuePair("googNoiseSuppression", "true"))
                mandatory.add(MediaConstraints.KeyValuePair("googAutoGainControl", "true"))
            }
            val audioSource = peerConnectionFactory!!.createAudioSource(audioConstraints)
            localAudioTrack = peerConnectionFactory!!.createAudioTrack("local_audio", audioSource)
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up media: ${e.message}", e)
            onError?.invoke("Failed to setup media: ${e.message}")
        }
    }

    private fun createCameraVideoCapturer(): CameraVideoCapturer {
        val enumerator = Camera2Enumerator(context)
        for (deviceName in enumerator.deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                return enumerator.createCapturer(deviceName, null)
            }
        }
        return enumerator.createCapturer(enumerator.deviceNames[0], null)
    }

    private fun createPeerConnection() {
        val iceServer = PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer()
        val rtcConfig = PeerConnection.RTCConfiguration(listOf(iceServer))

        // ADDED: ensure Plan-B to match patient side (fixes C++ addTrack failed)
        rtcConfig.sdpSemantics = PeerConnection.SdpSemantics.PLAN_B // ADDED

        peerConnection = peerConnectionFactory!!.createPeerConnection(
            rtcConfig,
            object : PeerConnection.Observer {
                override fun onIceCandidate(candidate: IceCandidate?) {
                    candidate?.let { sendIceCandidate(it) }
                }

                override fun onAddStream(stream: MediaStream?) {
                    stream?.let {
                        Log.d(TAG, "Remote stream added")
                        if (it.videoTracks.size > 0) {
                            remoteVideoView?.let { view -> it.videoTracks[0].addSink(view) }
                        }
                    }
                }

                override fun onIceConnectionChange(state: PeerConnection.IceConnectionState?) {
                    Log.d(TAG, "ICE connection state: $state")
                    when (state) {
                        PeerConnection.IceConnectionState.CONNECTED -> {
                            onCallConnected?.invoke()
                        }
                        PeerConnection.IceConnectionState.DISCONNECTED,
                        PeerConnection.IceConnectionState.FAILED,
                        PeerConnection.IceConnectionState.CLOSED -> {
                            endCall("Connection lost")
                        }
                        else -> {}
                    }
                }

                override fun onIceConnectionReceivingChange(receiving: Boolean) {
                    Log.d(TAG, "ICE connection receiving change: $receiving")
                }

                override fun onSignalingChange(state: PeerConnection.SignalingState?) {}
                override fun onIceGatheringChange(state: PeerConnection.IceGatheringState?) {}
                override fun onRemoveStream(stream: MediaStream?) {}
                override fun onDataChannel(channel: DataChannel?) {}
                override fun onRenegotiationNeeded() {}
                override fun onIceCandidatesRemoved(candidates: Array<out IceCandidate>?) {}
            }
        )

        // ADDED: only add tracks if they exist (prevents C++ addTrack failed when tracks null)
        localVideoTrack?.let {
            peerConnection?.addTrack(it, listOf("stream_id"))
        } ?: Log.w(TAG, "Local video track is null when trying to addTrack()")

        localAudioTrack?.let {
            peerConnection?.addTrack(it, listOf("stream_id"))
        } ?: Log.w(TAG, "Local audio track is null when trying to addTrack()")
    }

    fun endCall(reason: String = "Call ended by doctor") {
        currentPatientId?.let {
            val endJson = """
                {
                    "type": "call-ended",
                    "to": "$it",
                    "from": "$doctorId",
                    "reason": "$reason"
                }
            """.trimIndent()
            webSocket?.send(endJson)
        }

        try {
            peerConnection?.close()
        } catch (e: Exception) {
            Log.e(TAG, "Error closing peerConnection: ${e.message}")
        }
        peerConnection = null

        try {
            videoCapturer?.stopCapture()
            videoCapturer?.dispose()
        } catch (e: Exception) {
            Log.w(TAG, "Error stopping/disposing capturer: ${e.message}")
        }

        try {
            localVideoTrack?.dispose()
            localAudioTrack?.dispose()
        } catch (e: Exception) {
            Log.w(TAG, "Error disposing tracks: ${e.message}")
        }
        localVideoTrack = null
        localAudioTrack = null

        // ADDED: dispose surfaceTextureHelper
        try {
            surfaceTextureHelper?.dispose()
            surfaceTextureHelper = null
        } catch (e: Exception) {
            Log.w(TAG, "Error disposing surfaceTextureHelper: ${e.message}")
        }

        currentPatientId = null
        onCallEnded?.invoke(reason)
    }

    fun disconnect() {
        endCall("Doctor disconnected")

        webSocket?.close(1000, "Doctor unavailable")
        client?.dispatcher?.executorService?.shutdown()

        peerConnectionFactory?.dispose()

        isConnected = false
        isWebRtcInitialized = false
        webSocket = null
        client = null
        peerConnectionFactory = null

        Log.d(TAG, "Disconnected and cleaned up")
    }

    fun isConnected(): Boolean = isConnected

    fun getCurrentPatientId(): String? = currentPatientId
}