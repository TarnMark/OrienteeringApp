package ee.ut.cs.orienteering.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Simple in-memory implementation of QuestDao used ONLY in unit tests.
 */
class FakeQuestDao(
    val returnNull: Boolean = false
) : QuestDao {

    // In-memory storage for Quest entities.
    private val quests = mutableListOf<Quest>()

    /**
     * Inserts a Quest into the in-memory list.
     *
     * If id == 0, we simulate auto-increment by assigning the next available id.
     * Returns the id as Long, just like a real Room insert() call.
     */
    override suspend fun insert(quest: Quest): Long {
        val newId = if (quest.id == 0) {
            (quests.maxOfOrNull { it.id } ?: 0) + 1
        } else {
            quest.id
        }
        val stored = quest.copy(id = newId)
        quests.removeAll { it.id == newId }
        quests.add(stored)
        return newId.toLong()
    }

    override suspend fun update(quest: Quest) {
        // Not needed for our test scenario.
    }

    override suspend fun delete(quest: Quest) {
        quests.removeIf { it.id == quest.id }
    }
    override suspend fun deleteAll() {
        quests.clear()
    }
    override suspend fun count(): Int = quests.size
    override fun getQuestById(id: Int): Flow<Quest?> =
        flowOf(quests.firstOrNull { it.id == id })
    override fun getQuestWithQuestions(questId: Int): Flow<QuestWithQuestions?> =
        flowOf(null) // Not needed for the current tests.

    override suspend fun getByCode(code: String): Quest? =
        quests.firstOrNull { it.code == code }
}
