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

package com.team3006.RedRock.scouting_flow;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.team3006.RedRock.MainActivity;
import com.team3006.RedRock.R;
import com.team3006.RedRock.backend.AccountScope;
import com.team3006.RedRock.backend.StorageWrapper;
import com.team3006.RedRock.bluetooth.ClientConnectionTask;
import com.team3006.RedRock.schema.ScoutData;
import com.team3006.RedRock.schema.enumeration.ClimbTime;
import com.team3006.RedRock.schema.enumeration.HabLevel;
import com.team3006.RedRock.scouting_flow.view.CounterCompoundView;
import com.team3006.RedRock.util.TransitionUtils;

import java.util.Date;
import java.util.List;

public class ScoutingFlowActivity extends AppCompatActivity implements View.OnClickListener, ScoutingFlowDialogFragment.ScoutingFlowDialogFragmentListener, StorageWrapper.StorageListener {

    public static final String EXTRA_SCOUT_DATA = "EXTRA_SCOUT_DATA";

    private ScoutData scoutData;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.RedRock_BaseTheme);
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            scoutData = (ScoutData) savedInstanceState.getSerializable("ScoutData");
        } else {
            if (getIntent().hasExtra(EXTRA_SCOUT_DATA)) {
                scoutData = (ScoutData) getIntent().getSerializableExtra(EXTRA_SCOUT_DATA);
                //Fields are filled in by the fragments
            } else {
                scoutData = new ScoutData();

                ScoutingFlowDialogFragment dialogFragment = new ScoutingFlowDialogFragment();
                dialogFragment.setCancelable(false);
                dialogFragment.show(getSupportFragmentManager(), "ScoutingFlowDialogFragment");
            }
        }

        setContentView(R.layout.activity_scouting_flow);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_24dp);

        DataEntryFragment dataEntryFragment = new DataEntryFragment();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.data_entry_fragment, dataEntryFragment);
        ft.commit();

        fab = findViewById(R.id.fab_finish);
        fab.setOnClickListener(this);

        if (scoutData.getTeam() != null) { //Generate header based on presence of team number
            getSupportActionBar().setTitle("Scout: Team " + scoutData.getTeam());
            getSupportActionBar().setSubtitle("Qualification Match " + scoutData.getMatchNumber());

            toolbar.setBackground(new ColorDrawable(getResources().getColor(scoutData.getAllianceStation().getColor().getColorPrimary())));
            findViewById(R.id.app_bar_layout).setBackground(new ColorDrawable(getResources().getColor(scoutData.getAllianceStation().getColor().getColorPrimary())));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(getResources().getColor(scoutData.getAllianceStation().getColor().getColorPrimaryDark()));

                ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                ActivityManager.TaskDescription current = activityManager.getAppTasks().get(0).getTaskInfo().taskDescription;
                ActivityManager.TaskDescription taskDesc = new ActivityManager.TaskDescription("Scout: Team " + scoutData.getTeam(),
                        current.getIcon(), getResources().getColor(scoutData.getAllianceStation().getColor().getColorPrimary()));
                setTaskDescription(taskDesc);
            }
        } else {
            getSupportActionBar().setTitle("Scout a match...");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                ActivityManager.TaskDescription current = activityManager.getAppTasks().get(0).getTaskInfo().taskDescription;
                ActivityManager.TaskDescription taskDesc = new ActivityManager.TaskDescription("Scout a match...",
                        current.getIcon(), getResources().getColor(R.color.primary));
                setTaskDescription(taskDesc);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scouting_flow, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        if (id == R.id.action_edit_details) {
            ScoutingFlowDialogFragment dialogFragment = new ScoutingFlowDialogFragment();

            Bundle args = new Bundle();
            args.putSerializable(ScoutingFlowDialogFragment.EXTRA_DEFAULT_DATA, scoutData);
            dialogFragment.setArguments(args);

            dialogFragment.show(getSupportFragmentManager(), "ScoutingFlowDialogFragment");
        }

        /*if (id == R.id.action_add_to_home_screen) {
            if (ShortcutManagerCompat.isRequestPinShortcutSupported(this)) {

                ShortcutInfoCompat pinShortcutInfo =
                        new ShortcutInfoCompat.Builder(this, "match_scout")
                                .setShortLabel("Match scout")
                                .setLongLabel("Scout a match")
                                .setIcon(IconCompat.createWithResource(this, R.drawable.ic_shortcut_send))
                                .setIntent(new Intent(this, ScoutingFlowActivity.class)
                                        .setAction(Intent.ACTION_VIEW))
                                .build();

                // Create the PendingIntent object only if your app needs to be notified
                // that the user allowed the shortcut to be pinned. Note that, if the
                // pinning operation fails, your app isn't notified. We assume here that the
                // app has implemented a method called createShortcutResultIntent() that
                // returns a broadcast intent.
                Intent pinnedShortcutCallbackIntent =
                        ShortcutManagerCompat.createShortcutResultIntent(this, pinShortcutInfo);

                // Configure the intent so that your app's broadcast receiver gets
                // the callback successfully.
                PendingIntent successCallback = PendingIntent.getBroadcast(this, 0,
                        pinnedShortcutCallbackIntent, 0);

                ShortcutManagerCompat.requestPinShortcut(this, pinShortcutInfo,
                        successCallback.getIntentSender());
            } else {
                Toast.makeText(this, "Not supported by your launcher or OS", Toast.LENGTH_SHORT).show();
            }
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable("ScoutData", scoutData);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Discard draft?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Discard", (arg0, arg1) -> ScoutingFlowActivity.super.onBackPressed()).create().show();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab_finish) { //Send button - the only button that matters
            initScoutData();

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

            AlertDialog suspendDialog = null;

            // Local device
            if (prefs.getBoolean(getResources().getString(R.string.pref_ms_save_to_local_device), true)) {
                AccountScope.getStorageWrapper(AccountScope.LOCAL, this).writeData(getData(), new StorageWrapper.StorageListener() {
                    @Override
                    public void onDataWrite(@Nullable List<ScoutData> dataWritten) {
                        Intent refreshIntent = new Intent().setAction(MainActivity.ACTION_REFRESH_DATA_VIEW);
                        LocalBroadcastManager.getInstance(ScoutingFlowActivity.this).sendBroadcast(refreshIntent);
                    }
                });
                //If this errors, we'll catch it internally
            }

            // Bluetooth server
            if (prefs.getBoolean(getResources().getString(R.string.pref_ms_send_to_bluetooth_server), false)) {
                String address = prefs.getString(getResources().getString(R.string.pref_ms_bluetooth_server_device), null);

                try {
                    if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                        suspendDialog = new AlertDialog.Builder(this)
                                .setTitle("Bluetooth is disabled")
                                .setIcon(R.drawable.ic_warning_24dp)
                                .setMessage("Please enable Bluetooth and try again")
                                .setPositiveButton("OK", (dialog, which) -> finish())
                                .setOnDismissListener((dialog) -> finish())
                                .create();
                    }

                    BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);

                    ClientConnectionTask connectTask = new ClientConnectionTask(device, scoutData, getApplicationContext());
                    connectTask.execute();
                } catch (IllegalArgumentException e) {
                    suspendDialog = new AlertDialog.Builder(this)
                            .setTitle("Bluetooth server device not set")
                            .setIcon(R.drawable.ic_warning_24dp)
                            .setMessage("Please configure your scouting settings and try again")
                            .setPositiveButton("OK", (dialog, which) -> finish())
                            .setOnDismissListener((dialog) -> finish())
                            .create();
                }
            }

            PreferenceManager.getDefaultSharedPreferences(this).edit()
                    .putInt(getResources().getString(R.string.pref_last_used_match_number), scoutData.getMatchNumber())
                    .putString(getResources().getString(R.string.pref_last_used_alliance_station), scoutData.getAllianceStation().name())
                    .apply();

            if (suspendDialog == null) {
                finish();
            } else {
                suspendDialog.show();
            }
        }

    }

    @Override
    public void onDialogPositiveClick(ScoutingFlowDialogFragment dialog) {
        if (dialog.allFieldsComplete()) {
            dialog.initScoutData(scoutData);

            getSupportActionBar().setTitle("Scout: Team " + scoutData.getTeam());
            getSupportActionBar().setSubtitle("Qualification Match " + scoutData.getMatchNumber()); //TODO match types?

            int toolbarColor = ((ColorDrawable) findViewById(R.id.toolbar).getBackground()).getColor();

            int statusBarColor;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                statusBarColor = getWindow().getStatusBarColor();
            } else {
                statusBarColor = getResources().getColor(R.color.primary_dark);
            }

            TransitionUtils.toolbarAndStatusBarTransition(toolbarColor, statusBarColor,
                    getResources().getColor(scoutData.getAllianceStation().getColor().getColorPrimary()),
                    getResources().getColor(scoutData.getAllianceStation().getColor().getColorPrimaryDark()), this);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                ActivityManager.TaskDescription current = activityManager.getAppTasks().get(0).getTaskInfo().taskDescription;
                ActivityManager.TaskDescription taskDesc = new ActivityManager.TaskDescription("Scout: Team " + scoutData.getTeam(),
                        current.getIcon(), getResources().getColor(scoutData.getAllianceStation().getColor().getColorPrimary()));
                setTaskDescription(taskDesc);
            }

            dialog.dismiss();
        } else {
            //do not dismiss - TODO show error
        }
    }

    @Override
    public void onDialogNegativeClick(ScoutingFlowDialogFragment dialog) {
        dialog.dismiss();

        if (scoutData.getTeam() == null) {
            finish();
        }
    }

    /**
     * For data entry fragment only
     */
    protected FloatingActionButton getFab() {
        return fab;
    }

    protected ScoutData getData() {
        return scoutData;
    }

    private void initScoutData() { //TODO this SHOULD NOT BE IN THE ACTIVITY CODE
        // Init
        scoutData.setDate(new Date(System.currentTimeMillis()));

        scoutData.setSource(PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getResources().getString(R.string.pref_device_name), Build.MANUFACTURER + " " + Build.MODEL));

        // Sandstorm
        Spinner startingLevel = findViewById(R.id.storm_spinnerStartingLevel);
        scoutData.setStartingLevel(HabLevel.values()[startingLevel.getSelectedItemPosition()]);

        CheckBox crossedHabLine = findViewById(R.id.storm_checkBoxCrossedHabLine);
        scoutData.setCrossedHabLine(crossedHabLine.isChecked());

        CounterCompoundView storm_HighRocketHatchCount = findViewById(R.id.storm_counterHighRocketHatch);
        scoutData.setStormHighRocketHatchCount((int) storm_HighRocketHatchCount.getValue());

        CounterCompoundView storm_MidRocketHatchCount = findViewById(R.id.storm_counterMidRocketHatch);
        scoutData.setStormMiddleRocketHatchCount((int) storm_MidRocketHatchCount.getValue());

        CounterCompoundView storm_LowRocketHatchCount = findViewById(R.id.storm_counterLowRocketHatch);
        scoutData.setStormLowRocketHatchCount((int) storm_LowRocketHatchCount.getValue());

        CounterCompoundView storm_CargoShipHatchCount = findViewById(R.id.storm_counterCargoShipHatch);
        scoutData.setStormCargoShipHatchCount((int) storm_CargoShipHatchCount.getValue());

        CounterCompoundView storm_HighRocketCargoCount = findViewById(R.id.storm_counterHighRocketCargo);
        scoutData.setStormHighRocketCargoCount((int) storm_HighRocketCargoCount.getValue());

        CounterCompoundView storm_MidRocketCargoCount = findViewById(R.id.storm_counterMidRocketCargo);
        scoutData.setStormMiddleRocketCargoCount((int) storm_MidRocketCargoCount.getValue());

        CounterCompoundView storm_LowRocketCargoCount = findViewById(R.id.storm_counterLowRocketCargo);
        scoutData.setStormLowRocketCargoCount((int) storm_LowRocketCargoCount.getValue());

        CounterCompoundView storm_CargoShipCargoCount = findViewById(R.id.storm_counterCargoShipCargo);
        scoutData.setStormCargoShipCargoCount((int) storm_CargoShipCargoCount.getValue());

        // Teleop
        CounterCompoundView teleop_HighRocketHatchCount = findViewById(R.id.teleop_counterHighRocketHatch);
        scoutData.setTeleopHighRocketHatchCount((int) teleop_HighRocketHatchCount.getValue());

        CounterCompoundView teleop_MidRocketHatchCount = findViewById(R.id.teleop_counterMidRocketHatch);
        scoutData.setTeleopMiddleRocketHatchCount((int) teleop_MidRocketHatchCount.getValue());

        CounterCompoundView teleop_LowRocketHatchCount = findViewById(R.id.teleop_counterLowRocketHatch);
        scoutData.setTeleopLowRocketHatchCount((int) teleop_LowRocketHatchCount.getValue());

        CounterCompoundView teleop_CargoShipHatchCount = findViewById(R.id.teleop_counterCargoShipHatch);
        scoutData.setTeleopCargoShipHatchCount((int) teleop_CargoShipHatchCount.getValue());

        CounterCompoundView teleop_HighRocketCargoCount = findViewById(R.id.teleop_counterHighRocketCargo);
        scoutData.setTeleopHighRocketCargoCount((int) teleop_HighRocketCargoCount.getValue());

        CounterCompoundView teleop_MidRocketCargoCount = findViewById(R.id.teleop_counterMidRocketCargo);
        scoutData.setTeleopMiddleRocketCargoCount((int) teleop_MidRocketCargoCount.getValue());

        CounterCompoundView teleop_LowRocketCargoCount = findViewById(R.id.teleop_counterLowRocketCargo);
        scoutData.setTeleopLowRocketCargoCount((int) teleop_LowRocketCargoCount.getValue());

        CounterCompoundView teleop_CargoShipCargoCount = findViewById(R.id.teleop_counterCargoShipCargo);
        scoutData.setTeleopCargoShipCargoCount((int) teleop_CargoShipCargoCount.getValue());

        // Endgame
        Spinner climbLevel = findViewById(R.id.endgame_spinnerClimbLevel);
        scoutData.setEndgameClimbLevel(HabLevel.values()[climbLevel.getSelectedItemPosition()]);

        Spinner climbTime = findViewById(R.id.endgame_spinnerClimbTime);
        scoutData.setEndgameClimbTime(ClimbTime.values()[climbTime.getSelectedItemPosition()]);

        CheckBox supportedOtherRobots = findViewById(R.id.endgame_checkBoxSupportedOtherRobotsWhenClimbing);
        scoutData.setSupportedOtherRobots(supportedOtherRobots.isChecked());

        EditText climbDescription = findViewById(R.id.endgame_edittextClimbDescription);
        scoutData.setClimbDescription(climbDescription.getText().toString());

        // Notes
        EditText notes = findViewById(R.id.edittextNotes);
        scoutData.setNotes(notes.getText().toString());
    }
}
