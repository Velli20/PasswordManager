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
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.velli.passwordmanager.database.OnGetGroupsListener;
import com.velli.passwordmanager.database.OnGetLastLoginDateListener;
import com.velli.passwordmanager.database.PasswordDatabaseHandler;
import com.velli.passwordmanager.roboto.RobotoTextView;
import com.velli.passwordmanager.widget.DialogTheme;
import com.velli.passwordmanager.widget.NewGroupView;

import java.util.ArrayList;

public class ActivityMain extends ActivityBase implements OnSharedPreferenceChangeListener {
    public static final int REQUEST_RECREATE = 45;
    private static final String SAVED_INSTANCE_STATE_KEY_DRAWER_LOCKED = "drawer locked";
    private OnBackPressedListener mBackCallback;
    private NavigationView mNavigationDrawerFragment;

    private boolean mRecreateActivity = false;
    private boolean mIsSw600dp = false;

    private Toolbar mToolbar;
    private ActionBarDrawerToggle mToolbarDrawerToggle;
    private View mShadow;

    @SuppressLint("InflateParams")
    public static void createNewGroup(final Context c, boolean darkTheme) {
        final LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final NewGroupView v = (NewGroupView) inflater.inflate(R.layout.dialog_layout_add_new_group, null);

        v.setTheme(darkTheme ? DialogTheme.Dark : DialogTheme.Light);
        new MaterialDialog.Builder(c)
                .title(R.string.title_add_new_group)
                .customView(v, false)
                .negativeText(R.string.action_cancel)
                .positiveText(R.string.action_create_new_group)
                .theme(darkTheme ? Theme.DARK : Theme.LIGHT)
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
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        materialDialog.dismiss();
                    }
                })
                .build()
                .show();
    }

    @Override
    public int getActivityId() {
        return ApplicationBase.ACTIVITY_MAIN;
    }

    @Override
    public String getTag() {
        return getClass().getSimpleName();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final PasswordDatabaseHandler mDb = PasswordDatabaseHandler.getInstance();

        setContentView(R.layout.activity_main);

        if (findViewById(R.id.drawer_layout) == null) {
            mIsSw600dp = true;
        }

        if (!mDb.isDatabaseOpen()) {
            logOut();
            return;
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mShadow = findViewById(R.id.toolbar_shadow);

        setSupportActionBar(mToolbar);

        mNavigationDrawerFragment = (NavigationView) findViewById(R.id.navigation_drawer);
        initDrawer();
        setupDrawerGroups();

        if (savedInstanceState != null) {
            boolean drawerLocked = savedInstanceState.getBoolean(SAVED_INSTANCE_STATE_KEY_DRAWER_LOCKED, false);
            lockDrawer(drawerLocked, false);
        }

        if (savedInstanceState != null) {
            restoreFragmentsFromSavedInstanceState(savedInstanceState);
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new FragmentPasswords(), FragmentPasswords.TAG).commit();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setShadowVisible(false);
        }
    }

    public boolean isSw600dpLayout() {
        return mIsSw600dp;
    }

    public boolean lockDrawer(final boolean lock, boolean animate) {
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        boolean locked = isDrawerLocked();

        if (drawer != null && lock != locked) {
            if (lock) {
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            } else {
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                if (!animate) {
                    initDrawer();
                }
            }

            if (animate) {
                final ValueAnimator anim = ValueAnimator.ofFloat(lock ? 0 : 1, lock ? 1 : 0);
                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        float slideOffset = (Float) valueAnimator.getAnimatedValue();
                        mToolbarDrawerToggle.onDrawerSlide(drawer, slideOffset);
                    }
                });
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        anim.removeAllUpdateListeners();
                        if (!lock) {
                            initDrawer();
                        }
                        animation.removeAllListeners();
                    }
                });
                anim.setInterpolator(new DecelerateInterpolator());
                anim.setDuration(500);
                anim.start();
            } else {
                mToolbarDrawerToggle.onDrawerSlide(drawer, lock ? 1 : 0);

            }
            return true;
        }
        return false;
    }

    public boolean isDrawerLocked() {
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer == null) {
            return false;
        }
        return drawer.getDrawerLockMode(GravityCompat.START) == DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
    }

    public void initDrawer() {
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            mToolbarDrawerToggle = new ActionBarDrawerToggle(this, drawer, mToolbar
                    , R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(mToolbarDrawerToggle);
            mToolbarDrawerToggle.syncState();

        }
    }

    public void setupDrawerGroups() {
        PasswordDatabaseHandler.getInstance().getLastLoginDate(new OnGetLastLoginDateListener() {
            @Override
            public void onLastLoginDate(long millis) {
                if (mNavigationDrawerFragment != null && mNavigationDrawerFragment.getHeaderView(0) != null) {
                    RobotoTextView text = (RobotoTextView)
                            mNavigationDrawerFragment.getHeaderView(0).findViewById(R.id.navigation_drawer_header_last_login);
                    if (text != null) {
                        text.setText(Utils.getDateTimeString(millis));
                    }
                }
            }
        });
        PasswordDatabaseHandler.getInstance().getGroups(new OnGetGroupsListener() {
            @Override
            public void onGetValues(ArrayList<String> list) {
                if (mNavigationDrawerFragment != null
                        && mNavigationDrawerFragment.getMenu() != null
                        && list != null) {
                    Menu menu = mNavigationDrawerFragment.getMenu().findItem(R.id.navigation_item_custom_group).getSubMenu();
                    int i = 0;
                    MenuItem item;
                    for (String group : list) {
                        item = menu.add(R.id.navigation_group_groups, R.id.navigation_item_custom_group, i, group);
                        item.setIcon(R.drawable.ic_type_group);
                        i++;
                    }
                }
            }
        });

    }

    public void toolbarShowBackButton(final boolean show, final View.OnClickListener backListener) {

        if (show) {
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (backListener != null) {
                        backListener.onClick(v);
                    }
                    mToolbar.setNavigationOnClickListener(null);
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        ApplicationBase.logoutAutomatically(prefs.getBoolean(getString(R.string.preference_key_log_out_automatically), true));

        prefs.registerOnSharedPreferenceChangeListener(this);
    }


    public NavigationView getNavigationDrawer() {
        return mNavigationDrawerFragment;
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    public ActionBarDrawerToggle getActionBarDrawerToggle() {
        return mToolbarDrawerToggle;
    }

    public void setShadowVisible(boolean visible) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && visible) {
            return;
        }
        mShadow.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_RECREATE && resultCode == Activity.RESULT_OK) {
            mRecreateActivity = true;

            Intent main = new Intent(this, ActivityMain.class);
            Intent settings = new Intent(this, ActivitySettings.class);

            startActivities(new Intent[]{main, settings});
            finish();
        }
    }

    public void setOnBackListener(OnBackPressedListener listener) {
        mBackCallback = listener;
    }

    @Override
    public void onBackPressed() {
        if ((mBackCallback != null && !mBackCallback.doBack()) || (mBackCallback == null)) {
            super.onBackPressed();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        ApplicationBase.logoutAutomatically(sharedPreferences.getBoolean(getString(R.string.preference_key_log_out_automatically), true));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mRecreateActivity) {
            return;
        }
        FragmentPasswords mainFrag = (FragmentPasswords) getSupportFragmentManager().findFragmentByTag(FragmentPasswords.TAG);
        FragmentPasswordDetails details = (FragmentPasswordDetails) getSupportFragmentManager().findFragmentByTag(FragmentPasswordDetails.TAG);

        if (mainFrag != null && mainFrag.isAdded()) {
            getSupportFragmentManager().putFragment(outState, FragmentPasswords.TAG, mainFrag);
        }

        if (details != null && details.isAdded()) {
            getSupportFragmentManager().putFragment(outState, FragmentPasswordDetails.TAG, details);
        }

        outState.putBoolean(SAVED_INSTANCE_STATE_KEY_DRAWER_LOCKED, isDrawerLocked());

    }

    private void restoreFragmentsFromSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            final FragmentPasswordDetails details = (FragmentPasswordDetails) getSupportFragmentManager().getFragment(savedInstanceState, FragmentPasswordDetails.TAG);
            final FragmentSettings settings = (FragmentSettings) getFragmentManager().getFragment(savedInstanceState, FragmentSettings.TAG);
            if ((details == null || (!details.isAdded())) && (settings == null || (!settings.isAdded()))) {
                initDrawer();
            }
        }
    }

    public interface OnBackPressedListener {
        boolean doBack();
    }


}
