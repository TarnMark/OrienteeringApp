package ee.ut.cs.orienteering.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ee.ut.cs.orienteering.data.AppDatabase
import ee.ut.cs.orienteering.data.Question
import ee.ut.cs.orienteering.data.QuestionDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class QuestionsViewModel(app: Application) : AndroidViewModel(app) {
    private val dao: QuestionDao = AppDatabase.getDatabase(app).questionDao()
    val questions: StateFlow<List<Question>> =
        dao.getAll()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addQuestion(text: String) {
        viewModelScope.launch {
            val newQuestion = Question(
                0, 1, text, "", ""
            )
            dao.insert(newQuestion)
        }
    }

    // Store UI state: answer text and check state by question id
    private val _answers = MutableStateFlow<Map<Int, String>>(emptyMap())
    val answers: StateFlow<Map<Int, String>> = _answers

    private val _checked = MutableStateFlow<Set<Int>>(emptySet())
    val checked: StateFlow<Set<Int>> = _checked

    fun updateAnswer(questionId: Int, newAnswer: String) {
        _answers.value = _answers.value.toMutableMap().apply {
            put(questionId, newAnswer)
        }
    }

    fun toggleChecked(questionId: Int) {
        _checked.value = _checked.value.toMutableSet().apply {
            if (contains(questionId)) remove(questionId) else add(questionId)
        }
    }
}