/*
 * MIT License
 *
 * Copyright (c) 2019 Torin Turner (FRC 3006 RedRockRobotics)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.team3006.RedRock.bluetooth.util;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Used to manually select a Bluetooth device
 * Source: https://gist.github.com/timnew/7908603
 * TODO cite ALL my gists/etc
 */
public class BluetoothDeviceManager implements BluetoothDevicePicker {

    private Context context;

    public BluetoothDeviceManager(Context context) {
        this.context = context;
    }

    public void pickDevice(BluetoothDevicePickResultHandler handler) {
        context.registerReceiver(new BluetoothDeviceManagerReceiver(handler), new IntentFilter(ACTION_DEVICE_SELECTED));

        context.startActivity(new Intent(ACTION_LAUNCH)
                .putExtra(EXTRA_NEED_AUTH, false)
                .putExtra(EXTRA_FILTER_TYPE, FILTER_TYPE_TRANSFER) //TODO this should be fine? right?
                .setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS));
    }

    public interface BluetoothDevicePickResultHandler {
        void onDevicePicked(BluetoothDevice device);
    }

    private static class BluetoothDeviceManagerReceiver extends BroadcastReceiver {

        private final BluetoothDevicePickResultHandler handler;

        public BluetoothDeviceManagerReceiver(BluetoothDevicePickResultHandler handler) {
            this.handler = handler;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            context.unregisterReceiver(this);

            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            handler.onDevicePicked(device);
        }
    }
}