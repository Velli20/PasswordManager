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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.velli.passwordmanager.roboto.RobotoButton;
import com.velli.passwordmanager.roboto.RobotoTextView;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.velli.passwordmanager.adapter.CustomSpinnerAdapter;
import com.velli.passwordmanager.collections.CustomSpinnerIconAdapter;
import com.velli.passwordmanager.collections.NavigationDrawerConstants;
import com.velli.passwordmanager.collections.Utils;
import com.velli.passwordmanager.database.OnDatabaseUnlockedListener;
import com.velli.passwordmanager.database.OnGetGroupsListener;
import com.velli.passwordmanager.database.PasswordDatabaseHandler;
import com.velli.passwordmanager.widget.ConfirmPasswordView;
import com.velli.passwordmanager.widget.DialogTheme;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.Toast;

public class NewPasswordBase implements OnClickListener, OnItemSelectedListener, OnDatabaseUnlockedListener, TextWatcher {
	private static final String BUNDLE_KEY_DESCRIPTION = "";
	private static final String BUNDLE_KEY_LOGIN_TYPE = "";
	private static final String BUNDLE_KEY_URL = "";
	private static final String BUNDLE_KEY_USERNAME = "";
	private static final String BUNDLE_KEY_PASSWORD = "";
	private static final String BUNDLE_KEY_NOTE = "";
	private static final String BUNDLE_KEY_GROUP_POS = "";
	
	public static final int REQUEST_CODE_PASSWORD_GENERATION = 100;
	public static final String INTENT_DATA_PASSWORD = "password";
	
	private Activity mActivity;
	
	private Spinner mLabelSpinner;
	private Spinner mGroupSpinner;
	private Spinner mAppSpinner;
	private Spinner mWifiSecurity;
	
	private RobotoButton mCreateNewGroup;
	
	private MaterialEditText mDescription;
	private MaterialEditText mUrl;
	private MaterialEditText mUsername;
	private MaterialEditText mPassword;
	private MaterialEditText mNote;
	private MaterialEditText mWifiSSID;
	
	private RobotoTextView mAppsHint;
	private RobotoTextView mWifiSecurityHint;
	
	private MaterialEditText[] mTextFields;
	
	private String mGroups[];
	private String mAppTitles[];
	private String mAppPackageNames[];
	private String mWifiSecurityTypes[];
	
	private CustomSpinnerAdapter mStringAdapterGroups;
	private CustomSpinnerAdapter mStringAdapterApps;
	private CustomSpinnerAdapter mStringAdapterWifiSecurity;

	private boolean mInputFocused = false;
	private boolean mIsInEditMode = false;
	private boolean mIsDiscarted = false;
	
	private Password mPasswordForm;
	private Password mOriginalPasswordForm;
	
	
	private int mGroupSpinnerOldPosition = 0;
	private int mAppSpinnerSelection = 0;
	
	private MaterialDialog mProgressDialog;
	
	public NewPasswordBase(){
	
	}
	
	public void init(View entryView, Activity activity){
		final EditTextListener edittextFocusListener = new EditTextListener();
		final Intent intent = activity.getIntent();
		
		mActivity = activity;
		
		mLabelSpinner= (Spinner) entryView.findViewById(R.id.new_entry_login_icon);
		mGroupSpinner = (Spinner) entryView.findViewById(R.id.new_entry_group_spinner);
		mAppSpinner = (Spinner) entryView.findViewById(R.id.new_entry_app_spinner);
		mWifiSecurity = (Spinner) entryView.findViewById(R.id.new_entry_wifi_security);
        mGroupSpinner.post(new Runnable() {
			
			@Override
			public void run() {
				mGroupSpinner.setOnItemSelectedListener(NewPasswordBase.this);
			}
		});
        
        mLabelSpinner.post(new Runnable() {
			
			@Override
			public void run() {
				mLabelSpinner.setOnItemSelectedListener(NewPasswordBase.this);
				
			}
		});


		RobotoButton mGeneratePassButton = (RobotoButton) entryView.findViewById(R.id.new_entry_password_generate);
		mCreateNewGroup = (RobotoButton) entryView.findViewById(R.id.new_entry_button_new_group);
		mGeneratePassButton.setOnClickListener(this);
		mCreateNewGroup.setOnClickListener(this);
		
		mDescription = (MaterialEditText) entryView.findViewById(R.id.new_entry_description_input_field);
		mUrl = (MaterialEditText) entryView.findViewById(R.id.new_entry_url_input_field);
		mUsername = (MaterialEditText) entryView.findViewById(R.id.new_entry_username_input_field);
		mPassword = (MaterialEditText) entryView.findViewById(R.id.new_entry_password_input_field);
		mNote = (MaterialEditText) entryView.findViewById(R.id.new_entry_note_input_field);
		mWifiSSID = (MaterialEditText) entryView.findViewById(R.id.new_entry_ssid_input_field);
		
		mAppsHint = (RobotoTextView) entryView.findViewById(R.id.new_entry_app_title);
		mWifiSecurityHint = (RobotoTextView) entryView.findViewById(R.id.new_entry_wifi_security_hint);
		
		mTextFields = new MaterialEditText[]{mDescription, mUrl, mUsername, mPassword, mNote, mWifiSSID};

		for (MaterialEditText mTextField : mTextFields) {
			mTextField.setOnFocusChangeListener(edittextFocusListener);
		}
		
		if(intent.getExtras() != null && intent.getExtras().getString(Intent.EXTRA_TEXT) != null && !mIsInEditMode){
			if(mPasswordForm == null){
				createBlankPassword();
			} 
			final String uri = mActivity.getIntent().getExtras().getString(Intent.EXTRA_TEXT);
			
			mPasswordForm.setUrl(uri);
			mPasswordForm.setDescription(getDomainName(uri));
		}
		if(!PasswordDatabaseHandler.getInstance().isDatabaseOpen()){
			showLoginDialog(false);
			for (MaterialEditText mTextField : mTextFields) {
				mTextField.setEnabled(false);
			}
		} else {
			initGroupSpinner();
		}
		initAppListSpinner();
		initLoginLabelSpinner();
		initWifiSecuritySpinner();
		
		setData(mPasswordForm);
		
		mPassword.addTextChangedListener(this);
	}
	
	public void createBlankPassword(){
		if(mPasswordForm == null){
			mPasswordForm = new Password();
			mPasswordForm.setUrl("http://");
		}
		
	}

	public void showLoginDialog(boolean showError) {
		final ConfirmPasswordView v = (ConfirmPasswordView) View.inflate(mActivity, R.layout.dialog_layout_confirm_password, null);
		v.setTheme(DialogTheme.Light);
		if (showError) {
			v.setEnteredPasswordIncorrect();
		}
		new MaterialDialog.Builder(mActivity)
				.title(R.string.title_log_in)
				.customView(v, false)
				.cancelable(false)
				.negativeText(R.string.action_cancel)
				.positiveText(R.string.action_continue)
				.autoDismiss(false)
				.onPositive(new MaterialDialog.SingleButtonCallback() {
					@Override
					public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
						if (!v.hasErrors()) {
							PasswordDatabaseHandler.getInstance().openDatabase(v.getPassword().toCharArray(), NewPasswordBase.this);
							materialDialog.dismiss();
							showProgressDialog();
						}
					}
				}).show();
	}
	
	public void showProgressDialog(){
		mProgressDialog = new MaterialDialog.Builder(mActivity)
	    .title(R.string.title_log_in)
	    .content(R.string.action_please_wait)
	    .progress(true, 0)
	    .cancelable(false)
	    .show();
	}
	
	public static String getDomainName(String url) {
		try{
			URI uri = new URI(url);
	        String domain = uri.getHost();
	        return domain.startsWith("www.") ? domain.substring(4) : domain;
		} catch(URISyntaxException e){
			return "";
		}
	}
	
	public void setData(Password pass){
		if(pass == null && mPasswordForm != null){
			pass = mPasswordForm;
		} 
		
		if(pass != null){
			if(mIsInEditMode && mPasswordForm == null) {
				mPasswordForm = pass;
			}
			if(mDescription != null) {
				mDescription.setText(mPasswordForm.getDescription());
				mUrl.setText(mPasswordForm.getUrl());
				mUsername.setText(mPasswordForm.getUsername());
				mPassword.setText(mPasswordForm.getPassword());
				mNote.setText(mPasswordForm.getNote());
				mLabelSpinner.setSelection(mPasswordForm.getLoginIcon());
				
				if(mGroups != null){
					mGroupSpinner.setSelection(getSelection(mPasswordForm.getGroup(), mGroups));
				}
				if(mPasswordForm.getLoginIcon() == NavigationDrawerConstants.LABEL_APP){
					setToAppPasswordMode();
				} else if(mPasswordForm.getLoginIcon() == NavigationDrawerConstants.LABEL_WIFI){
					if(mWifiSecurityTypes != null){
						mWifiSecurity.setSelection(getSelection(mPasswordForm.getWifiSecurity(), mWifiSecurityTypes));
					}
					mWifiSSID.setText(mPasswordForm.getNetworkSSID());
					setToWifiPasswordMode();
				}
			}
		} 
	}
	
	public static int getSelection(String toMatch, String[] list){
		final int lenght = list.length;
		for (int i = 0; i < lenght; i++) {
			if (list[i].equals(toMatch)) {
				return i;
			}
		}
		return 0;
	}
	
	public void setInEditMode(boolean isInEditMode){
		mIsInEditMode = isInEditMode;
	}
	
	public void discard(){
		mIsDiscarted = true;
	}
	
	public void setOriginalPasswordForm(Password pass){
		mOriginalPasswordForm = Utils.copyPassword(pass);
	}
	
	
	public void saveTemporarilyPasswordData(){
		final Resources res = mActivity.getResources();
		final int loginTypeSelectionPos = mLabelSpinner.getSelectedItemPosition();
		
		if(mPasswordForm == null){
			createBlankPassword();
		}
		
		if (mDescription != null) {
			mPasswordForm.setLoginType(res.getString(Utils.getLoginLabelArray()[loginTypeSelectionPos]), loginTypeSelectionPos);
			mPasswordForm.setUrl(mUrl.getText().toString());
			mPasswordForm.setUsername(mUsername.getText().toString());
			mPasswordForm.setPassword(mPassword.getText().toString());
			mPasswordForm.setNote(mNote.getText().toString());
			
			if(mLabelSpinner.getSelectedItemPosition() == NavigationDrawerConstants.LABEL_APP){
				if(mAppSpinner != null && mAppTitles != null && mAppPackageNames != null){
					mAppSpinnerSelection = mAppSpinner.getSelectedItemPosition();
					mPasswordForm.setDescription(mAppTitles[mAppSpinnerSelection]);
					mPasswordForm.setAppPackageName(mAppPackageNames[mAppSpinnerSelection]);
				}
			} else if(mLabelSpinner.getSelectedItemPosition() == NavigationDrawerConstants.LABEL_WIFI){
				if(mWifiSecurityTypes != null && mWifiSecurity != null){
					mPasswordForm.setWifiSecurity(mWifiSecurityTypes[mWifiSecurity.getSelectedItemPosition()]);
				}
				mPasswordForm.setNetworkSSID(mWifiSSID.getText().toString());
				mPasswordForm.setDescription(mDescription.getText().toString());
			} else {
				mPasswordForm.setDescription(mDescription.getText().toString());
				mPasswordForm.setAppPackageName("");
				mPasswordForm.setNetworkSSID("");
			}
			
			if(mGroups == null || mGroups.length == 0 || mGroupSpinner.getSelectedItemPosition() >= mGroups.length){
				mPasswordForm.setGroup("");
			} else {
				mPasswordForm.setGroup(mGroups[mGroupSpinner.getSelectedItemPosition()]);
			}
		}
	}
	
	
	public void initGroupSpinner(){
		final PasswordDatabaseHandler db = PasswordDatabaseHandler.getInstance();

		db.getGroups(new OnGetGroupsListener() {

			@Override
			public void onGetValues(ArrayList<String> list) {
				mCreateNewGroup.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
				mGroupSpinner.setVisibility(list.isEmpty() ? View.GONE : View.VISIBLE);

				if (list != null && !list.isEmpty()) {
					mGroups = new String[list.size()];
					mGroups = list.toArray(mGroups);

					mStringAdapterGroups = new CustomSpinnerAdapter(mActivity, mGroups, R.layout.navigation_spinner_item_group, R.layout.navigation_spinner_item_group_dropdown);
					mStringAdapterGroups.setAddINewGroupItem();

					if (mGroupSpinner != null) {
						mGroupSpinner.setAdapter(mStringAdapterGroups);

					}
					if (mPasswordForm != null) {
						mGroupSpinner.setSelection(getSelection(mPasswordForm.getGroup(), mGroups));
					}
				}

			}
		});
	}
	
	public void initLoginLabelSpinner(){
		if(mLabelSpinner != null){
			mLabelSpinner.setAdapter(new CustomSpinnerIconAdapter(mActivity));
		}
	}
	
	
	public void initWifiSecuritySpinner(){
		mWifiSecurityTypes = mActivity.getResources().getStringArray(R.array.wifi_security_types);
		mStringAdapterWifiSecurity = new CustomSpinnerAdapter(mActivity,
						mWifiSecurityTypes, 
						R.layout.navigation_spinner_item_group,
						R.layout.navigation_spinner_item_group_dropdown);
		
		if(mWifiSecurity != null){
			mWifiSecurity.setAdapter(mStringAdapterWifiSecurity);
		}
	}
	
	public void initAppListSpinner(){
		if(mStringAdapterApps == null){
			AppListTask task = new AppListTask();
			task.execute();
		} else {
			mAppSpinner.setAdapter(mStringAdapterApps);
			mAppSpinner.setSelection(mAppSpinnerSelection);
		}
	}
	
	private class EditTextListener implements OnFocusChangeListener {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			mInputFocused = hasFocus;
			if(v.getId() == R.id.new_entry_password_input_field){
				mPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
			} else {
				mPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			}
		}
	}
	
	public boolean isTextFieldFocused(){
		return mInputFocused;
	}
	

	public boolean isDiscarded(){
		return mIsDiscarted;
	}
	
	public void unfocusAllTextFields() {
        for (MaterialEditText mTextField : mTextFields) {
            mTextField.setFocusable(false);
            mTextField.setFocusableInTouchMode(false);
        }
        for (MaterialEditText mTextField : mTextFields) {
            mTextField.setFocusable(true);
            mTextField.setFocusableInTouchMode(true);
        }
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.new_entry_password_generate){
			saveTemporarilyPasswordData();
			final Intent i = new Intent(mActivity, ActivityGeneratePassword.class);
			mActivity.startActivityForResult(i, REQUEST_CODE_PASSWORD_GENERATION);
		
		} else if(v.getId() == R.id.new_entry_button_new_group){
			ActivityMain.createNewGroup(mActivity, false);
		}
		
	}


	public void passwordGenerated(String password) {
		if(mPasswordForm != null){
			mPasswordForm.setPassword(password);
			setData(mPasswordForm);
		} else if(mPassword != null){
			mPassword.setText(password);
		}
	}
	
	public boolean checkForErrors(){
		final Resources res = mActivity.getResources();
		boolean isErrors = false;
		
		if(mDescription.getText().toString().isEmpty() && mLabelSpinner.getSelectedItemPosition() != NavigationDrawerConstants.LABEL_APP){
			mDescription.setError(res.getString(R.string.error_new_entry_no_description));
			isErrors = true;
		} if(mDescription.getText().toString().length() > 20 && mLabelSpinner.getSelectedItemPosition() != NavigationDrawerConstants.LABEL_APP){
			mDescription.setError(res.getString(R.string.error_new_entry_too_long_description));
			isErrors = true;
		} if(mUsername.getText().toString().isEmpty() && mLabelSpinner.getSelectedItemPosition() != NavigationDrawerConstants.LABEL_WIFI){
			mUsername.setError(res.getString(R.string.error_new_entry_no_username));
			isErrors = true;
		} if(mWifiSSID.getText().toString().isEmpty() && mLabelSpinner.getSelectedItemPosition() == NavigationDrawerConstants.LABEL_WIFI){
			mWifiSSID.setError(res.getString(R.string.error_new_entry_no_network_ssid));
			isErrors = true;
		}
		
		return isErrors;
	}
	
	public boolean save(){
		if(checkForErrors()){
			return false;
		}
		saveTemporarilyPasswordData();
		
		if(mIsInEditMode || mPasswordForm.getRowId() != -1){
			PasswordDatabaseHandler.getInstance().updatePassword(mPasswordForm);
		} else {
			PasswordDatabaseHandler.getInstance().addNewPassword(mPasswordForm);
		}
		
		Toast.makeText(mActivity, mActivity.getString(R.string.action_password_saved), Toast.LENGTH_LONG).show();
		
		return true;
	}

	public boolean checkIfEditsWasMade(){
		if(mOriginalPasswordForm == null){
			mOriginalPasswordForm = new Password();
			mOriginalPasswordForm.setUrl("http://");
		}
		saveTemporarilyPasswordData();

		return !mPasswordForm.equals(mOriginalPasswordForm);

	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		
		switch(parent.getId()){
		case R.id.new_entry_login_icon:
			if(position == NavigationDrawerConstants.LABEL_APP){
				setToAppPasswordMode();
			} else if(position == NavigationDrawerConstants.LABEL_WIFI){
				setToWifiPasswordMode();
			} else {
				setToNormalPasswordMode();
			}
			break;
		case R.id.new_entry_group_spinner:
			if(position == mStringAdapterGroups.getCount() - 1){
				ActivityMain.createNewGroup(mActivity, false);
				mGroupSpinner.setSelection(mGroupSpinnerOldPosition);
				mStringAdapterGroups.setSelectedItem(mGroupSpinnerOldPosition);
			} else {
				mGroupSpinnerOldPosition = position;
				mStringAdapterGroups.setSelectedItem(mGroupSpinnerOldPosition);
			}
			
			break;
		}
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		if(mGroupSpinner.getSelectedItemPosition() == mStringAdapterGroups.getCount() - 1 && mGroups.length == 0){
			ActivityMain.createNewGroup(mActivity, false);
		}
		
	}
	

	
	
	
	public void onSaveInstanceState(Bundle outState) {
		saveTemporarilyPasswordData();
		if(mPasswordForm != null){
			outState.putString(BUNDLE_KEY_DESCRIPTION, mPasswordForm.getDescription());
			outState.putInt(BUNDLE_KEY_LOGIN_TYPE, mPasswordForm.getLoginIcon());
			outState.putString(BUNDLE_KEY_URL, mPasswordForm.getUrl());
			outState.putString(BUNDLE_KEY_USERNAME, mPasswordForm.getUsername());
			outState.putString(BUNDLE_KEY_PASSWORD, mPasswordForm.getPassword());
			outState.putString(BUNDLE_KEY_NOTE, mPasswordForm.getNote());
			outState.putInt(BUNDLE_KEY_GROUP_POS, mGroupSpinner.getSelectedItemPosition());
		}
	}
	
	public void onRestoreSavedInstanceState(Bundle savedInstanceState){
		if(savedInstanceState != null){
			if(mPasswordForm == null){
				createBlankPassword();
			}
			int loginPos = savedInstanceState.getInt(BUNDLE_KEY_LOGIN_TYPE, 0);
			mPasswordForm.setDescription(savedInstanceState.getString(BUNDLE_KEY_DESCRIPTION));
			mPasswordForm.setLoginType(mActivity.getString(Utils.getLoginLabelArray()[loginPos]), loginPos);
			mPasswordForm.setUrl(savedInstanceState.getString(BUNDLE_KEY_URL));
			mPasswordForm.setUsername(savedInstanceState.getString(BUNDLE_KEY_USERNAME));
			mPasswordForm.setPassword(savedInstanceState.getString(BUNDLE_KEY_PASSWORD));
			mPasswordForm.setNote(savedInstanceState.getString(BUNDLE_KEY_NOTE));
			mPasswordForm.setGroupPosition(savedInstanceState.getInt(BUNDLE_KEY_GROUP_POS, 0));
			
			if(mGroups != null){
				mPasswordForm.setGroup(mGroups[savedInstanceState.getInt(BUNDLE_KEY_GROUP_POS, 0)]);
			}
			setData(mPasswordForm);
		}
	}
	
	private class AppListTask extends AsyncTask<Void, Void, CustomSpinnerAdapter> {

		@Override
		protected CustomSpinnerAdapter doInBackground(Void... params) {
			final PackageManager pm = mActivity.getPackageManager(); 
			final Intent mainIntent = new Intent(Intent.ACTION_MAIN);
			
			mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			
			final List<ResolveInfo> apps = pm.queryIntentActivities(mainIntent, 0);
			Collections.sort(apps, new ResolveInfo.DisplayNameComparator(pm));
			
			mAppTitles = new String[apps.size()];
			mAppPackageNames = new String[apps.size()];
			
			int i = 0;
			mAppSpinnerSelection = 0;
			
			String packageName = "";
			if(mPasswordForm != null){
				packageName = mPasswordForm.getAppPackageName();
			}
			for(ResolveInfo info : apps){
				mAppTitles[i] = info.loadLabel(pm).toString();
				mAppPackageNames[i] = info.activityInfo.applicationInfo.packageName;
				
				if(mAppPackageNames[i].equals(packageName)){
					mAppSpinnerSelection = i;
				}
				i++;
			}
			
			return new CustomSpinnerAdapter(mActivity, mAppTitles, R.layout.navigation_spinner_item_app, R.layout.navigation_spinner_item_group_dropdown);
		
		}
		
		@Override
		protected void onPostExecute(CustomSpinnerAdapter adapter){
			mStringAdapterApps = adapter;
			mAppSpinner.setAdapter(mStringAdapterApps);
			mAppSpinner.setSelection(mAppSpinnerSelection);
		}
		
	}

	@Override
	public void onDatabaseUnlocked(boolean result) {
		if(mProgressDialog != null){
			mProgressDialog.dismiss();
		}
		
		if(!result){
			showLoginDialog(true);
		} else {
            for (MaterialEditText mTextField : mTextFields) {
                mTextField.setEnabled(true);
            }
			initGroupSpinner();
		}
		
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) { }

	@Override
	public void afterTextChanged(Editable s) {
		String pass = mPassword.getText().toString();
		
		if(pass.isEmpty()){
			mPassword.setFloatingLabelText(mActivity.getResources().getString(R.string.hint_password));
			mPassword.setPrimaryColor(mActivity.getResources().getColor(R.color.color_accent_a200));
		} else if(pass.length() < 8){
			mPassword.setFloatingLabelText(mActivity.getResources().getString(R.string.error_password_weak));
			mPassword.setPrimaryColor(mActivity.getResources().getColor(R.color.password_weak));
		} else if(pass.length() < 16){
			mPassword.setFloatingLabelText(mActivity.getResources().getString(R.string.error_password_medium_strong));
			mPassword.setPrimaryColor(mActivity.getResources().getColor(R.color.password_fair));
		} else if(pass.length() >= 16){
			mPassword.setFloatingLabelText(mActivity.getResources().getString(R.string.error_password_strong));
			mPassword.setPrimaryColor(mActivity.getResources().getColor(R.color.password_strong));
		}
		
	}
	
	private void setToWifiPasswordMode(){
		mAppSpinner.setVisibility(View.GONE);
		mAppsHint.setVisibility(View.GONE);
		
		mUrl.setVisibility(View.GONE);
		mUsername.setVisibility(View.GONE);
		
		mWifiSecurity.setVisibility(View.VISIBLE);
		mWifiSecurityHint.setVisibility(View.VISIBLE);
		mWifiSSID.setVisibility(View.VISIBLE);
		mDescription.setVisibility(View.VISIBLE);
		
		if(mStringAdapterWifiSecurity != null && mWifiSecurity.getAdapter() == null){
			mWifiSecurity.setAdapter(mStringAdapterWifiSecurity);
		}
	}
	
    private void setToAppPasswordMode(){
    	mWifiSecurity.setVisibility(View.GONE);
		mWifiSecurityHint.setVisibility(View.GONE);
		mWifiSSID.setVisibility(View.GONE);
		
    	mUrl.setVisibility(View.GONE);
		mDescription.setVisibility(View.GONE);
		
		if(mStringAdapterApps == null){
			initAppListSpinner();
		} else if(mAppSpinner.getAdapter() == null){
			mAppSpinner.setAdapter(mStringAdapterApps);
			mAppSpinner.setSelection(mAppSpinnerSelection);
		}
		mAppSpinner.setVisibility(View.VISIBLE);
		mAppsHint.setVisibility(View.VISIBLE);
	}
    
    private void setToNormalPasswordMode(){
    	mAppSpinner.setVisibility(View.GONE);
		mAppsHint.setVisibility(View.GONE);
		
		mUrl.setVisibility(View.VISIBLE);
		mDescription.setVisibility(View.VISIBLE);
		mUsername.setVisibility(View.VISIBLE);
		
		mWifiSecurity.setVisibility(View.GONE);
		mWifiSecurityHint.setVisibility(View.GONE);
		mWifiSSID.setVisibility(View.GONE);
	}
	
}
