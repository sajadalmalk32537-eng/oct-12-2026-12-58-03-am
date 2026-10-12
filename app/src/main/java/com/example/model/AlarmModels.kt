package com.example.model

enum class AlarmSound(val arabicTitle: String, val description: String) {
    ATHAN_NAJAF("أذان العتبة العلوية المقدسة", "أذان روحاني مهيب من رحاب مرقد أمير المؤمنين (ع)"),
    ATHAN_AL_KAABI("أذان علي الكعبي", "أذان حجازي عراقي شجي ومؤثر"),
    ATHAN_HALWACHI("أذان أباذر الحلواجي", "أذان عذب بصوت الحاج أباذر الحلواجي"),
    TAKBEERAT("تكبيرات وصلاة على محمد وآل محمد", "تكبيرات بصوت شجي مع الصلوات المباركة"),
    GENTLE_CHIME("نغمة طمأنينة هادئة", "نغمة استيقاظ هادئة وخافتة لراحة النفس"),
    SILENT("تنبيه صامت (اهتزاز فقط)", "تنبيه بدون صوت مع اهتزاز الشاشة والجهاز")
}

data class PrayerAlarmSetting(
    val prayerId: String,
    val prayerName: String,
    val defaultTime: String,
    val isEnabled: Boolean = true,
    val sound: AlarmSound = AlarmSound.ATHAN_NAJAF,
    val isVibrationEnabled: Boolean = true,
    val snoozeMinutes: Int = 5,
    val preAlertMinutes: Int = 0, // 0 = at exact time, 5 = 5 min before, etc.
    val volume: Float = 0.85f
)
