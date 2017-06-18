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
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.velli.passwordmanager.adapter.PasswordOptionsAdapter;
import com.velli.passwordmanager.collections.ListItemPasswordOption;
import com.velli.passwordmanager.collections.Utils;
import com.velli.passwordmanager.listeners.OnPasswordOptionClickListener;
import com.velli.passwordmanager.roboto.RobotoTextView;
import com.velli.passwordmanager.widget.setPasswordLengthView;

import java.util.ArrayList;

public class ActivityGeneratePassword extends ActivityBase implements OnClickListener, OnItemClickListener, OnPasswordOptionClickListener {
    private PasswordOptionsAdapter mAdapter;
    private RobotoTextView mPassWord;

    private String mPasswordString = "";

    @Override
    public int getActivityId() {
        return ApplicationBase.ACTIVITY_GENERATE_PASSWORD;
    }

    @Override
    public String getTag() {
        return getClass().getSimpleName();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_layout_password_generation);

        final Resources res = getResources();
        final String[] options = res.getStringArray(R.array.password_generation_options);
        final ArrayList<ListItemPasswordOption> list = new ArrayList<>();

        int length = options.length;

        for (int i = 0; i < length; i++) {
            ListItemPasswordOption item = new ListItemPasswordOption();
            item.setTitle(options[i]);
            item.setBooleanValue(true);

            if (i == 0) {
                item.setViewType(PasswordOptionsAdapter.VIEW_TYPE_TITLE);
            } else if (i < length - 1) {
                item.setViewType(PasswordOptionsAdapter.VIEW_TYPE_CHECKBOX);
            } else {
                item.setIntValue(11);
                item.setViewType(PasswordOptionsAdapter.VIEW_TYPE_BUTTON);
            }
            list.add(item);
        }

        mAdapter = new PasswordOptionsAdapter(this, list);
        mAdapter.setOnPasswordOptionClickListener(this);

        ListView mOptionsList = (ListView) findViewById(R.id.password_generation_options_list);
        mOptionsList.setAdapter(mAdapter);
        mOptionsList.setOnItemClickListener(this);

        mPasswordString = generateRandomString();
        mPassWord = (RobotoTextView) findViewById(R.id.password_generation_password);
        mPassWord.setText(mPasswordString);

        ImageButton mRefresh = (ImageButton) findViewById(R.id.password_generation_button_refresh);
        mRefresh.setOnClickListener(this);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);


        final ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayShowHomeEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setDisplayShowTitleEnabled(true);
            bar.setTitle(getString(R.string.title_generate_password));
        }
        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.generate_password_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_save_password:
                Intent data = new Intent();
                data.putExtra(NewPasswordBase.INTENT_DATA_PASSWORD, mPasswordString);

                setResult(Activity.RESULT_OK, data);
                finish();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mAdapter.getItem(position).getViewType() == PasswordOptionsAdapter.VIEW_TYPE_CHECKBOX) {
            final CheckBox ch = (CheckBox) view.findViewById(R.id.password_generation_list_item_checkBox);
            if (ch != null) {
                ch.performClick();
            }
        } else if (mAdapter.getItem(position).getViewType() == PasswordOptionsAdapter.VIEW_TYPE_BUTTON) {
            final Button btn = (Button) view.findViewById(R.id.password_generation_list_item_button);
            if (btn != null) {
                btn.performClick();
            }
        }

    }

    @SuppressLint("InflateParams")
    @Override
    public void onPasswordOptionClick(int viewType, View v, final int position) {
        if (viewType == PasswordOptionsAdapter.VIEW_TYPE_BUTTON) {
            final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final setPasswordLengthView s = (setPasswordLengthView) inflater.inflate(R.layout.view_password_length_selector, null);

            s.setValue(mAdapter.getItem(position).getIntValue());

            new MaterialDialog.Builder(this)
                    .title(getString(R.string.title_set_password_lenght))
                    .customView(s, false)
                    .positiveText(R.string.action_ok)
                    .negativeText(R.string.action_cancel)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                            mAdapter.getItem(position).setIntValue(s.getValue());
                            mAdapter.notifyDataSetChanged();
                        }
                    })

                    .build()
                    .show();


        }

    }

    private String generateRandomString() {
        if (mAdapter == null) {
            return "";
        }
        final ArrayList<ListItemPasswordOption> list = mAdapter.getItemsList();
        final StringBuilder tmp = new StringBuilder();
        final int passwordLenght = list.get(5).getIntValue();
        boolean generate = false;

        for (int i = 1; i < 5; i++) {
            if (list.get(i).getBooleanValue()) {
                tmp.append(Utils.PASSWORD_VALID_CHARACHTERS[i - 1]);
                generate = true;
            }
        }

        if (!generate || passwordLenght < 1) {
            if (!generate) {
                Toast.makeText(this, "Select atleast one option!", Toast.LENGTH_LONG).show();
            }
            return "";
        }
        final Utils.RandomStringGenerator gen = new Utils.RandomStringGenerator(passwordLenght, tmp.toString().toCharArray());

        return gen.nextString();
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.password_generation_button_refresh) {
            animatePasswordChange();
        }

    }

    public void animatePasswordChange() {
        final PropertyValuesHolder progfirst = PropertyValuesHolder.ofFloat("rotationX", 0, 180);


        final ObjectAnimator progAnim = ObjectAnimator.ofPropertyValuesHolder(mPassWord, progfirst);
        progAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        progAnim.setDuration(100);
        progAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mPasswordString = generateRandomString();
                mPassWord.setText(mPasswordString);

                final PropertyValuesHolder progsecond = PropertyValuesHolder.ofFloat("rotationX", 180, 360);
                final ObjectAnimator progAnim = ObjectAnimator.ofPropertyValuesHolder(mPassWord, progsecond);

                progAnim.setInterpolator(new AccelerateDecelerateInterpolator());
                progAnim.setDuration(100);
                progAnim.start();
            }
        });
        progAnim.start();
    }


}
