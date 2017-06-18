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
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

public class HardwareAccelerateListener extends AnimatorListenerAdapter implements AnimationListener {
    private View mView;
    private AnimatorListenerAdapter mListener;

    public HardwareAccelerateListener(View v) {
        mView = v;
    }

    public void addAnimatorListenerAdapter(AnimatorListenerAdapter l) {
        mListener = l;
    }

    @Override
    public void onAnimationStart(Animator animation) {
        if (mView != null) {
            mView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        if (mListener != null) {
            mListener.onAnimationStart(animation);
        }
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if (mView != null) {
            mView.setLayerType(View.LAYER_TYPE_NONE, null);
        }

        if (mListener != null) {
            mListener.onAnimationEnd(animation);
        }

        mView = null;
        mListener = null;
    }

    @Override
    public void onAnimationStart(Animation animation) {
        onAnimationStart(((Animator) null));

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        onAnimationEnd(((Animator) null));

    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        // TODO Auto-generated method stub

    }
}