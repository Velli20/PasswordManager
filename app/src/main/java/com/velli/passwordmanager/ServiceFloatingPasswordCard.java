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

package com.velli.passwordmanager;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.velli.passwordmanager.roboto.RobotoTextView;

@SuppressLint({"RtlHardcoded", "ClickableViewAccessibility"})
public class ServiceFloatingPasswordCard extends Service implements OnClickListener {
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_LOGIN_TYPE = "logintype";
    public static final String EXTRA_USERNAME = "username";
    public static final String EXTRA_PASSWORD = "password";
    public static final String Tag = "ServiceFloatingPasswordCard ";
    private static final boolean DEBUG = false;
    private final ExpandCollapseAnimator mAnimator = new ExpandCollapseAnimator();
    private final TouchListener mTouchListener = new TouchListener();
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams params;
    private View mView;
    private CardView mContainer;
    private LinearLayout mContent;
    private RobotoTextView mUserName;
    private RobotoTextView mPassword;
    private RobotoTextView mTitle;
    private ImageButton mExpand;
    private ImageButton mClose;
    private String title;
    private String username;
    private String password;
    private int loginicon;
    private boolean mExpanded = true;
    private boolean mMovable = false;
    private boolean mAnimated = false;
    private int mViewWidth = -1;
    private int mButtonWidth = -1;
    public final Runnable mR = new Runnable() {

        @Override
        public void run() {
            mExpand.setClickable(true);
            mAnimator.expand();
        }


    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("InflateParams")
    @Override
    public void onCreate() {
        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final ButtonClickListener buttonListener = new ButtonClickListener();

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.END | Gravity.RIGHT | Gravity.CENTER_VERTICAL;

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        mView = inflater.inflate(R.layout.floating_password_view, null);
        mView.setEnabled(false);

        mContainer = (CardView) mView.findViewById(R.id.floating_pass_container);

        mUserName = (RobotoTextView) mView.findViewById(R.id.floating_pass_view_username);
        mPassword = (RobotoTextView) mView.findViewById(R.id.floating_pass_view_password);
        mTitle = (RobotoTextView) mView.findViewById(R.id.floating_pass_view_description);

        mExpand = (ImageButton) mView.findViewById(R.id.floating_pass_view_expand);
        mClose = (ImageButton) mView.findViewById(R.id.floating_pass_view_close);

        mContent = (LinearLayout) mView.findViewById(R.id.floating_pass_content);

        mExpand.setOnClickListener(buttonListener);
        mExpand.setOnLongClickListener(buttonListener);
        mExpand.setOnTouchListener(buttonListener);
        mExpand.setClickable(false);

        mClose.setOnClickListener(this);

        mWindowManager.addView(mView, params);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mContainer.setCardElevation(getResources().getDimension(R.dimen.floating_password_card_elevation));
        }

        setPassword(username, password, title, loginicon);
        autoHide();

    }

    private void autoHide() {
        final Handler h = new Handler();

        h.postDelayed(mR, 1000);
    }

    private void setTouchable(boolean touchable) {
        mMovable = touchable;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            title = intent.getStringExtra(EXTRA_TITLE);
            username = intent.getStringExtra(EXTRA_USERNAME);
            password = intent.getStringExtra(EXTRA_PASSWORD);
            loginicon = intent.getIntExtra(EXTRA_LOGIN_TYPE, 0);

            setPassword(username, password, title, loginicon);
        }
        return Service.START_NOT_STICKY;
    }

    public void setPassword(String username, String password, String title, int login) {
        if (mUserName != null && mPassword != null && title != null) {
            mTitle.setText(title);
            mUserName.setText(username);
            mPassword.setText(password);


        }
    }


    private void hideInfo(boolean hide) {
        mTitle.setVisibility(hide ? View.GONE : View.VISIBLE);
        mUserName.setVisibility(hide ? View.GONE : View.VISIBLE);
        mPassword.setVisibility(hide ? View.GONE : View.VISIBLE);
        mClose.setVisibility(hide ? View.GONE : View.VISIBLE);
        mContent.setVisibility(hide ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mView != null) {
            mWindowManager.removeView(mView);
        }
    }

    public class ButtonClickListener implements OnClickListener, OnLongClickListener, OnTouchListener {

        @Override
        public void onClick(View v) {
            mAnimator.expand();
            mExpand.animate().rotation(mExpand.getRotation() < 180 ? 180 : 0).setDuration(500).start();
        }

        @Override
        public boolean onLongClick(View v) {
            setTouchable(true);
            v.setPressed(false);

            return false;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                setTouchable(false);
            }
            return mMovable && mTouchListener.onTouch(v, event);

        }

    }

    private class ExpandCollapseAnimator implements AnimatorListener {

        public void expand() {
            int value = 0;

            if (mViewWidth == -1) {
                mViewWidth = mContainer.getWidth();
                mButtonWidth = mExpand.getWidth();
            }
            if (mExpanded) {
                setTouchable(false);
                value = mViewWidth;
            } else {
                setTouchable(true);
                value = 0;
            }
            mAnimated = false;
            mExpanded = !mExpanded;
            mContainer.animate().translationX(value).setListener(this).start();
        }


        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            params.width = mExpanded ? mViewWidth : mButtonWidth;
            mWindowManager.updateViewLayout(mView, params);
            animation.removeAllListeners();

            hideInfo(!mExpanded);
            if (!mExpanded && !mAnimated) {
                mAnimated = true;
                mContainer.animate().translationX(0).setDuration(500).start();
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }


    }

    private class TouchListener implements OnTouchListener {
        private int initialY;
        private float initialTouchY = -1;

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    getPoint(event);
                    return true;
                case MotionEvent.ACTION_UP:
                    return true;
                case MotionEvent.ACTION_MOVE:
                    if (initialTouchY == -1) {
                        getPoint(event);
                        return true;
                    }
                    params.y = initialY + (int) (event.getRawY() - initialTouchY);
                    mWindowManager.updateViewLayout(mView, params);
                    return true;
            }
            return false;
        }

        private void getPoint(MotionEvent event) {
            initialY = params.y;
            initialTouchY = event.getRawY();
        }

    }


}
