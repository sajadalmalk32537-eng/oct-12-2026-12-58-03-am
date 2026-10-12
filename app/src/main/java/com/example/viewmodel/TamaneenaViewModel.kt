package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.alarm.PrayerAlarmScheduler
import com.example.data.*
import com.example.model.*
import com.example.network.GeminiFiqhService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TamaneenaViewModel(application: Application) : AndroidViewModel(application) {

    private val context: Context get() = getApplication<Application>().applicationContext

    // Navigation Stack
    private val _currentScreen = MutableStateFlow(ScreenRoute.HOME)
    val currentScreen: StateFlow<ScreenRoute> = _currentScreen.asStateFlow()

    private val screenStack = mutableListOf<ScreenRoute>()

    // Selected Items for Detail Readers
    val selectedSurah = MutableStateFlow<Surah?>(QuranData.surahsList.first())
    val selectedDua = MutableStateFlow<DuaItem?>(DuasData.duasList.first())
    val selectedMunajat = MutableStateFlow<MunajatItem?>(MunajatData.munajatList.first())
    val selectedZiyarah = MutableStateFlow<ZiyarahItem?>(ZiyaratData.ziyaratList.first())

    // Prayer Alarms
    private val _prayerAlarms = MutableStateFlow<List<PrayerAlarmSetting>>(emptyList())
    val prayerAlarms: StateFlow<List<PrayerAlarmSetting>> = _prayerAlarms.asStateFlow()

    // Alarm Configuration Dialog/Editor
    val editingAlarm = MutableStateFlow<PrayerAlarmSetting?>(null)

    // Tasbih Counter State
    // Tasbih Fatima Al-Zahra: 34 Allahu Akbar, 33 Alhamdulillah, 33 Subhanallah
    enum class TasbihStage(val title: String, val maxCount: Int, val description: String) {
        ALLAHU_AKBAR("الله أكبر", 34, "تكبير الله وتعظيمه (٣٤ مرة)"),
        ALHAMDULILLAH("الحمد لله", 33, "حمد الله وشكره على نعمه (٣٣ مرة)"),
        SUBHANALLAH("سبحان الله", 33, "تسبيح الله وتنزيهه عن كل نقص (٣٣ مرة)")
    }

    val tasbihStage = MutableStateFlow(TasbihStage.ALLAHU_AKBAR)
    val tasbihCurrentCount = MutableStateFlow(0)
    val tasbihTotalCount = MutableStateFlow(0)
    val tasbihCompletedCycles = MutableStateFlow(0)
    val isCustomTasbihMode = MutableStateFlow(false)
    val customTasbihTarget = MutableStateFlow(100)
    val customTasbihTitle = MutableStateFlow("أستغفر الله وأتوب إليه")

    // Azkar Counters State Map
    val azkarCounts = MutableStateFlow<Map<String, Int>>(emptyMap())

    // Favorites / Bookmarks
    val favoriteIds = MutableStateFlow<Set<String>>(setOf("kumayl", "ashura", "1"))

    // Search Query
    val searchQuery = MutableStateFlow("")

    // Audio Playback Simulation State
    val isAudioPlaying = MutableStateFlow(false)
    val audioProgress = MutableStateFlow(0f)
    val activeReciter = MutableStateFlow("ميثم التمار")

    // Fiqh Chat State
    val selectedMarja = MutableStateFlow(FiqhKnowledgeBase.marjas.first())
    val fiqhInput = MutableStateFlow("")
    val isFiqhLoading = MutableStateFlow(false)
    val fiqhMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                isUser = false,
                text = "السلام عليكم ورحمة الله وبركاته.\nمرحباً بك في 'المجيب الفقهي الذكي'. يمكنك طرح أي سؤال أو استفتاء شرعي وفق مذهب أهل البيت (عليهم السلام) وطبقاً للمرجع المختار، وسنوافيك بالحكم الدقيق ومصدره المعتمد.",
                marja = FiqhKnowledgeBase.marjas.first(),
                rulingType = "ترحيب"
            )
        )
    )

    // Text Size Zoom
    val readerFontSizeMultiplier = MutableStateFlow(1.0f)

    init {
        loadAlarmSettings()
    }

    fun loadAlarmSettings() {
        val list = PrayerAlarmScheduler.loadSettings(context)
        _prayerAlarms.value = list
    }

    fun updateAlarmSetting(setting: PrayerAlarmSetting) {
        PrayerAlarmScheduler.saveSetting(context, setting)
        loadAlarmSettings()
    }

    fun toggleAlarm(prayerId: String, enabled: Boolean) {
        val current = _prayerAlarms.value.find { it.prayerId == prayerId } ?: return
        val updated = current.copy(isEnabled = enabled)
        updateAlarmSetting(updated)
    }

    fun testAlarm(setting: PrayerAlarmSetting) {
        vibratePhone(200)
        PrayerAlarmScheduler.triggerTestAlarm(context, setting)
    }

    // Navigation
    fun navigateTo(route: ScreenRoute) {
        if (_currentScreen.value != route) {
            screenStack.add(_currentScreen.value)
            _currentScreen.value = route
        }
    }

    fun navigateBack(): Boolean {
        if (screenStack.isNotEmpty()) {
            val prev = screenStack.removeAt(screenStack.size - 1)
            _currentScreen.value = prev
            return true
        }
        return false
    }

    // Tasbih Functions
    fun incrementTasbih() {
        vibratePhone(40)
        tasbihTotalCount.value += 1

        if (isCustomTasbihMode.value) {
            if (tasbihCurrentCount.value + 1 >= customTasbihTarget.value) {
                vibratePhone(150)
                tasbihCurrentCount.value = 0
                tasbihCompletedCycles.value += 1
            } else {
                tasbihCurrentCount.value += 1
            }
            return
        }

        // Tasbih Zahra logic
        val currentStage = tasbihStage.value
        val count = tasbihCurrentCount.value + 1

        if (count >= currentStage.maxCount) {
            vibratePhone(100)
            tasbihCurrentCount.value = 0
            when (currentStage) {
                TasbihStage.ALLAHU_AKBAR -> tasbihStage.value = TasbihStage.ALHAMDULILLAH
                TasbihStage.ALHAMDULILLAH -> tasbihStage.value = TasbihStage.SUBHANALLAH
                TasbihStage.SUBHANALLAH -> {
                    vibratePhone(250)
                    tasbihStage.value = TasbihStage.ALLAHU_AKBAR
                    tasbihCompletedCycles.value += 1
                }
            }
        } else {
            tasbihCurrentCount.value = count
        }
    }

    fun decrementTasbih() {
        if (tasbihCurrentCount.value > 0) {
            vibratePhone(25)
            tasbihCurrentCount.value -= 1
            if (tasbihTotalCount.value > 0) tasbihTotalCount.value -= 1
        }
    }

    fun resetTasbih() {
        vibratePhone(50)
        tasbihCurrentCount.value = 0
        tasbihStage.value = TasbihStage.ALLAHU_AKBAR
    }

    fun setCustomTasbih(title: String, target: Int) {
        isCustomTasbihMode.value = true
        customTasbihTitle.value = title
        customTasbihTarget.value = target
        tasbihCurrentCount.value = 0
    }

    fun setFatimaZahraTasbih() {
        isCustomTasbihMode.value = false
        tasbihCurrentCount.value = 0
        tasbihStage.value = TasbihStage.ALLAHU_AKBAR
    }

    // Azkar Counter
    fun incrementZikrCount(id: String, maxCount: Int) {
        vibratePhone(35)
        val current = azkarCounts.value[id] ?: 0
        val next = if (current + 1 >= maxCount) {
            vibratePhone(150)
            0
        } else {
            current + 1
        }
        azkarCounts.value = azkarCounts.value + (id to next)
    }

    fun resetZikrCount(id: String) {
        azkarCounts.value = azkarCounts.value + (id to 0)
    }

    // Favorites
    fun toggleFavorite(id: String) {
        val current = favoriteIds.value.toMutableSet()
        if (current.contains(id)) {
            current.remove(id)
        } else {
            current.add(id)
        }
        favoriteIds.value = current
    }

    fun isFavorite(id: String): Boolean = favoriteIds.value.contains(id)

    // Fiqh AI Send
    fun sendFiqhQuestion(query: String? = null) {
        val question = (query ?: fiqhInput.value).trim()
        if (question.isBlank()) return

        val userMsg = ChatMessage(isUser = true, text = question)
        fiqhMessages.value = fiqhMessages.value + userMsg
        fiqhInput.value = ""
        isFiqhLoading.value = true

        viewModelScope.launch {
            val response = GeminiFiqhService.askFiqhQuestion(question, selectedMarja.value)
            fiqhMessages.value = fiqhMessages.value + response
            isFiqhLoading.value = false
        }
    }

    // Audio Play/Pause simulation
    fun toggleAudio() {
        isAudioPlaying.value = !isAudioPlaying.value
    }

    private fun vibratePhone(durationMs: Long) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {}
    }
}
