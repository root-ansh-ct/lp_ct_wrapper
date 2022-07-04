package com.leanplum.tests.utils

import android.os.Bundle
import android.util.Log
import com.google.firebase.messaging.RemoteMessage

fun Map<String, Any>?.toBundle(): Bundle {
    this?:return  Bundle()
    val extras = Bundle()
    for ((key, value) in this) {
        when(value){
            is String -> extras.putString(key, value)
            is Float -> extras.putFloat(key, value)
            is Char -> extras.putChar(key, value)
            is Int -> extras.putInt(key, value)
            else -> extras.putString(key,value.toString())
        }
    }
    return extras

}
fun Bundle?.logBundle( initialText:String = "bundle") {
    this ?: kotlin.run {
        log(initialText,   "{}")
        return
    }
    log("$initialText \t{")
    this.keySet().forEach {
        log("\t\t $it : ${get(it).toString()}")
    }
    log("\t{")
}

fun RemoteMessage.log(){
    this.let {
        log(value = it.collapseKey, key = "it.collapseKey")
        log(value = it.data, key = " it.data")
        log(value = it.from, key = "it.from")
        log(value = it.messageId, key = "it.messageId")
        log(value = it.messageType, key = "it.messageType")
        log(value = it.originalPriority, key = "it.originalPriority")
        log(value = it.priority, key = "")
        log(value = it.rawData?.toList(), key = "it.rawData?.toList()")
        log(value = it.senderId, key = "it.senderId")
        log(value = it.sentTime, key = "it.sentTime")
        log(value = it.to, key = "it.to")
        log(value = it.ttl, key = "it.tt")

        log(value = it.notification?.body, key = "notification?.body")
        log(value = it.notification?.bodyLocalizationArgs?.toList(), key = ".notification?.bodyLocalizationArgs")
        log(value = it.notification?.bodyLocalizationKey, key = "notification?.bodyLocalizationKey")
        log(value = it.notification?.channelId, key = "notification?.channelId")
        log(value = it.notification?.clickAction, key = "notification?.clickAction")
        log(value = it.notification?.color, key = "notification?.color")
        log(value = it.notification?.defaultLightSettings, key = "notification?.defaultLightSettings")
        log(value = it.notification?.defaultVibrateSettings, key = "notification?.defaultVibrateSettings")
        log(value = it.notification?.eventTime, key = "notification?.eventTime")
        log(value = it.notification?.defaultSound, key = "notification?.defaultSound")
        log(value = it.notification?.icon, key = "notification?.icon")
        log(value = it.notification?.imageUrl, key = ".notification?.imageUrl")
        log(value = it.notification?.lightSettings?.toList(), key = "notification?.lightSettings")
        log(value = it.notification?.link, key = "notification?.link")
        log(value = it.notification?.localOnly, key = "notification?.localOnly")
        log(value = it.notification?.notificationCount, key = "notification?.notificationCoun")
        log(value = it.notification?.notificationPriority, key = "notification?.notificationPriority")
        log(value = it.notification?.sound, key = "notification?.sound")
        log(value = it.notification?.sticky, key = "notification?.sticky")
        log(value = it.notification?.tag, key = "notification?.tag")
        log(value = it.notification?.ticker, key = "notification?.ticker")
        log(value = it.notification?.title, key = "notification?.title")
        log(value = it.notification?.titleLocalizationArgs?.toList(), key = "notification?.titleLocalizationArgs")
        log(value = it.notification?.titleLocalizationKey, key = "notification?.titleLocalizationKey")
        log(value = it.notification?.vibrateTimings, key = "notification?.vibrateTimings")
        log(value = it.notification?.visibility, key = "notification?.visibility")

    }
}


fun log(key: String, value: Any? = null, tag: String = "CUSTOM_LOGS") {
    if(value == null) Log.e(tag, key )
    else Log.e(tag, "$key:$value ")
}