package ee.ut.cs.orienteering.ui.viewmodels

import android.app.Application
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Color
import android.provider.MediaStore
import android.util.Base64
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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * ViewModel responsible for managing questions within a quest and providing
 * utilities for exporting quests as JSON or QR codes.
 *
 * Responsibilities:
 * - Retrieve questions for a given quest
 * - Add new questions to the database
 * - Maintain UI state for answers and checked questions
 * - Generate QR codes and JSON exports for sharing quests
 * - Save generated QR codes to the device gallery
 *
 * This ViewModel interacts with the Room database through the `questionDao`
 * and exposes reactive state via [answers] and [checked].
 *
 * @param app The application context used to access the Room database and content resolver.
 */
class QuestionsViewModel(app: Application) : AndroidViewModel(app) {

    private val dao = AppDatabase.getDatabase(app).questionDao()

    /**
     * Returns a Flow of questions belonging to the given quest.
     *
     * @param questId The ID of the quest whose questions should be observed.
     */
    fun questionsForQuest(questId: Int) =
        dao.getQuestionsForQuest(questId)

    /**
     * Inserts a new question into the database.
     *
     * @param text The question text.
     * @param questId The ID of the quest this question belongs to.
     * @param location Optional map location in "lat,lon" format.
     */
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

    // -----------------------------
    // UI STATE: Answers & Checkmarks
    // -----------------------------

    private val _answers = MutableStateFlow<Map<Int, String>>(emptyMap())
    /** Map of questionId → answer text entered by the user. */
    val answers: StateFlow<Map<Int, String>> = _answers

    private val _checked = MutableStateFlow<Set<Int>>(emptySet())
    /** Set of questionIds that the user has marked as checked/completed. */
    val checked: StateFlow<Set<Int>> = _checked

    /**
     * Updates the answer text for a specific question.
     *
     * @param questionId The ID of the question.
     * @param newAnswer The updated answer text.
     */
    fun updateAnswer(questionId: Int, newAnswer: String) {
        _answers.value = _answers.value.toMutableMap().apply { put(questionId, newAnswer) }
    }

    /**
     * Toggles whether a question is marked as checked.
     *
     * @param questionId The ID of the question.
     */
    fun toggleChecked(questionId: Int) {
        _checked.value = _checked.value.toMutableSet().apply {
            if (contains(questionId)) remove(questionId) else add(questionId)
        }
    }

    // -----------------------------
    // QUEST EXPORT (JSON + QR CODE)
    // -----------------------------

    /**
     * Generates a QR code bitmap from the given string.
     *
     * @param data The encoded QR payload.
     * @param size The width/height of the generated bitmap.
     */
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

    /**
     * Creates a JSON export containing the quest and all its questions.
     *
     * @param currentQuest The quest to export.
     * @return A JSON string representing the quest and its questions.
     */
    suspend fun generateQuestExportJson(currentQuest: Quest): String {
        val quest = currentQuest
        val questions = questionsForQuest(quest.id).first()

        val export = QuestWithQuestions(
            quest = currentQuest,
            questions = questions
        )

        return  Json.encodeToString(export)
    }

    /**
     * Generates a QR code bitmap containing a Base64‑encoded export of the quest.
     *
     * @param currentQuest The quest to export.
     * @return A QR code bitmap.
     */
    suspend fun generateQuestQrBitmap(currentQuest: Quest): Bitmap {
        val exportJson = generateQuestQrString(currentQuest)
        return generateQRCode(exportJson)
    }

    /**
     * Generates a QR export string in the format:
     * `qrexport://quest?data=<base64>`
     *
     * @param currentQuest The quest to export.
     * @return A QR payload string.
     */
    suspend fun generateQuestQrString(currentQuest: Quest): String {
        val json = generateQuestExportJson(currentQuest)
        val base64 = Base64.encodeToString(json.toByteArray(), Base64.NO_WRAP)
        return "qrexport://quest?data=$base64"
    }

    /**
     * Saves a QR code bitmap to the device gallery under `Pictures/QuestQR`.
     *
     * @param bitmap The QR code image to save.
     */
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