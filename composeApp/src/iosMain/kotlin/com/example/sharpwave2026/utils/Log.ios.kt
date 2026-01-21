package com.example.sharpwave2026.utils

import platform.Foundation.NSLog

actual fun logd(tag: String, msg: String) {
    NSLog("D/$tag: $msg")
}

actual fun loge(tag: String, msg: String, t: Throwable?) {
    if (t != null) {
        NSLog("E/$tag: $msg | ${t}")
    } else {
        NSLog("E/$tag: $msg")
    }
}