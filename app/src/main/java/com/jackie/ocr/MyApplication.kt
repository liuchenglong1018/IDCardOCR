package com.jackie.ocr

import android.app.Application

val myContext: Application by lazy { MyApplication.instanceApp }

class MyApplication: Application() {

    companion object {
        lateinit var instanceApp: MyApplication
    }

    override fun onCreate() {
        super.onCreate()
        instanceApp = this
    }

}