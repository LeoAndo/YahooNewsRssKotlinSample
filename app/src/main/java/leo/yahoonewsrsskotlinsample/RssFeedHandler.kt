package leo.yahoonewsrsskotlinsample

import com.prof.rssparser.Article
import com.prof.rssparser.Parser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

internal class RssFeedHandler {
    companion object {
        private const val URL_STRING = "https://news.yahoo.co.jp/rss/topics/it.xml"
    }

    private val parser: Parser =
        Parser.Builder() // If you want to provide a custom charset (the default is utf-8):
            // .charset(Charset.forName("ISO-8859-7"))
            // .cacheExpirationMillis() and .context() not called because on Java side, caching is NOT supported
            .build()

    private val scope = CoroutineScope(Dispatchers.Default)

    inline fun fetchFeed(crossinline callback: (ApiResult<MutableList<Article>>) -> Unit) {
        scope.launch {
            try {
                val channel = parser.getChannel(URL_STRING)
                // Do something with your data
                callback(ApiResult.Success(channel.articles))
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle the exception
                callback(ApiResult.FetchFeedError)
            }
        }
    }

    fun clear() {
        scope.cancel()
    }
}