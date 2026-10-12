package com.example.model

enum class ScreenRoute {
    HOME,
    QURAN,
    SURAH_READER,
    DUAS,
    DUA_READER,
    MUNAJAT,
    MUNAJAT_READER,
    ZIYARAT,
    ZIYARAT_READER,
    AZKAR,
    TASBIH,
    SALAT_LAYL,
    AHLULBAYT,
    FIQH_CHAT,
    FAVORITES,
    SEARCH,
    SETTINGS
}

data class PrayerTime(
    val id: String,
    val arabicName: String,
    val timeFormatted: String,
    val isHighlighted: Boolean = false,
    val iconName: String = "prayer"
)

data class Verse(
    val number: Int,
    val text: String,
    val tafsir: String = ""
)

data class Surah(
    val id: Int,
    val name: String,
    val englishName: String,
    val type: String, // مكية / مدنية
    val ayahCount: Int,
    val verses: List<Verse> = emptyList()
)

data class DuaItem(
    val id: String,
    val title: String,
    val source: String,
    val category: String, // الصباح والمساء, الفرج, الصحيفة السجادية, etc.
    val description: String,
    val text: String,
    val virtue: String = "",
    val durationMinutes: Int = 15
)

data class MunajatItem(
    val id: Int,
    val title: String,
    val imam: String = "الإمام علي بن الحسين زين العابدين (ع)",
    val category: String,
    val description: String,
    val text: String
)

data class ZiyarahItem(
    val id: String,
    val title: String,
    val target: String,
    val category: String, // زيارة المعصومين, زيارات خاصة, etc.
    val description: String,
    val text: String,
    val etiquette: String = ""
)

data class ZikrItem(
    val id: String,
    val title: String,
    val category: String, // الصباح, المساء, بعد الصلاة, النوم
    val text: String,
    val targetCount: Int,
    val reward: String = ""
)

data class AhlulBaytFigure(
    val id: String,
    val name: String,
    val title: String,
    val category: String, // الأئمة الأطهار, الصحابة, النساء
    val lifespan: String,
    val shrine: String,
    val description: String,
    val famousSayings: List<String>
)

data class SalatLaylGuide(
    val title: String,
    val summary: String,
    val steps: List<SalatStep>,
    val qunutWitrSupplications: List<String>,
    val duaHazeenText: String,
    val virtues: List<String>
)

data class SalatStep(
    val stepNumber: Int,
    val title: String,
    val rakats: Int,
    val instruction: String,
    val recommendedSurahs: String
)

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val isUser: Boolean,
    val text: String,
    val marja: String? = null,
    val rulingType: String? = null, // واجب, مستحب, جائز / مباح, مكروه, حرام
    val references: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
