package leo.yahoonewsrsskotlinsample

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import java.util.*

class MyRemoteViewsService : RemoteViewsService() {

    companion object {
        private val TAG = MyRemoteViewsService::class.java.simpleName
    }

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        Log.d(TAG, "onGetViewFactory intent: $intent")
        return MyRemoteViewsFactory(applicationContext)
    }

    private class MyRemoteViewsFactory(context: Context) : RemoteViewsFactory {
        private val widgetItems: MutableList<WidgetItem>
        private val context: Context
        private val rvLoading: RemoteViews
        private val sharedPreferencesUtils: SharedPreferencesUtils

        companion object {
            private val TAG = MyRemoteViewsFactory::class.java.simpleName
        }

        init {
            Log.d(TAG, "MyRemoteViewsFactory")
            this.context = context
            rvLoading = RemoteViews(context.packageName, R.layout.widget_loading)
            sharedPreferencesUtils = SharedPreferencesUtils()
            widgetItems = ArrayList()
        }

        /**
         * ここで、メインスレッドの処理で20秒以上かかると、ANRになり強制終了するので注意.
         */
        override fun onCreate() {
            Log.d(TAG, "onCreate")
        }

        /**
         * AppWidgetManager#notifyAppWidgetViewDataChangedを呼び出すときにトリガーされます.
         * onCreateの後にも呼ばれる.
         */
        override fun onDataSetChanged() {
            Log.d(TAG, "onDataSetChanged")
            val articles = sharedPreferencesUtils.getArticles(context)
            Log.d(TAG, "articles: $articles")
            widgetItems.clear()
            for ((_, title, _, link, _, description) in articles) {
                widgetItems.add(WidgetItem(title.orEmpty(), description.orEmpty(), link.orEmpty()))
            }
        }

        override fun onDestroy() {
            Log.d(TAG, "onDestroy")
            widgetItems.clear()
        }

        override fun getCount(): Int {
            val count = widgetItems.size
            Log.d(TAG, "getCount: $count")
            return count
        }

        override fun getViewAt(position: Int): RemoteViews {
            Log.d(TAG, "getViewAt")
            val rv = RemoteViews(context.packageName, R.layout.widget_item)
            rv.setTextViewText(R.id.text_title, widgetItems[position].title)
            rv.setTextViewText(R.id.text_description, widgetItems[position].description)
            val fillInIntent = Intent(YahooNewsWidgetProvider.ACTION_ITEM_CLICKED)
            fillInIntent.putExtra(
                YahooNewsWidgetProvider.EXTRA_ITEM_LINK,
                widgetItems[position].link
            )
            rv.setOnClickFillInIntent(
                R.id.text_description,
                fillInIntent
            ) // rootのview IDを指定できない！ (@+id/item_container)
            return rv
        }

        /**
         * カスタムの読み込みビューを作成できる(たとえば、getViewAt() が遅い場合)
         */
        override fun getLoadingView(): RemoteViews {
            // return null; // ここで null を返すと、デフォルトの読み込みビューが表示される。
            return rvLoading
        }

        /**
         * すべてのリストアイテムに対して常に同じタイプのビューを返すのでその場合、 1 を返すでOK!
         */
        override fun getViewTypeCount(): Int {
            return 1
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun hasStableIds(): Boolean {
            return true
        }
    }
}