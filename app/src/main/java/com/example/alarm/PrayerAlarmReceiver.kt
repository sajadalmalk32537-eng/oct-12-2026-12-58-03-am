package com.example.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import com.example.MainActivity

class PrayerAlarmReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "tamaneena_prayer_alarms_channel"
        const val CHANNEL_NAME = "تنبيهات وأذان الصلوات (طمأنينة)"
        const val EXTRA_PRAYER_NAME = "extra_prayer_name"
        const val EXTRA_PRAYER_ID = "extra_prayer_id"
        const val EXTRA_SOUND_NAME = "extra_sound_name"
        const val EXTRA_VIBRATE = "extra_vibrate"
        const val EXTRA_SNOOZE_MINS = "extra_snooze_mins"
        const val ACTION_ALARM = "com.example.ACTION_PRAYER_ALARM"
        const val ACTION_DISMISS = "com.example.ACTION_DISMISS_ALARM"
        const val ACTION_SNOOZE = "com.example.ACTION_SNOOZE_ALARM"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action

        if (action == ACTION_DISMISS) {
            val notifId = intent.getIntExtra("notif_id", 1001)
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(notifId)
            return
        }

        if (action == ACTION_SNOOZE) {
            val notifId = intent.getIntExtra("notif_id", 1001)
            val prayerName = intent.getStringExtra(EXTRA_PRAYER_NAME) ?: "الصلاة"
            val snoozeMins = intent.getIntExtra(EXTRA_SNOOZE_MINS, 5)

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(notifId)

            // Reschedule in snoozeMins
            PrayerAlarmScheduler.scheduleSnooze(context, prayerName, snoozeMins)
            return
        }

        val prayerName = intent.getStringExtra(EXTRA_PRAYER_NAME) ?: "حان الآن وقت الصلاة"
        val prayerId = intent.getStringExtra(EXTRA_PRAYER_ID) ?: "prayer"
        val soundName = intent.getStringExtra(EXTRA_SOUND_NAME) ?: "أذان العتبة العلوية المقدسة"
        val shouldVibrate = intent.getBooleanExtra(EXTRA_VIBRATE, true)
        val snoozeMins = intent.getIntExtra(EXTRA_SNOOZE_MINS, 5)

        val notifId = prayerId.hashCode()

        // Trigger vibration if enabled
        if (shouldVibrate) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                    val vibrator = vibratorManager.defaultVibrator
                    vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 500, 200, 500, 200, 1000), -1))
                } else {
                    @Suppress("DEPRECATION")
                    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    vibrator.vibrate(longArrayOf(0, 500, 200, 500, 200, 1000), -1)
                }
            } catch (_: Exception) {}
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create Channel for Android O+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val alarmSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()

            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "قناة تنبيهات أذان الصلوات المباركة لتطبيق طمأنينة"
                enableVibration(shouldVibrate)
                setSound(alarmSoundUri, audioAttributes)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Tap opens app
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            notifId,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Dismiss Action
        val dismissIntent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            this.action = ACTION_DISMISS
            putExtra("notif_id", notifId)
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(
            context,
            notifId + 1,
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Snooze Action
        val snoozeIntent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            this.action = ACTION_SNOOZE
            putExtra("notif_id", notifId)
            putExtra(EXTRA_PRAYER_NAME, prayerName)
            putExtra(EXTRA_SNOOZE_MINS, snoozeMins)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            notifId + 2,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("🕌 حان موعد صلاة $prayerName")
            .setContentText("حيّ على الصلاة، حيّ على الفلاح، حيّ على خير العمل • $soundName")
            .setStyle(NotificationCompat.BigTextStyle().bigText("حان الآن موعد صلاة $prayerName وفق توقيت النجف الأشرف.\nنغم التنبيه: $soundName\nأقبل على ربك بطمأنينة وسكينة."))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(openAppPendingIntent)
            .addAction(android.R.drawable.ic_media_pause, "غفوة ($snoozeMins د)", snoozePendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "إيقاف الأذان", dismissPendingIntent)
            .build()

        notificationManager.notify(notifId, notification)
    }
}
