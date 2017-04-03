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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.velli.passwordmanager.roboto.RobotoTextView;
import com.velli.passwordmanager.Password;
import com.velli.passwordmanager.R;
import com.velli.passwordmanager.collections.Utils;
import com.velli.passwordmanager.widget.CircleView;

import java.util.ArrayList;

/**
 * Created by Hp on 8.1.2016.
 */
public class PasswordCardsSmallAdapter extends PasswordCardBaseAdapter {
    private LayoutInflater mInflater;
    private OnAvatarSelectedListener mAvatarSelectionListener;
    private boolean mAvatarsAreSelectable = false;

    public interface OnAvatarSelectedListener {
        void onAvatarSelected(int position, boolean selected);
    }

    public PasswordCardsSmallAdapter(Context context, ArrayList<Password> list) {
        super(context, list);
        mInflater = LayoutInflater.from(context);
        if(list != null && !list.isEmpty()) {
            setHasStableIds(list.get(0).getRowId() != -1);
        } else {
            setHasStableIds(false);
        }
    }

    private void setText(RobotoTextView text, String textToHighlight) {
        if(mSearchPattern != null && !mSearchPattern.isEmpty()) {
            text.setText(Utils.boldString(textToHighlight, mSearchPattern));
        } else {
            text.setText(textToHighlight);
        }
    }

    public void setOnAvatarSelectedListener(OnAvatarSelectedListener l) {
        mAvatarsAreSelectable = true;
        mAvatarSelectionListener = l;
    }

    @Override
    public long getItemId(int position){
        return mEntryList.get(position).getRowId();
    }

    public int getSelectionsCount() {
        if(mEntryList == null) { return 0; }
        int count = 0;

        for(Password pass : mEntryList) {
            if(pass.isSelected()) {
                count++;
            }
        }
        return count;
    }

    public ArrayList<Password> getSelectedItems() {
        if(mEntryList == null) { return null; }

        ArrayList<Password> selections = new ArrayList<>();

        for(Password pass : mEntryList) {
            if(pass.isSelected()) {
                selections.add(pass);
            }
        }
        return selections;
    }

    public void setAllSelected(boolean selected) {
        mAvatarsAreSelectable = true;
        if(mEntryList == null) { return; }

        for(Password pass : mEntryList) {
            pass.setSelected(selected);
        }
        notifyDataSetChanged();
    }

    public void setAvatarsSelectable(boolean selectable) {
        mAvatarsAreSelectable = selectable;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Password entry = getItem(position);
        String avatarLetter = "";

        if(entry.getDescription() != null && !entry.getDescription().isEmpty()) {
            setText(((PasswordCardViewHolder) holder).mTitle, entry.getDescription());
            avatarLetter = entry.getDescription().substring(0, 1);
        }
        if(entry.getUsername() != null && !entry.getUsername().isEmpty()) {
            setText(((PasswordCardViewHolder) holder).mUsername, entry.getUsername());
            avatarLetter = entry.getUsername().substring(0, 1);
        }
        if(entry.getUrl() != null && !entry.getUrl().isEmpty()) {
            setText(((PasswordCardViewHolder) holder).mUrl, entry.getUrl());
            avatarLetter = entry.getUrl().substring(0, 1);
        }
        if(entry.getLoginIcon() != -1) {
            ((PasswordCardViewHolder)holder).mAvatar.setCircleDrawable(mRes.getDrawable(Utils.getLoginLabelIconArray()[entry.getLoginIcon()]));
        } else {
            ((PasswordCardViewHolder)holder).mAvatar.setCircleDrawable(null);
            ((PasswordCardViewHolder)holder).mAvatar.setCircleText(avatarLetter);
        }
        ((PasswordCardViewHolder)holder).mAvatar.setSelected(entry.isSelected());
        ((PasswordCardViewHolder)holder).mAvatar.setOnCircleSelectedListener(new AvatarClickListener(position));
        ((PasswordCardViewHolder)holder).mStar.setImageDrawable(entry.isStarred() ? mStarEnabled : mStarDisabled);
        ((PasswordCardViewHolder)holder).mStar.setOnClickListener(new StarButtonListener(position));
        ((PasswordCardViewHolder)holder).itemView.setOnClickListener(new CardClickListener(position, holder.itemView));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new PasswordCardViewHolder(mInflater.inflate(R.layout.list_item_password_small, viewGroup, false));
    }



    public class PasswordCardViewHolder extends RecyclerView.ViewHolder{
        protected RobotoTextView mTitle;
        protected RobotoTextView mUsername;
        protected RobotoTextView mUrl;
        protected CircleView mAvatar;
        protected ImageButton mStar;

        public PasswordCardViewHolder(View itemView) {
            super(itemView);
            mTitle = (RobotoTextView) itemView.findViewById(R.id.password_card_title);
            mUsername = (RobotoTextView) itemView.findViewById(R.id.password_card_username);
            mUrl = (RobotoTextView) itemView.findViewById(R.id.password_card_url);

            mAvatar = (CircleView) itemView.findViewById(R.id.password_card_avatar);
            mAvatar.setSelectable(true);

            mStar = (ImageButton) itemView.findViewById(R.id.password_card_starred_button);
            if(!hasStableIds()) {
                mStar.setVisibility(View.GONE);
            }
        }

    }

    public class AvatarClickListener implements CircleView.OnCircleSelectedListener {
        private int mPosition;

        public AvatarClickListener(int position) {
            mPosition = position;
        }


        @Override
        public void onCircleSelected(CircleView v, boolean selected) {
            if(mAvatarSelectionListener != null) {
                Password password = getItem(mPosition);

                if(password != null) {
                    getItem(mPosition).setSelected(!password.isSelected());
                    mAvatarSelectionListener.onAvatarSelected(mPosition, password.isSelected());
                }

            }
        }
    }

}
