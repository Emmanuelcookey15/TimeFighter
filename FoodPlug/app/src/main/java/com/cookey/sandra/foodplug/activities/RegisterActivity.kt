package com.cookey.sandra.foodplug.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cookey.sandra.foodplug.R
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        supportActionBar?.hide()

        var intentDestination : Intent? = null

        register_intro.setOnClickListener {
            intentDestination = Intent(this, SignUpActivity::class.java)
            startActivity(intentDestination)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        login_intro.setOnClickListener {
            intentDestination = Intent(this, LoginActivity::class.java)
            startActivity(intentDestination)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }


    }
}
