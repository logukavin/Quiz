package com.example.quiz.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.quiz.R
import com.example.quiz.base.BaseActivity
import com.example.quiz.databinding.ActivityQuizBinding
import com.example.quiz.databinding.LayoutTimerBinding
import com.example.quiz.model.QuestionsItem
import com.example.quiz.viewmodel.QuizViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class QuizsActivity : BaseActivity<ActivityQuizBinding>(),View.OnClickListener{

    private lateinit var quizViewModel: QuizViewModel
    private lateinit var countdownTimer: CountDownTimer
    private lateinit var questionTimer: CountDownTimer
    private var timeLeftInMillis: Long = 20000 // Initial time for the countdown timer (20 seconds)

    private lateinit var layoutTimerBinding: LayoutTimerBinding
    private  var questionsAnswerList: List<QuestionsItem?>? = null
    private var selectedItem : QuestionsItem?=null
    private var selectedItemPosition : Int?=0
    private var correctQuestion:Int=0
    private var totalQuestion:Int=0
    private var clickCount:Int=0


    override fun inflateViewBinding(inflater: LayoutInflater): ActivityQuizBinding =
        ActivityQuizBinding.inflate(inflater)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view: View = binding.getRoot()
        layoutTimerBinding = LayoutTimerBinding.bind(binding.root)
        quizViewModel = ViewModelProvider(this)[QuizViewModel::class.java]
        layoutTimerBinding.tvOption1.setOnClickListener(this)
        layoutTimerBinding.tvOption2.setOnClickListener(this)
        layoutTimerBinding.tvOption3.setOnClickListener(this)
        layoutTimerBinding.tvOption4.setOnClickListener(this)
        layoutTimerBinding.ctSchedule.visibility=View.VISIBLE
        layoutTimerBinding.ctTimer.visibility=View.GONE
        layoutTimerBinding.ctQuestion.visibility=View.GONE
        initQuizist()
        // If there's a saved state, restore the time left
        if (savedInstanceState != null) {
            timeLeftInMillis = savedInstanceState.getLong("timeLeftInMillis")
//            startTimer()
        } else {
//            startTimer()
        }

        layoutTimerBinding.btTimeSave.setOnClickListener(View.OnClickListener {
            layoutTimerBinding.ctSchedule.visibility=View.GONE
            layoutTimerBinding.ctTimer.visibility=View.VISIBLE
            startInitialTimer()
        })

    }


    private fun checkExistingTime(){

        //let as assume 5 minutes delay

        //each question have 40 sec

        //5 minutes -> 300 seconds
        val delayTime=300000;

//        val cc=delayTime/40000
//        val cc=delayTime/40000

//        Log.d("STANBCC : "," cc: $cc")
//        Log.d("STANBCC : "," cc: $cc")





    }

    private fun startInitialTimer() {
        // Set the countdown timer for the remaining time
        val timer = object: CountDownTimer(20000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000

                if (secondsRemaining.toString().length == 1) {
                    layoutTimerBinding.tvCount.text = "00:" + "0" + secondsRemaining.toString()
                    binding.tvTimerCount.text = "00:" + "0" + secondsRemaining.toString()
                } else {
                    layoutTimerBinding.tvCount.text = "00:" + secondsRemaining.toString()
                    binding.tvTimerCount.text = "00:" + secondsRemaining.toString()
                }
            }

            override fun onFinish() {
                layoutTimerBinding.ctTimer.visibility=View.GONE
                layoutTimerBinding.ctQuestion.visibility=View.VISIBLE
                initializeQuestionTimer()
                startQuestionAnswer()
            }
        }
        timer.start()
    }
    private fun initializeQuestionTimer() {
        correctQuestion=0
        // Set the countdown timer for the remaining time
        questionTimer = object: CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                Log.d("STANBCC : "," $secondsRemaining")
                if (secondsRemaining.toString().length == 1) {
                    binding.tvTimerCount.text = "00:" + "0" + secondsRemaining.toString()
                } else {
                    binding.tvTimerCount.text = "00:" + secondsRemaining.toString()
                }
            }

            override fun onFinish() {
                //10 seconds delay
                if ((questionsAnswerList!!.size-1)==selectedItemPosition!!){
                    //start result screen

                    layoutTimerBinding.ctQuestion.visibility=View.GONE
                    layoutTimerBinding.tvGameOver.visibility=View.VISIBLE

                    lifecycleScope.launch {
                        delay(5000) // 5000 milliseconds = 5 seconds
                        // Code to be executed after 5 seconds
                        // For example, navigate to another activity
                        withContext(Dispatchers.Main) {
                            layoutTimerBinding.tvGameOver.visibility=View.GONE
                            layoutTimerBinding.tvScore.visibility=View.VISIBLE
                            layoutTimerBinding.tvTotal.visibility=View.VISIBLE
                            layoutTimerBinding.tvTotal.text=correctQuestion.toString()+"/"+totalQuestion
                        }
                    }
                }else{
                    layoutTimerBinding.view4.visibility=View.GONE
                    layoutTimerBinding.view6.visibility=View.VISIBLE
                    viewDisable()
                    intervalCheck()
                    lifecycleScope.launch {
                        delay(10000) // 10000 milliseconds = 10 seconds
                        // Code to be executed after 5 seconds
                        // For example, navigate to another activity
                        withContext(Dispatchers.Main) {
                            clickCount=0
                            layoutTimerBinding.view4.visibility=View.VISIBLE
                            layoutTimerBinding.view6.visibility=View.GONE
                            selectedItemPosition=selectedItemPosition!!+1
                            startQuestionAnswer()
             }
                    }

                }
            }
        }
    }




    private fun startQuestionAnswer() {
        layoutTimerBinding.tvOption1.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.bg_unselect))
        layoutTimerBinding.tvOption2.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.bg_unselect))
        layoutTimerBinding.tvOption3.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.bg_unselect))
        layoutTimerBinding.tvOption4.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.bg_unselect))
        layoutTimerBinding.tvAnswer1.visibility=View.GONE
        layoutTimerBinding.tvAnswer2.visibility=View.GONE
        layoutTimerBinding.tvAnswer3.visibility=View.GONE
        layoutTimerBinding.tvAnswer4.visibility=View.GONE
        layoutTimerBinding.tvOption1.isEnabled=true
        layoutTimerBinding.tvOption2.isEnabled=true
        layoutTimerBinding.tvOption3.isEnabled=true
        layoutTimerBinding.tvOption4.isEnabled=true


        if (questionsAnswerList!!.isNotEmpty()){
            selectedItem= questionsAnswerList!![selectedItemPosition!!]
            layoutTimerBinding.tvOption1.text=selectedItem!!.countries!![0]!!.countryName
            layoutTimerBinding.tvOption2.text= selectedItem!!.countries!![1]!!.countryName
            layoutTimerBinding.tvOption3.text= selectedItem!!.countries!![2]!!.countryName
            layoutTimerBinding.tvOption4.text= selectedItem!!.countries!![3]!!.countryName
            layoutTimerBinding.imageView.setImageDrawable(getDrawableImage(this,selectedItem!!.answerId!!))
        }
        questionTimer.start()
    }

    private fun storeLocalTimeDuration(){


    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("timeLeftInMillis", timeLeftInMillis)
    }

    override fun onPause() {
        super.onPause()
        // Pause the countdown timer when the activity is paused
//        countdownTimer.cancel()
    }

    override fun onResume() {
        super.onResume()
        // Resume the countdown timer when the activity is resumed
//        startTimer()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cancel the countdown timer to avoid memory leaks
        countdownTimer.cancel()
    }

    private fun initQuizist() {
        totalQuestion=0
        lifecycleScope.launch {
            quizViewModel.getQuizList(this@QuizsActivity).collectLatest {
                Log.d("question",it.questions.toString())
                questionsAnswerList=it.questions
                totalQuestion=it.questions!!.size
            }
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

    fun getDrawableImage(context: Context,Id: Int): Drawable? {
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

    override fun onClick(v: View?) {
        when (v) {
            findViewById<TextView>(R.id.tv_option1 )-> {
                clickCount=1


                layoutTimerBinding.tvOption1.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.bg_select))
                layoutTimerBinding.tvOption2.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.bg_unselect))
                layoutTimerBinding.tvOption3.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.bg_unselect))
                layoutTimerBinding.tvOption4.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.bg_unselect))


            }
            findViewById<TextView>(R.id.tv_option2 )-> {
                clickCount=2


                layoutTimerBinding.tvOption1.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.bg_unselect))
                layoutTimerBinding.tvOption2.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.bg_select))
                layoutTimerBinding.tvOption3.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.bg_unselect))
                layoutTimerBinding.tvOption4.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.bg_unselect))

            }
            findViewById<TextView>(R.id.tv_option3 )-> {
                clickCount=3


                layoutTimerBinding.tvOption1.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.bg_unselect))
                layoutTimerBinding.tvOption2.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.bg_unselect))
                layoutTimerBinding.tvOption3.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.bg_select))
                layoutTimerBinding.tvOption4.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.bg_unselect))

            }
            findViewById<TextView>(R.id.tv_option4 )-> {
                clickCount=4


                layoutTimerBinding.tvOption1.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.bg_unselect))
                layoutTimerBinding.tvOption2.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.bg_unselect))
                layoutTimerBinding.tvOption3.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.bg_unselect))
                layoutTimerBinding.tvOption4.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.bg_select))

            }
        }

    }


    private fun intervalCheck() {
        if (clickCount==1){

            if (selectedItem?.answerId == selectedItem?.countries!![0]?.id){
                correctQuestion++
                layoutTimerBinding.tvAnswer1.visibility=View.VISIBLE
                layoutTimerBinding.tvAnswer1.text="CORRECT"
                layoutTimerBinding.tvAnswer1.setTextColor(Color.parseColor("#01C414"))

            }else {
                layoutTimerBinding.tvAnswer1.visibility=View.VISIBLE
                layoutTimerBinding.tvAnswer1.text="WRONG"
                layoutTimerBinding.tvAnswer1.setTextColor(Color.parseColor("#FF0000"))

            }

            if (selectedItem?.answerId == selectedItem?.countries!![1]?.id && selectedItem?.answerId != selectedItem!!.countries!![0]?.id){
                layoutTimerBinding.tvAnswer2.visibility=View.VISIBLE
                layoutTimerBinding.tvAnswer2.text="CORRECT"
                layoutTimerBinding.tvAnswer2.setTextColor(Color.parseColor("#01C414"))
                layoutTimerBinding.tvOption2.setBackgroundDrawable(ContextCompat.getDrawable(this@QuizsActivity, R.drawable.bg_correct))


            }else if (selectedItem?.answerId == selectedItem?.countries!![2]?.id && selectedItem?.answerId != selectedItem!!.countries!![0]?.id){
                layoutTimerBinding.tvAnswer3.visibility=View.VISIBLE
                layoutTimerBinding.tvAnswer3.text="CORRECT"
                layoutTimerBinding.tvAnswer3.setTextColor(Color.parseColor("#01C414"))
                layoutTimerBinding.tvOption3.setBackgroundDrawable(ContextCompat.getDrawable(this@QuizsActivity, R.drawable.bg_correct))


            }else if (selectedItem?.answerId == selectedItem?.countries!![3]?.id && selectedItem?.answerId != selectedItem!!.countries!![0]?.id){
                layoutTimerBinding.tvAnswer4.visibility=View.VISIBLE
                layoutTimerBinding.tvAnswer4.text="CORRECT"
                layoutTimerBinding.tvAnswer4.setTextColor(Color.parseColor("#01C414"))
                layoutTimerBinding.tvOption4.setBackgroundDrawable(ContextCompat.getDrawable(this@QuizsActivity, R.drawable.bg_correct))

            }
        }else if (clickCount==2){

            if (selectedItem?.answerId == selectedItem?.countries!![1]?.id) {
                correctQuestion++
                layoutTimerBinding.tvAnswer2.visibility = View.VISIBLE
                layoutTimerBinding.tvAnswer2.text = "CORRECT"
                layoutTimerBinding.tvAnswer2.setTextColor(Color.parseColor("#01C414"))

            } else {
                layoutTimerBinding.tvAnswer2.visibility = View.VISIBLE
                layoutTimerBinding.tvAnswer2.text = "WRONG"
                layoutTimerBinding.tvAnswer2.setTextColor(Color.parseColor("#FF0000"))

            }


            if (selectedItem?.answerId == selectedItem?.countries!![0]?.id && selectedItem?.answerId != selectedItem!!.countries!![1]?.id) {
                layoutTimerBinding.tvAnswer1.visibility = View.VISIBLE
                layoutTimerBinding.tvAnswer1.text = "CORRECT"
                layoutTimerBinding.tvAnswer1.setTextColor(Color.parseColor("#01C414"))
                layoutTimerBinding.tvOption1.setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        this@QuizsActivity,
                        R.drawable.bg_correct
                    )
                )


            } else if (selectedItem?.answerId == selectedItem?.countries!![2]?.id && selectedItem?.answerId != selectedItem!!.countries!![1]?.id) {
                layoutTimerBinding.tvAnswer3.visibility = View.VISIBLE
                layoutTimerBinding.tvAnswer3.text = "CORRECT"
                layoutTimerBinding.tvAnswer3.setTextColor(Color.parseColor("#01C414"))
                layoutTimerBinding.tvOption3.setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        this@QuizsActivity,
                        R.drawable.bg_correct
                    )
                )


            } else if (selectedItem?.answerId == selectedItem?.countries!![3]?.id && selectedItem?.answerId != selectedItem!!.countries!![1]?.id) {
                layoutTimerBinding.tvAnswer4.visibility = View.VISIBLE
                layoutTimerBinding.tvAnswer4.text = "CORRECT"
                layoutTimerBinding.tvAnswer4.setTextColor(Color.parseColor("#01C414"))
                layoutTimerBinding.tvOption4.setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        this@QuizsActivity,
                        R.drawable.bg_correct
                    )
                )

            }

        }else if (clickCount==3){

            if (selectedItem?.answerId == selectedItem?.countries!![2]?.id){
                correctQuestion++
                layoutTimerBinding.tvAnswer3.visibility=View.VISIBLE
                layoutTimerBinding.tvAnswer3.text="CORRECT"
                layoutTimerBinding.tvAnswer3.setTextColor(Color.parseColor("#01C414"))

            }else {
                layoutTimerBinding.tvAnswer3.visibility=View.VISIBLE
                layoutTimerBinding.tvAnswer3.text="WRONG"
                layoutTimerBinding.tvAnswer3.setTextColor(Color.parseColor("#FF0000"))

            }


            if (selectedItem?.answerId == selectedItem?.countries!![0]?.id && selectedItem?.answerId != selectedItem!!.countries!![2]?.id){
                layoutTimerBinding.tvAnswer1.visibility=View.VISIBLE
                layoutTimerBinding.tvAnswer1.text="CORRECT"
                layoutTimerBinding.tvAnswer1.setTextColor(Color.parseColor("#01C414"))
                layoutTimerBinding.tvOption1.setBackgroundDrawable(ContextCompat.getDrawable(this@QuizsActivity, R.drawable.bg_correct))


            }else if (selectedItem?.answerId == selectedItem?.countries!![1]?.id && selectedItem?.answerId != selectedItem!!.countries!![2]?.id){
                layoutTimerBinding.tvAnswer2.visibility=View.VISIBLE
                layoutTimerBinding.tvAnswer2.text="CORRECT"
                layoutTimerBinding.tvAnswer2.setTextColor(Color.parseColor("#01C414"))
                layoutTimerBinding.tvOption2.setBackgroundDrawable(ContextCompat.getDrawable(this@QuizsActivity, R.drawable.bg_correct))


            }else if (selectedItem?.answerId == selectedItem?.countries!![3]?.id && selectedItem?.answerId != selectedItem!!.countries!![2]?.id){
                layoutTimerBinding.tvAnswer4.visibility=View.VISIBLE
                layoutTimerBinding.tvAnswer4.text="CORRECT"
                layoutTimerBinding.tvAnswer4.setTextColor(Color.parseColor("#01C414"))
                layoutTimerBinding.tvOption4.setBackgroundDrawable(ContextCompat.getDrawable(this@QuizsActivity, R.drawable.bg_correct))

            }
        }else if (clickCount==4){

            if (selectedItem?.answerId == selectedItem?.countries!![3]?.id){
                correctQuestion++
                layoutTimerBinding.tvAnswer4.visibility=View.VISIBLE
                layoutTimerBinding.tvAnswer4.text="CORRECT"
                layoutTimerBinding.tvAnswer4.setTextColor(Color.parseColor("#01C414"))

            }else {
                layoutTimerBinding.tvAnswer4.visibility=View.VISIBLE
                layoutTimerBinding.tvAnswer4.text="WRONG"
                layoutTimerBinding.tvAnswer4.setTextColor(Color.parseColor("#FF0000"))

            }


            if (selectedItem?.answerId == selectedItem!!.countries!![0]?.id  && selectedItem?.answerId != selectedItem!!.countries!![3]?.id){
                layoutTimerBinding.tvAnswer1.visibility=View.VISIBLE
                layoutTimerBinding.tvAnswer1.text="CORRECT"
                layoutTimerBinding.tvAnswer1.setTextColor(Color.parseColor("#01C414"))
                layoutTimerBinding.tvOption1.setBackgroundDrawable(ContextCompat.getDrawable(this@QuizsActivity, R.drawable.bg_correct))


            }else if (selectedItem?.answerId == selectedItem!!.countries!![1]?.id  &&  selectedItem?.answerId != selectedItem!!.countries!![3]?.id){
                layoutTimerBinding.tvAnswer2.visibility=View.VISIBLE
                layoutTimerBinding.tvAnswer2.text="CORRECT"
                layoutTimerBinding.tvAnswer2.setTextColor(Color.parseColor("#01C414"))
                layoutTimerBinding.tvOption2.setBackgroundDrawable(ContextCompat.getDrawable(this@QuizsActivity, R.drawable.bg_correct))


            }else if (selectedItem?.answerId == selectedItem!!.countries!![2]?.id  && selectedItem?.answerId != selectedItem!!.countries!![3]?.id){
                layoutTimerBinding.tvAnswer3.visibility=View.VISIBLE
                layoutTimerBinding.tvAnswer3.text="CORRECT"
                layoutTimerBinding.tvAnswer3.setTextColor(Color.parseColor("#01C414"))
                layoutTimerBinding.tvOption3.setBackgroundDrawable(ContextCompat.getDrawable(this@QuizsActivity, R.drawable.bg_correct))

            }
        }
    }

    private fun viewDisable() {
        layoutTimerBinding.tvOption1.isEnabled=false
        layoutTimerBinding.tvOption2.isEnabled=false
        layoutTimerBinding.tvOption3.isEnabled=false
        layoutTimerBinding.tvOption4.isEnabled=false
    }


}