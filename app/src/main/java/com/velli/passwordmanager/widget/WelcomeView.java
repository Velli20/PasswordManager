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
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.velli.passwordmanager.R;
import com.velli.passwordmanager.roboto.RobotoTextView;

public class WelcomeView extends RelativeLayout {
    private static final int ANIMATION_DURATION = 300;
    private RobotoTextView mHello;
    private RobotoTextView mTapToAdd;
    private RobotoTextView mInfo;

    private boolean mVisible = false;

    public WelcomeView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public WelcomeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public WelcomeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO Auto-generated constructor stub
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public WelcomeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        mHello = (RobotoTextView) findViewById(R.id.welcome_view_hello);
        mTapToAdd = (RobotoTextView) findViewById(R.id.welcome_view_tap_to_add);
        mInfo = (RobotoTextView) findViewById(R.id.welcome_view_info);

        if (getVisibility() == View.GONE || getVisibility() == View.INVISIBLE) {
            mVisible = false;
        } else {
            show(true, null);
        }
    }

    public boolean isVisible() {
        return mVisible;
    }

    public void show(boolean animate, OnAnimationCompleteListener l) {
        if (mVisible) {
            return;
        }

        if (!animate) {
            setVisibility(View.VISIBLE);
            if (l != null) {
                l.onAnimationComplete();
            }
            return;
        }
        showOrHide(true, l);
    }

    public void hide(boolean animate, OnAnimationCompleteListener l) {
        if (!mVisible) {
            return;
        }

        if (!animate) {
            setVisibility(View.GONE);
            if (l != null) {
                l.onAnimationComplete();
            }
            return;
        }
        showOrHide(false, l);
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

    public interface OnAnimationCompleteListener {
        void onAnimationComplete();
    }

}
