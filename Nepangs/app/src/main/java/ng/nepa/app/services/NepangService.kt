package ng.nepa.app.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.nepa.app.R
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class NepangService : FirebaseMessagingService() {

    private var TAG = "NepangService"

    override fun onMessageReceived(p0: RemoteMessage) {
        // remoteMessage object contains all you need ;-)
        // remoteMessage object contains all you need ;-)
        val notificationTitle = p0.notification?.title
        val notificationContent: String? = p0.notification?.body
        val imageUrl = p0.data["image-url"]
        // lets create a notification with these data
        // lets create a notification with these data
        createNotification(notificationTitle, notificationContent, imageUrl, p0)
    }

    private fun createNotification(notificationTitle: String?, notificationContent: String?, imageUrl: String?, remoteMessage: RemoteMessage?) {

        // Let's create a notification builder object
        // Let's create a notification builder object
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, resources.getString(R.string.notification_channel_id))
        // Create a notificationManager object
        // Create a notificationManager object
        val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // If android version is greater than 8.0 then create notification channel
        // If android version is greater than 8.0 then create notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // Create a notification channel
            val notificationChannel = NotificationChannel(resources.getString(R.string.notification_channel_id), resources.getString(R.string.notification_channel_name), NotificationManager.IMPORTANCE_DEFAULT)
            // Set properties to notification channel
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.vibrationPattern = longArrayOf(100, 200, 300)
            // Pass the notificationChannel object to notificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        // Set the notification parameters to the notification builder object
        // Set the notification parameters to the notification builder object
        builder.setContentTitle(remoteMessage?.notification?.title)
            .setContentText(remoteMessage?.notification?.body)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setSound(defaultSoundUri)
            .setAutoCancel(true)
        // Set the image for the notification
        // Set the image for the notification
        if (remoteMessage?.notification?.imageUrl != null) {
            val bitmap: Bitmap? = getBitmapfromUrl(remoteMessage.data["image-url"])
            builder.setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(bitmap)
                    .bigLargeIcon(null)
            ).setLargeIcon(bitmap)
        }

        notificationManager.notify(1, builder.build())

    }

    private fun getBitmapfromUrl(imageUrl: String?): Bitmap? {

        return try {
            val url = URL(imageUrl)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            Log.e("awesome", "Error in getting notification image: " + e.localizedMessage)
            null
        }

    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String?) {

    }




}
