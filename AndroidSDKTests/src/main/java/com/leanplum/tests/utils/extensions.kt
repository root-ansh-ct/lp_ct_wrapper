package com.leanplum.tests.utils

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.RemoteMessage


fun Context?.showNotif(msg:RemoteMessage){
    msg.log()
    showNotif(title = msg.notification?.title?:"", body = msg.notification?.body)
}
fun Context?.showNotif(
    title: String = "title",
    body: String? = "body",
    @DrawableRes smallIcon: Int = android.R.drawable.ic_notification_clear_all,
    channelId: String = "default",
    channelInfo: String = "channel info",
    priorityFromBundle: Int? = null,
    notificationId: Int = 0,
    soundUri: Uri? = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
    autoCancel: Boolean = true,
    onClickPendingIntent: PendingIntent? = null,
) {
    this ?: return
    val manager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
    log("showNotif() called with: body = $body, smallIcon = $smallIcon, channelId = $channelId, channelInfo = $channelInfo, priorityFromBundle = $priorityFromBundle, title = $title, notificationId = $notificationId, soundUri = $soundUri, autoCancel = $autoCancel, onClickPendingIntent = $onClickPendingIntent")
    val priorityFinal = priorityFromBundle ?: NotificationManager.IMPORTANCE_DEFAULT

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) registerChannel(channelId,priorityFinal, channelInfo)
    val notificationBuilder = NotificationCompat.Builder(this, channelId)
        .setSmallIcon(smallIcon)
        .setContentTitle(title)
        .setAutoCancel(autoCancel)

    if (soundUri != null) notificationBuilder.setSound(soundUri)
    if (body != null) notificationBuilder.setContentText(body)
    if (onClickPendingIntent != null) notificationBuilder.setContentIntent(onClickPendingIntent)

    manager.notify(notificationId, notificationBuilder.build())
}

@TargetApi(Build.VERSION_CODES.N)
fun Context?.registerChannel(channelId:String,priority:Int = NotificationManager.IMPORTANCE_DEFAULT,channelInfo: String=channelId){
    this ?: return
    val manager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(channelId, channelInfo, priority)
        manager.createNotificationChannel(channel)
    }

}
