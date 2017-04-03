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

import com.velli.passwordmanager.FragmentLockScreen.OnPasswordEnteredListener;
import com.velli.passwordmanager.database.Constants;
import com.velli.passwordmanager.database.PasswordDatabaseHandler;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.File;

public class ActivityLockScreen extends ActivityBase implements OnPasswordEnteredListener {

	@Override
	public int getActivityId() { return ApplicationBase.ACTIVITY_LOCK_SCREEN; }

	@Override
	public String getTag() { return getClass().getSimpleName(); }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock_screen);

        final Bundle bundle = new Bundle();
        final FragmentLockScreen frag = new FragmentLockScreen();

        bundle.putInt(FragmentLockScreen.BUNDLE_KEY_ACTION, isFirstStart() ?
                FragmentLockScreen.ACTION_CREATE_NEW_PASSWORD : FragmentLockScreen.ACTION_COMPARE_PASSWORD);
		
		frag.setOnPasswordEnteredListener(this);
		frag.setArguments(bundle);

		FragmentTransaction fr = getSupportFragmentManager().beginTransaction();
		fr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		fr.replace(R.id.container, frag, FragmentLockScreen.Tag).commit();
	}

	@Override
	public void onPasswordEntered(boolean correct) {
		if(correct){
			startActivityMain();
		} else {
			setUnlockAttemptFailed(ApplicationBase.getAppContext(), true);
		}
	}

	@Override
	public void onNewPasswordCreated() {
		startActivityMain();

		PasswordDatabaseHandler mDb = PasswordDatabaseHandler.getInstance();
        mDb.addNewGroup(getResources().getString(R.string.group_personal));
	}
	
	@Override 
	public void onResume(){
		super.onResume();
		FragmentLockScreen frag = (FragmentLockScreen) getSupportFragmentManager().findFragmentByTag(FragmentLockScreen.Tag);
		
		if(frag != null){
			frag.setOnPasswordEnteredListener(this);
		}
	}
	
	@Override
	public void onPause(){
		super.onPause();
        FragmentLockScreen frag = (FragmentLockScreen) getSupportFragmentManager().findFragmentByTag(FragmentLockScreen.Tag);
		
		if(frag != null){
			frag.setOnPasswordEnteredListener(null);
		}
	}


    private void startActivityMain() {
        ApplicationBase.setActivityStatus(ApplicationBase.ACTIVITY_MAIN, ApplicationBase.STATUS_STARTING_ACTIVITY, false);
        final Intent i = new Intent(this, ActivityMain.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

	private boolean isFirstStart(){
		return !doesDatabaseExist(this, Constants.DATABASE_NAME);
	}

	private static boolean doesDatabaseExist(ContextWrapper context, String dbName) {
		final File dbFile = context.getDatabasePath(dbName);
		return dbFile.exists();
	}

	private static void setUnlockAttemptFailed(Context c, boolean failed){
		final SharedPreferences prefs = c.getSharedPreferences("password", Context.MODE_PRIVATE);
		final int maxRetryCount = PreferenceManager.getDefaultSharedPreferences(c).getInt(c.getString(R.string.preference_key_max_retry_count), 15);

		if(failed) {
			final int retrysleft = prefs.getInt("retrys left", 15);
			prefs.edit().putInt("retrys left", retrysleft - 1).apply();

			if(retrysleft == 1){
				maxRetrycountExceeded(c);
			}
		} else {
			prefs.edit().putInt("retrys left", maxRetryCount).apply();
		}
	}

	public static void maxRetrycountExceeded(Context c){
		final SharedPreferences prefs = c.getSharedPreferences("password", Context.MODE_PRIVATE);
		final int maxRetryCount = PreferenceManager.getDefaultSharedPreferences(c).getInt(c.getString(R.string.preference_key_max_retry_count), 15);

		PasswordDatabaseHandler.getInstance().closeDatabase();
		c.deleteDatabase(Constants.DATABASE_NAME);
		prefs.edit().putInt("retrys left", maxRetryCount).apply();

		Toast.makeText(c, "All passwords deleted!", Toast.LENGTH_LONG).show();
		if(c instanceof AppCompatActivity){
			((AppCompatActivity)c).finish();
		}
	}
}
