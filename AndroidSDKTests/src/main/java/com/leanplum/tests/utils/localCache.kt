package com.leanplum.tests.utils

import android.content.Context
import android.content.SharedPreferences

val Context.mySharedPref: SharedPreferences?
    get() = this.getSharedPreferences("my_word_cache", Context.MODE_PRIVATE)

fun Context.saveDataToCachePref(key: String, value: Any) {
    val pref = mySharedPref
    log("pref is ", pref)
    log("key = $key ,val = $value")
    pref?.edit()?.apply {
        when (value) {
            is Boolean -> putBoolean(key, value)
            is String -> putString(key, value)
            is Int -> putInt(key, value)
            is Float -> putFloat(key, value)
            is Long -> putLong(key, value)
            else -> putString(key, value.toString())
        }
        commit()
    }

}

// why the tmp ? because : https://www.javatpoint.com/method-overloading-in-java#:~:text=Q)%20Why%20Method%20Overloading%20is%20not%20possible%20by%20changing%20the%20return%20type%20of%20method%20only%3F

fun Context.getCachePrefData(key: String, tmp:Boolean = false): Boolean {
    val pref = mySharedPref
    log("pref is ", pref)
    if (pref == null) error("pref is null")

    val value = pref.getBoolean(key, false)
    log("returning value for key:$key as", value)
    return value
}

fun Context.getCachePrefData(key: String, tmp:String = ""): String {
    val pref = mySharedPref
    log("pref is ", pref)
    if (pref == null) error("pref is null")

    val value = pref.getString(key, "")?:""
    log("returning value for key:$key as", value)
    return value
}

fun Context.getCachePrefData(key: String ,tmp:Float = 0f): Float {
    val pref = mySharedPref
    log("pref is ", pref)
    if (pref == null) error("pref is null")

    val value = pref.getFloat(key, 0f)
    log("returning value for key:$key as", value)
    return value
}

fun Context.getCachePrefData(key: String,tmp:Int = 0): Int {
    val pref = mySharedPref
    log("pref is ", pref)
    if (pref == null) error("pref is null")

    val value = pref.getInt(key, 0)
    log("returning value for key:$key as", value)
    return value
}

fun Context.getCachePrefData(key: String, tmp:Long=0L): Long {
    val pref = mySharedPref
    log("pref is ", pref)
    if (pref == null) error("pref is null")

    val value = pref.getLong(key, 0L)
    log("returning value for key:$key as", value)
    return value
}

