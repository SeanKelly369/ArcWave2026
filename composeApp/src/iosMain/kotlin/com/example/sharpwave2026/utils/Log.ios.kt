package com.example.sharpwave2026.utils

import platform.Foundation.NSLog

actual fun logd(tag: String, msg: String) {
    NSLog("D/%@: %@", tag, msg)
}

actual fun loge(tag: String, msg: String, t: Throwable?) {
    val extra = t?.let { "\n$it\n${it.stackTraceToString()}" } ?: ""
    NSLog("E/%@: %@%@", tag, msg, extra)
}