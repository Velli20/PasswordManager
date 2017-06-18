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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ListView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.velli.passwordmanager.adapter.ManageGroupsFragmentListAdapter;
import com.velli.passwordmanager.adapter.ManageGroupsFragmentListAdapter.ManageGroupsFragmentAdapterCallback;
import com.velli.passwordmanager.collections.ListItemGroup;
import com.velli.passwordmanager.database.Constants;
import com.velli.passwordmanager.database.OnGetGroupsListTaskListener;
import com.velli.passwordmanager.database.PasswordDatabaseHandler;
import com.velli.passwordmanager.widget.DialogTheme;
import com.velli.passwordmanager.widget.NewGroupView;

import java.util.ArrayList;

public class ActivityManageGroups extends ActivityBase implements OnClickListener, OnGetGroupsListTaskListener, ManageGroupsFragmentAdapterCallback, OnItemClickListener {

    private static final String Tag = "ManageGroupsFragment ";
    private static final boolean DEBUG = false;

    private ListView mList;
    private ManageGroupsFragmentListAdapter mAdapter;
    private ContextualToolbarCallback mContextualCallback;
    private ActionMode mActionMode;
    private MaterialDialog mDialog;
    private Toolbar mToolbar;
    private boolean mLogOutAutomatically = true;

    @Override
    public int getActivityId() {
        return ApplicationBase.ACTIVITY_MANAGE_GROUPS;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        setContentView(R.layout.activity_manage_groups);
        mList = (ListView) findViewById(R.id.manage_groups_fragment_list);
        mList.setOnItemClickListener(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        final ActionBar bar = getSupportActionBar();

        mLogOutAutomatically = prefs.getBoolean(getString(R.string.preference_key_log_out_automatically), true);


        FloatingActionButton add = (FloatingActionButton) findViewById(R.id.manage_groups_add);
        add.setOnClickListener(this);

        if (bar != null) {
            bar.setDisplayShowHomeEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setDisplayShowTitleEnabled(true);
            bar.setTitle(getString(R.string.title_groups));
        }
        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });


        if (mAdapter != null) {
            mList.setAdapter(mAdapter);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        final PasswordDatabaseHandler db = PasswordDatabaseHandler.getInstance();

        if (db.isDatabaseOpen()) {
            getItems();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mLogOutAutomatically && mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.manage_groups_add) {
            createNewGroup();
        }

    }

    @SuppressLint("InflateParams")
    public void createNewGroup() {
        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final NewGroupView v = (NewGroupView) inflater.inflate(R.layout.dialog_layout_add_new_group, null);

        v.setTheme(DialogTheme.Dark);

        new MaterialDialog.Builder(this)
                .title(R.string.title_add_new_group)
                .customView(v, false)
                .theme(Theme.DARK)
                .negativeText(R.string.action_cancel)
                .positiveText(R.string.action_create_new_group)
                .autoDismiss(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        if (v.checkIfNameIsValid()) {
                            final String name = v.getGroupName();
                            PasswordDatabaseHandler.getInstance().addNewGroup(name);
                            materialDialog.dismiss();
                        }
                    }
                })
                .build()
                .show();

    }

    private void deleteGroups() {
        if (mAdapter != null) {
            final ArrayList<ListItemGroup> list = mAdapter.getItems();

            mDialog = new MaterialDialog.Builder(this)
                    .content(composeRemoveDialogMessage(list))
                    .theme(Theme.DARK)
                    .positiveText(R.string.action_ok)
                    .negativeText(R.string.action_cancel)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                            final int size = list.size() - 1;
                            final PasswordDatabaseHandler db = PasswordDatabaseHandler.getInstance();

                            for (int i = size; i > 0; i--) {
                                final ListItemGroup group = list.get(i);
                                if (group.checked) {
                                    db.deleteValue(group.rowId, false);
                                    Integer rowids[] = group.passwordsRowIds.toArray(new Integer[group.passwordsRowIds.size()]);
                                    db.deletePasswords(rowids);
                                }

                            }
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                            mDialog = null;
                            uncheckAllItems();
                        }
                    })
                    .show();

        }
        setDeleteVisible(false);
    }

    private SpannableString composeRemoveDialogMessage(ArrayList<ListItemGroup> list) {
        final int size = list.size() - 1;
        final SpannableStringBuilder builder = new SpannableStringBuilder();
        final Resources r = getResources();
        int checkedCount = 0;

        for (int i = size; i > 0; i--) {
            final ListItemGroup group = list.get(i);
            if (group.checked) {
                checkedCount++;
                final ArrayList<String> selectedPasswords = group.passwordsTitles;
                final int selectedPasswordCount = selectedPasswords.size();

                for (int y = 0; y < selectedPasswordCount; y++) {
                    final StyleSpan italicSpan = new StyleSpan(Typeface.BOLD);
                    String title = selectedPasswords.get(y);
                    if (builder.toString().length() == 0) {
                        builder.append("\n");
                    }
                    builder.append("\n- ").append(title);
                    builder.setSpan(italicSpan, builder.length() - title.length(), builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }

        boolean passwordsSelected = builder.toString().isEmpty();

        builder.insert(0, checkedCount > 1 ? r.getString(!passwordsSelected ? R.string.title_remove_group_with_passwords : R.string.title_remove_groups)
                : r.getString(!passwordsSelected ? R.string.title_remove_group_with_passwords : R.string.title_remove_group));

        return new SpannableString(builder);
    }

    @Override
    public void onDatabaseHasBeenEdited(String tablename, long rowid) {
        if (Constants.TABLE_GROUPS.equals(tablename)) {
            uncheckAllItems();
            getItems();
        }

    }

    private void getItems() {
        if (DEBUG) {
            Log.i(Tag, Tag + "getItems()");
        }
        PasswordDatabaseHandler.getInstance().getGroupsList(this);
    }

    @Override
    public void onGetValuesCount(@NonNull ArrayList<ListItemGroup> listgroup) {
        if (DEBUG) {
            Log.i(Tag, Tag + "onGetValuesCount()");
        }
        final Resources res = getResources();
        final ListItemGroup title = new ListItemGroup();
        final ArrayList<ListItemGroup> list = new ArrayList<>();

        title.primary = res.getString(R.string.title_groups);
        title.viewType = ManageGroupsFragmentListAdapter.VIEW_TYPE_TITLE;
        list.add(title);
        list.addAll(listgroup);

        if (mAdapter == null) {
            mAdapter = new ManageGroupsFragmentListAdapter(this, list);
            mAdapter.setManageGroupsFragmentAdapterCallback(this);
            mList.setAdapter(mAdapter);
        } else {
            mAdapter.setListItems(list);
            mAdapter.notifyDataSetChanged();
        }

    }


    @Override
    public void onCheckboxChecked() {
        checkIfItemsAreChecked();

    }

    private void checkIfItemsAreChecked() {
        if (mAdapter != null) {
            final ArrayList<ListItemGroup> list = mAdapter.getItems();
            if (getCheckedItemsCount(list) > 0) {
                setDeleteVisible(true);
            } else {
                setDeleteVisible(false);
            }
        }
    }

    private int getCheckedItemsCount(@NonNull ArrayList<ListItemGroup> list) {
        final int size = list.size() - 1;
        int count = 0;

        for (int i = size; i > 0; i--) {
            final ListItemGroup group = list.get(i);
            if (group.checked) {
                count++;
            }
        }
        return count;
    }

    private void setDeleteVisible(boolean visible) {
        if (visible) {
            if (mContextualCallback == null || mActionMode == null) {
                mContextualCallback = new ContextualToolbarCallback();
                mActionMode = mToolbar.startActionMode(mContextualCallback);
            } else {
                mActionMode.invalidate();
            }
        } else {
            mActionMode.finish();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final CheckBox c = (CheckBox) view.findViewById(R.id.list_item_group_checkbox);
        if (c != null) {
            c.performClick();
        }

    }

    private void uncheckAllItems() {
        ArrayList<ListItemGroup> list = mAdapter.getItems();
        for (ListItemGroup l : list) {
            l.checked = false;
        }
        mAdapter.notifyDataSetChanged();
    }

    private class ContextualToolbarCallback implements ActionMode.Callback {
        private boolean mDelete = false;

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mDelete = false;
            mode.getMenuInflater().inflate(R.menu.manage_groups_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            final ArrayList<ListItemGroup> list = mAdapter.getItems();
            mode.setTitle(String.valueOf(getCheckedItemsCount(list)));
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.menu_remove_group) {
                mDelete = true;
                deleteGroups();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mContextualCallback = null;
            mActionMode = null;
            if (!mDelete) {
                uncheckAllItems();
            }
        }

    }

}
