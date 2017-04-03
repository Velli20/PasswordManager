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

import java.util.ArrayList;

import com.velli.passwordmanager.R;
import com.velli.passwordmanager.roboto.RobotoTextView;
import com.velli.passwordmanager.collections.ListItemGroup;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;



public class ManageGroupsFragmentListAdapter extends BaseAdapter {
	private static final int VIEW_TYPE_COUNT = 2;
	public static final int VIEW_TYPE_NORMAL = 0;
	public static final int VIEW_TYPE_TITLE = 1;
	
	private final LayoutInflater mInflater;
	private final Resources mRes;
	
	private ArrayList<ListItemGroup> mList;
	private ManageGroupsFragmentAdapterCallback mListener;
	
	
	public interface ManageGroupsFragmentAdapterCallback {
		void onCheckboxChecked();
	}
	
	public ManageGroupsFragmentListAdapter(Context context, ArrayList<ListItemGroup> list){
		mInflater = LayoutInflater.from(context);
		mRes = context.getResources();
		mList = list;
	}
	
	public void setListItems(ArrayList<ListItemGroup> list){
		mList = list;
	}
	
	public void setManageGroupsFragmentAdapterCallback(ManageGroupsFragmentAdapterCallback l){
		mListener = l;
	}
	
	@Override
	public int getViewTypeCount(){
		return VIEW_TYPE_COUNT;
	}
	
	@Override
	public boolean isEnabled(int position){
		return getItemViewType(position) != VIEW_TYPE_TITLE;
	}
	
	@Override
	public int getItemViewType(int position){
		return mList.get(position).viewType;
	}
	
	public ArrayList<ListItemGroup> getItems(){
		return mList;
	}
	
	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public ListItemGroup getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ListItemGroup group = getItem(position);
		
		ViewHolderNormal normal = null;
		ViewHolderTitle title = null;
		
		if(convertView == null){
			switch(group.viewType){
			case VIEW_TYPE_NORMAL:
				normal = new ViewHolderNormal();
				convertView = mInflater.inflate(R.layout.list_item_group, parent, false);
				normal.mPrimary = (RobotoTextView)convertView.findViewById(R.id.list_item_group_primary_text);
				normal.mSecondary = (RobotoTextView)convertView.findViewById(R.id.list_item_group_secondary_text);
				normal.mCheckbox = (CheckBox)convertView.findViewById(R.id.list_item_group_checkbox);
				convertView.setTag(R.layout.list_item_group, normal);
				break;
			case VIEW_TYPE_TITLE:
				title = new ViewHolderTitle();
				convertView = mInflater.inflate(R.layout.list_item_subheader, parent, false);
				title.mTitle = (RobotoTextView)convertView.findViewById(R.id.password_generation_list_item_title);
				title.mTitle.setTextColor(mRes.getColor(R.color.color_accent_a100_dark));
				title.mTitle.setAlpha(1);
				convertView.setTag(R.layout.list_item_subheader, title);
				break;
			}
		} else {
			normal = (ViewHolderNormal)convertView.getTag(R.layout.list_item_group);
			title = (ViewHolderTitle)convertView.getTag(R.layout.list_item_subheader);
		}
		
		if(group.viewType == VIEW_TYPE_NORMAL){
			normal.mPrimary.setText(group.primary);
			normal.mSecondary.setText(mRes.getQuantityString(R.plurals.password, Integer.parseInt(group.secondary), Integer.parseInt(group.secondary)));
			normal.mCheckbox.setOnCheckedChangeListener(null);
			normal.mCheckbox.setChecked(group.checked);
			normal.mCheckbox.setOnCheckedChangeListener(new CheckBoxListener(position));
		} else {
			title.mTitle.setText(group.primary);
		}
		
		return convertView;
	}
	
	
	private static class ViewHolderNormal {
		RobotoTextView mPrimary;
		RobotoTextView mSecondary;
		CheckBox mCheckbox;
	}
	
	private static class ViewHolderTitle {
		RobotoTextView mTitle;
	}
	

	
	private class CheckBoxListener implements OnCheckedChangeListener {
		private final int position;
		
		public CheckBoxListener(int pos){
			position = pos;
		}
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			getItem(position).checked = isChecked;
			if(mListener != null){
				mListener.onCheckboxChecked();
			}
		}
		
	}

}
