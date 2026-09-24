package app.lade.chat

import org.json.JSONArray

object PhraseLoader {
    fun load(json: String): List<PhraseCase> {
        val arr = JSONArray(json)
        return buildList {
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                add(
                    PhraseCase(
                        input = obj.getString("input"),
                        result = obj.getString("result"),
                        kind = obj.optString("kind").takeIf { it.isNotBlank() },
                        title = obj.optString("title").takeIf { it.isNotBlank() },
                        rrule = obj.optString("rrule").takeIf { it.isNotBlank() },
                        dateFrom = obj.optString("dateFrom").takeIf { it.isNotBlank() },
                        dateTo = obj.optString("dateTo").takeIf { it.isNotBlank() },
                        timeFrom = obj.optString("timeFrom").takeIf { it.isNotBlank() },
                        timeEnd = obj.optString("timeEnd").takeIf { it.isNotBlank() },
                        goals = obj.optString("goals").takeIf { it.isNotBlank() },
                        remaining = obj.optString("remaining").takeIf { it.isNotBlank() },
                    ),
                )
            }
        }
    }
}