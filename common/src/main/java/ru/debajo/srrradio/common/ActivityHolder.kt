package ru.debajo.srrradio.common

import android.app.Activity
import java.lang.ref.WeakReference

class ActivityHolder {

    private var ref: WeakReference<Activity>? = null

    val currentActivity: Activity?
        get() = ref?.get()

    fun attach(starter: Activity) {
        ref?.clear()
        ref = WeakReference(starter)
    }

    fun detach(starter: Activity) {
        if (currentActivity === starter) {
            ref?.clear()
            ref = null
        }
    }
}
