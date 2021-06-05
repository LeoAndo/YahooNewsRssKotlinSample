package leo.yahoonewsrsskotlinsample

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.prof.rssparser.Article

internal class SharedPreferencesUtils {
    @Synchronized
    fun setArticles(context: Context, articles: List<Article?>?) {
        val prefs = getSharedPreferences(context)
        val gson = Gson()
        prefs.edit().putString(ARTICLES_PREF_KEY, gson.toJson(articles)).apply()
    }

    @Synchronized
    fun getArticles(context: Context): List<Article> {
        val prefs = getSharedPreferences(context)
        val gson = Gson()
        val json = prefs.getString(ARTICLES_PREF_KEY, LIST_PREFS_DEF_VALUE)
        return if (LIST_PREFS_DEF_VALUE == json) {
            emptyList()
        } else {
            gson.fromJson(json, object : TypeToken<List<Article>>() {}.type)
        }
    }

    @Synchronized
    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("pref", Context.MODE_PRIVATE)
    }

    @Synchronized
    fun clearArticles(context: Context) {
        val prefs = getSharedPreferences(context)
        prefs.edit().remove(ARTICLES_PREF_KEY).apply()
    }

    companion object {
        const val ARTICLES_PREF_KEY = "ARTICLES_PREF_KEY"
        private const val LIST_PREFS_DEF_VALUE = "[]"
    }
}