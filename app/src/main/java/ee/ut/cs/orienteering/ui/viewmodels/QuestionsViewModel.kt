package ee.ut.cs.orienteering.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ee.ut.cs.orienteering.data.Question
import ee.ut.cs.orienteering.data.QuestionDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import ee.ut.cs.orienteering.data.AppDatabase


class QuestionsViewModel(app: Application) : AndroidViewModel(app) {
    private val dao: QuestionDao = AppDatabase.getDatabase(app).questionDao()
    val questions: StateFlow<List<Question>> =
        dao.getAll()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}