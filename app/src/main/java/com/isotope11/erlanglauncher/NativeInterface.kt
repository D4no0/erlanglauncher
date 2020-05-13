package com.isotope11.erlanglauncher

object NativeInterface {
    init {
        System.loadLibrary("hello-jni")
    }

    external fun startTask(a: String?)
}