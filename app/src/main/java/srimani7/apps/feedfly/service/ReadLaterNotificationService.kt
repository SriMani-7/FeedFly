package srimani7.apps.feedfly.service

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import srimani7.apps.feedfly.FeedFlyApplication
import srimani7.apps.feedfly.R

class ReadLaterNotificationService : Service() {

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val deepLinkIntent = Intent(Intent.ACTION_VIEW, "feedfly://read_later".toUri())

        val pendingIntent = TaskStackBuilder.create(this)
            .addNextIntentWithParentStack(deepLinkIntent)
            .getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT)

        // Create a notification for the foreground service
        val notification = NotificationCompat
            .Builder(this, FeedFlyApplication.READ_LATER_NOTIFICATION_ID)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle("Read later Reminder")
            .setContentText("Don't forget to read your saved articles!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        Log.d("read_later", "running in foreground")
        startForeground(1, notification)

        Log.d("read_later", "read later notification")
        getSystemService<NotificationManager>()?.notify(1, notification)

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null
}