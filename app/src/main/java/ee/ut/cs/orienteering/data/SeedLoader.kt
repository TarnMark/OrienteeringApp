package ee.ut.cs.orienteering.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object SeedLoader {
    private val gson = Gson()

    private fun readAsset(context: Context, name: String): String =
        context.assets.open(name).bufferedReader().use { it.readText() }

    fun loadQuestions(context: Context): List<QuestionDto> {
        val json = readAsset(context, "questions.json")
        val type = object : TypeToken<List<QuestionDto>>() {}.type
        return gson.fromJson(json, type)
    }

    fun loadCheckpoints(context: Context): List<CheckpointDto> {
        val json = readAsset(context, "checkpoints.json")
        val type = object : TypeToken<List<CheckpointDto>>() {}.type
        return gson.fromJson(json, type)
    }
}
