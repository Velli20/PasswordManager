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

package com.velli.passwordmanager.collections;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.velli.passwordmanager.R;
import com.velli.passwordmanager.roboto.RobotoTextView;

import java.util.ArrayList;

public class CustomSpinnerIconAdapter extends BaseAdapter {
    private final Resources mRes;
    private LayoutInflater mInflater;
    private ArrayList<String> labels = new ArrayList<>();

    public CustomSpinnerIconAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mRes = context.getResources();


        final int label[] = Utils.getLoginLabelArray();
        final int navLenght = label.length;

        for (int i = 0; i < navLenght; i++) {
            labels.add(mRes.getString(label[i]));
        }
    }

    @Override
    public int getCount() {
        return labels.size();
    }

    @Override
    public Drawable getItem(int position) {
        Drawable d = mRes.getDrawable(Utils.getLoginLabelIconArray()[position]);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        return d;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.navigation_spinner_item_group_dropdown, parent, false);
            holder.v = (RobotoTextView) convertView.findViewById(R.id.spinner_item_dropdown);
            convertView.setTag(R.layout.navigation_spinner_item_group_dropdown, holder);
        } else {
            holder = (ViewHolder) convertView.getTag(R.layout.navigation_spinner_item_group_dropdown);
        }
        holder.v.setText(labels.get(position));
        holder.v.setCompoundDrawables(null, null, getItem(position), null);
        return convertView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.navigation_spinner_item_group, parent, false);
            holder.v = (RobotoTextView) convertView.findViewById(R.id.spinner_item);
            convertView.setTag(R.layout.navigation_spinner_item_group, holder);
        } else {
            holder = (ViewHolder) convertView.getTag(R.layout.navigation_spinner_item_group);
        }
        holder.v.setText(labels.get(position));
        holder.v.setCompoundDrawables(null, null, getItem(position), null);
        return convertView;
    }

    static class ViewHolder {
        RobotoTextView v;
    }

}
