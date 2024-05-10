package com.example.quiz.ui

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.quiz.R

import com.example.quiz.base.BaseActivity
import com.example.quiz.databinding.ActivityQuizBinding
import com.example.quiz.viewmodel.QuizViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class QuizsActivity : BaseActivity<ActivityQuizBinding>() {

    private lateinit var quizViewModel: QuizViewModel
    private lateinit var countdownTimer: CountDownTimer
    private var timeLeftInMillis: Long = 20000 // Initial time for the countdown timer (20 seconds)


    override fun inflateViewBinding(inflater: LayoutInflater): ActivityQuizBinding =
        ActivityQuizBinding.inflate(inflater)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quizViewModel = ViewModelProvider(this)[QuizViewModel::class.java]

        initMovieList()
        // If there's a saved state, restore the time left
        if (savedInstanceState != null) {
            timeLeftInMillis = savedInstanceState.getLong("timeLeftInMillis")
            startTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        // Set the countdown timer for the remaining time
        countdownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateCountdownText()
            }

            override fun onFinish() {

            }
        }

        // Start the countdown timer
        countdownTimer.start()
    }

    private fun updateCountdownText() {
        val secondsRemaining = timeLeftInMillis / 1000

        if (secondsRemaining.toString().length == 1) {
            binding.tvTimerCount.text = "00:" + "0" + secondsRemaining.toString()
        } else {
            binding.tvTimerCount.text = "00:" + secondsRemaining.toString()

        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("timeLeftInMillis", timeLeftInMillis)
    }

    override fun onPause() {
        super.onPause()
        // Pause the countdown timer when the activity is paused
        countdownTimer.cancel()
    }

    override fun onResume() {
        super.onResume()
        // Resume the countdown timer when the activity is resumed
        startTimer()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cancel the countdown timer to avoid memory leaks
        countdownTimer.cancel()
    }

    private fun initMovieList() {
        lifecycleScope.launch {
            quizViewModel.getQuizList(this@QuizsActivity).collectLatest {
                Log.d("question",it.questions.toString())

            }
        }
    }


    fun getDrawable(context: Context,Id: Int): Drawable? {
        return when (Id) {
            160 -> ContextCompat.getDrawable(context, R.drawable.ic_new_zealand)
            13 -> ContextCompat.getDrawable(context, R.drawable.ic_aruba)
            66 -> ContextCompat.getDrawable(context, R.drawable.ic_ecuador)
            174 -> ContextCompat.getDrawable(context, R.drawable.ic_paraguay)
            122 -> ContextCompat.getDrawable(context, R.drawable.ic_clip)
            192 -> ContextCompat.getDrawable(context, R.drawable.ic_saint_pierre_and_miquelon)
            113 -> ContextCompat.getDrawable(context, R.drawable.ic_japan)
            81 -> ContextCompat.getDrawable(context, R.drawable.ic_gabon)
            141 -> ContextCompat.getDrawable(context, R.drawable.ic_martinique)
            23 -> ContextCompat.getDrawable(context, R.drawable.ic_belize)
            60 -> ContextCompat.getDrawable(context, R.drawable.ic_czech_republic)
            235 -> ContextCompat.getDrawable(context, R.drawable.ic_united_arab_emirates)
            114 -> ContextCompat.getDrawable(context, R.drawable.ic_jersey)
            126 -> ContextCompat.getDrawable(context, R.drawable.ic_lesotho)
            230 -> ContextCompat.getDrawable(context, R.drawable.ic_turkmenistan)
            // Add more cases as needed
            else -> null // Return null if no matching drawable found
        }
    }
}