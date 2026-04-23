package com.kelly3d.arcwave2026.player

import android.content.Context

object AndroidContextProvider {
    lateinit var appContext: Context
        private set

    fun init(context: Context) {
        appContext = context.applicationContext
    }
}