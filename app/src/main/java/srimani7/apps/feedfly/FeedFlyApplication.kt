package srimani7.apps.feedfly

import android.app.Application
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FeedFlyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val notificationChannelCompat = NotificationChannelCompat
            .Builder(READ_LATER_NOTIFICATION_ID, NotificationManagerCompat.IMPORTANCE_HIGH)
            .setName("Read later remainder").build()

        NotificationManagerCompat.from(this).createNotificationChannel(notificationChannelCompat)
    }

    companion object {
        const val READ_LATER_NOTIFICATION_ID = "feedfly_read_later"
    }
}