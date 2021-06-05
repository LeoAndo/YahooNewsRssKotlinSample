package leo.yahoonewsrsskotlinsample

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import java.util.*

/**
 * Implementation of App Widget functionality.
 */
class YahooNewsWidgetProvider : AppWidgetProvider() {
    private val rssFeedHandler = RssFeedHandler()
    private val sharedPreferencesUtils = SharedPreferencesUtils()


    private fun getRemoteViews(context: Context, appWidgetId: Int): RemoteViews {
        Log.d(TAG, "getWidgetRemoteViews appWidgetId: $appWidgetId")
        val intent = Intent(context, MyRemoteViewsService::class.java)
        val remoteViews = RemoteViews(context.packageName, R.layout.yahoo_news_widget_provider)
        remoteViews.setRemoteAdapter(R.id.view_flipper, intent)
        remoteViews.setEmptyView(R.id.view_flipper, R.id.empty_view) // コレクションにアイテムがない場合、空のビューを表示させる
        // ボタンのクリックイベントをハンドリングする.
        val refreshManualIntent = Intent(context, YahooNewsWidgetProvider::class.java)
        refreshManualIntent.action = ACTION_REFRESH_MANUAL
        refreshManualIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        remoteViews.setOnClickPendingIntent(
            R.id.button_refresh_manual,
            PendingIntent.getBroadcast(
                context,
                appWidgetId,
                refreshManualIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        )

        // セルのクリックイベントをハンドリングする場合は、setPendingIntentTemplateとsetOnClickFillInIntentの組み合わせで
        // PendingIntentを作成する.
        val cellItemIntent = Intent(context, YahooNewsWidgetProvider::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            appWidgetId,
            cellItemIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        remoteViews.setPendingIntentTemplate(R.id.view_flipper, pendingIntent)
        return remoteViews
    }

    /**
     * ユーザがウィジェットを追加した時も呼び出される。
     * ただし、設定アクティビティを宣言している場合は、
     * このメソッドはユーザーがウィジェットを追加したときには呼び出されず、以後の更新時に呼び出される。
     * 設定の完了時に最初の更新を実行するのは設定アクティビティの責任。
     */
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        Log.d(TAG, "onUpdate appWidgetIds: " + Arrays.toString(appWidgetIds))
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetId)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    /**
     * ウィジェットのインスタンスが初めて作成されたときに呼び出される。
     * たとえば、ユーザーがウィジェットのインスタンスを2つ追加した場合は、初回のみ呼び出される。
     * 1 回だけ必要なセットアップを行う必要がある場合は、このタイミングが適している。
     */
    override fun onEnabled(context: Context) {
        Log.d(TAG, "onEnabled")
        // Enter relevant functionality for when the first widget is created
    }

    /**
     * ウィジェットの最後のインスタンスがウィジェット ホストから削除されたときに呼び出される。
     * [YahooNewsWidgetProvider.onEnabled]で行った作業がある場合はこのタイミングで消去する
     */
    override fun onDisabled(context: Context) {
        Log.d(TAG, "onDisabled")
        // Enter relevant functionality for when the last widget is disabled
        sharedPreferencesUtils.clearArticles(context)
        rssFeedHandler.clear()
    }

    /**
     * ウィジェットがウィジェット ホスト(例えば、HOME画面)から削除されるたびに呼び出される。
     */
    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        Log.d(TAG, "onDeleted appWidgetIds: " + appWidgetIds.contentToString())
    }

    /**
     * ウィジェットが最初に配置されたとき、
     * および以後ウィジェットのサイズが変更されたときに呼び出される
     */
    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        Log.d(TAG, "onAppWidgetOptionsChanged appWidgetId: $appWidgetId newOptions: $newOptions")
    }

    /**
     * 通常はこのメソッドを実装する必要はない。
     *
     *
     * android.appwidget.action.APPWIDGET_ENABLED インテントが飛んできて、
     * onEnabled()を内部で呼び出している
     *
     *
     * android.appwidget.action.APPWIDGET_UPDATE インテントが飛んできて、
     * onUpdate()を内部で呼び出している
     *
     *
     * android.appwidget.action.APPWIDGET_DELETED インテントが飛んできて、
     * onDeleted()を内部で呼び出している
     *
     *
     * android.appwidget.action.APPWIDGET_DISABLED インテントが飛んできて、
     * onDisabled()を内部で呼び出している
     */
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive intent: $intent")
        super.onReceive(context, intent)
        val action = intent.action
        if (ACTION_ITEM_CLICKED == action) {
            val link = intent.getStringExtra(EXTRA_ITEM_LINK)
            val newIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                context.startActivity(newIntent)
            } catch (ex: ActivityNotFoundException) {
                ex.printStackTrace()
            }
        } else if (ACTION_REFRESH_MANUAL == action) {
            val appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
            updateAppWidget(context, appWidgetId)
        }
    }

    private fun updateAppWidget(context: Context, appWidgetId: Int) {
        AppWidgetManager.getInstance(context)
            .updateAppWidget(appWidgetId, getRemoteViews(context, appWidgetId))
        fetchFeed(context, appWidgetId)
    }

    private fun fetchFeed(context: Context, appWidgetId: Int) {
        Log.d(TAG, "fetchFeed appWidgetId: $appWidgetId")
        rssFeedHandler.fetchFeed { result ->
            when (result) {
                is ApiResult.Success -> {
                    val articles = result.value
                    Log.d(TAG, "onTaskCompleted articles: $articles")
                    sharedPreferencesUtils.setArticles(context, articles)
                    AppWidgetManager.getInstance(context)
                        .notifyAppWidgetViewDataChanged(appWidgetId, R.id.view_flipper)
                }
                is ApiResult.FetchFeedError -> {
                    Log.e(TAG, "FetchFeedError!!!")
                }
            }
        }
    }

    companion object {
        private val TAG = YahooNewsWidgetProvider::class.java.simpleName
        const val EXTRA_ITEM_LINK = BuildConfig.APPLICATION_ID + "EXTRA_ITEM_LINK"
        const val ACTION_ITEM_CLICKED = BuildConfig.APPLICATION_ID + "ACTION_ITEM_CLICKED"
        private const val ACTION_REFRESH_MANUAL =
            BuildConfig.APPLICATION_ID + "ACTION_REFRESH_MANUAL"
    }
}