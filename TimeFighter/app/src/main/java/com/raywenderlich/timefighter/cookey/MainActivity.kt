package com.raywenderlich.timefighter.cookey

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    internal var score = 0

    internal lateinit var tapMeButton: Button
    internal lateinit var gameScoreTextView: TextView
    internal lateinit var timeLeftTextView: TextView

    internal var gameStarted = false

    internal lateinit var countDownTimer: CountDownTimer
    internal val iniitialCountDown: Long = 60000
    internal val countDownInteval: Long = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tapMeButton = findViewById(R.id.tapMeButton)
        gameScoreTextView = findViewById(R.id.gameScoreTextView)
        timeLeftTextView = findViewById(R.id.timeLeftTextView)

        //Setting the placeholder text to be zero are runtime
        gameScoreTextView.text = getString(R.string.yourScore, score)

        tapMeButton.setOnClickListener {
            incrementScore()
        }
    }

    private fun resetGame(){
        score = 0

        gameScoreTextView.text = getString(R.string.yourScore, score)

        val initialTimeLeft = iniitialCountDown/1000
        timeLeftTextView.text = getString(R.string.timeLeft, initialTimeLeft)


        countDownTimer = object: CountDownTimer(iniitialCountDown, countDownInteval){
            override fun onFinish() {
                //To be Implemented Later
            }

            override fun onTick(millisUntilFinished: Long) {
                val timeLeft = millisUntilFinished/1000
                timeLeftTextView.text = getString(R.string.timeLeft, timeLeft)
            }

        }
        gameStarted = false
    }

    private fun incrementScore() {
        score += 1
        val newScore = getString(R.string.yourScore, score)
        gameScoreTextView.text = newScore
    }
}
