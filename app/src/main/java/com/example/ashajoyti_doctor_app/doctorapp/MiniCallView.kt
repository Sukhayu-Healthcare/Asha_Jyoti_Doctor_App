package com.example.ashajoyti_doctor_app.doctorapp

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.cardview.widget.CardView
import com.example.ashajoyti_doctor_app.R

/**
 * Minimal mini-call floating view. Keeps code simple so R references resolve.
 * Make sure res/layout/view_mini_call.xml and its drawables exist.
 */
class MiniCallView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    interface MiniCallListener {
        fun onRestoreRequested()
        fun onEndRequested()
        fun onMuteToggle(muted: Boolean)
    }

    private var listener: MiniCallListener? = null
    private var btnRestore: ImageButton
    private var btnEnd: ImageButton
    private var btnMute: ImageButton

    // drag state
    private var lastX = 0f
    private var lastY = 0f
    private var dragging = false

    init {
        val root: View = LayoutInflater.from(context).inflate(R.layout.view_mini_call, this, true)

        btnRestore = root.findViewById(R.id.btnMiniRestore)
        btnEnd = root.findViewById(R.id.btnMiniEnd)
        btnMute = root.findViewById(R.id.btnMiniMute)

        btnRestore.setOnClickListener { listener?.onRestoreRequested() }
        btnEnd.setOnClickListener { listener?.onEndRequested() }
        btnMute.setOnClickListener {
            // toggle icon and notify listener
            val muted = btnMute.tag as? Boolean ?: false
            val newMuted = !muted
            btnMute.tag = newMuted
            // try to set drawable safely — if drawable missing, ignore
            try {
                btnMute.setImageResource(if (newMuted) R.drawable.ic_mic_off else R.drawable.ic_mic_on)
            } catch (_: Throwable) {}
            listener?.onMuteToggle(newMuted)
        }

        // Enable dragging within parent
        setOnTouchListener { v, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    lastX = event.rawX
                    lastY = event.rawY
                    dragging = true
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    if (!dragging) return@setOnTouchListener false
                    val dx = event.rawX - lastX
                    val dy = event.rawY - lastY
                    lastX = event.rawX
                    lastY = event.rawY
                    val lp = v.layoutParams as MarginLayoutParams
                    lp.leftMargin = (lp.leftMargin + dx).toInt().coerceAtLeast(0)
                    lp.topMargin = (lp.topMargin + dy).toInt().coerceAtLeast(0)
                    v.layoutParams = lp
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    dragging = false
                    true
                }
                else -> false
            }
        }
    }

    fun setListener(l: MiniCallListener) {
        listener = l
    }
}
