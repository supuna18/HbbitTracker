package com.example.hbbittracker.reminders

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast

object HydrationScheduler {

    fun scheduleReminder(context: Context, intervalSeconds: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            101,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // ⏰ trigger time - interval seconds
        val triggerTime = System.currentTimeMillis() + (intervalSeconds * 1000L)

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            (intervalSeconds * 1000L),
            pendingIntent
        )

        Toast.makeText(
            context,
            "🔔 Reminder set for every $intervalSeconds seconds",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun cancelReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            101,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
        Toast.makeText(context, "❌ Reminder cancelled", Toast.LENGTH_SHORT).show()
    }
}
