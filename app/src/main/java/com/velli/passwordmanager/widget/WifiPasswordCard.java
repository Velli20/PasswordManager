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

import com.velli.passwordmanager.roboto.RobotoTextView;
import com.velli.passwordmanager.Password;
import com.velli.passwordmanager.R;
import com.velli.passwordmanager.collections.Utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.ImageView;

public class WifiPasswordCard extends CardView {
	private RobotoTextView mDescription;
	private RobotoTextView mSecurity;
	private RobotoTextView mSSID;
	
	private RobotoTextView mTitleSecurity;
	private RobotoTextView mTitleSSID;

	private ImageButton mStar;
	private ImageView mIcon;
	private String mSearchPattern = "";
	
	public WifiPasswordCard(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public WifiPasswordCard(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public WifiPasswordCard(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onFinishInflate(){
		super.onFinishInflate();
		mDescription = (RobotoTextView)findViewById(R.id.password_card_title);
		mSecurity = (RobotoTextView)findViewById(R.id.wifi_card_security);
		mSSID = (RobotoTextView)findViewById(R.id.wifi_card_ssid);
	    mStar = (ImageButton)findViewById(R.id.wifi_card_starred_button);
	    
	    mTitleSecurity = (RobotoTextView)findViewById(R.id.wifi_card_security_title);
	    mTitleSSID = (RobotoTextView)findViewById(R.id.wifi_card_ssid_title);
		
		mIcon = (ImageView) findViewById(R.id.password_card_icon);
	}
	
	public void setCardData(Password pass){
		if(pass == null){
			return;
		}
				
		if(mSearchPattern != null && !mSearchPattern.isEmpty() && !pass.getDescription().isEmpty()){
			mDescription.setText(Utils.boldString(pass.getDescription(), mSearchPattern));
		} else {
			mDescription.setText(pass.getDescription());
		}
		if(mSearchPattern != null && mSearchPattern != null && !mSearchPattern.isEmpty() && !pass.getNetworkSSID().isEmpty()){
			mSSID.setText(Utils.boldString(pass.getNetworkSSID(), mSearchPattern));
		} else {
			mSSID.setText(pass.getNetworkSSID());
		}
		mSecurity.setText(pass.getWifiSecurity());
		mSSID.setText(Utils.boldString(pass.getNetworkSSID(), mSearchPattern));
	}
	
	public void setStarbuttonOnClickListener(OnClickListener l){
		mStar.setOnClickListener(l);
	}
	
	public void setStarButtonDrawable(Drawable d){
		mStar.setImageDrawable(d);
	}
	
	public void setSearchPattern(String pattern){
		mSearchPattern = pattern;
	}

}
