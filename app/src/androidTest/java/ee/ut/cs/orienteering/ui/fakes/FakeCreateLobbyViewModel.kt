package ee.ut.cs.orienteering.ui.fakes

import android.app.Application
import ee.ut.cs.orienteering.data.FakeQuestDao
import ee.ut.cs.orienteering.ui.viewmodels.CreateLobbyViewModel

class FakeCreateLobbyViewModel(
    application: Application,
    questDao: FakeQuestDao
) : CreateLobbyViewModel(application, questDao) {

    var createLobbyCalled = false
    var createdTitle: String? = null
    var createdCode: String? = null
    var returnedId: Int = 123

    override fun createLobby(title: String, codeInput: String?, onCreated: (Int) -> Unit) {
        createLobbyCalled = true
        createdTitle = title
        createdCode = codeInput

        onCreated(returnedId)
    }
}
