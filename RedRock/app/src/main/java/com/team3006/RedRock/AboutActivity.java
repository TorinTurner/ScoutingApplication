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

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;

import com.danielstone.materialaboutlibrary.MaterialAboutActivity;
import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem;
import com.danielstone.materialaboutlibrary.items.MaterialAboutTitleItem;
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard;
import com.danielstone.materialaboutlibrary.model.MaterialAboutList;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

public class AboutActivity extends MaterialAboutActivity {

    @NonNull
    @Override
    protected MaterialAboutList getMaterialAboutList(@NonNull Context context) {
        MaterialAboutCard.Builder titleCard = new MaterialAboutCard.Builder();

        titleCard.addItem(new MaterialAboutTitleItem.Builder()
                .text(R.string.app_name)
                .icon(R.mipmap.ic_launcher)
                .build());

        titleCard.addItem(new MaterialAboutActionItem.Builder()
                .text("Version " + BuildConfig.VERSION_NAME)
                .subText("Click for patch notes")
                .icon(getTintedIcon(R.drawable.ic_info_outline_24dp))
                .setOnClickAction(() -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AboutActivity.this);
                    builder.setTitle("New in version " + BuildConfig.VERSION_NAME);
                    builder.setMessage(R.string.update_notes);
                    builder.setPositiveButton("OK", null);
                    builder.create().show();
                })
                .build());


        titleCard.addItem(new MaterialAboutActionItem.Builder()
                .text("View on GitHub")
                .subText("TorinTurner/RedRock-Android")
                .icon(getTintedIcon(R.drawable.ic_github_24dp))
                .setOnClickAction(() -> {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("https://github.com/TorinTurner/RedRock-Android"));
                    startActivity(i);
                })
                .build());



        MaterialAboutCard.Builder authorCard = new MaterialAboutCard.Builder();
        authorCard.title("Author");

        authorCard.addItem(new MaterialAboutActionItem.Builder()
                .text("Torin Torin")
                .subText("Lead Software")
                .icon(getTintedIcon(R.drawable.ic_person_24dp))
                .build());


        MaterialAboutCard.Builder teamCard = new MaterialAboutCard.Builder();
        teamCard.addItem(new MaterialAboutTitleItem.Builder()
                .text("FRC Team 3006 Red Rock Robotics")
                .icon(R.mipmap.avatar_icon)
                .build());




        teamCard.addItem(new MaterialAboutActionItem.Builder()
                .text("Follow us on Instagram")
                .subText("@redrockrobotics")
                .icon(getTintedIcon(R.drawable.ic_instagram_24dp))
                .setOnClickAction(() -> {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("https://www.instagram.com/redrockrobotics/"));
                    startActivity(i);
                })
                .build());



        teamCard.addItem(new MaterialAboutActionItem.Builder()
                .text("Visit our website")
                .subText("redrockrobotics.space")
                .icon(getTintedIcon(R.drawable.ic_web_24dp))
                .setOnClickAction(() -> {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("https://redrockrobotics.space/"));
                    startActivity(i);
                })
                .build());

        return new MaterialAboutList.Builder()
                .addCard(titleCard.build())
                .addCard(authorCard.build())
                .addCard(teamCard.build())
                .build();
    }

    @Override
    protected CharSequence getActivityTitle() {
        return getString(R.string.mal_title_about);
    }

    /**
     * Manually create Drawable icons to "support" DayNight icon tinting
     */
    private Drawable getTintedIcon(@DrawableRes int iconRes) {
        Drawable icon = getResources().getDrawable(iconRes);
        DrawableCompat.setTint(icon, getResources().getColor(R.color.about_icon_override));
        return icon;
    }
}