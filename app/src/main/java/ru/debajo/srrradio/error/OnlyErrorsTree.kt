package ru.debajo.srrradio.error

import timber.log.Timber

class OnlyErrorsTree(private val sendingErrorsManager: SendingErrorsManager) : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (t != null) {
            sendingErrorsManager.send(t)
        }
    }
}
