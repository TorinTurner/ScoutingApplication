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

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.team3006.RedRock.R;
import com.team3006.RedRock.RedRock;
import com.team3006.RedRock.schema.ScoutData;
import com.team3006.RedRock.schema.enumeration.AllianceStation;

public class ScoutingFlowDialogFragment extends AppCompatDialogFragment implements PopupMenu.OnMenuItemClickListener {

    public static final String EXTRA_DEFAULT_DATA = "com.team3006.RedRock.EXTRA_DEFAULT_DATA";
    // Use this instance of the interface to deliver action events
    private ScoutingFlowDialogFragmentListener mListener;
    private EditText teamNumber;
    private EditText matchNumber;
    private AppCompatButton allianceStationButton;
    private AllianceStation allianceStation;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Match settings");
        builder.setCancelable(false);

        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_scouting_flow, null);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        teamNumber = dialogView.findViewById(R.id.dialog_editTextTeamNumber);
        teamNumber.requestFocus();

        matchNumber = dialogView.findViewById(R.id.dialog_editTextMatchNumber);
        matchNumber.setText(String.valueOf(prefs.getInt(getResources().getString(R.string.pref_last_used_match_number), 0) + 1)); //increment the last match number

        allianceStation = AllianceStation.valueOf(prefs.getString(getResources().getString(R.string.pref_last_used_alliance_station), AllianceStation.RED_1.name()));
        allianceStationButton = dialogView.findViewById(R.id.dialog_allianceStationButton);

        allianceStationButton.setText(allianceStation.toString().replace('_', ' '));

        if (allianceStation.getColor() == AllianceStation.AllianceColor.RED) {
            ViewCompat.setBackgroundTintList(allianceStationButton, ContextCompat.getColorStateList(getContext(), R.color.alliance_red_primary));
        } else { //If red, switch to blue, and vice versa
            ViewCompat.setBackgroundTintList(allianceStationButton, ContextCompat.getColorStateList(getContext(), R.color.alliance_blue_primary));
        }

        if (getArguments() != null && getArguments().containsKey(EXTRA_DEFAULT_DATA)) { //Fill the data with previously set values
            ScoutData fillData = (ScoutData) getArguments().getSerializable(EXTRA_DEFAULT_DATA);

            teamNumber.setText(fillData.getTeam());
            matchNumber.setText(fillData.getMatchNumber() + "");

            allianceStation = fillData.getAllianceStation();

            allianceStationButton.setText(fillData.getAllianceStation().toString().replace('_', ' '));

            if (fillData.getAllianceStation().getColor() == AllianceStation.AllianceColor.RED) {
                ViewCompat.setBackgroundTintList(allianceStationButton, ContextCompat.getColorStateList(getContext(), R.color.alliance_red_primary));
            } else { //If red, switch to blue, and vice versa
                ViewCompat.setBackgroundTintList(allianceStationButton, ContextCompat.getColorStateList(getContext(), R.color.alliance_blue_primary));
            }
        }

        allianceStationButton.setOnClickListener(v -> {
            PopupMenu menu = new PopupMenu(getContext(), v);
            menu.inflate(R.menu.menu_alliance_stations);
            menu.setOnMenuItemClickListener(ScoutingFlowDialogFragment.this); //this works ;)
            menu.show();
        });

        builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton("Confirm", (dialog, id) -> mListener.onDialogPositiveClick(ScoutingFlowDialogFragment.this))
                .setNegativeButton("Cancel", (dialog, id) -> mListener.onDialogNegativeClick(ScoutingFlowDialogFragment.this));

        final Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        //Complex code to override auto dismiss
        dialog.setOnShowListener(d -> {
            //TextView title = dialog.findViewById(R.id.alertTitle);
            //title.setTypeface(ResourcesCompat.getFont(getContext(), R.font.arimo));

            Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> mListener.onDialogPositiveClick(ScoutingFlowDialogFragment.this));
        });

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return dialog;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the ScoutingFlowDialogFragmentListener so we can send events to the host
            mListener = (ScoutingFlowDialogFragmentListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement ScoutingFlowDialogFragmentListener");
        }
    }


    public boolean allFieldsComplete() {
        if (teamNumber.getText().toString().isEmpty()) {
            return false;
        }

        return !(matchNumber.getText().toString().isEmpty() || !RedRock.isInteger(matchNumber.getText().toString()));

    }

    public void initScoutData(ScoutData data) {
        data.setTeam(teamNumber.getText().toString());
        data.setMatchNumber(Integer.valueOf(matchNumber.getText().toString()));
        data.setAllianceStation(allianceStation);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        allianceStation = AllianceStation.valueOf(item.getTitle().toString().toUpperCase().replace(' ', '_'));

        allianceStationButton.setText(item.getTitle().toString());

        if (allianceStation.getColor() == AllianceStation.AllianceColor.RED) {
            ViewCompat.setBackgroundTintList(allianceStationButton, ContextCompat.getColorStateList(getContext(), R.color.alliance_red_primary));
        } else { //If red, switch to blue, and vice versa
            ViewCompat.setBackgroundTintList(allianceStationButton, ContextCompat.getColorStateList(getContext(), R.color.alliance_blue_primary));
        }
        return true;
    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface ScoutingFlowDialogFragmentListener {
        void onDialogPositiveClick(ScoutingFlowDialogFragment dialog);

        void onDialogNegativeClick(ScoutingFlowDialogFragment dialog);
    }

}
