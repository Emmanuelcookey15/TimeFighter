package com.timbu.booking.androidcore

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {

    private var notificationManager: NotificationManagerCompat? = null
    lateinit var editTextTitle: EditText
    lateinit var editTextMessage: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextTitle = findViewById(R.id.edit_text_title)
        editTextMessage = findViewById(R.id.edit_text_message)

        notificationManager = NotificationManagerCompat.from(this)

    }

    fun sendOnChannel1(view: View) {
        val title = editTextTitle.text.toString()
        val message = editTextMessage.text.toString()

        val activityIntent = Intent(this, MainActivity::class.java)
        val contentIntent = PendingIntent.getActivity(this,
            0, activityIntent, 0)

        val broadcastIntent = Intent(this, NotificationReceiver::class.java)
        broadcastIntent.putExtra("toastMessage", message)
        val actionIntent = PendingIntent.getBroadcast(this,
            0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.ic_android)

        val notification = NotificationCompat.Builder(this, App().CHANNEL_1_ID)
            .setSmallIcon(R.drawable.ic_one)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(getString(R.string.long_dummy_text))
                .setBigContentTitle("Big Context Title")
                .setSummaryText("Summary Text")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setContentIntent(contentIntent)
            .setColor(Color.BLUE)
            .setOnlyAlertOnce(true)
            .addAction(R.mipmap.ic_launcher, "Toast", actionIntent)
            .setAutoCancel(true)
            .build()

        notificationManager?.notify(1, notification)

    }

    fun sendOnChannel2(view: View) {
        val title = editTextTitle.text.toString()
        val message = editTextMessage.text.toString()

        val notification = NotificationCompat.Builder(this, App().CHANNEL_2_ID)
            .setSmallIcon(R.drawable.ic_two)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.InboxStyle()
                .addLine("This is line 1")
                .addLine("This is line 2")
                .addLine("This is line 3")
                .addLine("This is line 4")
                .addLine("This is line 5")
                .addLine("This is line 6")
                .addLine("This is line 7"))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        notificationManager?.notify(2, notification)
    }
}


class NotificationReceiver: BroadcastReceiver(){
    override fun onReceive(p0: Context?, p1: Intent?) {
        val message = p1?.getStringExtra("toastMessage")
        Toast.makeText(p0, message, Toast.LENGTH_LONG).show()

    }
}