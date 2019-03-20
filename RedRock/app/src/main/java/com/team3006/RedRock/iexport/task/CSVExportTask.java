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

package com.team3006.RedRock.iexport.task;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.opencsv.CSVWriter;
import com.team3006.RedRock.R;
import com.team3006.RedRock.iexport.ExportActivity;
import com.team3006.RedRock.schema.ScoutData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class CSVExportTask extends AsyncTask<ScoutData, Integer, File> {

    private ExportActivity activity;

    public CSVExportTask(ExportActivity activity) {
        this.activity = activity;
    }

    @Override
    public File doInBackground(ScoutData... params) {

        CSVWriter writer;

        File dir = new File(Environment.getExternalStorageDirectory(), "RedRock");
        dir.mkdir();
        dir.setReadable(true, false);

        String deviceName = PreferenceManager.getDefaultSharedPreferences(activity)
                .getString(activity.getResources().getString(R.string.pref_device_name), Build.MANUFACTURER + " " + Build.MODEL);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");

        File csv = new File(dir, deviceName.replace(' ', '_') + "_exported_" + formatter.format(System.currentTimeMillis()) + ".csv");
        csv.setReadable(true, false);

        try {
            writer = new CSVWriter(new FileWriter(csv));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        int i = 0;
        for (ScoutData data : params) {
            writer.writeNext(data.toStringArray());
            publishProgress(i++);
        }

        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            //ignore
        }

        return csv;
    }

    @Override
    protected void onProgressUpdate(Integer... matchesWritten) {
        super.onProgressUpdate(matchesWritten);

        activity.onExportProgressUpdate(matchesWritten[0]);
    }

    @Override
    protected void onPostExecute(File file) {
        super.onPostExecute(file);

        Toast.makeText(activity, "CSV export complete: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();

        activity.onExportCompletion(file);
    }

}