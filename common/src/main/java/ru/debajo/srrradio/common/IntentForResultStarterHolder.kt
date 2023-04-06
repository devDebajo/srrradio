package ru.debajo.srrradio.common

import java.lang.ref.WeakReference

class IntentForResultStarterHolder {

    private var ref: WeakReference<IntentForResultStarter>? = null

    val currentStarter: IntentForResultStarter?
        get() = ref?.get()

    fun attach(starter: IntentForResultStarter) {
        ref?.clear()
        ref = WeakReference(starter)
    }
}
