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

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.velli.passwordmanager.CreditCardInfo;
import com.velli.passwordmanager.Password;
import com.velli.passwordmanager.R;
import com.velli.passwordmanager.collections.Utils;
import com.velli.passwordmanager.roboto.RobotoTextView;

public class CreditCard extends CardView {
    private RobotoTextView mDescription;
    private RobotoTextView mCardNumber;
    private RobotoTextView mExpirationDate;

    private RobotoTextView mTitleCardNumber;
    private RobotoTextView mTitleExpirationDate;

    private ImageButton mStar;
    private ImageView mIcon;
    private String mSearchPattern = "";

    public CreditCard(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public CreditCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }


    public CreditCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    public static String formatCreditCardNumber(String number) {
        final StringBuilder result = new StringBuilder();
        final int lenght = number.length();

        for (int i = 0; i < lenght; i++) {
            char charAt = number.charAt(i);

            if (i == 4 && charAt != ' ') {
                result.append(" ");
            } else if (i % 4 == 0 && i != 0 && charAt != ' ') {
                result.append(" ");
            }

            result.append(number.charAt(i));
        }
        return result.toString();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mDescription = (RobotoTextView) findViewById(R.id.credit_card_title);
        mCardNumber = (RobotoTextView) findViewById(R.id.credit_card_number);
        mExpirationDate = (RobotoTextView) findViewById(R.id.credit_card_expiration_date);
        mStar = (ImageButton) findViewById(R.id.credit_card_starred_button);

        mTitleCardNumber = (RobotoTextView) findViewById(R.id.credit_card_number_title);
        mTitleExpirationDate = (RobotoTextView) findViewById(R.id.credit_card_expiration_date_title);

        mIcon = (ImageView) findViewById(R.id.credit_card_icon);
    }

    public void setCardData(Password pass) {
        if (pass == null || pass.getCreditCard() == null) {
            return;
        }

        final CreditCardInfo card = pass.getCreditCard();

        if (mSearchPattern != null && !mSearchPattern.isEmpty() && !pass.getDescription().isEmpty()) {
            mDescription.setText(Utils.boldString(pass.getDescription(), mSearchPattern));
        } else {
            mDescription.setText(pass.getDescription());
        }
        if (mSearchPattern != null && !mSearchPattern.isEmpty() && !card.getCardNumber().isEmpty()) {
            mCardNumber.setText(Utils.boldString(formatCreditCardNumber(card.getCardNumber()), mSearchPattern));
        } else {
            mCardNumber.setText(formatCreditCardNumber(card.getCardNumber()));
        }

        mExpirationDate.setText(card.getCardExpirationDate());
    }

    public void setStarbuttonOnClickListener(OnClickListener l) {
        mStar.setOnClickListener(l);
    }

    public void setStarButtonDrawable(Drawable d) {
        mStar.setImageDrawable(d);
    }

    public void setSearchPattern(String pattern) {
        mSearchPattern = pattern;
    }
}
