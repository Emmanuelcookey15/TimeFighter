package com.raywenderlich.timefighter.cookey

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

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


        resetGame()

        tapMeButton.setOnClickListener {
            incrementScore()
        }
    }

    private fun resetGame(){
        score = 0

        //Setting the placeholder text to be zero are runtime
        gameScoreTextView.text = getString(R.string.yourScore, score)

        val initialTimeLeft = iniitialCountDown/1000
        timeLeftTextView.text = getString(R.string.timeLeft, initialTimeLeft)


        countDownTimer = object: CountDownTimer(iniitialCountDown, countDownInteval){
            override fun onFinish() {
                endGame()
            }

            override fun onTick(millisUntilFinished: Long) {
                val timeLeft = millisUntilFinished/1000
                timeLeftTextView.text = getString(R.string.timeLeft, timeLeft)
            }

        }
        gameStarted = false
    }

    private fun incrementScore() {
        if (!gameStarted){
            startGame()
        }

        score += 1
        val newScore = getString(R.string.yourScore, score)
        gameScoreTextView.text = newScore
    }

    private fun startGame(){
        countDownTimer.start()
        gameStarted = true
    }

    private fun endGame(){
        Toast.makeText(this, getString(R.string.gameOverMessage), Toast.LENGTH_LONG).show()
        resetGame()
    }
}
