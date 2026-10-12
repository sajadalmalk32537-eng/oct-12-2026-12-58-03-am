package com.example.data

import com.example.model.PrayerTime

object PrayerTimesData {
    val locationName = "النجف الأشرف"
    val hijriDate = "الاثنين، 2 ربيع الثاني 1448 هـ"
    val gregorianDate = "14 أيلول / سبتمبر 2026 م"

    val defaultPrayers = listOf(
        PrayerTime(id = "fajr", arabicName = "الفجر", timeFormatted = "4:37", isHighlighted = false),
        PrayerTime(id = "sunrise", arabicName = "الشروق", timeFormatted = "6:00", isHighlighted = false),
        PrayerTime(id = "dhuhr", arabicName = "الظهر", timeFormatted = "12:10", isHighlighted = false),
        PrayerTime(id = "asr", arabicName = "العصر", timeFormatted = "3:42", isHighlighted = false),
        PrayerTime(id = "maghrib", arabicName = "المغرب", timeFormatted = "6:12", isHighlighted = true),
        PrayerTime(id = "isha", arabicName = "العشاء", timeFormatted = "7:32", isHighlighted = false)
    )

    val midnightTime = "11:45 م"
    val imsakTime = "4:25 ص"
}
