package com.gialen.sentrydemo

import android.app.Application
import io.sentry.Sentry
import io.sentry.android.AndroidSentryClientFactory

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        CrashHandler.instance!!.init(this)
        Sentry.init("https://e6d97a2b7dec4298bdb4847d2dd250c6:e6e6381df42f4359acd3d7ae030c6a07@sentry.io/1882099", AndroidSentryClientFactory(this))
    }
}