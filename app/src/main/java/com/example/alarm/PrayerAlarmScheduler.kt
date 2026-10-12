package com.example.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.model.AlarmSound
import com.example.model.PrayerAlarmSetting
import java.util.Calendar

object PrayerAlarmScheduler {

    private const val PREFS_NAME = "tamaneena_prayer_alarms_prefs"

    val defaultSettings = listOf(
        PrayerAlarmSetting("fajr", "الفجر", "04:37", isEnabled = true, sound = AlarmSound.ATHAN_AL_KAABI, isVibrationEnabled = true, snoozeMinutes = 5, preAlertMinutes = 0),
        PrayerAlarmSetting("sunrise", "الشروق", "06:00", isEnabled = false, sound = AlarmSound.GENTLE_CHIME, isVibrationEnabled = false, snoozeMinutes = 5, preAlertMinutes = 10),
        PrayerAlarmSetting("dhuhr", "الظهر", "12:10", isEnabled = true, sound = AlarmSound.ATHAN_NAJAF, isVibrationEnabled = true, snoozeMinutes = 10, preAlertMinutes = 0),
        PrayerAlarmSetting("asr", "العصر", "15:42", isEnabled = true, sound = AlarmSound.ATHAN_HALWACHI, isVibrationEnabled = true, snoozeMinutes = 5, preAlertMinutes = 0),
        PrayerAlarmSetting("maghrib", "المغرب", "18:12", isEnabled = true, sound = AlarmSound.ATHAN_NAJAF, isVibrationEnabled = true, snoozeMinutes = 5, preAlertMinutes = 0),
        PrayerAlarmSetting("isha", "العشاء", "19:32", isEnabled = true, sound = AlarmSound.ATHAN_AL_KAABI, isVibrationEnabled = true, snoozeMinutes = 5, preAlertMinutes = 0),
        PrayerAlarmSetting("layl", "صلاة الليل", "03:30", isEnabled = true, sound = AlarmSound.GENTLE_CHIME, isVibrationEnabled = true, snoozeMinutes = 10, preAlertMinutes = 15)
    )

    fun loadSettings(context: Context): List<PrayerAlarmSetting> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return defaultSettings.map { default ->
            val enabled = prefs.getBoolean("${default.prayerId}_enabled", default.isEnabled)
            val soundName = prefs.getString("${default.prayerId}_sound", default.sound.name) ?: default.sound.name
            val sound = try { AlarmSound.valueOf(soundName) } catch (_: Exception) { default.sound }
            val vibrate = prefs.getBoolean("${default.prayerId}_vibrate", default.isVibrationEnabled)
            val snooze = prefs.getInt("${default.prayerId}_snooze", default.snoozeMinutes)
            val preAlert = prefs.getInt("${default.prayerId}_preAlert", default.preAlertMinutes)
            val volume = prefs.getFloat("${default.prayerId}_volume", default.volume)

            default.copy(
                isEnabled = enabled,
                sound = sound,
                isVibrationEnabled = vibrate,
                snoozeMinutes = snooze,
                preAlertMinutes = preAlert,
                volume = volume
            )
        }
    }

    fun saveSetting(context: Context, setting: PrayerAlarmSetting) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply {
            putBoolean("${setting.prayerId}_enabled", setting.isEnabled)
            putString("${setting.prayerId}_sound", setting.sound.name)
            putBoolean("${setting.prayerId}_vibrate", setting.isVibrationEnabled)
            putInt("${setting.prayerId}_snooze", setting.snoozeMinutes)
            putInt("${setting.prayerId}_preAlert", setting.preAlertMinutes)
            putFloat("${setting.prayerId}_volume", setting.volume)
            apply()
        }

        if (setting.isEnabled) {
            schedulePrayerAlarm(context, setting)
        } else {
            cancelPrayerAlarm(context, setting.prayerId)
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    fun schedulePrayerAlarm(context: Context, setting: PrayerAlarmSetting) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        // Parse HH:mm
        val parts = setting.defaultTime.split(":")
        if (parts.size != 2) return
        val hour = parts[0].toIntOrNull() ?: 12
        val minute = parts[1].toIntOrNull() ?: 0

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (setting.preAlertMinutes > 0) {
                add(Calendar.MINUTE, -setting.preAlertMinutes)
            }
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            action = PrayerAlarmReceiver.ACTION_ALARM
            putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_NAME, setting.prayerName)
            putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_ID, setting.prayerId)
            putExtra(PrayerAlarmReceiver.EXTRA_SOUND_NAME, setting.sound.arabicTitle)
            putExtra(PrayerAlarmReceiver.EXTRA_VIBRATE, setting.isVibrationEnabled)
            putExtra(PrayerAlarmReceiver.EXTRA_SNOOZE_MINS, setting.snoozeMinutes)
        }

        val requestCode = setting.prayerId.hashCode()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            }
        } catch (_: SecurityException) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }
    }

    fun cancelPrayerAlarm(context: Context, prayerId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            action = PrayerAlarmReceiver.ACTION_ALARM
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            prayerId.hashCode(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }
    }

    fun scheduleSnooze(context: Context, prayerName: String, snoozeMinutes: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val triggerTime = System.currentTimeMillis() + (snoozeMinutes * 60 * 1000L)

        val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            action = PrayerAlarmReceiver.ACTION_ALARM
            putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_NAME, "$prayerName (تذكير الغفوة)")
            putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_ID, "snooze_${prayerName}")
            putExtra(PrayerAlarmReceiver.EXTRA_SOUND_NAME, "تذكير الغفوة")
            putExtra(PrayerAlarmReceiver.EXTRA_VIBRATE, true)
            putExtra(PrayerAlarmReceiver.EXTRA_SNOOZE_MINS, snoozeMinutes)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            9999,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            }
        } catch (_: Exception) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        }
    }

    fun triggerTestAlarm(context: Context, setting: PrayerAlarmSetting) {
        val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            action = PrayerAlarmReceiver.ACTION_ALARM
            putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_NAME, setting.prayerName)
            putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_ID, setting.prayerId)
            putExtra(PrayerAlarmReceiver.EXTRA_SOUND_NAME, setting.sound.arabicTitle)
            putExtra(PrayerAlarmReceiver.EXTRA_VIBRATE, setting.isVibrationEnabled)
            putExtra(PrayerAlarmReceiver.EXTRA_SNOOZE_MINS, setting.snoozeMinutes)
        }
        context.sendBroadcast(intent)
    }
}
