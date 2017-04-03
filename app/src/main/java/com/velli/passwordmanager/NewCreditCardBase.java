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

import java.util.ArrayList;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.velli.passwordmanager.roboto.RobotoButton;
import com.velli.passwordmanager.adapter.CustomSpinnerAdapter;
import com.velli.passwordmanager.collections.NavigationDrawerConstants;
import com.velli.passwordmanager.database.OnGetGroupsListener;
import com.velli.passwordmanager.database.PasswordDatabaseHandler;
import com.velli.passwordmanager.widget.NewGroupView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

public class NewCreditCardBase implements OnItemSelectedListener, OnClickListener {
	private static final String TAG = "NewCreditCardFragment ";
	private static final boolean DEBUG = false;
	
	private RobotoButton mNewGroupButton;
	
	private MaterialEditText mDescription;
	private MaterialEditText mCardNumber;
	private MaterialEditText mCardExpirationDate;
	private MaterialEditText mCardSecurityCode;
	
	private Spinner mCardTypeSpinner;
	private Spinner mGroupSpinner;
	
	private String[] mCardTypes;
	private String[] mGroups;
	
	private int mGroupSpinnerOldPosition = 0;
	
	private CustomSpinnerAdapter mAdapterGroups;
	private Context mContext;
	
	private Password mPassword;

	public NewCreditCardBase(Context c){
		mContext = c;
	}
	
	public void setView(View v){
		if(DEBUG){
			Log.i(TAG, TAG + " onCreateView()" );
		}
		
		mNewGroupButton = (RobotoButton)v.findViewById(R.id.new_credit_card_button_new_group);
		mNewGroupButton.setOnClickListener(this);
		
		mCardTypeSpinner = (Spinner)v.findViewById(R.id.new_credit_card_card_type);
		
		mGroupSpinner = (Spinner)v.findViewById(R.id.new_credit_card_group_spinner);
		mGroupSpinner.setOnItemSelectedListener(this);
		
		mDescription = (MaterialEditText) v.findViewById(R.id.new_credit_card_description_input_field);
		mCardNumber = (MaterialEditText) v.findViewById(R.id.new_credit_card_card_number_field);
		mCardExpirationDate = (MaterialEditText) v.findViewById(R.id.new_credit_card_expiration_date_field);
		mCardSecurityCode = (MaterialEditText) v.findViewById(R.id.new_credit_card_security_code_field);
		
        initCardTypeAdapter();
        initGroupSpinner();
		
	}
	

	public void setData(Password card){
		mPassword = card;
		
		if(mPassword != null){
			fillData(card);
		}
	}
	
	private void fillData(Password pass){
		CreditCardInfo card = pass.getCreditCard();
		
		mDescription.setText(pass.getDescription());
		mCardNumber.setText(card.getCardNumber());
		mCardExpirationDate.setText(card.getCardExpirationDate());
		mCardSecurityCode.setText(card.getCardCSV());
		
		mCardTypeSpinner.setSelection(getPositionInArray(mCardTypes, card.getCardType()));
		if(mGroups != null){
			mGroupSpinner.setSelection(getPositionInArray(mGroups, pass.getGroup()));
		}
		
		
	}
	
	private static int getPositionInArray(String[] names, String name){
		final int length = names.length;
		
		for (int i = 0; i < length; i++) {
			if (names[i].equals(name)) {
				return i;
			}
		}
		
		return 0;
	}
	
	
	
	private void initCardTypeAdapter(){
		mCardTypes = mContext.getResources().getStringArray(R.array.credit_card_types);
		
		final CustomSpinnerAdapter adapter = 
				new CustomSpinnerAdapter(mContext, mCardTypes,
						R.layout.navigation_spinner_item_group, 
						R.layout.navigation_spinner_item_group_dropdown);
		mCardTypeSpinner.setAdapter(adapter);
		
		
	}
	
	public void initGroupSpinner(){
		final PasswordDatabaseHandler db = PasswordDatabaseHandler.getInstance();

		db.getGroups(new OnGetGroupsListener() {

			@Override
			public void onGetValues(ArrayList<String> list) {
				mNewGroupButton.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
				mGroupSpinner.setVisibility(list.isEmpty() ? View.GONE : View.VISIBLE);

				if (list != null && !list.isEmpty()) {
					mGroups = new String[list.size()];
					mGroups = list.toArray(mGroups);

					mAdapterGroups = new CustomSpinnerAdapter(mContext, mGroups, R.layout.navigation_spinner_item_group, R.layout.navigation_spinner_item_group_dropdown);
					mAdapterGroups.setAddINewGroupItem();

					if (mGroupSpinner != null) {
						mGroupSpinner.setAdapter(mAdapterGroups);
						if (mPassword != null) {
							mGroupSpinner.setSelection(getPositionInArray(mGroups, mPassword.getGroup()));
						}
					}
				}

			}
		});
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		if(position == mAdapterGroups.getCount() - 1){
			createNewGroup();
			mGroupSpinner.setSelection(mGroupSpinnerOldPosition);
			mAdapterGroups.setSelectedItem(mGroupSpinnerOldPosition);
		} else {
			mGroupSpinnerOldPosition = position;
			mAdapterGroups.setSelectedItem(mGroupSpinnerOldPosition);
		}
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean checkForErrors(){
		final String description = mDescription.getText().toString();
		
		if(description.isEmpty()){
			mDescription.setError(mContext.getString(R.string.error_new_entry_no_description));
			return true;
		} else if(description.length() > 20){
			mDescription.setError(mContext.getString(R.string.error_new_entry_too_long_description));
			return true;
		}
		return false;
	}
	
	public void save(){
		if(checkForErrors()){
			return;
		}
		
		if(mPassword == null){
			mPassword = new Password();
		}
		final CreditCardInfo card = new CreditCardInfo();
		mPassword.setDescription(mDescription.getText().toString());
		if(mGroups == null || mGroups.length == 0){
			mPassword.setGroup("");
		} else {
			mPassword.setGroup(mGroups[mGroupSpinner.getSelectedItemPosition()]);
		}
		mPassword.setLoginType(mContext.getString(R.string.label_credit_card), NavigationDrawerConstants.LABEL_CREDIT_CARD);
		card.setCardType(mCardTypes[mCardTypeSpinner.getSelectedItemPosition()]);
		card.setCardNumber(mCardNumber.getText().toString());
		card.setCardExpirationDate(mCardExpirationDate.getText().toString());
		card.setCardCSV(mCardSecurityCode.getText().toString());
		
		mPassword.setCreditCard(card);
		
		if(mPassword.getRowId() != -1){
			PasswordDatabaseHandler.getInstance().updatePassword(mPassword);
		} else {
			PasswordDatabaseHandler.getInstance().addNewPassword(mPassword);
		}
	}

	@SuppressLint("InflateParams")
	public void createNewGroup() {
		final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final NewGroupView v = (NewGroupView) inflater.inflate(R.layout.dialog_layout_add_new_group, null);

		new MaterialDialog.Builder(mContext)
				.title(R.string.title_add_new_group)
				.customView(v, false)
				.negativeText(R.string.action_cancel)
				.positiveText(R.string.action_create_new_group)
				.autoDismiss(false)
				.onPositive(new MaterialDialog.SingleButtonCallback() {
					@Override
					public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
						if (v.checkIfNameIsValid()) {
							final String name = v.getGroupName();
							PasswordDatabaseHandler.getInstance().addNewGroup(name);
							materialDialog.dismiss();
						}
					}
				})
				.build()
				.show();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.new_credit_card_button_new_group:
			createNewGroup();
			break;
		}
		
	}


}
