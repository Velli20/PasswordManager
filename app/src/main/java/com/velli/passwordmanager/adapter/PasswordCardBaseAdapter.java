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
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.velli.passwordmanager.Password;
import com.velli.passwordmanager.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Hp on 8.1.2016.
 */
public class PasswordCardBaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int VIEW_TYPE_PASSWORD = 0;
    public static final int VIEW_TYPE_PASSWORD_NO_URL = 1;
    public static final int VIEW_TYPE_CREDIT_CARD = 2;
    public static final int VIEW_TYPE_WIFI_PASSWORD = 3;

    public ArrayList<Password> mEntryList;

    private HashMap<Integer, Integer> mNewPositions = new HashMap<>();
    private HashMap<Integer, Integer> mOldPositions;

    public Drawable mStarEnabled;
    public Drawable mStarDisabled;
    public String mSearchPattern = "";

    private OnStarPressedListener mStarListener;
    private OnCardClickListener mCardClickListener;

    private Handler mUpdateHandler = new Handler();

    public Resources mRes;



    public PasswordCardBaseAdapter(Context context, ArrayList<Password> list){
        mRes = context.getResources();
        setHasStableIds(true);
        mEntryList = list;
        mapPositions();

        mStarEnabled = mRes.getDrawable(R.drawable.ic_action_starred_enabled);
        mStarDisabled = mRes.getDrawable(R.drawable.ic_action_starred_disabled);

        mStarEnabled.setBounds(0, 0, mStarEnabled.getIntrinsicWidth(), mStarEnabled.getIntrinsicHeight());
        mStarDisabled.setBounds(0, 0, mStarDisabled.getIntrinsicWidth(), mStarDisabled.getIntrinsicHeight());

    }

    public interface OnStarPressedListener {
        void onStarPressed(Password entry, int position);
    }

    public interface OnCardClickListener {
        void onCardClick(View v, Password entry, int position);
    }

    public void notifyListItemsChanged(){
        mUpdateHandler.removeCallbacks(mUpdater);


        if(mOldPositions != null){
            int lenght = mEntryList.size();
            boolean deleted = mOldPositions.size() > lenght;

            if((deleted && mOldPositions.size() - lenght != 1)
                    || (!deleted && lenght - mOldPositions.size() != 1)){
                notifyDataSetChanged();
                return;
            }

            int firstDeletedRow = -1;



            ArrayList<Integer> toAdd = new ArrayList<>();
            ArrayList<Integer> toUpdate = new ArrayList<>();
            ArrayList<int[]> toMove = new ArrayList<>();
            ArrayList<Integer> toDelete = new ArrayList<>();

            for(int i = 0; i < lenght; i++){
                int rowId = mEntryList.get(i).getRowId();

                if(mOldPositions.get(rowId) == null){
                    toAdd.add(i);
                } else if(mOldPositions.get(rowId) != null){
                    int newPos = mNewPositions.get(rowId);
                    int oldPos = mOldPositions.get(rowId);

                    if(newPos == oldPos){
                        toUpdate.add(i);
                    } else {
                        toMove.add(new int[]{oldPos, newPos});
                    }
                }
                mOldPositions.remove(rowId);
            }

            if(mOldPositions.size() > 0){
                for (HashMap.Entry<Integer, Integer> entry : mOldPositions.entrySet()) {
                    int value = entry.getValue();
                    if(firstDeletedRow == -1 || firstDeletedRow > value){
                        firstDeletedRow = value;
                    }
                    toDelete.add(value);
                }
            }

            //First, delete
            for(Integer position : toDelete){
                notifyItemRemoved(position);
            }

            //Next, move
            for(int[] position : toMove){
                if(toAdd.isEmpty() && toDelete.isEmpty()){
                    notifyItemMoved(position[0], position[1]);
                }
            }

            //Next, update
            for(Integer position : toUpdate){
                if(toMove.isEmpty() && toAdd.isEmpty()){
                    notifyDataSetChanged();
                } else {
                    mUpdateHandler.postDelayed(mUpdater, 300);
                }
                break;
            }

            //Finally, add
            for(Integer position : toAdd){
                notifyItemInserted(position);
            }

            if(toUpdate.isEmpty()){
                mUpdateHandler.postDelayed(mUpdater, 300);
            }
        } else {
            notifyDataSetChanged();
        }
    }

    final Runnable mUpdater = new Runnable(){

        @Override
        public void run(){
            notifyDataSetChanged();
        }
    };


    private void mapPositions(){
        if(mEntryList != null){

            int length = mEntryList.size();

            mOldPositions = new HashMap<>();
            mOldPositions.putAll(mNewPositions);

            mNewPositions.clear();
            for(int i = 0; i < length; i++){
                mNewPositions.put(mEntryList.get(i).getRowId(), i);
            }
        }
    }

    public void setEntryList(ArrayList<Password> list){
        mEntryList = list;
        mapPositions();
    }

    public void setSearchPattern(String pattern){
        mSearchPattern = pattern;
    }

    public void setOnStarPressedListener(OnStarPressedListener l){
        mStarListener = l;
    }

    public void setOnCardClickListener(OnCardClickListener l){
        mCardClickListener = l;
    }

    public void removeAllListItems(){
        if (mEntryList != null) {
            mEntryList.clear();
            mapPositions();
            notifyDataSetChanged();
        }
    }

    @Override
    public long getItemId(int position){
        return mEntryList.get(position).getRowId();
    }

    public ArrayList<Password> getItems() { return mEntryList; }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        if(mEntryList == null){
            return 0;
        }
        return mEntryList.size();
    }

    public Password getItem(int position) {
        if(mEntryList == null){
            return null;
        }
        return mEntryList.get(position);
    }

    public class StarButtonListener implements View.OnClickListener {
        private final int position;

        public StarButtonListener(int position){
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            if(mStarListener != null){
                mStarListener.onStarPressed(mEntryList.get(position), position);
            }
        }

    }

    public class CardClickListener implements View.OnClickListener {
        private final int position;
        private final View view;

        public CardClickListener(int position, View v){
            this.position = position;
            this.view = v;
        }

        @Override
        public void onClick(View v) {
            if(mCardClickListener != null){
                mCardClickListener.onCardClick(view, mEntryList.get(position), position);
            }
        }

    }
}
