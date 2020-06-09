package com.qianxin.hiddentale

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        instrance = this
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var instrance: Context
    }

}