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

package com.velli.passwordmanager.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.velli.passwordmanager.ActivityLockScreen;
import com.velli.passwordmanager.R;
import com.velli.passwordmanager.database.OnDatabaseUnlockedListener;
import com.velli.passwordmanager.database.PasswordDatabaseHandler;
import com.velli.passwordmanager.roboto.RobotoTextView;


public class ConfirmPasswordView extends LinearLayout {
    private Context mContext;
    private MaterialEditText mPasswordField;
    private MaterialEditText mConfirmPasswordField;
    private RobotoTextView mMessage;
    private SharedPreferences mPrefs;
    private boolean mCreateNewPassword = false;
    private DialogTheme mTheme;


    public ConfirmPasswordView(Context context) {
        super(context);
        init(context);
    }

    public ConfirmPasswordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ConfirmPasswordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        mContext = context;
        mPrefs = context.getSharedPreferences("password", Context.MODE_PRIVATE);
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        mPasswordField = (MaterialEditText) findViewById(R.id.confirm_password_input_field);
        mConfirmPasswordField = (MaterialEditText) findViewById(R.id.confirm_password_confirm_field);
        mMessage = (RobotoTextView) findViewById(R.id.confirm_password_message);
        mCreateNewPassword = mConfirmPasswordField != null;

        if (mTheme != null) {

        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mContext = null;
    }


    public void setMessage(SpannableStringBuilder mLoginTo) {
        if (mMessage != null) {
            mMessage.setVisibility(View.VISIBLE);
            mMessage.setText(mLoginTo);
        }
    }

    public void setTheme(DialogTheme theme) {
        mTheme = theme;

        if (mPasswordField == null) {
            return;
        }

        if (theme == DialogTheme.Light) {
            mPasswordField.setBaseColor(getResources().getColor(R.color.black));
            mPasswordField.setPrimaryColor(getResources().getColor(R.color.color_primary_500));
            if (mConfirmPasswordField != null) {
                mConfirmPasswordField.setBaseColor(getResources().getColor(R.color.black));
                mConfirmPasswordField.setPrimaryColor(getResources().getColor(R.color.color_primary_500));
            }
        } else if (theme == DialogTheme.Dark) {
            mPasswordField.setBaseColor(getResources().getColor(R.color.white));
            mPasswordField.setPrimaryColor(getResources().getColor(R.color.color_accent_a200_dark));
            if (mConfirmPasswordField != null) {
                mConfirmPasswordField.setBaseColor(getResources().getColor(R.color.black));
                mConfirmPasswordField.setPrimaryColor(getResources().getColor(R.color.color_accent_a200));
            }
        }
    }


    public boolean hasErrors() {
        final String pass = mPasswordField.getText().toString().replaceAll("'", "''");


        if (mCreateNewPassword) {
            final String confirm = mConfirmPasswordField.getText().toString().replaceAll("'", "''");
            if (!confirm.equals(pass)) {
                mConfirmPasswordField.setError(mContext.getText(R.string.error_passwords_dont_match));
                return true;
            }
        }
        return false;
    }

    public void createNewPassword(OnDatabaseUnlockedListener listener) {
        final String pass = mPasswordField.getText().toString().replaceAll("'", "''");
        if (hasErrors()) {
            return;
        }
        PasswordDatabaseHandler.getInstance().changePassword(pass.toCharArray(), listener);

    }

    public String getPassword() {
        return mPasswordField.getText().toString().replaceAll("'", "''");
    }

    public boolean confirmPassword() {
        final String pass = mPasswordField.getText().toString().replaceAll("'", "''");
        if (hasErrors()) {
            return false;
        }
        /*if(PasswordDatabaseHandler.getInstance().confirmPassword(pass)){
			final int maxRetryCount = PreferenceManager.getDefaultSharedPreferences(mContext).getInt(mContext.getString(R.string.preference_key_max_retry_count), 15);
			mPrefs.edit().putInt("retrys left", maxRetryCount).apply();
			
			return true;
		}  else {
			setEnteredPasswordIncorrect();
			return false;

		} */
        return true;
    }

    public void setEnteredPasswordIncorrect() {
        final int retrysleft = mPrefs.getInt("retrys left", 15);
        mPrefs.edit().putInt("retrys left", retrysleft - 1).apply();

        if (retrysleft == 1) {
            ActivityLockScreen.maxRetrycountExceeded(mContext);
        } else {
            mPasswordField.setError(mContext.getText(R.string.error_wrong_password_retrys_left) + " " + String.valueOf(retrysleft - 1));
        }
    }


}
