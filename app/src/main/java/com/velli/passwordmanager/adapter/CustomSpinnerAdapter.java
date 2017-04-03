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

package com.velli.passwordmanager.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.velli.passwordmanager.R;
import com.velli.passwordmanager.roboto.RobotoTextView;

public class CustomSpinnerAdapter extends BaseAdapter {

	
	private LayoutInflater mInflater;
    private String[] itemsList;

    private int mCurrentNavigationItem = 0;
    
    private final int mLayout;
    private final int mLayoutDrop;
    
    private Drawable mIcon;
    private boolean mHasAddItem = false;
    private String mAddText = "";
    private Context mContext;
    
	public CustomSpinnerAdapter (Context context, String[] list, int spinnerItem, int spinnerDrop){
		mContext = context;
		mInflater = LayoutInflater.from(context);
		itemsList = list;
		
		mLayout = spinnerItem;
		mLayoutDrop = spinnerDrop;
	}
	

	
    public void setAddINewGroupItem(){
    	mIcon = mContext.getResources().getDrawable(R.drawable.ic_action_new_group);
    	mIcon.setBounds(0, 0, mIcon.getIntrinsicWidth(), mIcon.getIntrinsicHeight());
    	
    	mAddText = mContext.getResources().getString(R.string.action_create_new_group);
    	mHasAddItem = true;
    	notifyDataSetChanged();
    }
	
	public void setSelectedItem(int position){
		mCurrentNavigationItem = position;
	}
	

	@Override
	public int getCount() {
		return mHasAddItem ? (itemsList.length + 1) : itemsList.length;
	}

	@Override
	public String getItem(int position) {

		if(mHasAddItem && position == getCount() - 1){
			return mAddText;
		} else {
			return itemsList[position];
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		ViewHolderText holder;
		
		if(convertView == null){
			holder = new ViewHolderText();
			convertView = mInflater.inflate(mLayoutDrop, parent, false);
			holder.v = (RobotoTextView)convertView.findViewById(R.id.spinner_item_dropdown);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolderText)convertView.getTag();
		}
		
		if(position == getCount() -1 && mHasAddItem){
			holder.v.setCompoundDrawables(null, null, mIcon, null);
		} else {
			holder.v.setCompoundDrawables(null, null, null, null);
		}

		holder.v.setTag(position);
		holder.v.setText(getItem(position));
		return convertView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolderText holder;
		
		if(convertView == null){
			holder = new ViewHolderText();
			convertView = mInflater.inflate(mLayout, parent, false);
			holder.v = (RobotoTextView)convertView.findViewById(R.id.spinner_item);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolderText)convertView.getTag();
		}
		
		if(mHasAddItem && position == getCount() -1){
			final int newPos = mCurrentNavigationItem;

			holder.v.setText(getItem(newPos < 0 ? 0 : newPos));
		} else {
			holder.v.setText(getItem(position));
		}
		return convertView;
	}

	static class ViewHolderText {
		RobotoTextView v;
	}



}
