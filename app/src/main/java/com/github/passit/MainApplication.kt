package com.github.passit

import android.app.Application
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.AmplifyConfiguration
import com.amplifyframework.storage.s3.AWSS3StoragePlugin
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Amplify.addPlugin(AWSCognitoAuthPlugin())
        Amplify.addPlugin(AWSApiPlugin())
        Amplify.addPlugin(AWSS3StoragePlugin())

        val config = AmplifyConfiguration.builder(applicationContext)
                .devMenuEnabled(false)
                .build()
        Amplify.configure(config, applicationContext)
    }
}