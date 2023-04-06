package ru.debajo.srrradio.common

import android.content.Intent
import android.content.IntentSender
import android.os.Bundle

interface IntentForResultStarter {
    fun startIntentForResult(
        intent: IntentSender,
        requestCode: Int,
        fillInIntent: Intent?,
        flagsMask: Int,
        flagsValues: Int,
        extraFlags: Int,
        options: Bundle?,
    )
}
