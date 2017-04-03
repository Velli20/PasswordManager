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


import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.velli.passwordmanager.adapter.PasswordCardsListAdapter;
import com.velli.passwordmanager.adapter.PasswordCardsSmallAdapter;
import com.velli.passwordmanager.database.OnGetPasswordsListener;
import com.velli.passwordmanager.database.OnPasswordsAddedListener;
import com.velli.passwordmanager.database.PasswordDatabaseHandler;

import java.io.File;
import java.util.ArrayList;

public class ActivityImportPasswords extends ActivityBase implements PasswordCardsSmallAdapter.OnAvatarSelectedListener, CompoundButton.OnCheckedChangeListener {
    public static final String INTENT_EXTRA_FILE = "intent extra file";
    private Toolbar mToolbar;
    private View mShadow;
    private CheckBox mSelectedAll;
    private RecyclerView mRecycler;
    private PasswordCardsSmallAdapter mAdapter;

    @Override
    public int getActivityId() { return ApplicationBase.ACTIVITY_IMPORT_PASSWORDS; }

    @Override
    public String getTag() { return getClass().getSimpleName(); }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_import_passwords);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSelectedAll = (CheckBox) mToolbar.findViewById(R.id.activity_import_passwords_checkbox_all);
        if(mSelectedAll != null) {
            mSelectedAll.setText("0 " + getString(R.string.title_selected));
            mSelectedAll.setOnCheckedChangeListener(this);
        }
        mShadow = findViewById(R.id.toolbar_shadow);
        mRecycler = (RecyclerView) findViewById(R.id.activity_import_passwords_list);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");

        String uri = getIntent().getStringExtra(INTENT_EXTRA_FILE);

        if(uri != null) {
            final File file = new File(uri);

            new ExcelImportPasswordsTask(file, new OnGetPasswordsListener() {
                @Override
                public void onGetPasswords(ArrayList<Password> list) {
                    mAdapter = new PasswordCardsSmallAdapter(ActivityImportPasswords.this, list);
                    mAdapter.setOnAvatarSelectedListener(ActivityImportPasswords.this);
                    mRecycler.setAdapter(mAdapter);
                }
            });
        }
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem importButton = menu.findItem(R.id.menu_import_passwords);
        if(importButton != null) {
            importButton.setVisible(mAdapter != null && mAdapter.getSelectionsCount() > 0);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_import_passwords, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_import_passwords:
                if (mAdapter == null) { return true; }

                ArrayList<Password> selections = mAdapter.getSelectedItems();

                if (selections != null) {
                    final MaterialDialog d = new MaterialDialog.Builder(this)
                            .title("Tuodaan")
                            .content("Odota hetki...")
                            .progress(true, 0)
                            .cancelable(false)
                            .show();

                    PasswordDatabaseHandler.getInstance().addNewPasswords(selections, new OnPasswordsAddedListener() {
                        @Override
                        public void onPasswordsAdded(int successfullyAddedCount, int totalCount) {
                            if(d != null) {
                                d.dismiss();
                                Toast.makeText(ActivityImportPasswords.this, successfullyAddedCount + " of " + totalCount + " was succesfully added", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }


                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAvatarSelected(int position, boolean selected) {
        int count = mAdapter.getSelectionsCount();

        if(mSelectedAll != null) {
            mSelectedAll.setText(count + " " + getString(R.string.title_selected));
            mSelectedAll.setOnCheckedChangeListener(null);
            mSelectedAll.setChecked(count == mAdapter.getItemCount() && mAdapter.getItemCount() > 0);
            mSelectedAll.setOnCheckedChangeListener(this);
            this.invalidateOptionsMenu();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch(buttonView.getId()) {
            case R.id.activity_import_passwords_checkbox_all:
                mAdapter.setAllSelected(isChecked);
                mSelectedAll.setText((isChecked ? mAdapter.getItemCount() : 0) + " " + getString(R.string.title_selected));
                this.invalidateOptionsMenu();
                break;
        }
    }
}
