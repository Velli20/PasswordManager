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

import com.velli.passwordmanager.R;
import com.velli.passwordmanager.roboto.RobotoTextView;
import com.velli.passwordmanager.collections.Utils;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class PasswordCard extends CardView {
	private RobotoTextView mDescription;
	private RobotoTextView mUrl;
	private RobotoTextView mUsername;
	
	private RobotoTextView mTitleUrl;
	private RobotoTextView mTitleUsername;
	private View mTitleDivider;

	private ImageButton mStar;
	private ImageView mIcon;
	private String mSearchPattern = "";

	public PasswordCard(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public PasswordCard(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	
	public PasswordCard(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		
	}
		
	@Override
	protected void onFinishInflate(){
		super.onFinishInflate();
		mDescription = (RobotoTextView)findViewById(R.id.password_card_title);
	    mUrl = (RobotoTextView)findViewById(R.id.password_card_url);
	    mUsername = (RobotoTextView)findViewById(R.id.password_card_username);
	    mStar = (ImageButton)findViewById(R.id.password_card_starred_button);
	    
	    mTitleUrl = (RobotoTextView)findViewById(R.id.password_card_url_title);
		mTitleDivider = findViewById(R.id.password_card_title_divider);
		mTitleUsername = (RobotoTextView)findViewById(R.id.password_card_username_title);
		
		mIcon = (ImageView) findViewById(R.id.password_card_icon);
	}
	
	@SuppressLint("NewApi")
	public void animateOut(){
		mTitleUrl.animate().alpha(0).setDuration(544).start();
		mTitleDivider.animate().alpha(0).setDuration(544).start();
		mTitleUsername.animate().alpha(0).setDuration(544).start();
		mUrl.animate().alpha(0).setDuration(544).start();
		mStar.animate().alpha(0).setDuration(544).start();
		
		
		int colorFrom = getResources().getColor(R.color.l_black);
		int colorTo = getResources().getColor(android.R.color.transparent);
		final ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
		
		colorAnimation.addUpdateListener(new AnimatorUpdateListener() {

		    @Override
		    public void onAnimationUpdate(ValueAnimator animator) {
		    	mUsername.setTextColor((int)animator.getAnimatedValue());
		    }

		});
		colorAnimation.setDuration(544).start();
		
		setBackgroundColor(getResources().getColor(android.R.color.transparent));
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
			setElevation(0);
		} 
		
	}

	
	public void setDescription(String description){
		if(description.isEmpty()){
			mDescription.setText("-");
		} else if(mSearchPattern != null && !mSearchPattern.isEmpty() && !description.isEmpty()){
			mDescription.setText(Utils.boldString(Utils.upperCaseFirstLetter(description), mSearchPattern));
		} else {
			mDescription.setText(Utils.upperCaseFirstLetter(description));
		}
	}
	
	public void setUrl(String url){
		if(mUrl == null){
			return;
		}
		if(mSearchPattern != null && !mSearchPattern.isEmpty() && !url.isEmpty()){
			mUrl.setText(Utils.boldString(url, mSearchPattern));
		} else {
			mUrl.setText(url);
		}	
	}
	
	public void setUsername(String username){
		if(mSearchPattern != null && !mSearchPattern.isEmpty() && !username.isEmpty()){
			mUsername.setText(Utils.boldString(username, mSearchPattern));
		} else {
			mUsername.setText(username);
		}
	}
	
	public void setStarbuttonOnClickListener(OnClickListener l){
		mStar.setOnClickListener(l);
	}
	
	public void setStarButtonDrawable(Drawable d){
		mStar.setImageDrawable(d);
	}

	public void setLoginIcon(Drawable d){
		mIcon.setImageDrawable(d);
	}
	
	public void setSearchPattern(String pattern){
		mSearchPattern = pattern;
	}
}
