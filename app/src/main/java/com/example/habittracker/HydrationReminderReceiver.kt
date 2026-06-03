package com.example.habittracker

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class HydrationReminderReceiver : BroadcastReceiver() {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent?) {
        ensureNotificationChannel(context)

        val contentIntent = PendingIntent.getActivity(
            context,
            REQUEST_CODE,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            pendingIntentFlags()
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_water_drop)
            .setContentTitle(context.getString(R.string.hydration_reminder_title))
            .setContentText(context.getString(R.string.hydration_reminder_body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val intervalMinutes = prefs.getInt(KEY_INTERVAL_MINUTES, -1)
        if (intervalMinutes > 0) {
            scheduleExact(context, intervalMinutes)
        }
    }

    companion object {
        const val PREFS_NAME = "hydration_reminder_prefs"
        const val KEY_INTERVAL_MINUTES = "interval_minutes"
        private const val CHANNEL_ID = "hydration_reminders_channel"
        private const val REQUEST_CODE = 1010
        private const val NOTIFICATION_ID = 2025

        fun scheduleExact(context: Context, intervalMinutes: Int) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val triggerAtMillis = System.currentTimeMillis() + intervalMinutes * 60 * 1000L
            val pendingIntent = buildPendingIntent(context) ?: return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            }
        }

        fun cancelScheduled(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pendingIntent = buildPendingIntent(context, flagOverride = PendingIntent.FLAG_NO_CREATE)
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
            }
        }

        private fun buildPendingIntent(
            context: Context,
            flagOverride: Int? = null
        ): PendingIntent? {
            val intent = Intent(context, HydrationReminderReceiver::class.java)
            val baseFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
            val flags = flagOverride?.let { it or baseFlags }
                ?: (PendingIntent.FLAG_UPDATE_CURRENT or baseFlags)
            return PendingIntent.getBroadcast(context, REQUEST_CODE, intent, flags)
        }

        private fun ensureNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = context.getString(R.string.reminder_channel_name)
                val description = context.getString(R.string.reminder_channel_description)
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    name,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    this.description = description
                }
                val manager = context.getSystemService(NotificationManager::class.java)
                manager?.createNotificationChannel(channel)
            }
        }

        private fun pendingIntentFlags(): Int {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        }
    }
}
