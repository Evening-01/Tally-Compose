package com.evening.tally.ext

import android.widget.Toast
import com.evening.tally.App

private var toast: Toast? = null

fun showToast(message: String?, duration: Int = Toast.LENGTH_SHORT) {
    toast?.cancel()
    toast = Toast.makeText(App.CONTEXT, message, duration)
    toast?.show()
}

fun showToastLong(message: String?) {
    showToast(message, Toast.LENGTH_LONG)
}