package dev.aplominski.openbatterytrack

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class LatestRelease(val tag: String, val url: String)

object GithubUpdates {
    private const val API = "https://api.github.com/repos/aplominski/openbatterytrack/releases/latest"

    fun buildNumber(tag: String): Int? =
        Regex("""build-(\d+)""").find(tag)?.groupValues?.get(1)?.toIntOrNull()

    suspend fun fetchLatest(): LatestRelease? = withContext(Dispatchers.IO) {
        try {
            val conn = (URL(API).openConnection() as HttpURLConnection).apply {
                connectTimeout = 8000
                readTimeout = 8000
                setRequestProperty("Accept", "application/vnd.github+json")
            }
            if (conn.responseCode != 200) return@withContext null
            val json = JSONObject(conn.inputStream.bufferedReader().readText())
            LatestRelease(json.getString("tag_name"), json.getString("html_url"))
        } catch (_: Exception) {
            null
        }
    }
}
