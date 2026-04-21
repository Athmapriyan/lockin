package com.lockin.app.data

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.lockin.app.model.TaskItem
import java.util.Calendar

const val CHANNEL_ID = "lockin_tasks"

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title       = intent.getStringExtra("title") ?: "Reminder"
        val body        = intent.getStringExtra("body")  ?: ""
        val notifId     = intent.getIntExtra("notifId", 0)
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(CHANNEL_ID, "Tasks", NotificationManager.IMPORTANCE_HIGH)
        nm.createNotificationChannel(channel)
        val notif = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        nm.notify(notifId, notif)
    }
}

fun scheduleNotification(
    context: Context,
    task: TaskItem,
    hideDetails: Boolean,
    privacyModeActive: Boolean
) {
    val isPrivate = task.isPrivate && (hideDetails || privacyModeActive)
    val title = if (isPrivate) "\uD83D\uDD12 Private Task" else task.title
    val body  = if (isPrivate) "You have a protected reminder." else "Task due: ${task.title}"

    val intent = Intent(context, NotificationReceiver::class.java).apply {
        putExtra("title",   title)
        putExtra("body",    body)
        putExtra("notifId", task.id.hashCode())
    }
    val pi = PendingIntent.getBroadcast(
        context,
        task.id.hashCode(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    runCatching {
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, task.dateMillis, pi)
    }
}

fun cancelNotification(context: Context, taskId: String) {
    val intent = Intent(context, NotificationReceiver::class.java)
    val pi = PendingIntent.getBroadcast(
        context,
        taskId.hashCode(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).cancel(pi)
}
