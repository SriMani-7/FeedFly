package srimani7.apps.feedfly.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class ReadLaterBroadcast: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("read_later", "in ReadLater broadcasting")

    }
}