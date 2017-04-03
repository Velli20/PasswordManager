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

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class setPasswordLengthView extends RelativeLayout implements OnSeekBarChangeListener {
	private static final int MINIMUM_VALUE = 1;
	private SeekBar mSeekbar;
	private RobotoTextView mLabel;
	private int mCurrentValue = 11;
	
	public setPasswordLengthView(Context context) {
		super(context);
	}
	
	public setPasswordLengthView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public setPasswordLengthView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	

	
	@Override
	public void onFinishInflate(){
		super.onFinishInflate();
		
		mSeekbar = (SeekBar)findViewById(R.id.set_password_lenght_seekbar);
		mSeekbar.setProgress(mCurrentValue);
		mSeekbar.setOnSeekBarChangeListener(this);
		
		mLabel = (RobotoTextView)findViewById(R.id.set_password_lenght_label);
		mLabel.setText(String.valueOf(mCurrentValue));
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if(progress <= MINIMUM_VALUE){
			seekBar.setProgress(MINIMUM_VALUE);
			mLabel.setText(String.valueOf(MINIMUM_VALUE));
		} else {
			mLabel.setText(String.valueOf(progress));
		}
		
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}
	
	public void setValue(int value){
		mCurrentValue = value;
		if(value < MINIMUM_VALUE){
			mCurrentValue = MINIMUM_VALUE;
		} else {
			mCurrentValue = value;
		}
		if(mSeekbar != null){
			mSeekbar.setProgress(mCurrentValue);
		}
	}
	
	public int getValue(){
		return mSeekbar.getProgress();
	}

}
