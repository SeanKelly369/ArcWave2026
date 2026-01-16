package com.example.sharpwave2026.utils

import android.util.Log

actual fun logd(tag: String, msg: String) {
    Log.d(tag, msg)
}

actual fun loge(tag: String, msg: String, t: Throwable?) {
    Log.e(tag, msg, t)
}