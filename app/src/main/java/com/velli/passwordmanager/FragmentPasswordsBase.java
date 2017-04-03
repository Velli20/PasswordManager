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
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.velli.passwordmanager.database.OnGetSearchSuggestionsListener;
import com.velli.passwordmanager.database.PasswordDatabaseHandler;


public class FragmentPasswordsBase extends Fragment implements ActivityMain.OnBackPressedListener, NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "FragmentPasswordsBase";
    private static final String BUNDLE_KEY_SEARCH_KEY_WORD = "search keyword";
    private static final String BUNDLE_KEY_SEARCH_SUBMITTED = "search is submitted";
    private static final String BUNDLE_KEY_NAV_ID = "nav id";

    private NavigationView mNavigationDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private MaterialSearchView mSearchView;

    private boolean mIsQuerySubmitted = false;
    private String mSearchQuery = null;

    private int mLastSelectedNavItemId = -1;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ActivityMain activity = (ActivityMain) getActivity();

        if(activity != null) {
            mNavigationDrawer = activity.getNavigationDrawer();
            if(mNavigationDrawer != null) {
                mNavigationDrawer.setNavigationItemSelectedListener(this);
            }

        }

        SearchListener listener = new SearchListener();

        mSearchView = (MaterialSearchView) getActivity().findViewById(R.id.search_view);
        mSearchView.setOnQueryTextListener(listener);
        mSearchView.setOnSearchViewListener(listener);
        mSearchView.setSubmitOnClick(true);

        if(savedInstanceState != null) {
            mSearchQuery = savedInstanceState.getString(BUNDLE_KEY_SEARCH_KEY_WORD, null);
            mIsQuerySubmitted = savedInstanceState.getBoolean(BUNDLE_KEY_SEARCH_SUBMITTED, false);
            mLastSelectedNavItemId = savedInstanceState.getInt(BUNDLE_KEY_NAV_ID, -1);

            if(mIsQuerySubmitted) {
                onSearchActionStarted();
                onSearchQueryTextSubmitted(mSearchQuery);
            }
        }

        MenuItem currentItem = mLastSelectedNavItemId != -1 ?getMenuItemById(mLastSelectedNavItemId) : getDefaultMenuItem();


        if(currentItem != null && !isInSearchMode()) {
            onNavigationItemSelection(currentItem);
        }
        setHasOptionsMenu(true);

    }

    @Override
    public void onResume() {
        super.onResume();
        ((ActivityMain) getActivity()).setOnBackListener(this);
        if(!isInSearchMode()) {
            ((ActivityMain) getActivity()).lockDrawer(false, true);
        } else {
            setToolbarInSearchMode(mSearchQuery, true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        ((ActivityMain) getActivity()).setOnBackListener(null);
    }

    @Override
    public boolean doBack() {
        if(mSearchQuery != null) {
            setToolbarInSearchMode(null, false);
            return true;
        }
        if (mSearchView.isSearchOpen()) {
            mSearchView.closeSearch();
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mNavigationDrawer != null) {
            mNavigationDrawer.setNavigationItemSelectedListener(null);
        }
        mNavigationDrawer = null;
        if(mDrawerToggle != null) {
            mDrawerToggle.setToolbarNavigationClickListener(null);
        }
        mDrawerToggle = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_screen, menu);
        MenuItem search = menu.findItem(R.id.menu_search);
        mSearchView.setMenuItem(search);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BUNDLE_KEY_SEARCH_KEY_WORD, mSearchQuery);
        outState.putBoolean(BUNDLE_KEY_SEARCH_SUBMITTED, mIsQuerySubmitted);
        outState.putInt(BUNDLE_KEY_NAV_ID, mLastSelectedNavItemId);
    }


    private class SearchListener implements MaterialSearchView.OnQueryTextListener, MaterialSearchView.SearchViewListener {


        @Override
        public boolean onQueryTextSubmit(String query) {
            onSearchQueryTextSubmitted(query);
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            PasswordDatabaseHandler.getInstance().getSearchSuggestions(newText, new OnGetSearchSuggestionsListener() {
                @Override
                public void onGetSearchSuggestions(String[] suggestions) {
                    if(mSearchView != null && suggestions != null) {
                        mSearchView.setSuggestions(suggestions);
                    }
                }
            });
            return false;
        }

        @Override
        public void onSearchViewShown() {
            onSearchActionStarted();
        }

        @Override
        public void onSearchViewClosed() {
            if(!mIsQuerySubmitted) {
                mSearchQuery = null;
                onSearchActionCancelled();
            }
        }
    }

    public void onSearchQueryTextSubmitted(String query) {
        mSearchQuery = query;
        mIsQuerySubmitted = true;
        setToolbarInSearchMode(query, true);

    }


    public void onSearchActionCancelled() {}

    public void onSearchActionStarted() {}


    public boolean isInSearchMode() {
        return mSearchQuery != null;
    }

    public String getSearchQuery() {
        return mSearchQuery;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        if(item.getItemId() != R.id.navigation_item_log_out && drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
        onNavigationItemSelection(item);
        return true;
    }

    public void onNavigationItemSelection(MenuItem item) {
        int id = item.getItemId();
        if(id != R.id.navigation_item_settings
                && id != R.id.navigation_item_log_out
                && id != R.id.navigation_item_manage_groups) {
            mLastSelectedNavItemId = id;
            final ActionBar bar = ((AppCompatActivity)getActivity()).getSupportActionBar();

            if(bar != null) {
                bar.setTitle(item.getTitle().toString());
            }
        }
    }


    public MenuItem getMenuItemById(int id) {
        if(mNavigationDrawer != null) {
            if(id != -1) {
                return mNavigationDrawer.getMenu().findItem(id);
            }
        }
        return getDefaultMenuItem();
    }

    public MenuItem getDefaultMenuItem() {
        if(mNavigationDrawer != null) {
            return mNavigationDrawer.getMenu().getItem(0);
        }
        return null;
    }

    public MenuItem getCurrentMenuItem() {
        return getMenuItemById(mLastSelectedNavItemId);
    }

    public void setToolbarInSearchMode(String query, boolean submitted) {
        final ActivityMain activity = (ActivityMain)getActivity();
        final ActionBar bar = activity.getSupportActionBar();

        if(submitted && query != null) {
            bar.setTitle(getActivity().getString(R.string.action_search_for) + "\"" + query + "\"");

            activity.lockDrawer(true, true);
            activity.toolbarShowBackButton(true, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setToolbarInSearchMode(null, false);
                }
            });

        } else {
            activity.lockDrawer(false, true);
            mSearchQuery = null;
            mIsQuerySubmitted = false;
            onSearchActionCancelled();

            if(mLastSelectedNavItemId != -1) {
                onNavigationItemSelection(getMenuItemById(mLastSelectedNavItemId));
            }
            if(bar != null) {
                bar.setDisplayShowTitleEnabled(true);
                bar.setHomeButtonEnabled(!activity.isSw600dpLayout());
                bar.setDisplayHomeAsUpEnabled(!activity.isSw600dpLayout());
                bar.setDisplayShowCustomEnabled(false);
                bar.show();
            }
            activity.initDrawer();
        }
    }

}
