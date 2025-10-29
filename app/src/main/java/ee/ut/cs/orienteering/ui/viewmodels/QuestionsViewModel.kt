package ee.ut.cs.orienteering.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ee.ut.cs.orienteering.data.AppDatabase
import ee.ut.cs.orienteering.data.Question
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class QuestionsViewModel(app: Application) : AndroidViewModel(app) {

    private val dao = AppDatabase.getDatabase(app).questionDao()

    fun questionsForQuest(questId: Int) =
        dao.getQuestionsForQuest(questId) // Flow<List<Question>>

    fun questionsForQuestState(questId: Int): StateFlow<List<Question>> =
        dao.getQuestionsForQuest(questId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addQuestion(text: String, questId: Int) {
        viewModelScope.launch {
            val newQuestion = Question(
                id = 0,
                questId = questId,
                questionText = text,
                answer = "",
                location = ""
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
}