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

package com.team3006.RedRock.backend.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ScoutDataDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "RedRock_SCOUT_DATA_2019.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String FLOAT_TYPE = " REAL";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String BLOB_TYPE = " BLOB";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ScoutDataContract.ScoutDataTable.TABLE_NAME + " (" +
                    ScoutDataContract.ScoutDataTable._ID + " INTEGER PRIMARY KEY," +
                    ScoutDataContract.ScoutDataTable.COLUMN_NAME_TEAM_NUMBER + TEXT_TYPE + COMMA_SEP +
                    ScoutDataContract.ScoutDataTable.COLUMN_NAME_MATCH_NUMBER + INTEGER_TYPE + COMMA_SEP +
                    ScoutDataContract.ScoutDataTable.COLUMN_NAME_ALLIANCE_STATION + TEXT_TYPE + COMMA_SEP +

                    ScoutDataContract.ScoutDataTable.COLUMN_NAME_DATE_ADDED + INTEGER_TYPE + COMMA_SEP +
                    ScoutDataContract.ScoutDataTable.COLUMN_NAME_DATA_SOURCE + TEXT_TYPE + COMMA_SEP +

                    ScoutDataContract.ScoutDataTable.COLUMN_NAME_STORM_STARTING_LEVEL + TEXT_TYPE + COMMA_SEP +
                    ScoutDataContract.ScoutDataTable.COLUMN_NAME_STORM_CROSSED_HAB_LINE + INTEGER_TYPE + COMMA_SEP +

                    ScoutDataContract.ScoutDataTable.COLUMN_NAME_STORM_HIGH_ROCKET_HATCH_COUNT + INTEGER_TYPE + COMMA_SEP +
                    ScoutDataContract.ScoutDataTable.COLUMN_NAME_STORM_MIDDLE_ROCKET_HATCH_COUNT + INTEGER_TYPE + COMMA_SEP +
                    ScoutDataContract.ScoutDataTable.COLUMN_NAME_STORM_LOW_ROCKET_HATCH_COUNT + INTEGER_TYPE + COMMA_SEP +
                    ScoutDataContract.ScoutDataTable.COLUMN_NAME_STORM_CARGO_SHIP_HATCH_COUNT + INTEGER_TYPE + COMMA_SEP +

                    ScoutDataContract.ScoutDataTable.COLUMN_NAME_STORM_HIGH_ROCKET_CARGO_COUNT + INTEGER_TYPE + COMMA_SEP +
                    ScoutDataContract.ScoutDataTable.COLUMN_NAME_STORM_MIDDLE_ROCKET_CARGO_COUNT + INTEGER_TYPE + COMMA_SEP +
                    ScoutDataContract.ScoutDataTable.COLUMN_NAME_STORM_LOW_ROCKET_CARGO_COUNT + INTEGER_TYPE + COMMA_SEP +
                    ScoutDataContract.ScoutDataTable.COLUMN_NAME_STORM_CARGO_SHIP_CARGO_COUNT + INTEGER_TYPE + COMMA_SEP +

                    ScoutDataContract.ScoutDataTable.COLUMN_NAME_TELEOP_HIGH_ROCKET_HATCH_COUNT + INTEGER_TYPE + COMMA_SEP +
                    ScoutDataContract.ScoutDataTable.COLUMN_NAME_TELEOP_MIDDLE_ROCKET_HATCH_COUNT + INTEGER_TYPE + COMMA_SEP +
                    ScoutDataContract.ScoutDataTable.COLUMN_NAME_TELEOP_LOW_ROCKET_HATCH_COUNT + INTEGER_TYPE + COMMA_SEP +
                    ScoutDataContract.ScoutDataTable.COLUMN_NAME_TELEOP_CARGO_SHIP_HATCH_COUNT + INTEGER_TYPE + COMMA_SEP +

                    ScoutDataContract.ScoutDataTable.COLUMN_NAME_TELEOP_HIGH_ROCKET_CARGO_COUNT + INTEGER_TYPE + COMMA_SEP +
                    ScoutDataContract.ScoutDataTable.COLUMN_NAME_TELEOP_MIDDLE_ROCKET_CARGO_COUNT + INTEGER_TYPE + COMMA_SEP +
                    ScoutDataContract.ScoutDataTable.COLUMN_NAME_TELEOP_LOW_ROCKET_CARGO_COUNT + INTEGER_TYPE + COMMA_SEP +
                    ScoutDataContract.ScoutDataTable.COLUMN_NAME_TELEOP_CARGO_SHIP_CARGO_COUNT + INTEGER_TYPE + COMMA_SEP +

                    ScoutDataContract.ScoutDataTable.COLUMN_NAME_ENDGAME_CLIMB_LEVEL + TEXT_TYPE + COMMA_SEP +
                    ScoutDataContract.ScoutDataTable.COLUMN_NAME_ENDGAME_CLIMB_TIME + TEXT_TYPE + COMMA_SEP +
                    ScoutDataContract.ScoutDataTable.COLUMN_NAME_ENDGAME_SUPPORTED_OTHER_ROBOT_WHEN_CLIMBING + INTEGER_TYPE + COMMA_SEP +
                    ScoutDataContract.ScoutDataTable.COLUMN_NAME_ENDGAME_CLIMB_DESCRIPTION + TEXT_TYPE + COMMA_SEP +

                    ScoutDataContract.ScoutDataTable.COLUMN_NAME_NOTES + TEXT_TYPE + ")";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ScoutDataContract.ScoutDataTable.TABLE_NAME;

    public ScoutDataDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for event data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
