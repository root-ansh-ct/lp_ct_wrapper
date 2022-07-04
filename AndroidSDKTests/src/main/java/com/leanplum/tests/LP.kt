@file:Suppress("ObjectLiteralToLambda")

package com.leanplum.tests

import android.app.Application
import android.location.Location
import com.leanplum.*
import com.leanplum.LeanplumDeviceIdMode.ANDROID_ID
import com.leanplum.LeanplumLocationAccuracyType.CELL
import com.leanplum.annotations.Parser
import com.leanplum.callbacks.*
import com.leanplum.internal.LeanplumEventDataManager
import com.leanplum.internal.LeanplumInternal
import com.leanplum.internal.Log
import com.leanplum.internal.http.LeanplumHttpConnection
import com.leanplum.models.GeofenceEventType.ENTER_REGION
import com.leanplum.models.MessageArchiveData
import org.json.JSONObject
import com.leanplum.tests.utils.log

object LP {
    const val APP_ID = "app_4E1oVqnj8hvB2KmrAXRS5M6STH1fQGy3RvuhSQM73ew"
    const val ACCESS_KEY_DEV = "dev_ggCy2JYsAMpoZBVEnuDQwiaLfgfQsCckSazYWlSIgns"
    const val ACCESS_KEY_PROD = "prod_be2EraPzw8kcAjdwXogxsXaEu3aSXABQUt8WDiYrShU"
    const val NOTIF_CHANNEL = "lp_channel"
    fun initSDKInApplication(app: Application) {
        Leanplum.setLogLevel(Log.Level.DEBUG)
        Leanplum.setApplicationContext(app)
        Parser.parseVariables(app)
        LeanplumActivityHelper.enableLifecycleCallbacks(app)

        if (BuildConfig.DEBUG) {
            Leanplum.setAppIdForProductionMode(APP_ID, ACCESS_KEY_DEV) //Leanplum.setAppIdForDevelopmentMode(APP_ID, ACCESS_KEY_PROD)
        } else {
            Leanplum.setAppIdForProductionMode(APP_ID, ACCESS_KEY_PROD)
        }
        Leanplum.trackAllAppScreens();

        Leanplum.setLocale("")

        Leanplum.setDeviceIdMode(LeanplumDeviceIdMode.MD5_MAC_ADDRESS)
        Leanplum.start(app)

        //LPFCMService.registerChannelOnServer(APP_ID, ACCESS_KEY_PROD, NOTIF_CHANNEL,app)//todo not working for prod
        LPFCMService.registerChannelOnServer(APP_ID, ACCESS_KEY_DEV, NOTIF_CHANNEL, app)


    }

    fun allPublicMethods(app: Application) {

        //all public methods
        Leanplum.advanceTo("state")
        Leanplum.clearUserContent()
        Leanplum.countAggregator()
        Leanplum.defineAction("name", 0, ActionArgs().with("nae", "value"))
        Leanplum.disableLocationCollection()
        Leanplum.enableTestMode()


        Leanplum.featureFlagManager().isFeatureFlagEnabled("flagname").let { log("isFeatureFlagEnabled", it) }
        Leanplum.featureFlagManager().setEnabledFeatureFlags(setOf("flagname"))
        Leanplum.featureFlagManager().isFeatureFlagEnabled("flagname").let { log("isFeatureFlagEnabled", it) }

        Leanplum.forceContentUpdate()
        Leanplum.forceContentUpdate(object : ForceContentUpdateCallback {
            override fun onContentUpdated(success: Boolean) {

            }
        })
        Leanplum.forceContentUpdate(object : VariablesChangedCallback() {
            override fun variablesChanged() {

            }
        })

        Leanplum.getInbox()
        Leanplum.getContext()
        Leanplum.getDeviceId()
        Leanplum.getUserId()

        Leanplum.hasStarted()
        Leanplum.hasStartedAndRegisteredAsDeveloper()

        Leanplum.isLocationCollectionEnabled()
        Leanplum.isScreenTrackingEnabled()
        Leanplum.isPushDeliveryTrackingEnabled()
        Leanplum.isResourceSyncingEnabled()
        Leanplum.isTestModeEnabled()

        Leanplum.messageMetadata()

        Leanplum.messageBodyFromContext(ActionContext("name", mutableMapOf(), "messageId", "origMessageId", 0))

        Leanplum.objectForKeyPath()
        Leanplum.objectForKeyPathComponents(arrayOf())


        Leanplum.onAction("actionname", object : ActionCallback() {
            override fun onResponse(context: ActionContext?): Boolean {
                return true
            }
        })

        Leanplum.parseFeatureFlags(JSONObject())

        Leanplum.pauseState()
        Leanplum.parseFilenameToURLs(JSONObject())
        Leanplum.parseFeatureFlags(JSONObject())
        Leanplum.pathForResource("filename")
        Leanplum.parseSdkCounters(JSONObject())

        Leanplum.resumeState()
        Leanplum.removeMessageDisplayedHandler(object : MessageDisplayedCallback() {
            override fun messageDisplayed(messageArchiveData: MessageArchiveData?) {
            }
        })
        Leanplum.removeOnceVariablesChangedAndNoDownloadsPendingHandler(object : VariablesChangedCallback() {
            override fun variablesChanged() {

            }
        })
        Leanplum.removeStartResponseHandler(object : StartCallback() {
            override fun onResponse(success: Boolean) {

            }
        })
        Leanplum.removeVariablesChangedAndNoDownloadsPendingHandler(object : VariablesChangedCallback() {
            override fun variablesChanged() {

            }
        })
        Leanplum.removeVariablesChangedHandler(object : VariablesChangedCallback() {
            override fun variablesChanged() {

            }
        })

        Leanplum.securedVars()

        Leanplum.setApplicationContext(app)
        Leanplum.setAppIdForProductionMode("", "")
        Leanplum.setAppIdForDevelopmentMode("", "")
        Leanplum.setAppVersion("1.0")
        Leanplum.setApiConnectionSettings("", "", false)
        Leanplum.setDeviceId("")
        Leanplum.setDeviceIdMode(ANDROID_ID)//;ADVERTISING_ID;MD5_MAC_ADDRESS
        Leanplum.setDeviceLocation(Location(""))
        Leanplum.setDeviceLocation(Location(""), CELL)//;GPS;IP
        Leanplum.setEventsUploadInterval(EventsUploadInterval.AT_MOST_10_MINUTES)
        Leanplum.setFileUploadingEnabledInDevelopmentMode(true)
        Leanplum.setIsTestModeEnabled(true)
        Leanplum.setLocale("")
        Leanplum.setLogLevel(Log.Level.DEBUG)
        Leanplum.setNetworkTimeout(10000, 10000)
        Leanplum.setPushDeliveryTracking(true)
        Leanplum.setSocketConnectionSettings("", 8888)
        Leanplum.setTrafficSourceInfo(mapOf())
        Leanplum.setUserAttributes(mapOf())
        Leanplum.setUserAttributes("", mapOf<String, Any>())
        Leanplum.setUserId("")
        Leanplum.setVariantDebugInfoEnabled(true)



        LeanplumActivityHelper.deferMessagesForActivities()
        LeanplumActivityHelper.enableLifecycleCallbacks(app)
        LeanplumActivityHelper.getCurrentActivity()
        LeanplumActivityHelper.isActivityPaused()
        LeanplumActivityHelper.setCurrentActivity(app)
        LeanplumActivityHelper.queueActionUponActive(Runnable { })

        LeanplumApplication.getInstance()

        //LeanplumDeviceIdMode
        //LeanplumException
        LeanplumEventDataManager.sharedInstance()
        //LeanplumExceptionReporter().let {
        //    it.reportException(Throwable())
        //    it.onBeforeSend(RaygunMessage())
        //}
        LeanplumFirebaseServiceHandler().let {
            it.onCreate(app)
            //it.onMessageReceived(RemoteMessage(bundleOf()),app)
            it.onNewToken("", app)
        }
        //LeanplumGcmRegistrationJobService().let {
        //}
        //LeanplumHmsHandler().let {
        //    it.onCreate(app)
        //    //it.onMessageReceived(RemoteMessage(bundleOf()),app)
        //    it.onNewToken("",app)
        //}
        //LeanplumHmsMessageService().let {
        //    it.onCreate(app)
        //    it.onMessageReceived(RemoteMessage(bundleOf()),app)
        //    it.onNewToken("",app)
        //}

        val o1 = (object : LeanplumHttpConnection() {})
        o1.run {
            jsonResponse
            response
            responseCode
            url
            connect()
            disconnect()
        }

        LeanplumInbox.disableImagePrefetching()


        LeanplumInternal.addStartIssuedHandler(Runnable { })

        LeanplumInternal.connectDevelopmentServer()

        LeanplumInternal.enableAutomaticScreenTracking()


        LeanplumInternal.getActionHandlers()
        LeanplumInternal.getIsScreenTrackingEnabled()
        LeanplumInternal.getIsVariantDebugInfoEnabled()
        LeanplumInternal.getUserAttributeChanges()

        LeanplumInternal.hasCalledStart()
        LeanplumInternal.hasStarted()
        LeanplumInternal.hasStartedAndRegisteredAsDeveloper()
        LeanplumInternal.hasStartedInBackground()

        LeanplumInternal.isPaused()
        LeanplumInternal.issuedStart()
        LeanplumInternal.isStartSuccessful()

        LeanplumInternal.maybePerformActions("", "", 0, "", ActionContext.ContextualValues())
        LeanplumInternal.moveToForeground()
        LeanplumInternal.maybeThrowException(RuntimeException())

        LeanplumInternal.performTrackedAction("", "")
        LeanplumInternal.recordAttributeChanges()

        LeanplumInternal.setCalledStart(true)
        LeanplumInternal.setHasStarted(true)
        LeanplumInternal.setIsPaused(true)
        LeanplumInternal.setStartSuccessful(true)
        LeanplumInternal.setStartedInBackground(true)
        LeanplumInternal.setIsVariantDebugInfoEnabled(true)
        LeanplumInternal.setUserLocationAttribute(Location(""), CELL, LeanplumInternal.locationAttributeRequestsCallback { })

        LeanplumInternal.track("event", 1.2, "", mapOf<String, Any>(), mapOf())
        LeanplumInternal.trackGeofence(ENTER_REGION, 1.2, "", mapOf<String, Any>(), mapOf())//;EXIT_REGION
        LeanplumInternal.triggerAction(ActionContext("", mapOf(), ""))
        LeanplumInternal.triggerStartIssued()
        LeanplumInternal.validateAttributes(mapOf<String, Any>(), "", true)

        //LeanplumIntegration(app,"","",com.segment.analytics.integrations.Logger(),false).run {
        //    track()
        //    flush()
        //    identify()
        //    screen()
        //}

        //LeanplumInflater.from(app).inflate(android.R.layout.activity_list_item)

        //LeanplumJobStartReceiver
        //LeanplumLocalPushListenerService
        //LeanplumMiPushHandler.setApplication()
        //LeanplumManifestHelper
        //LeanplumMiPushMessageReceiver
        //LeanplumMessageMatchFilter
        //LeanplumPushFirebaseMessagingService
        //LeanplumPushInstanceIDService
        //LeanplumPushListenerService
        //LeanplumPushNotificationCustomizer
        //LeanplumPushReceiver
        //LeanplumPushService
        //LeanplumPushServiceGcm
        //LeanplumResources

    }
}

fun logUserId():String {
    Leanplum.getUserId().let {
        log("user id", it)
    }
    return Leanplum.getUserId()
}


fun loginSignUp(email: String,signup:Boolean) {
    if (signup) {
        Leanplum.setUserAttributes(mapOf("email" to email)) //todo :signup
    } else {
        Leanplum.setUserAttributes(mapOf("email" to email))//todo :login
    }
}

fun updateProfile(name: String, age: String, gender: String, type: String, subjects: List<String>, showUpdates: Boolean) {
    val msg = "updateProfile() called with: name = $name, age = $age, gender = $gender, type = $type, subjects = $subjects, showUpdates = $showUpdates"
    log(msg)
    Leanplum.setUserAttributes(
        mapOf(
            "name" to name,
            "age" to age,
            "gender" to gender,
            "category" to type,
            "selectedSubjects" to subjects,
            "enableMarketing" to showUpdates
        )

    )
}

fun lpTrack(name: String, params: Map<String, Any>? = null) {
    if (params == null) Leanplum.track(name)
    else Leanplum.track(name, params)
}

fun lpTrackPurchase(event: String, value: Double, currencyCode: String, params: Map<String, Any>) {
    Leanplum.trackPurchase(event, value, currencyCode, params)
}

