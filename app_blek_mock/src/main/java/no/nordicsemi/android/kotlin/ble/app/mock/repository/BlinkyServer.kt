package no.nordicsemi.android.kotlin.ble.app.mock.repository

import android.annotation.SuppressLint
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import no.nordicsemi.android.common.core.DataByteArray
import no.nordicsemi.android.kotlin.ble.app.mock.screen.viewmodel.BlinkySpecifications
import no.nordicsemi.android.kotlin.ble.core.MockServerDevice
import no.nordicsemi.android.kotlin.ble.core.data.BleGattPermission
import no.nordicsemi.android.kotlin.ble.core.data.BleGattProperty
import no.nordicsemi.android.kotlin.ble.server.main.ServerBleGatt
import no.nordicsemi.android.kotlin.ble.server.main.service.ServerBleGattCharacteristicConfig
import no.nordicsemi.android.kotlin.ble.server.main.service.ServerBleGattServiceConfig
import no.nordicsemi.android.kotlin.ble.server.main.service.ServerBleGattServiceType
import javax.inject.Inject

@SuppressLint("MissingPermission")
class BlinkyServer @Inject constructor(
    private val scope: CoroutineScope
) {

    fun start(context: Context) = scope.launch {
        val ledCharacteristic = ServerBleGattCharacteristicConfig(
            BlinkySpecifications.UUID_LED_CHAR,
            listOf(BleGattProperty.PROPERTY_READ, BleGattProperty.PROPERTY_WRITE),
            listOf(BleGattPermission.PERMISSION_READ, BleGattPermission.PERMISSION_WRITE)
        )

        val buttonCharacteristic = ServerBleGattCharacteristicConfig(
            BlinkySpecifications.UUID_BUTTON_CHAR,
            listOf(BleGattProperty.PROPERTY_READ, BleGattProperty.PROPERTY_NOTIFY),
            listOf(BleGattPermission.PERMISSION_READ, BleGattPermission.PERMISSION_WRITE)
        )

        val serviceConfig = ServerBleGattServiceConfig(
            BlinkySpecifications.UUID_SERVICE_DEVICE,
            ServerBleGattServiceType.SERVICE_TYPE_PRIMARY,
            listOf(ledCharacteristic, buttonCharacteristic)
        )

        val server = ServerBleGatt.create(
            context = context,
            config = arrayOf(serviceConfig),
            mock = MockServerDevice()
        )

        launch {
            server.connections
                .mapNotNull { it.values.firstOrNull() }
                .collect {
                    val service = it.services.findService(BlinkySpecifications.UUID_SERVICE_DEVICE)!!
                    val ledCharacteristic = service.findCharacteristic(BlinkySpecifications.UUID_LED_CHAR)!!
                    val buttonCharacteristic = service.findCharacteristic(BlinkySpecifications.UUID_BUTTON_CHAR)!!

                    launch {
                        while (true) {
                            delay(1000)
                            buttonCharacteristic.setValue(newButtonValue())
                        }
                    }
                }
        }
    }

    private var isButtonPressed = false

    private fun newButtonValue(): DataByteArray {
        return if (isButtonPressed) {
            DataByteArray.from(0x01)
        } else {
            DataByteArray.from(0x00)
        }.also {
            isButtonPressed = !isButtonPressed
        }
    }
}
