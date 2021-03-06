package com.supercilex.robotscouter.core.data

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigFetchException
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.supercilex.robotscouter.core.await
import com.supercilex.robotscouter.core.logFailures
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.util.concurrent.TimeUnit

// Mirrored in remote_config_defaults.xml
private const val KEY_MINIMUM_APP_VERSION = "minimum_app_version"
private const val KEY_UPDATE_MESSAGE = "update_required_message"
private const val KEY_SHOW_RATING_DIALOG = "show_rating_dialog"
private const val KEY_ENABLE_AUTO_SCOUT = "enable_auto_scout"

val minimumAppVersion
    get() = FirebaseRemoteConfig.getInstance().getDouble(KEY_MINIMUM_APP_VERSION).toInt()
val updateRequiredMessage: String
    get() = FirebaseRemoteConfig.getInstance().getString(KEY_UPDATE_MESSAGE)

val showRatingDialog
    get() = FirebaseRemoteConfig.getInstance().getBoolean(KEY_SHOW_RATING_DIALOG)

val enableAutoScout
    get() = FirebaseRemoteConfig.getInstance().getBoolean(KEY_ENABLE_AUTO_SCOUT)

fun initRemoteConfig() {
    FirebaseRemoteConfig.getInstance().apply {
        setDefaults(R.xml.remote_config_defaults)
        setConfigSettings(FirebaseRemoteConfigSettings.Builder()
                                  .setDeveloperModeEnabled(BuildConfig.DEBUG)
                                  .build())

        activateFetched()
        GlobalScope.async {
            try {
                fetch(if (info.configSettings.isDeveloperModeEnabled) {
                    0
                } else {
                    TimeUnit.HOURS.toSeconds(12)
                }).await()
            } catch (e: FirebaseRemoteConfigFetchException) {
                // Ignore throttling since it shouldn't happen on release builds and isn't really a public
                // API error.
            }
        }.logFailures()
    }
}
