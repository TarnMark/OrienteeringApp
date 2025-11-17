package ee.ut.cs.orienteering.ui.viewmodels

import android.app.Application
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Color
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import ee.ut.cs.orienteering.data.AppDatabase
import ee.ut.cs.orienteering.data.Quest
import ee.ut.cs.orienteering.data.QuestWithQuestions
import ee.ut.cs.orienteering.data.Question
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class QuestionsViewModel(app: Application) : AndroidViewModel(app) {

    private val dao = AppDatabase.getDatabase(app).questionDao()

    fun questionsForQuest(questId: Int) =
        dao.getQuestionsForQuest(questId) // Flow<List<Question>>

    fun questionsForQuestState(questId: Int): StateFlow<List<Question>> =
        dao.getQuestionsForQuest(questId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addQuestion(text: String, questId: Int, location: String = "") {
        viewModelScope.launch {
            val newQuestion = Question(
                id = 0,
                questId = questId,
                questionText = text,
                answer = "",
                location = location
            )
            dao.insert(newQuestion)
        }
    }

    private val _answers = MutableStateFlow<Map<Int, String>>(emptyMap())
    val answers: StateFlow<Map<Int, String>> = _answers

    private val _checked = MutableStateFlow<Set<Int>>(emptySet())
    val checked: StateFlow<Set<Int>> = _checked

    fun updateAnswer(questionId: Int, newAnswer: String) {
        _answers.value = _answers.value.toMutableMap().apply { put(questionId, newAnswer) }
    }

    fun toggleChecked(questionId: Int) {
        _checked.value = _checked.value.toMutableSet().apply {
            if (contains(questionId)) remove(questionId) else add(questionId)
        }
    }

    fun loadQuestionsFromApi(lobbyId: Int) {
        viewModelScope.launch {
            try {
                val response = listOf(
                    Question(1, lobbyId, "What is Kotlin?", "", ""),
                    Question(2, lobbyId, "What is Jetpack Compose?", "", "")
                )
                response.forEach { dao.insert(it) }
            } catch (e: Exception) {
                Log.e("QuestionsViewModel", "API error: ${e.message}")
            }
        }
    }
    fun addQuestionWithLocation(text: String, questId: Int, location: String) {
        viewModelScope.launch {
            val newQuestion = Question(
                id = 0,
                questId = questId,
                questionText = text,
                answer = "",
                location = location
            )
            dao.insert(newQuestion)
        }
    }

    // Quests QR code export

    fun generateQRCode(data: String, size: Int = 512): Bitmap {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, size, size)

        val bmp = createBitmap(size, size, Bitmap.Config.RGB_565)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bmp[x, y] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
            }
        }
        return bmp
    }

    suspend fun generateQuestExportJson(currentQuest: Quest): String {
        val quest = currentQuest
        val questions = questionsForQuest(quest.id).first()

        val export = QuestWithQuestions(
            quest = currentQuest,
            questions = questions
        )

        return  Json.encodeToString(export)
    }

    suspend fun generateQuestQrBitmap(currentQuest: Quest): Bitmap {
        val exportJson = generateQuestQrString(currentQuest)
        return generateQRCode(exportJson)
    }

    suspend fun generateQuestQrString(currentQuest: Quest): String {
        val json = generateQuestExportJson(currentQuest)
        val base64 = Base64.encodeToString(json.toByteArray(), Base64.NO_WRAP)
        return "qrexport://quest?data=$base64"
    }
    fun saveQrToGallery(bitmap: Bitmap) {
        val resolver = getApplication<Application>().contentResolver

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "quest_${System.currentTimeMillis()}.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/QuestQR")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        if (uri != null) {
            resolver.openOutputStream(uri).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out!!)
            }

            contentValues.clear()
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)
        }
    }


}