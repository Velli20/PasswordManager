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


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.velli.passwordmanager.filepicker.ActivityFilePicker;
import com.velli.passwordmanager.database.Constants;
import com.velli.passwordmanager.database.OnDatabaseUnlockedListener;
import com.velli.passwordmanager.database.OnGetLastLoginDateListener;
import com.velli.passwordmanager.database.PasswordDatabaseHandler;
import com.velli.passwordmanager.preferences.CustomCounterPreference;
import com.velli.passwordmanager.preferences.CustomSwitchPreference;
import com.velli.passwordmanager.widget.ConfirmPasswordView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;


import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

public class FragmentSettings extends PreferenceFragment implements Preference.OnPreferenceClickListener, OnGetLastLoginDateListener, SharedPreferences.OnSharedPreferenceChangeListener {
    public static final int EXPORT_REQUEST_CODE = 43;
	public static final int IMPORT_REQUEST_CODE = 44;
    public static final int RESTORE_REQUEST_CODE = 47;
    public static final int BACKUP_REQUEST_CODE = 49;
	public static final String TAG = "FragmentSettings ";
	private static final boolean DEBUG = false;
	
	private Preference mChangePassword;
	private Preference mLastLogin;
	private Preference mLicenses;
	private Preference mGroups;
	private Preference mFeedback;
	private Preference mExport;
	private Preference mImport;
	private Preference mRemovePasswords;
	private Preference mBackup;
	private Preference mRestore;
	
	private CustomCounterPreference mRetrys;
	private PreferenceCategory mStartupLock;
	private CustomSwitchPreference mEnableScreenshots;

	private SharedPreferences mPrefs;
	private boolean mEnableScreenshotsOldValue = false;
	private MaterialDialog mChangePassDialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		final Resources res = getActivity().getResources();		
		
		mChangePassword = findPreference(res.getString(R.string.preference_key_change_password));
		mLastLogin = findPreference(res.getString(R.string.preference_key_last_login));
		mLicenses = findPreference(res.getString(R.string.preference_key_licenses));
		mGroups = findPreference(res.getString(R.string.preference_key_manage_groups));
		mFeedback = findPreference(res.getString(R.string.preference_key_send_feedback));
		mExport = findPreference(res.getString(R.string.preference_key_export_passwords));
		mImport = findPreference(res.getString(R.string.preference_key_import_passwords));
		mRemovePasswords = findPreference(res.getString(R.string.preference_key_erase_data));
		mRetrys = (CustomCounterPreference)findPreference(res.getString(R.string.preference_key_max_retry_count));
		mStartupLock = (PreferenceCategory) findPreference(res.getString(R.string.preference_key_screen_start_up_lock));
		mEnableScreenshots = (CustomSwitchPreference)findPreference(res.getString(R.string.preference_key_screenshots_enabled));
		mBackup = findPreference(res.getString(R.string.preference_key_backup));
		mRestore = findPreference(res.getString(R.string.preference_key_restore));

		mChangePassword.setOnPreferenceClickListener(this);
		mLicenses.setOnPreferenceClickListener(this);
		mGroups.setOnPreferenceClickListener(this);
		mFeedback.setOnPreferenceClickListener(this);
		mExport.setOnPreferenceClickListener(this);
		mImport.setOnPreferenceClickListener(this);
		mBackup.setOnPreferenceClickListener(this);
		mRestore.setOnPreferenceClickListener(this);

		mPrefs = getPreferenceManager().getSharedPreferences();
		
        final boolean removePasswordsChecked = mPrefs.getBoolean(mRemovePasswords.getKey(), false);
        mEnableScreenshotsOldValue = mPrefs.getBoolean(mEnableScreenshots.getKey(), false);
        mRetrys.setOnPreferenceClickListener(this);
        
        if(removePasswordsChecked){
			mStartupLock.addPreference(mRetrys);
		} else {
			mStartupLock.removePreference(mRetrys);
		}
        

	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View v = super.onCreateView(inflater, container, savedInstanceState);

        if(v != null) {
            final ListView list = (ListView)v.findViewById(android.R.id.list);
            if(list != null && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
                list.setSelector(getResources().getDrawable(R.drawable.selectable_background_apptheme));
            }
        }

		mPrefs.registerOnSharedPreferenceChangeListener(this);

		PasswordDatabaseHandler.getInstance().getLastLoginDate(this);
				
	    return v;
	}
	
	
	@Override
	public void onDestroyView(){
		if(mPrefs != null){
			mPrefs.unregisterOnSharedPreferenceChangeListener(this);
		}
		if(mChangePassDialog != null){
			mChangePassDialog.dismiss();
			mChangePassDialog = null;
		}
		super.onDestroyView();
	}




    private void enableScreenShots(boolean enable) {
        if (mEnableScreenshotsOldValue != enable) {
            final String message = enable ? getResources().getString(R.string.action_restart_to_enable_screenshots)
                    : getResources().getString(R.string.action_restart_to_disable_screenshots);

            new MaterialDialog.Builder(getActivity())
                    .title(R.string.title_restart_app)
                    .content(message)
                    .theme(Theme.DARK)
                    .negativeText(R.string.action_cancel)
                    .positiveText(R.string.action_restart)
                    .cancelable(false)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                            materialDialog.dismiss();
                            getActivity().setResult(Activity.RESULT_OK);
                            getActivity().finish();
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                            materialDialog.dismiss();
                            mEnableScreenshots.setChecked(mEnableScreenshotsOldValue);
                        }
                    }).show();

        }
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		String key = preference.getKey();
		if(key.equals(mChangePassword.getKey())){
			changePassword(false);
			return true;
		} else if(key.equals(mLicenses.getKey())){
			ApplicationBase.setActivityStatus(ApplicationBase.ACTIVITY_LICENCES, ApplicationBase.STATUS_STARTING_ACTIVITY, false);
			final Intent licences = new Intent(getActivity(), ActivityLicenses.class);
			startActivity(licences);
			return true;
		} else if(key.equals(mGroups.getKey())){
			ApplicationBase.setActivityStatus(ApplicationBase.ACTIVITY_MANAGE_GROUPS, ApplicationBase.STATUS_STARTING_ACTIVITY, false);
			final Intent groups = new Intent(getActivity(), ActivityManageGroups.class);
			startActivity(groups);
			return true;
		} else if(key.equals(mFeedback.getKey())){
			final Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "velli.su@gmail.com", null));
			emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Pasword manager");
			startActivity(Intent.createChooser(emailIntent, "Send email..."));
			return true;
		} else if(key.equals(mExport.getKey()) || key.equals(mImport.getKey())){
            ApplicationBase.setActivityStatus(ApplicationBase.ACTIVITY_FILE_PICKER, ApplicationBase.STATUS_STARTING_ACTIVITY, false);
            final Intent i = new Intent(getActivity(), ActivityFilePickerExtended.class);
            i.putExtra(ActivityFilePicker.INTENT_EXTRA_FILE_EXTENSION, ".xls");
            i.putExtra(ActivityFilePicker.INTENT_EXTRA_MODE, key.equals(mExport.getKey()) ? ActivityFilePicker.MODE_CREATE_FILE : ActivityFilePicker.MODE_PICK_FILE);
            if(key.equals(mExport.getKey())) {
                i.putExtra(ActivityFilePicker.INTENT_EXTRA_FILENAME, getString(R.string.app_name));
            }

            startActivityForResult(i, key.equals(mExport.getKey()) ? EXPORT_REQUEST_CODE : IMPORT_REQUEST_CODE);
			return true;
		} else if(key.equals(mBackup.getKey())){
            ApplicationBase.setActivityStatus(ApplicationBase.ACTIVITY_MANAGE_GROUPS, ApplicationBase.STATUS_STARTING_ACTIVITY, false);
            final Intent i = new Intent(getActivity(), ActivityFilePickerExtended.class);
            i.putExtra(ActivityFilePicker.INTENT_EXTRA_FILE_EXTENSION, Constants.DATABASE_FILE_EXTENSION);
            i.putExtra(ActivityFilePicker.INTENT_EXTRA_MODE, ActivityFilePicker.MODE_CREATE_FILE);
            i.putExtra(ActivityFilePicker.INTENT_EXTRA_FILENAME, getString(R.string.app_name));

            startActivityForResult(i, BACKUP_REQUEST_CODE);
		} else if(key.equals(mRestore.getKey())){
            ApplicationBase.setActivityStatus(ApplicationBase.ACTIVITY_MANAGE_GROUPS, ApplicationBase.STATUS_STARTING_ACTIVITY, false);
            final Intent i = new Intent(getActivity(), ActivityFilePickerExtended.class);
            i.putExtra(ActivityFilePicker.INTENT_EXTRA_FILE_EXTENSION, Constants.DATABASE_FILE_EXTENSION);
            i.putExtra(ActivityFilePicker.INTENT_EXTRA_MODE, ActivityFilePicker.MODE_PICK_FILE);

            startActivityForResult(i, RESTORE_REQUEST_CODE);
		}
		return false;
	}
	
	

	
	private void changePassword(final boolean passwordConfirmed){

        final ConfirmPasswordView v = (ConfirmPasswordView) View.inflate(getActivity(), passwordConfirmed ? R.layout.dialog_layout_change_password : R.layout.dialog_layout_confirm_password, null);
        mChangePassDialog = new MaterialDialog.Builder(getActivity())
                .title(passwordConfirmed ? R.string.title_change_password : R.string.title_enter_your_password)
                .customView(v, false)
                .theme(Theme.DARK)
                .negativeText(R.string.action_cancel)
                .positiveText(R.string.action_continue)
                .autoDismiss(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog,@NonNull DialogAction dialogAction) {
                        if (v.hasErrors()) {
                            return;
                        }
                        if (passwordConfirmed) {
                            v.createNewPassword(new OnDatabaseUnlockedListener() {


                                @Override
                                public void onDatabaseUnlocked(boolean result) {
                                    if (result) {
                                        Toast.makeText(getActivity(), getActivity().getString(R.string.title_password_changed_successfully), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                        } else if (v.confirmPassword()) {
                            changePassword(true);
                        }
                        mChangePassDialog = null;
                        materialDialog.dismiss();
                    }
                })
                .build();
        mChangePassDialog.show();
    
	}

	@Override
	public void onLastLoginDate(long millis) {
		if(millis > 0){
			DateFormat formOut = new SimpleDateFormat("dd.MM.yyyy ' 'HH:mm:ss", Locale.getDefault());
			mLastLogin.setSummary(formOut.format(millis));
		}
	}

	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String uri = null;
        if (data != null) {
            uri = data.getStringExtra(ActivityFilePicker.INTENT_EXTRA_FILEPATH);
        }

        if(uri != null && resultCode == Activity.RESULT_OK) {
            final File file = new File(uri);

            if (requestCode == EXPORT_REQUEST_CODE) {
                new ExcelExportPasswordsTask(getActivity()).setFile(file).writeOut();
                        //.setOnFileSavedListener(this);
            } else if(requestCode == IMPORT_REQUEST_CODE) {
                ApplicationBase.setActivityStatus(ApplicationBase.ACTIVITY_IMPORT_PASSWORDS, ApplicationBase.STATUS_STARTING_ACTIVITY, false);
                Intent intent = new Intent(getActivity(), ActivityImportPasswords.class);
                intent.putExtra(ActivityImportPasswords.INTENT_EXTRA_FILE, uri);
                getActivity().startActivity(intent);
            } else if (requestCode == BACKUP_REQUEST_CODE) {
                backup(file);
            } else if (requestCode == RESTORE_REQUEST_CODE) {
                restore(file);
            }
        }

    }

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		final boolean removePasswordsChecked = mPrefs.getBoolean(mRemovePasswords.getKey(), false);
		
		if(key.equals(mEnableScreenshots.getKey())){
			boolean value = mPrefs.getBoolean(mEnableScreenshots.getKey(), false);
			enableScreenShots(value);
		} 
		if(removePasswordsChecked){
			mStartupLock.addPreference(mRetrys);
		} else {
			mStartupLock.removePreference(mRetrys);
		}
	}
	
	@SuppressWarnings("resource")
	@SuppressLint("SdCardPath")
	private void backup(File backupDB) {

        try {
			final File sd = Environment.getExternalStorageDirectory();

			if (sd.canWrite()) {
                final File currentDB = getActivity().getDatabasePath(PasswordDatabaseHandler.getDatabaseName());

				if (currentDB.exists() && backupDB.exists()) {
					final FileChannel src = new FileInputStream(currentDB).getChannel();
					final FileChannel dst = new FileOutputStream(backupDB).getChannel();

					dst.transferFrom(src, 0, src.size());
					src.close();
					dst.close();
					Toast.makeText(getActivity(), getActivity().getText(R.string.action_backup_complete) + " " + backupDB.getPath(), Toast.LENGTH_SHORT).show();
				}
			}
		} catch (Exception ignored) {
			Toast.makeText(getActivity(), "backup failed", Toast.LENGTH_SHORT).show();
		}
	}

	@SuppressWarnings("resource")
	private void restore(File fileToRestore) {

		PasswordDatabaseHandler db = PasswordDatabaseHandler.getInstance();
		try {
			File sd = Environment.getExternalStorageDirectory();

			if (sd.canWrite()) {
				final File currentDB = getActivity().getDatabasePath(PasswordDatabaseHandler.getDatabaseName());
				
				if (currentDB.exists()) {
					final FileChannel src = new FileInputStream(fileToRestore).getChannel();
					final FileChannel dst = new FileOutputStream(currentDB).getChannel();
					
					dst.transferFrom(src, 0, src.size());
					src.close();
					dst.close();
					
					Toast.makeText(getActivity(), getActivity().getText(R.string.action_restore_complete), Toast.LENGTH_SHORT).show();
					db.closeDatabase();
					getActivity().setResult(Activity.RESULT_CANCELED);
					getActivity().finish();
				}
			}
		} catch (Exception ignored) {}
	}
	



}
