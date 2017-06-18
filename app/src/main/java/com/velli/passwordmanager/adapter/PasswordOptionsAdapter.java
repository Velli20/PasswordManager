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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.velli.passwordmanager.R;
import com.velli.passwordmanager.collections.ListItemPasswordOption;
import com.velli.passwordmanager.listeners.OnPasswordOptionClickListener;
import com.velli.passwordmanager.roboto.RobotoButton;
import com.velli.passwordmanager.roboto.RobotoTextView;

import java.util.ArrayList;

public class PasswordOptionsAdapter extends BaseAdapter {

    public static final int VIEW_TYPE_CHECKBOX = 1;
    public static final int VIEW_TYPE_BUTTON = 2;
    public static final int VIEW_TYPE_TITLE = 0;
    private static final int VIEW_TYPE_COUNT = 3;
    private LayoutInflater mInflater;
    private ArrayList<ListItemPasswordOption> itemsList;

    private OnPasswordOptionClickListener mListener;

    public PasswordOptionsAdapter(Context context, ArrayList<ListItemPasswordOption> list) {
        mInflater = LayoutInflater.from(context);
        itemsList = list;
    }


    public void setOnPasswordOptionClickListener(OnPasswordOptionClickListener l) {
        mListener = l;
    }

    public ArrayList<ListItemPasswordOption> getItemsList() {
        return itemsList;
    }

    @Override
    public int getCount() {
        return itemsList.size();
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        return itemsList.get(position).getViewType();
    }

    @Override
    public boolean isEnabled(int position) {
        return getItemViewType(position) != VIEW_TYPE_TITLE;
    }

    @Override
    public ListItemPasswordOption getItem(int position) {
        return itemsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderTitle holderTitle = null;
        ViewHolderCheckBox holderCheckbox = null;
        ViewHolderButton holderButton = null;

        ListItemPasswordOption item = getItem(position);

        if (convertView == null && item.getViewType() == VIEW_TYPE_TITLE) {
            holderTitle = new ViewHolderTitle();
            convertView = mInflater.inflate(R.layout.list_item_subheader, parent, false);

            holderTitle.t = (RobotoTextView) convertView.findViewById(R.id.password_generation_list_item_title);

            convertView.setTag(R.layout.list_item_subheader, holderTitle);

        } else if (convertView == null && item.getViewType() == VIEW_TYPE_CHECKBOX) {
            holderCheckbox = new ViewHolderCheckBox();
            convertView = mInflater.inflate(R.layout.password_generation_listitem_checkbox, parent, false);

            holderCheckbox.v = (CheckBox) convertView.findViewById(R.id.password_generation_list_item_checkBox);
            holderCheckbox.t = (RobotoTextView) convertView.findViewById(R.id.password_generation_list_item_text);

            convertView.setTag(R.layout.password_generation_listitem_checkbox, holderCheckbox);

        } else if (convertView == null && item.getViewType() == VIEW_TYPE_BUTTON) {
            holderButton = new ViewHolderButton();
            convertView = mInflater.inflate(R.layout.password_generation_listitem_button, parent, false);

            holderButton.v = (RobotoButton) convertView.findViewById(R.id.password_generation_list_item_button);
            holderButton.t = (RobotoTextView) convertView.findViewById(R.id.password_generation_list_item_text);

            convertView.setTag(R.layout.password_generation_listitem_button, holderButton);
        } else {
            holderTitle = (ViewHolderTitle) convertView.getTag(R.layout.list_item_subheader);
            holderCheckbox = (ViewHolderCheckBox) convertView.getTag(R.layout.password_generation_listitem_checkbox);
            holderButton = (ViewHolderButton) convertView.getTag(R.layout.password_generation_listitem_button);
        }

        if (item.getViewType() == VIEW_TYPE_TITLE) {
            holderTitle.t.setText(item.getTitle());

        } else if (item.getViewType() == VIEW_TYPE_CHECKBOX) {
            holderCheckbox.v.setOnCheckedChangeListener(null);
            holderCheckbox.v.setChecked(item.getBooleanValue());
            holderCheckbox.v.setOnCheckedChangeListener(new CheckBoxListener(position));
            holderCheckbox.t.setText(item.getTitle());

        } else {
            holderButton.v = (RobotoButton) convertView.findViewById(R.id.password_generation_list_item_button);
            holderButton.v.setOnClickListener(new ButtonListener(position));
            holderButton.v.setText(String.valueOf(item.getIntValue()));
            holderButton.t.setText(item.getTitle());
        }

        return convertView;
    }

    static class ViewHolderCheckBox {
        RobotoTextView t;
        CheckBox v;
    }

    static class ViewHolderButton {
        RobotoTextView t;
        RobotoButton v;
    }

    static class ViewHolderTitle {
        RobotoTextView t;
    }


    private class CheckBoxListener implements OnCheckedChangeListener {
        private final int mPosition;

        public CheckBoxListener(int position) {
            mPosition = position;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            itemsList.get(mPosition).setBooleanValue(isChecked);
            mListener.onPasswordOptionClick(VIEW_TYPE_CHECKBOX, buttonView, mPosition);
        }

    }

    private class ButtonListener implements OnClickListener {
        private final int mPosition;

        public ButtonListener(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View v) {
            mListener.onPasswordOptionClick(VIEW_TYPE_BUTTON, v, mPosition);
        }
    }
}
