package leo.yahoonewsrsskotlinsample

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import leo.yahoonewsrsskotlinsample.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.buttonRequestPinWidget.setOnClickListener { _ -> requestWidget() }
    }

    private fun requestWidget() {
        Log.d(TAG, "requestWidget-START")
        val appWidgetManager = AppWidgetManager.getInstance(this)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Toast.makeText(this, R.string.toast_message_andorid_o_above, Toast.LENGTH_LONG).show()
            return
        }
        if (!appWidgetManager.isRequestPinAppWidgetSupported) {
            Toast.makeText(
                this,
                getString(R.string.error_widget_pinning_not_supported_by_launcher),
                Toast.LENGTH_LONG
            ).show()
            return
        }
        val successCallback: PendingIntent = WidgetPinnedReceiver.getPendingIntent(this)
        val remoteViews = getPreviewRemoteViews(this)
        val bundle = Bundle()
        bundle.putParcelable(AppWidgetManager.EXTRA_APPWIDGET_PREVIEW, remoteViews)
        val provider = ComponentName(this, YahooNewsWidgetProvider::class.java)
        try {
            Log.d(TAG, "call appWidgetManager.requestPinAppWidget")
            appWidgetManager.requestPinAppWidget(provider, bundle, successCallback)
        } catch (ex: IllegalStateException) {
            ex.printStackTrace()
            Toast.makeText(
                this,
                "The caller doesn't have a foreground activity or a foreground.",
                Toast.LENGTH_LONG
            ).show()
        }
        Log.d(TAG, "requestWidget-END")
    }

    private fun getPreviewRemoteViews(context: Context): RemoteViews {
        val remoteViews = RemoteViews(context.packageName, R.layout.yahoo_news_widget_provider)
        remoteViews.setTextViewText(R.id.text_title, getString(R.string.dummy_text_title))
        remoteViews.setTextViewText(
            R.id.text_description,
            getString(R.string.dummy_text_description)
        )
        return remoteViews
    }
}