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

import android.net.Uri;
import android.os.AsyncTask;

import com.opencsv.CSVReader;
import com.team3006.RedRock.RedRock;
import com.team3006.RedRock.iexport.ImportActivity;
import com.team3006.RedRock.schema.ScoutData;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CSVImportTask extends AsyncTask<Uri, Integer, List<ScoutData>> {

    private ImportActivity activity;

    public CSVImportTask(ImportActivity activity) {
        this.activity = activity;
    }

    @Override
    public List<ScoutData> doInBackground(Uri... fileUri) {

        CSVReader reader;
        try {
            reader = new CSVReader(new InputStreamReader(activity.getContentResolver().openInputStream(fileUri[0])));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        ArrayList<ScoutData> dataList = new ArrayList<>();

        String[] nextLine;
        try {
            while ((nextLine = reader.readNext()) != null) { // :)
                if (!RedRock.isInteger(nextLine[0])) {
                    continue;
                }
                try {
                    dataList.add(ScoutData.fromStringArray(nextLine));
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
                publishProgress(dataList.size());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return dataList;
    }

    @Override
    protected void onProgressUpdate(Integer... matchesRead) {
        super.onProgressUpdate(matchesRead);

        activity.onImportProgressUpdate(matchesRead[0]);
    }

    @Override
    protected void onPostExecute(List<ScoutData> dataList) {
        super.onPostExecute(dataList);

        activity.onImportCompletion(dataList);
    }
}