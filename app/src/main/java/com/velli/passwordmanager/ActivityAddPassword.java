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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.velli.passwordmanager.database.Constants;
import com.velli.passwordmanager.database.OnDatabaseEditedListener;
import com.velli.passwordmanager.database.OnGetPasswordListener;
import com.velli.passwordmanager.database.PasswordDatabaseHandler;

public class ActivityAddPassword extends ActivityBase implements OnGetPasswordListener, OnDatabaseEditedListener {
    public static final String INTENT_EXTRA_ROW_ID = "entry to edit id";
    private static final String TAG = "ActivityAddPassword ";
    private Toolbar mToolbar;
    private NewPasswordBase mEntryBase;

    private int mEntryToEdit;
    private boolean mIsInEditMode = false;

    @Override
    public int getActivityId() {
        return ApplicationBase.ACTIVITY_ADD_PASSWORD;
    }

    @Override
    public String getTag() {
        return getClass().getSimpleName();
    }

    @Override
    public boolean implementsOnDatabaseEditedListener() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle args = getIntent().getExtras();

        if (args != null) {
            mEntryToEdit = args.getInt(INTENT_EXTRA_ROW_ID, -1);
        } else {
            mEntryToEdit = -1;
        }

        mIsInEditMode = mEntryToEdit != -1;

        if (mEntryBase == null) {
            mEntryBase = new NewPasswordBase();
            mEntryBase.setInEditMode(mIsInEditMode);
            if (!mIsInEditMode) {
                mEntryBase.createBlankPassword();
            }
        }
        setContentView(R.layout.activity_add_password);

        final View v = findViewById(android.R.id.content);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        initActionBar();

        mEntryBase.init(v, this);

    }

    public void onResume() {
        super.onResume();
        final PasswordDatabaseHandler db = PasswordDatabaseHandler.getInstance();

        if (mIsInEditMode && db.isDatabaseOpen()) {
            db.getPassword(mEntryToEdit, this);
        } else if (!mIsInEditMode && db.isDatabaseOpen() && mEntryBase != null) {
            mEntryBase.setData(null);
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mEntryBase = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_entry_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save_entry:
                if (mEntryBase.save()) {
                    final InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(findViewById(android.R.id.content).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    finish();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initActionBar() {
        final ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayShowHomeEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setDisplayShowTitleEnabled(true);
            bar.setTitle(getString(mIsInEditMode ? R.string.title_edit_password : R.string.title_new_password));
        }

        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_cancel));
        mToolbar.setNavigationOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mEntryBase.checkIfEditsWasMade() && !mEntryBase.isDiscarded()) {
                    showExitDialog();
                } else {
                    final InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputManager != null) {
                        inputManager.hideSoftInputFromWindow(findViewById(android.R.id.content).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                    finish();
                }

            }
        });

    }

    @Override
    public void onBackPressed() {
        if (mEntryBase == null) {
            super.onBackPressed();
        } else if (mEntryBase.isTextFieldFocused()) {
            mEntryBase.unfocusAllTextFields();
        } else if (mEntryBase.checkIfEditsWasMade() && !mEntryBase.isDiscarded()) {
            showExitDialog();
        } else if (!mEntryBase.isTextFieldFocused()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onGetPassword(Password entry) {
        if (DEBUG) {
            Log.i(TAG, TAG + " onGetPassword()");
        }
        if (entry != null) {
            mEntryBase.setOriginalPasswordForm(entry);
            mEntryBase.setData(entry);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NewPasswordBase.REQUEST_CODE_PASSWORD_GENERATION && resultCode == Activity.RESULT_OK) {
            if (mEntryBase != null) {
                mEntryBase.passwordGenerated(data.getStringExtra(NewPasswordBase.INTENT_DATA_PASSWORD));
            }
        }
    }

    @Override
    public void onDatabaseHasBeenEdited(String tablename, long rowid) {
        if (Constants.TABLE_GROUPS.equals(tablename) && mEntryBase != null) {
            mEntryBase.initGroupSpinner();
        }

    }

    private void showExitDialog() {
        new MaterialDialog.Builder(this)
                .content(mIsInEditMode ? R.string.title_leave_without_saving : R.string.title_discard_password)
                .positiveText(R.string.action_ok)
                .negativeText(R.string.action_cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        mEntryBase.discard();
                        final InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (inputManager != null) {
                            inputManager.hideSoftInputFromWindow(findViewById(android.R.id.content).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                        finish();
                    }
                })

                .show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (DEBUG) {
            Log.i(TAG, "onSaveInstanceState()");
        }
        if (mEntryBase != null) {
            mEntryBase.onSaveInstanceState(outState);
        }
    }
}
