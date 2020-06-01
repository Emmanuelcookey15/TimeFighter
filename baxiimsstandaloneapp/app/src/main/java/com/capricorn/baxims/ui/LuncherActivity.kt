package com.capricorn.baxims.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.capricorn.baxims.ui.auth.AuthContainer
import com.capricorn.baxims.utils.navigateTo

class LuncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigateTo<AuthContainer>()
    }
}
