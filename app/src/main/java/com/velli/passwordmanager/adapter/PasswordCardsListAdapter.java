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
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.velli.passwordmanager.Password;
import com.velli.passwordmanager.R;
import com.velli.passwordmanager.collections.NavigationDrawerConstants;
import com.velli.passwordmanager.collections.Utils;
import com.velli.passwordmanager.widget.CreditCard;
import com.velli.passwordmanager.widget.PasswordCard;
import com.velli.passwordmanager.widget.WifiPasswordCard;

import java.util.ArrayList;

public class PasswordCardsListAdapter extends PasswordCardBaseAdapter {
    private float mCardElevation;

    public PasswordCardsListAdapter(Context context, ArrayList<Password> list) {
        super(context, list);
        mCardElevation = (context.getResources().getDimension(R.dimen.password_card_elevation));
    }


    @Override
    public int getItemViewType(int position) {
        Password pass = mEntryList.get(position);
        if (pass.isCreditCard()) {
            return VIEW_TYPE_CREDIT_CARD;
        } else if (pass.isWifiPassword()) {
            return VIEW_TYPE_WIFI_PASSWORD;
        } else if (pass.getUrl() == null || pass.getUrl().isEmpty() || pass.getLoginIcon() == NavigationDrawerConstants.LABEL_APP) {
            return VIEW_TYPE_PASSWORD_NO_URL;
        } else {
            return VIEW_TYPE_PASSWORD;
        }
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final int viewType = getItemViewType(position);
        final Password entry = mEntryList.get(position);

        if (viewType == VIEW_TYPE_PASSWORD || viewType == VIEW_TYPE_PASSWORD_NO_URL) {
            ((PasswordCardViewHolder) holder).mCard.setSearchPattern(mSearchPattern);
            ((PasswordCardViewHolder) holder).mCard.setDescription(entry.getDescription());
            ((PasswordCardViewHolder) holder).mCard.setUrl(entry.getUrl());
            ((PasswordCardViewHolder) holder).mCard.setUsername(entry.getUsername());
            ((PasswordCardViewHolder) holder).mCard.setLoginIcon(entry.getLoginIcon() == -1 ? null : mRes.getDrawable(Utils.getLoginLabelIconArray()[entry.getLoginIcon()]));
            ((PasswordCardViewHolder) holder).mCard.setStarButtonDrawable(entry.isStarred() ? mStarEnabled : mStarDisabled);
            ((PasswordCardViewHolder) holder).mCard.setStarbuttonOnClickListener(new StarButtonListener(position));
            ((PasswordCardViewHolder) holder).mCard.setOnClickListener(new CardClickListener(position, holder.itemView));
        } else if (viewType == VIEW_TYPE_CREDIT_CARD) {
            ((CreditCardViewHolder) holder).mCard.setSearchPattern(mSearchPattern);
            ((CreditCardViewHolder) holder).mCard.setCardData(entry);
            ((CreditCardViewHolder) holder).mCard.setStarbuttonOnClickListener(new StarButtonListener(position));
            ((CreditCardViewHolder) holder).mCard.setOnClickListener(new CardClickListener(position, holder.itemView));
            ((CreditCardViewHolder) holder).mCard.setStarButtonDrawable(entry.isStarred() ? mStarEnabled : mStarDisabled);
        } else if (viewType == VIEW_TYPE_WIFI_PASSWORD) {
            ((WifiCardViewHolder) holder).mCard.setSearchPattern(mSearchPattern);
            ((WifiCardViewHolder) holder).mCard.setCardData(entry);
            ((WifiCardViewHolder) holder).mCard.setStarbuttonOnClickListener(new StarButtonListener(position));
            ((WifiCardViewHolder) holder).mCard.setOnClickListener(new CardClickListener(position, holder.itemView));
            ((WifiCardViewHolder) holder).mCard.setStarButtonDrawable(entry.isStarred() ? mStarEnabled : mStarDisabled);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView;

        if (viewType == VIEW_TYPE_PASSWORD) {
            itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_password_card, viewGroup, false);
            ((PasswordCard) itemView).setCardElevation(mCardElevation);
            return new PasswordCardViewHolder(itemView);
        } else if (viewType == VIEW_TYPE_PASSWORD_NO_URL) {
            itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_password_card_no_url, viewGroup, false);
            ((PasswordCard) itemView).setCardElevation(mCardElevation);
            return new PasswordCardViewHolder(itemView);
        } else if (viewType == VIEW_TYPE_WIFI_PASSWORD) {
            itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_wifi_password_card, viewGroup, false);
            ((WifiPasswordCard) itemView).setCardElevation(mCardElevation);
            return new WifiCardViewHolder(itemView);
        } else {
            itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_credit_card, viewGroup, false);
            ((CreditCard) itemView).setCardElevation(mCardElevation);
            return new CreditCardViewHolder(itemView);
        }

    }


    public class CreditCardViewHolder extends RecyclerView.ViewHolder {
        protected CreditCard mCard;

        public CreditCardViewHolder(View itemView) {
            super(itemView);
            mCard = (CreditCard) itemView;
        }

    }

    public class WifiCardViewHolder extends RecyclerView.ViewHolder {
        protected WifiPasswordCard mCard;

        public WifiCardViewHolder(View itemView) {
            super(itemView);
            mCard = (WifiPasswordCard) itemView;
        }

    }

    public class PasswordCardViewHolder extends RecyclerView.ViewHolder {
        protected PasswordCard mCard;

        public PasswordCardViewHolder(View itemView) {
            super(itemView);
            mCard = (PasswordCard) itemView;
        }

    }

}
