package com.leanplum.tests

import android.content.Context
import com.moczul.ok2curl.CurlInterceptor
import com.moczul.ok2curl.logger.Logger
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import com.leanplum.tests.utils.log
import com.leanplum.tests.utils.registerChannel
import java.io.IOException


class LPFCMService {




    companion object{

        fun getOkHttpClient(): OkHttpClient {
            val curl = CurlInterceptor(object :Logger{ override fun log(message: String) {
                com.leanplum.tests.utils.log(message)
            } })
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client: OkHttpClient = OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(curl)
                .build()
            return client
        }

        fun registerChannelOnServer(appId:String,accessKey:String,channelName:String ,context: Context){
            context.registerChannel(channelName)
            val mediaType = "application/json".toMediaTypeOrNull()
            val body = """{
                |"appId":"$appId",
                |"clientKey":"$accessKey",
                |"apiVersion":"1.0.6",
                |"importance":3,
                |"id":"$channelName",
                |"name":"$channelName"
                |}""".trimMargin()
            log("finalBody",body)
            val request = Request.Builder().url("https://api.leanplum.com/api?action=addAndroidNotificationChannel").post(body.toRequestBody(mediaType)).addHeader("Accept", "application/json").addHeader("Content-Type", "application/json").build()
            getOkHttpClient().newCall(request).enqueue(object :Callback{
                override fun onFailure(call: Call, e: IOException) {
                    kotlin.runCatching {
                        log("cause",e.cause)
                        log("localizedMessage",e.localizedMessage)
                        log("message",e.message)
                        log("stackTrace",e.stackTrace)
                        log("suppressed",e.suppressed)
                        e.printStackTrace()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    response.let {r->
                        r.message.let { log("messsage",it) }
                        r.body.let { log("body",it) }
                        r.body?.string().let { log("body string",it) }
                    }
                }
            })
        }

    }
}