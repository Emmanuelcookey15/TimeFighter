package com.timbu.booking.androidcore

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class App : Application() {

    val CHANNEL_1_ID = "channel1"
    val CHANNEL_2_ID = "channel2"

    override fun onCreate() {
        super.onCreate()

        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel1 = NotificationChannel(
                CHANNEL_1_ID,
                "channel1",
                NotificationManager.IMPORTANCE_HIGH
            )

            channel1.description = "This is Channel 1"

            val channel2 = NotificationChannel(
                CHANNEL_2_ID,
                "channel2",
                NotificationManager.IMPORTANCE_LOW
            )

            channel2.description = "This is Channel 2"

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel1)
            manager.createNotificationChannel(channel2)
        }
    }

}