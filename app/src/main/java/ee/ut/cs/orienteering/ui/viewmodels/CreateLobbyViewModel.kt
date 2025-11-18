package ee.ut.cs.orienteering.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ee.ut.cs.orienteering.data.AppDatabase
import ee.ut.cs.orienteering.data.Quest
import ee.ut.cs.orienteering.data.QuestDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CreateLobbyViewModel : AndroidViewModel {

    private val questDao: QuestDao
    constructor(application: Application) : super(application) {
        questDao = AppDatabase.getDatabase(application).questDao()
    }
    constructor(application: Application, questDao: QuestDao) : super(application) {
        this.questDao = questDao
    }

    fun createLobby(title: String, codeInput: String?, onCreated: (Int) -> Unit) {
        viewModelScope.launch {
            val finalCode = withContext(Dispatchers.IO) { ensureUniqueCode(codeInput) }
            val id = withContext(Dispatchers.IO) {
                questDao.insert(Quest(0, title, finalCode))
            }
            onCreated(id.toInt())
        }
    }

    suspend fun ensureUniqueCode(userInput: String?): String {
        var candidate = userInput?.trim().orEmpty()
        if (candidate.isBlank()) candidate = generateLobbyCode()
        while (questDao.getByCode(candidate) != null) {
            candidate = generateLobbyCode()
        }
        return candidate
    }

    private fun generateLobbyCode(): String {
        val chars = ('A'..'Z') + ('0'..'9')
        return (1..5).joinToString("") { chars.random().toString() }
    }
}


