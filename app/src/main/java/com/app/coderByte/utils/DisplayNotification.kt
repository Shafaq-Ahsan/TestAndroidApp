package com.app.coderByte.utils

import android.content.Context
import android.graphics.Color
import android.os.CountDownTimer
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import com.app.coderByte.R
import com.app.coderByte.databinding.NotificationDialogBinding

//notification view to show and hide
object DisplayNotification {

    private const val NOTIFICATION_TIMER = 1000L //time to show top drop sown view after that hide that view

    private lateinit var mTimer: CountDownTimer

    enum class STYLE {
        INFO,
        SUCCESS,
        FAILURE,
        GENERAL
    }

    private fun setStyle(style: STYLE, binding: NotificationDialogBinding, message: String) {
        binding.apply {
            txtViewNotificationMessage.text = message
            when (style) {
                STYLE.FAILURE -> {
                    txtViewNotificationTitle.visibility = View.GONE
                    cardViewNotification.setCardBackgroundColor(Color.parseColor("#fe8083"))
                    imgViewNotificationIcon.setImageResource(R.drawable.icon_close_white_24dp)
                }

                STYLE.INFO -> {
                    txtViewNotificationTitle.visibility = View.GONE
                    cardViewNotification.setCardBackgroundColor(Color.parseColor("#7AC0FF"))
                    imgViewNotificationIcon.setImageResource(R.drawable.icon_info_outline_white_24dp)
                }

                STYLE.SUCCESS -> {
                    txtViewNotificationTitle.visibility = View.GONE
                    cardViewNotification.setCardBackgroundColor(Color.parseColor("#137BDE"))
                    imgViewNotificationIcon.setImageResource(R.drawable.icon_check_white_24dp)

                }

                STYLE.GENERAL -> {
                    txtViewNotificationTitle.visibility = View.GONE
                    txtViewNotificationMessage.setTextColor(Color.parseColor("#000000"))
                    cardViewNotification.setCardBackgroundColor(Color.parseColor("#137BDE"))
                    imgViewNotificationIcon.setImageResource(R.drawable.ic_baseline_report_problem_24)
                }

            }
            executePendingBindings()
        }

    }


    fun show(cxt: Context, binding: NotificationDialogBinding, style: STYLE, message: String) {
        if (binding.root.isVisible && binding.txtViewNotificationMessage.text.toString() == message) {
            mTimer.apply {
                cancel()
                start()
            }
            return
        } else if (binding.root.isVisible) {
            mTimer.apply {
                cancel()
                start()
            }
        }
        val animationEnter = AnimationUtils.loadAnimation(cxt, R.anim.notification_dialog_enter)
        val animationExit = AnimationUtils.loadAnimation(cxt, R.anim.notification_dialog_exit)
        setStyle(style, binding, message)

        if (!binding.root.isVisible) {
            binding.root.startAnimation(animationEnter)
        }
        animationEnter.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                binding.root.apply {
                    visibility = View.VISIBLE


                    mTimer = object : CountDownTimer(NOTIFICATION_TIMER, 1000) {
                        override fun onFinish() {
                            startAnimation(animationExit)
                        }

                        override fun onTick(millisUntilFinished: Long) {
                        }

                    }
                    mTimer.start()
                }
            }

            override fun onAnimationStart(animation: Animation?) {
            }

        })

        animationExit.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                binding.root.visibility = View.GONE
            }

            override fun onAnimationStart(animation: Animation?) {
            }

        })
    }
}