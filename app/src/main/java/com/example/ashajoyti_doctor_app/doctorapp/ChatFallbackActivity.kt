package com.example.ashajoyti_doctor_app.doctorapp

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.ashajoyti_doctor_app.R
import java.text.SimpleDateFormat
import java.util.*

class ChatFallbackActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var tvDoctorName: TextView
    private lateinit var btnVoiceCall: ImageButton
    private lateinit var btnVideoCall: ImageButton

    private lateinit var scrollViewMessages: ScrollView
    private lateinit var messagesContainer: LinearLayout
    private lateinit var etMessage: EditText
    private lateinit var btnSend: ImageButton
    private lateinit var btnEmoji: ImageButton
    private lateinit var btnAttach: ImageButton
    private lateinit var btnCamera: ImageButton

    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Make sure you have res/layout/activity_chat.xml in place
        setContentView(R.layout.activity_chat)

        // find views
        btnBack = findViewById(R.id.btnBack)
        tvDoctorName = findViewById(R.id.tvDoctorName)
        btnVoiceCall = findViewById(R.id.btnVoiceCall)
        btnVideoCall = findViewById(R.id.btnVideoCall)

        scrollViewMessages = findViewById(R.id.scrollViewMessages)
        messagesContainer = findViewById(R.id.messagesContainer)

        etMessage = findViewById(R.id.etMessage)
        btnSend = findViewById(R.id.btnSend)
        btnEmoji = findViewById(R.id.btnEmoji)
        btnAttach = findViewById(R.id.btnAttach)
        btnCamera = findViewById(R.id.btnCamera)

        // header name from intent (QuickConsultActivity passes patient/doctor name)
        val doctorName = intent.getStringExtra("doctor_name")
            ?: intent.getStringExtra("patient_name")
            ?: "Dr. Amit Kumar"
        tvDoctorName.text = doctorName

        btnBack.setOnClickListener { finish() }

        btnVoiceCall.setOnClickListener {
            try {
                val i = Intent(this, VoiceCallActivity::class.java)
                i.putExtra("doctor_name", doctorName)
                startActivity(i)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "Voice call not available", Toast.LENGTH_SHORT).show()
            }
        }

        btnVideoCall.setOnClickListener {
            try {
                val i = Intent(this, VideoCallActivity::class.java)
                i.putExtra("doctor_name", doctorName)
                startActivity(i)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "Video call not available", Toast.LENGTH_SHORT).show()
            }
        }

        // sample default (dummy) messages — WhatsApp style
        seedDummyMessages(doctorName)

        // typing watcher toggles send icon (mic -> send)
        etMessage.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val hasText = !s.isNullOrBlank()
                try {
                    btnSend.setImageResource(if (hasText) R.drawable.ic_send else R.drawable.ic_mic)
                } catch (_: Exception) { }
            }
        })

        etMessage.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                handleSend()
                true
            } else false
        }

        btnSend.setOnClickListener { handleSend() }

        btnEmoji.setOnClickListener { Toast.makeText(this, "Emoji picker (placeholder)", Toast.LENGTH_SHORT).show() }
        btnAttach.setOnClickListener { Toast.makeText(this, "Attach (placeholder)", Toast.LENGTH_SHORT).show() }
        btnCamera.setOnClickListener { Toast.makeText(this, "Camera (placeholder)", Toast.LENGTH_SHORT).show() }
    }

    private fun seedDummyMessages(doctorName: String) {
        // Clear then add
        messagesContainer.removeAllViews()

        // Received (doctor)
        addMessage("$doctorName: Hello! I'm Dr. Kumar. How can I help you today?", false, "10:30 AM")
        // Sent (patient)
        addMessage("I am experiencing chest pain and difficulty breathing.", true, "10:32 AM")
        // Received
        addMessage("Please stay calm. When did it start?", false, "10:33 AM")
        // Sent
        addMessage("Started about 20 minutes ago. It's sharp.", true, "10:34 AM")
        // Received (urgent)
        addMessage("⚠️ This could be serious. Sit down and breathe. I want to see you on video.", false, "10:35 AM")

        // scroll to bottom
        scrollToBottomDelayed()
    }

    private fun handleSend() {
        val text = etMessage.text.toString().trim()
        if (text.isNotEmpty()) {
            addMessage(text, true, timeFormat.format(Date()))
            etMessage.setText("")
            scrollToBottomDelayed()
            // simulate a simple automated doctor reply after 1s (optional)
            messagesContainer.postDelayed({
                addMessage("Thanks for the update. Please hold while I open the video.", false, timeFormat.format(Date()))
                scrollToBottomDelayed()
            }, 1000)
        } else {
            Toast.makeText(this, "Start voice recording (placeholder)", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addMessage(message: String, sent: Boolean, time: String? = null) {
        val msgTime = time ?: timeFormat.format(Date())
        val screenWidth = resources.displayMetrics.widthPixels
        val maxBubbleWidth = (screenWidth * 0.70).toInt() // 70% of screen width

        // row layout (match parent)
        val row = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { it.setMargins(0, dp(6), 0, dp(6)) }
            orientation = LinearLayout.HORIZONTAL
            gravity = if (sent) Gravity.END else Gravity.START
        }

        // bubble CardView: MATCH_PARENT but side margins limit bubble width visually
        val bubble = CardView(this).apply {
            radius = dp(8).toFloat()
            cardElevation = 0f
            useCompatPadding = true
            val bg = if (sent) 0xFFDCF8C6.toInt() else 0xFFFFFFFF.toInt() // WhatsApp green / white
            setCardBackgroundColor(bg)

            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { lp ->
                val sideMargin = dp(64)
                if (sent) lp.setMargins(sideMargin, 0, dp(8), 0) else lp.setMargins(dp(8), 0, sideMargin, 0)
            }
        }

        // inside bubble: vertical with message and time
        val inner = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(10), dp(6), dp(10), dp(6))
        }

        val tv = TextView(this).apply {
            text = message
            textSize = 16f
            setTextColor(0xFF000000.toInt())
            // Constrain bubble content width so long text wraps like WhatsApp
            maxWidth = maxBubbleWidth
        }

        val tvTime = TextView(this).apply {
            text = msgTime
            textSize = 11f
            setTextColor(0xFF6B9B64.toInt())
            val p = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            p.topMargin = dp(6)
            layoutParams = p
        }

        inner.addView(tv)
        inner.addView(tvTime)
        bubble.addView(inner)
        row.addView(bubble)
        messagesContainer.addView(row)
    }

    private fun scrollToBottomDelayed() {
        scrollViewMessages.post { scrollViewMessages.fullScroll(View.FOCUS_DOWN) }
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()
}
