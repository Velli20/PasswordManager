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
import com.velli.passwordmanager.R;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.LinearInterpolator;

public class FabMenu extends CoordinatorLayout implements OnClickListener {
	private FloatingActionButton mAdd;
	private FloatingActionButton mAddPassword;
	private FloatingActionButton mAddCreditCard;
	private FloatingActionButton mButtons[];

	private RobotoTextView mTitles[];
	
	private boolean mMenuOpen = false;
	private boolean mTitlesAligned = false;
	
	private OnClickListener mClickListener;
	
	public FabMenu(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public FabMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public FabMenu(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}
	

	
	@Override
	public void onFinishInflate(){
		super.onFinishInflate();
		
		mAdd = (FloatingActionButton) findViewById(R.id.main_list_fab);
		mAddPassword = (FloatingActionButton) findViewById(R.id.main_list_fab_new_password);
		mAddCreditCard = (FloatingActionButton) findViewById(R.id.main_list_fab_new_credit_card);
		mButtons = new FloatingActionButton[]{mAddPassword, mAddCreditCard};

		RobotoTextView mTitlePassword = (RobotoTextView) findViewById(R.id.main_list_title_new_password);
		RobotoTextView mTitleCreditCard = (RobotoTextView) findViewById(R.id.main_list_title_new_credit_card);
		mTitles = new RobotoTextView[]{mTitlePassword, mTitleCreditCard};
		
		openMenu(false, false);
		mAdd.setOnClickListener(this);
		mAddPassword.setOnClickListener(this);
		mAddCreditCard.setOnClickListener(this);
		
		mAddPassword.setClickable(false);
		mAddCreditCard.setClickable(false);
	}
	
	@Override
	public void setOnClickListener(OnClickListener l){
		mClickListener = l;
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.main_list_fab:

			break;
		case R.id.main_list_fab_new_password:
		case R.id.main_list_fab_new_credit_card:
			if(mClickListener != null){
				mClickListener.onClick(v);
			}
			openMenu(false, true);
			break;
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev){
		if(!mMenuOpen){
			return super.onTouchEvent(ev);
		}
		
		switch(ev.getAction()){
		case MotionEvent.ACTION_DOWN:
			return true;
		case MotionEvent.ACTION_UP:
			openMenu(false, true);
			return true;
		}
		return false;
	}
	
	public void openMenu(boolean open, boolean animate){
		if(open && !mTitlesAligned){
			alignTitles(animate);
			return;
		}
		
		mAddPassword.setClickable(open);
		mAddCreditCard.setClickable(open);
		
		int colorFrom = getResources().getColor(open ? R.color.fab_menu_background : R.color.fab_menu_background_open);
		int colorTo = getResources().getColor(open ? R.color.fab_menu_background_open : R.color.fab_menu_background);

		
		if (animate) {
			final ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
			colorAnimation.addUpdateListener(new AnimatorUpdateListener() {

				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					setBackgroundColor((int) animation.getAnimatedValue());
				}
			});
			colorAnimation.addListener(new HardwareAccelerateListener(this));
			colorAnimation.setDuration(150);
			colorAnimation.start();
			
	
		} else {
			setBackgroundColor(colorTo);
		}
		
		int lenght = mButtons.length;
		int offset = 0;
		
		for(int i = 0; i < lenght; i++){
			final RobotoTextView title = mTitles[i];
			final FloatingActionButton button = mButtons[i];
			
			button.setClickable(open);
			
			if (animate) {
				button.animate()
				.scaleX(open ? 1f : 0f)
				.setListener(new HardwareAccelerateListener(button))
				.scaleY(open ? 1f : 0f)
				.setDuration(150)
				.setStartDelay(open? offset : 0)
				.setInterpolator(new LinearInterpolator())
				.start();
				
				title.animate().alpha(open ? 1 : 0)
				.setListener(new HardwareAccelerateListener(title))
				.setDuration(150)
				.setInterpolator(new LinearInterpolator())
				.setStartDelay(open? offset : 0)
				.start();

				offset += 40;
			} else {
				button.setScaleX(open ? 1f : 0f);
				button.setScaleY(open ? 1f : 0f);
				title.setAlpha(open ? 1 : 0);
			}
		}
		
	
		mMenuOpen = open;
		
	}
	
	
	private void alignTitles(boolean forceAnim){
		
		
		int lenght = mButtons.length;
		for(int i = 0; i < lenght; i++){
			int buttonPos[] = {0, 0};
			int titlePos[] = {0, 0}; // x = 0; y = 1;
			
			final RobotoTextView title = mTitles[i];
			final FloatingActionButton button = mButtons[i];
			
			title.getLocationOnScreen(titlePos);
			button.getLocationOnScreen(buttonPos);
			
			title.setTranslationY((buttonPos[1] - titlePos[1]) - (title.getHeight() / 2));
		}
		
		mTitlesAligned = true;
		openMenu(true, forceAnim);
	}
}
