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

package com.team3006.RedRock;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.service.quicksettings.TileService;
import android.support.multidex.MultiDexApplication;
import android.support.v7.app.AppCompatDelegate;

import com.team3006.RedRock.bluetooth.BluetoothServerService;
import com.team3006.RedRock.bluetooth.util.BluetoothQuickTileService;

public class RedRock extends MultiDexApplication implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static boolean isInteger(String str) { //TODO use this for all the int checks
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onCreate() { //This isn't why loading is slow
        super.onCreate();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        if (!sharedPref.contains(getResources().getString(R.string.pref_device_name))) {
            sharedPref.edit().putString(getResources().getString(R.string.pref_device_name), Build.MANUFACTURER + " " + Build.MODEL).apply();
        }

        boolean runServer = sharedPref.getBoolean(getResources().getString(R.string.pref_enable_bluetooth_server), false);

        if (runServer) { //I must be launching multiple instances?
            startService(new Intent(this, BluetoothServerService.class));
        }

        sharedPref.registerOnSharedPreferenceChangeListener(this);

        AppCompatDelegate.setDefaultNightMode(Integer.valueOf(sharedPref.getString(getResources().getString(R.string.pref_app_theme), "-1")));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getResources().getString(R.string.pref_enable_bluetooth_server))) {
            Boolean isServer = sharedPreferences.getBoolean(getResources().getString(R.string.pref_enable_bluetooth_server), false);

            if (isServer) {
                startService(new Intent(this, BluetoothServerService.class));
            } else {
                stopService(new Intent(this, BluetoothServerService.class));
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                TileService.requestListeningState(this, new ComponentName(this, BluetoothQuickTileService.class));
            }
        }
    }
}
