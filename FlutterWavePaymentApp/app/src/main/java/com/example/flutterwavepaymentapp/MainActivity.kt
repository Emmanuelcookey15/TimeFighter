package com.example.flutterwavepaymentapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.flutterwave.raveandroid.RaveConstants
import com.flutterwave.raveandroid.RavePayActivity
import com.flutterwave.raveandroid.RavePayManager
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    val amount_1 = 5000
    var email = "emmanuelcookey744@gmail.com"
    var fName = "Emmanuel"
    var lName = "Cookey"
    var narration = "payment for service"
    var txRef: String? = null
    var country = "NG"
    var currency = "NGN"


    private val ACTION_UPDATE_NOTIFICATION = "com.example.flutterwavepaymentapp.ACTION_UPDATE_NOTIFICATION"

    private val PRIMARY_CHANNEL_ID = "primary_notification_channel"
    private val NOTIFICATION_ID = 0

    private var mNotifyManager: NotificationManager? = null

    private val publicKey =
        "FLWPUBK_TEST-47c0f911dc178ee7e1ad6ab091c333d6-X" //Get your public key from your account

    private val encryptionKey =
        "FLWSECK_TEST016ed9422564" //Get your encryption key from your account

    private var mReceiver = NotificationReceiver()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()

        setNotificationButtonState(true, false, false)

        registerReceiver(mReceiver, IntentFilter(ACTION_UPDATE_NOTIFICATION))

        payment_btn_pay_now.setOnClickListener {
            makePayment(amount_1)
        }

        notify.setOnClickListener {
            sendNotification()
        }

        update.setOnClickListener {
            updateNotification()
        }

        cancel.setOnClickListener {
            cancelNotification()
        }

    }



    fun makePayment(amount: Int) {
        txRef = email + " " + UUID.randomUUID().toString()
        /*
        Create instance of RavePayManager
         */RavePayManager(this).setAmount(amount.toDouble())
            .setCountry(country)
            .setCurrency(currency)
            .setEmail(email)
            .setfName(fName)
            .setlName(lName)
            .setNarration(narration)
            .setPublicKey(publicKey)
            .setEncryptionKey(encryptionKey)
            .setTxRef(txRef)
            .acceptAccountPayments(true)
            .acceptCardPayments(true)
            .acceptMpesaPayments(false)
            .acceptGHMobileMoneyPayments(false)
            .onStagingEnv(false)
            .allowSaveCardFeature(true)
            .withTheme(R.style.DefaultPayTheme)
            .initialize()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {
            val message = data.getStringExtra("response")
            when (resultCode) {
                RavePayActivity.RESULT_SUCCESS -> {
                    Toast.makeText(this, "SUCCESS $message", Toast.LENGTH_SHORT).show()
                }
                RavePayActivity.RESULT_ERROR -> {
                    Toast.makeText(this, "ERROR $message", Toast.LENGTH_SHORT).show()
                }
                RavePayActivity.RESULT_CANCELLED -> {
                    Toast.makeText(this, "CANCELLED $message", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun createNotificationChannel() {
        mNotifyManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(PRIMARY_CHANNEL_ID,
                "Mascot Notification", NotificationManager.IMPORTANCE_HIGH)

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.CYAN
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Who the fuck are you"
            mNotifyManager!!.createNotificationChannel(notificationChannel)

        }

    }


    fun updateNotification() {
        val androidImage = BitmapFactory.decodeResource(resources,R.drawable.mascot_1)

        val notifyBuilder = getNotificationBuilder()

        notifyBuilder.setStyle(NotificationCompat.BigPictureStyle()
            .bigPicture(androidImage)
            .setBigContentTitle("Notification Updated!"))

        mNotifyManager?.notify(NOTIFICATION_ID, notifyBuilder.build())

        setNotificationButtonState(false, false, true)
    }


    private fun cancelNotification() {
        mNotifyManager?.cancel(NOTIFICATION_ID)

        setNotificationButtonState(true, false, false)
    }


    private fun sendNotification(){

        val updateIntent = Intent(ACTION_UPDATE_NOTIFICATION);
        val updatePendingIntent = PendingIntent
            .getBroadcast(this, NOTIFICATION_ID, updateIntent, PendingIntent.FLAG_ONE_SHOT)

        val notifyBuilder = getNotificationBuilder()
        notifyBuilder.addAction(R.drawable.ic_update_foreground, "Update Notification", updatePendingIntent)
        mNotifyManager?.notify(NOTIFICATION_ID, notifyBuilder.build())
        setNotificationButtonState(false, true, true)
    }


    private fun getNotificationBuilder(): NotificationCompat.Builder{
        val notificationIntent = Intent(this, MainActivity::class.java)
        val notificationPendingIntent = PendingIntent.getActivity(this,
            NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notifyBuilder = NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
            .setContentTitle("You've been notified!")
            .setContentText("This is your notification text.")
            .setColor(Color.CYAN)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setSmallIcon(R.drawable.ic_android_foreground)
            .setContentIntent(notificationPendingIntent)
            .setAutoCancel(true)

        return notifyBuilder
    }


    fun setNotificationButtonState(isNotifyEnabled: Boolean, isUpdateEnabled: Boolean, isCancelEnabled: Boolean) {
        notify.isEnabled = isNotifyEnabled
        update.isEnabled = isUpdateEnabled
        cancel.isEnabled = isCancelEnabled
    }


    override fun onDestroy() {
        unregisterReceiver(mReceiver)
        super.onDestroy()
    }


    inner class NotificationReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            // Update the notification
            updateNotification()

        }

    }

}
