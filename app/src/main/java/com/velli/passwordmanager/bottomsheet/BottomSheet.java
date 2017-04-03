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

package com.velli.passwordmanager.bottomsheet;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.velli.passwordmanager.R;
import com.velli.passwordmanager.collections.SpacesItemDecoration;

import java.util.ArrayList;


public class BottomSheet extends FrameLayout {
    private ArrayList<BottomSheetItem> mBottomSheetItems = new ArrayList<>();
    private OnBottomSheetItemClickedListener mCallback;
    private BottomSheetIconAdapter mAdapter;


    public BottomSheet(Context context) {
        super(context);
    }


    public BottomSheet(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public BottomSheet(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public interface OnBottomSheetItemClickedListener {
        void onBottomSheetItemClicked(int id);
    }



    public void setOnBottomSheetItemClickedListener(OnBottomSheetItemClickedListener l) {
        mCallback = l;
    }

    public void addBottomSheetItem(BottomSheetItem item) {
        mBottomSheetItems.add(item);
        if(mAdapter != null) {
            mAdapter.notifyItemInserted(mBottomSheetItems.size()-1);
        }
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        final RecyclerView recycler = (RecyclerView) findViewById(R.id.view_fragment_main_menu_recycler);

        mAdapter = new BottomSheetIconAdapter(getContext());

        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(mAdapter);



    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mCallback = null;
        mAdapter = null;
    }


    public class BottomSheetIconAdapter extends  RecyclerView.Adapter<BottomSheetIconViewHolder> {
        private LayoutInflater mInflater;
        private Resources mRes;

        public BottomSheetIconAdapter(Context c) {
            mInflater = LayoutInflater.from(c);
            mRes = c.getResources();
        }


        @Override
        public BottomSheetIconViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new BottomSheetIconViewHolder(mInflater.inflate(R.layout.list_item_bottom_sheet, parent, false));
        }

        @Override
        public void onBindViewHolder(BottomSheetIconViewHolder holder, int position) {
            BottomSheetItem item = mBottomSheetItems.get(position);

            Drawable d = DrawableCompat.wrap(getContext().getResources().getDrawable(item.mDrawableRes));
            if(d != null) {
                DrawableCompat.setTint(d, mRes.getColor(item.mDrawableColor));
                d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            }
            holder.mText.setText(item.mTitle);
            holder.mText.setCompoundDrawables(d, null, null, null);
            holder.itemView.setOnClickListener(new BottomSheetIconClickListener(item.mId));
        }

        @Override
        public int getItemCount() {
            return mBottomSheetItems.size();
        }
    }

    public class BottomSheetIconClickListener implements View.OnClickListener {
        private int mId;

        public BottomSheetIconClickListener(int id) {
            mId = id;
        }

        @Override
        public void onClick(View v) {
            if(mCallback != null) {
                mCallback.onBottomSheetItemClicked(mId);
                mCallback = null;
            }
            BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(BottomSheet.this);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }


}
