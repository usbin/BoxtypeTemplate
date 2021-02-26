/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.boxtypetemplate.device

import java.util.*

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
object SampleGattAttributes {
    private val attributes: HashMap<String, String> =
        HashMap<String, String>()
    const val SCBT_SERVICE_UUID = "0000aaa0-0000-1000-8000-00805f9b34fb"
    const val SCBT_CHARA_UUID = "00001234-0000-1000-8000-00805f9b34fb"
    fun lookup(uuid: String?, defaultName: String): String {
        val name = attributes[uuid]
        return name ?: defaultName
    }

    init {
        // Sample Services.
        attributes[SCBT_SERVICE_UUID] = "Senior Cane Service"
        attributes["00001800-0000-1000-8000-00805f9b34fb"] = "Generic access profile"
        attributes["00001800-0000-1000-8000-00805f9b34fb"] = "Generic Attribute"

        // Sample Characteristics.
        attributes["00001234-0000-1000-8000-00805f9b34fb"] = "Senior Cane Data"
        attributes["00001235-0000-1000-8000-00805f9b34fb"] = "Senior Cane Data2"
        attributes["00002a00-0000-1000-8000-00805f9b34fb"] = "Device Name"
        attributes["00002a01-0000-1000-8000-00805f9b34fb"] = "Appearance"
        attributes["00002a02-0000-1000-8000-00805f9b34fb"] = "Peripheral Privacy Flag"
        attributes["00002a03-0000-1000-8000-00805f9b34fb"] = "Reconnection Address"
        attributes["00002a04-0000-1000-8000-00805f9b34fb"] = "Peripheral Preferred Connection Parameters"
        attributes["00002a05-0000-1000-8000-00805f9b34fb"] = "Service Changed"

    }
}