/*
 *
 *  * MIT License
 *  *
 *  * Copyright (c) [2017] [velli20]
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in all
 *  * copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  * SOFTWARE.
 *
 */

package com.velli.passwordmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.velli.passwordmanager.database.OnDatabaseEditedListener;
import com.velli.passwordmanager.database.PasswordDatabaseHandler;


/**
 * Created by Hp on 7.12.2015.
 */
public class ActivityBase extends AppCompatActivity implements OnDatabaseEditedListener {
    public static final boolean DEBUG = true;
    private final String TAG = getClass().getSimpleName();

    public String getTag() {
        return TAG;
    }

    public int getActivityId() {
        return -2;
    }

    public boolean isDebugging() {
        return DEBUG;
    }

    public boolean implementsOnDatabaseEditedListener() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationBase.setActivityStatus(getActivityId(), ApplicationBase.STATUS_VISIBLE, false);

        if (isDebugging()) {
            Log.i(getTag(), getTag() + " onCreate()");
        }

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        boolean secure = prefs.getBoolean(getString(R.string.preference_key_screenshots_enabled), false);

        if (!secure) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isDebugging()) {
            Log.i(getTag(), getTag() + " onResume()");
        }

        ApplicationBase.setActivityStatus(getActivityId(), ApplicationBase.STATUS_VISIBLE, false);
        if (getActivityId() != ApplicationBase.ACTIVITY_LOCK_SCREEN && !PasswordDatabaseHandler.getInstance().isDatabaseOpen()) {
            logOut();
        }

        if (implementsOnDatabaseEditedListener()) {
            PasswordDatabaseHandler.getInstance().registerOnDatabaseEditedListener(this);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isDebugging()) {
            Log.i(getTag(), getTag() + " onStart()");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isDebugging()) {
            Log.i(getTag(), getTag() + " onPause() is changing configurations? " + String.valueOf(isChangingConfigurations()));
        }
        ApplicationBase.setActivityStatus(getActivityId(), ApplicationBase.STATUS_ON_PAUSE, isChangingConfigurations());

        if (implementsOnDatabaseEditedListener()) {
            PasswordDatabaseHandler.getInstance().unregisterOnDatabaseEditedListener(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isDebugging()) {
            Log.i(getTag(), getTag() + " onStop() is changing configurations? " + String.valueOf(isChangingConfigurations()));
        }
        ApplicationBase.setActivityStatus(getActivityId(), ApplicationBase.STATUS_ON_STOP, isChangingConfigurations());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isDebugging()) {
            Log.i(getTag(), getTag() + " onDestroy() is changing configurations? " + String.valueOf(isChangingConfigurations()));
        }
        ApplicationBase.setActivityStatus(getActivityId(), ApplicationBase.STATUS_ON_DESTROY, isChangingConfigurations());
    }

    public void logOut() {
        ApplicationBase.forceToLockApp();
        Intent i = new Intent(this, ActivityLockScreen.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.putExtra(FragmentLockScreen.BUNDLE_KEY_ACTION, FragmentLockScreen.ACTION_COMPARE_PASSWORD);

        startActivity(i);
        finish();
    }

    @Override
    public void onDatabaseHasBeenEdited(String tablename, long rowid) {
    }


}
