package ru.debajo.srrradio.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import ru.debajo.srrradio.common.utils.hasPermission
import ru.debajo.srrradio.common.utils.inject
import ru.debajo.srrradio.media.MediaController

@SuppressLint("MissingPermission")
class BluetoothReceiver : BroadcastReceiver() {

    private val bluetoothAutoplayPreference: BluetoothAutoplayPreference by inject()
    private val mediaController: MediaController by inject()

    override fun onReceive(context: Context, intent: Intent) {
        val connected = when (intent.action) {
            BluetoothDevice.ACTION_ACL_CONNECTED -> true
            BluetoothDevice.ACTION_ACL_DISCONNECTED -> false
            else -> return
        }
        if (!context.hasBluetoothConnectPermission) {
            return
        }
        if (!bluetoothAutoplayPreference.get()) {
            return
        }

        val bluetoothDevice = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE) ?: return
        if (connected && bluetoothDevice.isAudioDevice && !mediaController.playing) {
            mediaController.play()
            return
        }

        if (!connected && bluetoothDevice.isAudioDevice && mediaController.playing) {
            mediaController.pause()
        }
    }

    private val BluetoothDevice.isAudioDevice: Boolean
        get() {
            return bluetoothClass.deviceClass in setOf(
                BluetoothClass.Device.AUDIO_VIDEO_CAR_AUDIO,
                BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES,
                BluetoothClass.Device.AUDIO_VIDEO_WEARABLE_HEADSET,
                BluetoothClass.Device.AUDIO_VIDEO_PORTABLE_AUDIO,
                BluetoothClass.Device.AUDIO_VIDEO_HIFI_AUDIO,
            )
        }
}

val Context.hasBluetoothConnectPermission: Boolean
    get() {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            hasPermission(Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            true
        }
    }
