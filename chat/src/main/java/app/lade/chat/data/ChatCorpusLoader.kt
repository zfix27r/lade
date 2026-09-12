package app.lade.chat.data

import android.content.Context
import app.lade.chat.domain.model.CorpusEntry
import app.lade.chat.domain.pipeline.Needle
import app.lade.chat.domain.pipeline.UserIntent
import app.lade.entry.domain.models.EntryKind
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatCorpusLoader @Inject constructor(
	@ApplicationContext private val context: Context,
) {
	fun loadAll(): List<CorpusEntry> = CORPUS_FILES.flatMap { (file, kind) ->
		loadFile(file, kind)
	}

	fun loadBySystemKey(systemKey: String): CorpusEntry? =
		loadAll().find { it.systemKey == systemKey }

	private fun loadFile(assetPath: String, kind: EntryKind): List<CorpusEntry> {
		val json = context.assets.open(assetPath).bufferedReader().use { it.readText() }
		val root = JSONObject(json)
		val entries = root.getJSONArray("entries")
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

	/** v1: string needles; v2: `{text, intent?}` objects. */
	private fun parseNeedles(arr: org.json.JSONArray): List<Needle> =
		buildList {
			for (j in 0 until arr.length()) {
				when (val item = arr.get(j)) {
					is String -> add(Needle(text = item.trim().lowercase()))
					is JSONObject -> {
						val text = item.getString("text").trim().lowercase()
						val intent = item.optString("intent", "")
							.takeIf { it.isNotBlank() }
							?.let { storage -> UserIntent.fromStorage(storage) }
						add(Needle(text = text, intent = intent))
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
