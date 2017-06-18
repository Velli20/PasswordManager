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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.velli.passwordmanager.R;
import com.velli.passwordmanager.roboto.RobotoTextView;
import com.velli.passwordmanager.widget.WelcomeView.OnAnimationCompleteListener;

public class NoPasswordsView extends FrameLayout {
    public static final String BUNDLE_KEY_IS_OPEN = "is open";
    public static final String BUNDLE_KEY_TITLE = "current title";
    private static final int ANIMATION_DURATION = 300;
    private RobotoTextView mNoPasswordsText;
    private String mSearchPattern;
    private String mCategory;
    private String mGroup;
    private String mCurrentTitle;

    private boolean mVisible = false;

    public NoPasswordsView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public NoPasswordsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public NoPasswordsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO Auto-generated constructor stub
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NoPasswordsView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();

        mNoPasswordsText = (RobotoTextView) findViewById(R.id.view_no_passwords_text);

        if (getVisibility() == View.GONE || getVisibility() == View.INVISIBLE) {
            mVisible = false;
        } else {
            show(true, null);
        }
    }

    public NoPasswordsView setSearchPattern(String pattern) {
        mSearchPattern = pattern;
        return this;
    }

    public NoPasswordsView setCategroy(String category) {
        mCategory = category;
        return this;
    }

    public NoPasswordsView setGroup(String group) {
        mGroup = group;
        return this;
    }

    public void show(boolean animate, OnAnimationCompleteListener l) {
        final Resources res = getResources();
        final String title;

        if (mSearchPattern != null) {
            title = res.getString(R.string.title_no_items_found_1)
                    + mSearchPattern
                    + res.getString(R.string.title_no_items_found_2);
        } else if (mCategory != null) {
            title = res.getString(R.string.title_no_items_in_category)
                    + " " + mCategory;
        } else if (mGroup != null) {
            title = res.getString(R.string.title_no_items_in_group)
                    + " " + mGroup;
        } else {
            title = res.getString(R.string.title_no_passwords);
        }

        mNoPasswordsText.setText(title);

        mSearchPattern = null;
        mCategory = null;
        mGroup = null;

        if (!animate) {
            mVisible = true;
            setVisibility(View.VISIBLE);
            if (l != null) {
                l.onAnimationComplete();
            }
            return;
        } else {
            showOrHide(true, l);
        }
    }

    public void hide(boolean animate, OnAnimationCompleteListener l) {
        if (!mVisible) {
            return;
        }

        if (!animate) {
            mVisible = false;
            setVisibility(View.GONE);
            if (l != null) {
                l.onAnimationComplete();
            }
            return;
        }
        showOrHide(false, l);
    }

    public void onSaveInstanceState(Bundle outState) {
        if (outState == null) {
            return;
        }

        outState.putString(BUNDLE_KEY_TITLE, mCurrentTitle == null ? "" : mCurrentTitle);
        outState.putBoolean(BUNDLE_KEY_IS_OPEN, mVisible);
    }

    public void onRestoreSavedInstanceState(Bundle savedInstanceState) {
        mNoPasswordsText.setText(savedInstanceState.getString(BUNDLE_KEY_TITLE));
        mVisible = savedInstanceState.getBoolean(BUNDLE_KEY_IS_OPEN);
        setVisibility(mVisible ? View.VISIBLE : View.GONE);
    }

    private void showOrHide(final boolean show, final OnAnimationCompleteListener l) {
        if (mVisible == show) {
            return;
        }

        mVisible = show;
        final HardwareAccelerateListener listener = new HardwareAccelerateListener(this);
        listener.addAnimatorListenerAdapter(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                if (show) {
                    setVisibility(View.VISIBLE);
                }
                if (l != null) {
                    l.onAnimationComplete();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!show) {
                    setVisibility(View.GONE);
                }
                if (l != null) {
                    l.onAnimationComplete();
                }
            }
        });

        setAlpha(show ? 0 : 1);
        animate().alpha(show ? 1 : 0).setDuration(ANIMATION_DURATION).setListener(listener).start();
    }
}
