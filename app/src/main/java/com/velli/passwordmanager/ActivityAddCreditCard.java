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


import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.velli.passwordmanager.database.Constants;
import com.velli.passwordmanager.database.OnGetPasswordListener;
import com.velli.passwordmanager.database.PasswordDatabaseHandler;

public class ActivityAddCreditCard extends ActivityBase {
    public static final String INTENT_EXTRA_ROW_ID = "entry to edit id";

    private NewCreditCardBase mBase;

    private int mCardRowId = -1;
    private boolean mIsInEditMode = false;

    @Override
    public int getActivityId() {
        return ApplicationBase.ACTIVITY_ADD_CREDIT_CARD;
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
            mCardRowId = args.getInt(INTENT_EXTRA_ROW_ID, -1);
        }

        mIsInEditMode = mCardRowId != -1;

        setContentView(R.layout.activity_add_credit_card);

        mBase = new NewCreditCardBase(this);
        mBase.setView(findViewById(android.R.id.content));

        if (mIsInEditMode) {
            PasswordDatabaseHandler.getInstance().getPassword(mCardRowId, new OnGetPasswordListener() {

                @Override
                public void onGetPassword(Password entry) {
                    if (entry != null) {
                        mBase.setData(entry);
                    }

                }
            });
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initActionBar();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.new_entry_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save_entry:
                if (!mBase.checkForErrors()) {
                    mBase.save();

                } else {
                    return true;
                }
            case android.R.id.home:
                final InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(findViewById(android.R.id.content).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                finish();
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
            bar.setTitle(getString(mIsInEditMode ? R.string.title_edit_credit_card : R.string.title_new_credit_card));
        }

    }

    @Override
    public void onDatabaseHasBeenEdited(String tablename, long rowid) {
        if (Constants.TABLE_GROUPS.equals(tablename) && mBase != null) {
            mBase.initGroupSpinner();
        }

    }
}
