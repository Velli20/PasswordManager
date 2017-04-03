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

import com.rengwuxian.materialedittext.MaterialEditText;
import com.velli.passwordmanager.database.PasswordDatabaseHandler;
import com.velli.passwordmanager.R;
import com.velli.passwordmanager.database.Constants;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.widget.LinearLayout;


public class NewGroupView extends LinearLayout {
	public static final int BUNDLE_KEY_TYPE_GROUP = 0;
	public static final int BUNDLE_KEY_TYPE_LOGIN_TYPE= 1;
	
	private Context mContext;
	private MaterialEditText mGroupNameField;
	private int mAction = 0;
	private DialogTheme mTheme;
	
	
	
	public NewGroupView(Context context) {
		super(context);
		init(context);
	}
	
	public NewGroupView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public NewGroupView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context){
		mContext = context;
	}
	
	@Override
	public void onFinishInflate(){
		super.onFinishInflate();
		mGroupNameField = (MaterialEditText)findViewById(R.id.add_new_group_input_field);
		setType(mAction);
		if(mTheme != null){
			setTheme(mTheme);
		}
	}
	
	public void setTheme(DialogTheme theme){
		mTheme = theme;
		if(mGroupNameField == null){
			return;
		}
		if(theme == DialogTheme.Dark){
			mGroupNameField.setBaseColor(getResources().getColor(R.color.white));
			mGroupNameField.setPrimaryColor(getResources().getColor(R.color.color_accent_a200_dark));
		}
	}
	public void setType(int type){
		final Resources res = mContext.getResources();
		mAction = type;
		
		if(mGroupNameField != null && type == BUNDLE_KEY_TYPE_GROUP){
			mGroupNameField.setHint(res.getString(R.string.input_field_new_group));
		} else if(mGroupNameField != null && type == BUNDLE_KEY_TYPE_LOGIN_TYPE){
			mGroupNameField.setHint(res.getString(R.string.input_field_new_login_type));
		}
	}

	
	public boolean checkIfNameIsValid(){
		final Resources res = mContext.getResources();
		
		if(getGroupName().isEmpty()){
			mGroupNameField.setError(res.getString(R.string.error_group_name_field_is_empty));
			return false;
		}
		if(getGroupName().length() > 35){
			mGroupNameField.setError(res.getString(R.string.error_group_name_is_too_long));
			return false;
		}
		final PasswordDatabaseHandler db = PasswordDatabaseHandler.getInstance();
		if(db.isDatabaseOpen() && db.checkIfValueExists(true, getGroupName()) == Constants.RESULT_ERROR_GROUP_ALREADY_EXISTS){
			mGroupNameField.setError(res.getString(R.string.error_group_name));
			return false;
		} else if(db.isDatabaseOpen() && db.checkIfValueExists(true, getGroupName()) == Constants.RESULT_ERROR_INVALID_CHARACTER){
			mGroupNameField.setError(res.getString(R.string.error_group_name_invalid_charachter));
			return false;
		}
		return true;
	}
	
	public String getGroupName(){
		return mGroupNameField.getText().toString();
	}

}
