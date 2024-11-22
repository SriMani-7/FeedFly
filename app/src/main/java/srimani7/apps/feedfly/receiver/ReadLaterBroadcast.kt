package srimani7.apps.feedfly.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import srimani7.apps.feedfly.service.ReadLaterNotificationService

class ReadLaterBroadcast: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationIntent = Intent(context, ReadLaterNotificationService::class.java)
        Log.d("read_later", "in ReadLater broadcasting")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context?.startForegroundService(notificationIntent)
        } else context?.startService(notificationIntent)
    }
}