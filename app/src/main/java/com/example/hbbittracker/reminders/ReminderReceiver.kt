package com.example.hbbittracker.reminders

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.hbbittracker.R
import com.example.hbbittracker.util.NotificationUtils

@SuppressLint("MissingPermission") // prevent compile-time warning after we handle runtime request elsewhere
class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(ctx: Context, intent: Intent?) {
        // Ensure channel exists
        NotificationUtils.createChannel(ctx)

        val notification = NotificationCompat.Builder(ctx, NotificationUtils.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_water)
            .setContentTitle("Hydration Reminder 💧")
            .setContentText("Time to drink some water and stay hydrated!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(ctx).notify(1001, notification)
    }
}
