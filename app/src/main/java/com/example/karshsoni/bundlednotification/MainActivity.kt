package com.example.karshsoni.bundlednotification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import android.app.PendingIntent
import android.os.Build
import android.support.annotation.RequiresApi
import android.widget.Toast


class MainActivity : AppCompatActivity(), View.OnClickListener {

    val TAG = "MainActivity"
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    private val channelId = "com.example.karshsoni.bundlednotification"
    private val text ="test"
    lateinit var builder: NotificationCompat.Builder
    lateinit var builder2: NotificationCompat.Builder
    var counter: Int = 0
    var bundleNotificationId: Int = 100
    var singleNotificationId = 100
    var bundle_notification_id : String? = null
    val group_key = "com.example.karshsoni.bundlednotification"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        btnBundleNotification.setOnClickListener(this)
        btnSingleNotification.setOnClickListener(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val extras = intent.extras
        if (extras != null) {
            val notification_id = extras.getInt("notification_id")
            Toast.makeText(applicationContext, "Notification with ID $notification_id is cancelled", Toast.LENGTH_LONG).show()
            notificationManager.cancel(notification_id)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.btnBundleNotification -> {
                if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
                    val groupChannel = NotificationChannel("bundle_channel_id", "bundle_channel_name"
                            , NotificationManager.IMPORTANCE_HIGH)
                    notificationManager.createNotificationChannel(groupChannel);
                }
                bundleNotificationId += 100
                singleNotificationId = bundleNotificationId
                val bundle_notification_id = "bundle_notification_$bundleNotificationId"
                val resultIntent = Intent(this, MainActivity::class.java)
                resultIntent.putExtra("notification", "Summary Notification Clicked")
                resultIntent.putExtra("notification_id", bundleNotificationId);
                resultIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                val resultPendingIntent = PendingIntent.getActivity(this, 0,
                        resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)


                val summaryNotificationBuilder = NotificationCompat.Builder(this, "bundle_channel_id")
                        .setGroup(bundle_notification_id)
                        .setGroupSummary(true)
                        .setContentTitle("Bundled Notification. $bundleNotificationId")
                        .setContentText("Content Text for bundle notification")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(resultPendingIntent)
                notificationManager.notify(bundleNotificationId, summaryNotificationBuilder.build());

            }

            R.id.btnSingleNotification -> {
                bundle_notification_id = "bundle_notification_" + bundleNotificationId;
                var resultIntent = Intent(this, MainActivity::class.java)
                resultIntent.putExtra("notification", "Summary Notification Clicked")
                resultIntent.putExtra("notification_id", bundleNotificationId)
                resultIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                var resultPendingIntent = PendingIntent.getActivity(this, 0,
                        resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    if (notificationManager.notificationChannels.size < 2) {
                        val groupChannel = NotificationChannel("bundle_channel_id", "bundle_channel_name", NotificationManager.IMPORTANCE_LOW)
                        notificationManager.createNotificationChannel(groupChannel)
                        val channel = NotificationChannel("channel_id", "channel_name", NotificationManager.IMPORTANCE_DEFAULT)
                        notificationManager.createNotificationChannel(channel)
                    }
                }
                val summaryNotificationBuilder = NotificationCompat.Builder(this, "bundle_channel_id")
                        .setGroup(bundle_notification_id)
                        .setGroupSummary(true)
                        .setContentTitle("Bundled Notification $bundleNotificationId")
                        .setContentText("Content Text for group summary")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(resultPendingIntent)
                if (singleNotificationId == bundleNotificationId)
                    singleNotificationId = bundleNotificationId + 1
                else
                    singleNotificationId++

                resultIntent = Intent(this, MainActivity::class.java)
                resultIntent.putExtra("notification", "Single notification clicked")
                resultIntent.putExtra("notification_id", singleNotificationId)
                resultIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                resultPendingIntent = PendingIntent.getActivity(this, singleNotificationId, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                val notification = NotificationCompat.Builder(this, "channel_id")
                        .setGroup(bundle_notification_id)
                        .setContentTitle("New Notification $singleNotificationId")
                        .setContentText("Content for the notification")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setGroupSummary(false)
                        .setContentIntent(resultPendingIntent)

                notificationManager.notify(singleNotificationId, notification.build())
                notificationManager.notify(bundleNotificationId, summaryNotificationBuilder.build())
            }

        }
    }

}
