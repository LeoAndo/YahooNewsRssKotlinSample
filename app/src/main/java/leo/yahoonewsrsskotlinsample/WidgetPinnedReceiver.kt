package leo.yahoonewsrsskotlinsample

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast


class WidgetPinnedReceiver : BroadcastReceiver() {
    companion object {
        private const val BROADCAST_ID = 12345
        private val TAG = WidgetPinnedReceiver::class.java.simpleName

        fun getPendingIntent(context: Context): PendingIntent {
            val callbackIntent = Intent(context, WidgetPinnedReceiver::class.java)
            val bundle = Bundle()
            callbackIntent.putExtras(bundle)
            return PendingIntent.getBroadcast(
                context, BROADCAST_ID, callbackIntent, PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val appwidgetId = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )
        Log.d(TAG, "onReceive appwidgetId: $appwidgetId")
        Toast.makeText(
            context, context.getString(R.string.created_widget_message, appwidgetId),
            Toast.LENGTH_SHORT
        ).show()
    }
}