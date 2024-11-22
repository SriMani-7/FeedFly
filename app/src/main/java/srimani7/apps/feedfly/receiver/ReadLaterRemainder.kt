package srimani7.apps.feedfly.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import srimani7.apps.feedfly.core.preferences.model.ReadLaterRemainder
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

class ReadLaterRemainder {
    companion object {
        internal fun scheduleDailyReminder(context: Context, remainderTime: ReadLaterRemainder?) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, ReadLaterBroadcast::class.java)

            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            if (remainderTime == null) {
                alarmManager.cancel(pendingIntent)
                Log.d("read_later", "remainder cancelled")
            }
            else {
                val date = LocalDate.now()
                val time = LocalTime.of(remainderTime.hour, remainderTime.minute, 0)
                var zonedDateTime = ZonedDateTime.of(date, time, ZoneId.systemDefault())
                if (zonedDateTime.isBefore(ZonedDateTime.now())) zonedDateTime = zonedDateTime.plusDays(1)
                Log.d("read_later", zonedDateTime.toString())
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    zonedDateTime.toEpochSecond() * 1000,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
            }
        }
    }
}