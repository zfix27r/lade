package app.lade.chat.internal.data

import android.content.Context
import app.lade.entrykind.EntryKind
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ChatMatchCatalog @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val cached: List<CorpusEntry> by lazy { loadAll() }

    fun activeEntries(): List<CorpusEntry> = cached

    private fun loadAll(): List<CorpusEntry> = CORPUS_FILES.flatMap { (file, kind) ->
        loadFile(file, kind)
    }

    private fun loadFile(assetPath: String, kind: EntryKind): List<CorpusEntry> {
        val json = context.assets.open(assetPath).bufferedReader().use { it.readText() }
        val entries = JSONObject(json).getJSONArray("entries")
        return buildList {
            for (i in 0 until entries.length()) {
                val obj = entries.getJSONObject(i)
                add(
                    CorpusEntry(
                        systemKey = obj.getString("systemKey"),
                        kind = kind,
                        title = obj.getString("title"),
                        needles = parseNeedles(obj.getJSONArray("needles")),
                    ),
                )
            }
        }
    }

    private fun parseNeedles(arr: JSONArray): List<CorpusNeedle> = buildList {
        for (j in 0 until arr.length()) {
            when (val item = arr.get(j)) {
                is String -> add(CorpusNeedle(text = item.trim().lowercase(), intent = null))
                is JSONObject -> {
                    val text = item.getString("text").trim().lowercase()
                    val intent = item.optString("intent").takeIf { it.isNotBlank() }
                    add(CorpusNeedle(text = text, intent = intent))
                }
            }
        }
    }

    companion object {
        private val CORPUS_FILES = listOf(
            "chat/corpus-habit.json" to EntryKind.HABIT,
            "chat/corpus-event.json" to EntryKind.EVENT,
            "chat/corpus-task.json" to EntryKind.TASK,
            "chat/corpus-schedule.json" to EntryKind.SCHEDULE,
        )
    }
}