package io.github.quoteme.corsanotest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.corsano.sdk.CorsanoSdk
import com.corsano.sdk.ble.BleSdk
import com.corsano.sdk.ble.Bluetooth
import com.corsano.sdk.ble.BluetoothState
import com.corsano.sdk.ble.scan.BleScanEvent
import com.corsano.sdk.ble.scan.BleScanRecord
import com.corsano.sdk.ble.util.BluetoothUtil
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        CorsanoSdk.initialize(this)

        btnVerbinden.setOnClickListener {
            tv_log.text = "button clicked"
            if (!BluetoothUtil.isBluetoothSupported()) {
                // show Bluetooth not supported screen
                tv_log.text = "Bluetooth is not supported\n"
                return@setOnClickListener
            }

            if (!BluetoothUtil.isBluetoothEnabled()) {
                tv_log.text = "Bluetooth is not enabled\n"
                // request to turn the Bleutooth on
                val intent = BluetoothUtil.getEnableBluetoothIntent()
                // TODO: hier muss irgendwie die Bluetooth-Einschaltung angefordert werden
//                fragment.startActivity(intent)
            }//            CorsanoSdk.connect()
            tv_log.text = "Verbindungsaufforderung gesendet\n"

            // Listen to Bluetooth state updates
            val listener = object : Bluetooth.StateListener {
                override fun onBluetoothStateChanged(state: BluetoothState) {
                    tv_bluetoothlog.text = "Bluetooth state changed: $state\n"
                    // handle state change here
                    // zum Beispiel: "Turned_ON" oder "Turned_OFF"
                }
            }

            val lelBluetooth  = BleSdk.getInstance().getBluetooth()
            val lalScanner = BleSdk.getInstance().getScanner()

            // get BleSdk instance
            val bleSdk = BleSdk.getInstance()

            // get BleScanner instance
            val scanner = bleSdk.getScanner()

            bleSdk.getBluetooth().addStateListener(listener)
            tv_bluetoothlog.text = "Bluetooth state listener added\n"

            val permissions = scanner.getMissingPermissions()
            if (permissions.isNotEmpty()) {
                println("missing permissions: $permissions")
                // TODO: hier müssen irgendwie noch die Permissions angefordert werden
//                fragment.requestPermissions(permissions)
            } else {
                var foundDevices = emptyList<BleScanRecord>()
                scanner.startScan { event ->
                    tv_bluetoothlog.text = "scanner found something!"
                    when (event) {
                        is BleScanEvent.BleScanStarted -> {
                            tv_bluetoothlog.text = "scanner started"
                        }
                        is BleScanEvent.BleScanResults -> {
                            foundDevices = (event.results + foundDevices).distinctBy { it.address }
                            tv_bluetoothlog.text = "device was found!\n${foundDevices.size}"
                            // handle scan results here
                        }
                        is BleScanEvent.BleScanStopped -> {
                            tv_bluetoothlog.text = "scanner stopped.\n ${foundDevices.size} devices found"
                        }
                    }
                }
//                scanner.stopScan()
            }
        }
    }
}