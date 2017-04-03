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

import com.velli.passwordmanager.database.OnDatabaseUnlockedListener;
import com.velli.passwordmanager.database.PasswordDatabaseHandler;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;

public class FragmentLockScreen extends Fragment implements OnClickListener, OnDatabaseUnlockedListener, OnKeyListener {
	public static final String Tag = "FragmentLockScreen ";
	private static final boolean DEBUG = false;
	
	public static final int ACTION_CREATE_NEW_PASSWORD = 0;
	public static final int ACTION_COMPARE_PASSWORD = 1;
	
	public static final String BUNDLE_KEY_ACTION = "action";
	public static final String BUNDLE_KEY_PASSWORD= "password";
	public static final String BUNDLE_KEY_VERIFY_PASSWORD= "verify password";
	
	private AppCompatButton mButtonOk;

	
	private TextInputEditText mPasswordField;
	private TextInputEditText mVerifyPassField;

	private TextInputLayout mEnterPassTitle;
	private TextInputLayout mVerifyPassTitle;
	
	private int mAction = 1; //Default: compare password
	
	private OnPasswordEnteredListener mListener;

	public interface OnPasswordEnteredListener {
		void onPasswordEntered(boolean correct);
		void onNewPasswordCreated();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle savedInstanceState){
		final View v = inflater.inflate(R.layout.fragment_layout_lock_screen, root, false);
		Bundle bundle = getArguments();
		
		mButtonOk = (AppCompatButton)v.findViewById(R.id.pin_lock_ok_button);

		mPasswordField = (TextInputEditText)v.findViewById(R.id.pin_lock_password_field);
		mVerifyPassField = (TextInputEditText)v.findViewById(R.id.pin_lock_verify_password_field);


		mEnterPassTitle = (TextInputLayout)v.findViewById(R.id.pin_lock_hint);
		mVerifyPassTitle = (TextInputLayout)v.findViewById(R.id.pin_lock_reenter_hint);
		
		
		mButtonOk.setOnClickListener(this);
		

		
		if(bundle != null){
			mAction = bundle.getInt(BUNDLE_KEY_ACTION, ACTION_COMPARE_PASSWORD);
			
		}
		
		if(savedInstanceState != null){
			String pass = savedInstanceState.getString(BUNDLE_KEY_PASSWORD, "");
			String verify = savedInstanceState.getString(BUNDLE_KEY_VERIFY_PASSWORD, "");

			mPasswordField.setText(pass);
			
			if(mAction == ACTION_CREATE_NEW_PASSWORD){
				mVerifyPassField.setText(verify);
			}
		}
		
		
		if(getActivity() instanceof ActivityMain){
			final NavigationView drawer = ((ActivityMain)getActivity()).getNavigationDrawer();
			final ActionBar bar = ((ActivityMain)getActivity()).getSupportActionBar();
            if(bar != null) {
                bar.hide();
            }
			if(drawer != null){
				drawer.setClickable(false);
			}
		}
		init();

		return v;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		
		
	}
	
	@Override
	public void onResume(){
		super.onResume();
		if(getView() != null) {
			InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		    inputManager.hideSoftInputFromWindow(getView().getWindowToken(), InputMethodManager.SHOW_IMPLICIT);
		}
	}
	
	@Override
	public void onPause(){
		super.onPause();
		mListener = null;

	}
	
	private void init(){
		final Resources res = getActivity().getResources();
		
		if(mAction == ACTION_CREATE_NEW_PASSWORD){
            mEnterPassTitle.setHint(res.getString(R.string.action_choose_a_password));
            mVerifyPassTitle.setHint(res.getString(R.string.action_verify_password));
			
			mVerifyPassTitle.setVisibility(View.VISIBLE);
			mVerifyPassField.setVisibility(View.VISIBLE);

			mVerifyPassField.setOnKeyListener(this);
			
		} else {
            mEnterPassTitle.setHint(res.getString(R.string.action_enter_your_password));
			
			mVerifyPassTitle.setVisibility(View.GONE);
			mVerifyPassField.setVisibility(View.GONE);

			mPasswordField.setOnKeyListener(this);
		}
		
		
	}


	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.pin_lock_ok_button && getView() != null) {

			InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		    inputManager.hideSoftInputFromWindow(getView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		  
			if(mAction == ACTION_COMPARE_PASSWORD) {
				checkIfPasswordIsCorrect();
			} else {
				createNewPassword();
			}
		} 
		
	}
	
	
	public void setOnPasswordEnteredListener(OnPasswordEnteredListener listener){
		mListener = listener;

	}
	
	private void checkIfPasswordIsCorrect(){
		mButtonOk.setEnabled(false);
        mButtonOk.setText(getString(R.string.title_logging_in));
		final char[] pass = mPasswordField.getText().toString().replaceAll("'", "''").toCharArray();
		
		PasswordDatabaseHandler.getInstance().openDatabase(pass, this);
	}

	private void createNewPassword(){
		String pass = mPasswordField.getText().toString();
		final String verify = mVerifyPassField.getText().toString();
		
		if(pass.contentEquals(verify)){
			if(DEBUG){
				Log.i(Tag, Tag + "createNewPassword() passwords equals");
			}
			pass = pass.replaceAll("'", "''");
			PasswordDatabaseHandler.getInstance().changePassword(pass.toCharArray(), this);
			mButtonOk.setEnabled(false);
            mButtonOk.setText(getString(R.string.title_logging_in));
		} else {
			if(DEBUG){
				Log.i(Tag, Tag + "createNewPassword() passwords not equal");
			}
            mVerifyPassTitle.setError(getResources().getString(R.string.error_passwords_dont_match));
		}
	}
	
	@Override
	public void onDatabaseUnlocked(boolean result) {
		if(DEBUG){
			Log.i(Tag, Tag + "onDatabaseUnlocked(" + String.valueOf(result) + ")");
		}
		
		if(mAction == ACTION_COMPARE_PASSWORD){
			
			if(result){
                if(mListener != null){
                    mListener.onPasswordEntered(true);
                }

			} else {
				if(mListener != null){
					mListener.onPasswordEntered(false);
				}
                mEnterPassTitle.setError(getResources().getString(R.string.error_wrong_password));
                mButtonOk.setEnabled(true);
                mButtonOk.setText(getString(R.string.title_log_in));
			}
			
			
		} else {
			if(DEBUG){
				Log.i(Tag, Tag + "onDatabaseUnlocked() action: ACTION_CREATE_NEW_PASSWORD");
			}

            if(mListener != null){
                mListener.onNewPasswordCreated();
            }
			
			
		}
		
	}


	
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if(event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER){
			onClick(mButtonOk);
		}
		return false;
	}
	
	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BUNDLE_KEY_ACTION, mAction);
        outState.putString(BUNDLE_KEY_PASSWORD, mPasswordField.getText().toString());
        
        if(mAction == ACTION_CREATE_NEW_PASSWORD){
        	outState.putString(BUNDLE_KEY_VERIFY_PASSWORD, mVerifyPassField.getText().toString());
        }
	}

	


}
