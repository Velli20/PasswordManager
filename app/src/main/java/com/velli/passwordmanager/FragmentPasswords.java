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



import java.util.ArrayList;


import com.velli.passwordmanager.roboto.RobotoTextView;
import com.velli.passwordmanager.adapter.PasswordCardBaseAdapter;
import com.velli.passwordmanager.adapter.PasswordCardsListAdapter;
import com.velli.passwordmanager.adapter.PasswordCardsSmallAdapter;
import com.velli.passwordmanager.bottomsheet.BottomSheet;
import com.velli.passwordmanager.bottomsheet.BottomSheetItem;
import com.velli.passwordmanager.collections.NavigationDrawerConstants;
import com.velli.passwordmanager.collections.SpacesItemDecoration;
import com.velli.passwordmanager.database.Constants;
import com.velli.passwordmanager.database.OnDatabaseEditedListener;
import com.velli.passwordmanager.database.OnGetPasswordsListener;
import com.velli.passwordmanager.database.PasswordDatabaseHandler;
import com.velli.passwordmanager.widget.NoPasswordsView;
import com.velli.passwordmanager.widget.WelcomeView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;

public class FragmentPasswords extends FragmentPasswordsBase implements OnGetPasswordsListener, OnDatabaseEditedListener, OnClickListener,
        PasswordCardsListAdapter.OnStarPressedListener, PasswordCardBaseAdapter.OnCardClickListener {
	public static final String TAG = "FragmentPasswords ";
	private static final String BUNDLE_KEY_LAYOUT_MANAGER_STATE = "layout manager state";

	private PasswordCardBaseAdapter mAdapter;
	private RecyclerView mList;
    private SpacesItemDecoration mItemDecoration;
	private Snackbar mSnack;
	private FloatingActionButton mFab;
	private BottomSheet mBottomSheet;
	private ItemTouchHelper mItemTouchHelper;
    private WelcomeView mWelcomeView;

    private BottomSheetBehavior mBottomSheetBehavior;
    private View mBottomSheetShadow;
	
	private Parcelable mLayoutManagerState;

    private boolean mIsFromSavedInstanceState = false;
	private boolean mSavedInstanceStateListSet = false;
	private boolean mUpdateListOnResume = false;
	private boolean mIsGridLayoutManager;
	private boolean mShowAsList = false;
    private boolean mShowingBottomSheet = false;

    private int mSelectedMenuItemId = -1;


	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		PasswordDatabaseHandler.getInstance().registerOnDatabaseEditedListener(this);
	}
	
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle savedInstanceState){
		final View mMain = inflater.inflate(R.layout.fragment_layout_main_screen, root, false);

		mList = (RecyclerView)mMain.findViewById(R.id.main_list);
		mList.setItemAnimator(new DefaultItemAnimator());
		mList.setHasFixedSize(true);

		mBottomSheet = (BottomSheet) mMain.findViewById(R.id.bottom_sheet_new_password);


        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet);
		mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetCallback());
        initBottomSheet();

		mItemTouchHelper = new ItemTouchHelper(mSimpleItemTouchCallback);
		
		mWelcomeView = (WelcomeView) mMain.findViewById(R.id.welcome_view);

        mBottomSheetShadow =  mMain.findViewById(R.id.main_list_bottom_sheet_shadow);
        mBottomSheetShadow.setOnClickListener(this);

		mFab = (FloatingActionButton) mMain.findViewById(R.id.main_list_fab);

		setHasOptionsMenu(true);
		return mMain;
	}

	
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
        mFab.setOnClickListener(this);
		mShowAsList = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(getString(R.string.preference_key_show_as), false);

	    if (savedInstanceState != null) {
	    	mIsFromSavedInstanceState = true;
	    	mSavedInstanceStateListSet = false;
	    	mLayoutManagerState = savedInstanceState.getParcelable(BUNDLE_KEY_LAYOUT_MANAGER_STATE);

        }

        initLayoutManager(mLayoutManagerState);
		
	    if(mAdapter != null) {
	    	mAdapter.setOnStarPressedListener(this);
			mAdapter.setOnCardClickListener(this);
	    	mList.setAdapter(mAdapter);
	    	mSavedInstanceStateListSet = true;

			if(mLayoutManagerState != null){
				mList.getLayoutManager().onRestoreInstanceState(mLayoutManagerState);
			}
	    }
	}


	
	@Override
	public void onResume(){
        super.onResume();

		if(mUpdateListOnResume){
			mUpdateListOnResume = false;
            onNavigationItemSelection(getCurrentMenuItem());
		}

		if(PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(getString(R.string.preference_key_swipe_gestures), true)){
			mItemTouchHelper.attachToRecyclerView(mList);
		} else {
			mItemTouchHelper.attachToRecyclerView(null);
		}
		

	}
	
	@Override
	public void onPause(){
        super.onPause();
		mLayoutManagerState = mList.getLayoutManager().onSaveInstanceState();
		mItemTouchHelper.attachToRecyclerView(null);

	}

	@Override
	public void onDestroy(){
        super.onDestroy();
		PasswordDatabaseHandler.getInstance().unregisterOnDatabaseEditedListener(this);
        mAdapter = null;
        mItemDecoration = null;
	}

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem viewAsButton = menu.findItem(R.id.menu_view_as);
        if(viewAsButton != null) {
            viewAsButton.setIcon(mShowAsList ? R.drawable.ic_action_view_list : R.drawable.ic_action_view_grid);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        hideBottomSheet();
        switch (item.getItemId()) {
            case R.id.menu_view_as:
                mShowAsList = !mShowAsList;
                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean(getString(R.string.preference_key_show_as), mShowAsList).apply();
                if(mList != null && mList.getLayoutManager() != null) {
                    initAdapter(mAdapter.getItems());
                    initLayoutManager(mList.getLayoutManager().onSaveInstanceState());
                }
                getActivity().invalidateOptionsMenu();
                return true;
        }
        return false;
    }

    private void initLayoutManager(Parcelable layoutManagerInstanceState){
        final int screenWidth = Utils.getScreenSize(getContext()).x;
        final int requiredMinWidth = getResources().getDimensionPixelSize(R.dimen.password_min_width);
        final int mSpanCount = screenWidth > requiredMinWidth ? screenWidth / requiredMinWidth : 1;

        if(mList == null) {
            return;
        }
        if (mSpanCount > 1 && !mShowAsList) {
            mList.setLayoutManager(new GridLayoutManager(getActivity(), mSpanCount));
            mIsGridLayoutManager = true;
        } else {
            mList.setLayoutManager(new LinearLayoutManager(getActivity()));
            mIsGridLayoutManager = false;
        }
        if(!mShowAsList) {
            mItemDecoration = new SpacesItemDecoration((int) getResources().getDimension(R.dimen.password_card_margin), mShowAsList ? 0 : mSpanCount);
            mList.addItemDecoration(mItemDecoration);
        } else {
            mList.removeItemDecoration(mItemDecoration);
        }

        if(layoutManagerInstanceState != null){
            mList.getLayoutManager().onRestoreInstanceState(layoutManagerInstanceState);
        }
    }

    private void initAdapter(ArrayList<Password> list) {
        if(mAdapter != null) {
            mAdapter.setOnStarPressedListener(null);
            mAdapter.setOnCardClickListener(null);
        }
        mAdapter = mShowAsList ? new PasswordCardsSmallAdapter(getContext(), list) : new PasswordCardsListAdapter(getContext(), list);
        mAdapter.setOnStarPressedListener(this);
        mAdapter.setOnCardClickListener(this);
        mAdapter.setSearchPattern(getSearchQuery());
        mList.setAdapter(mAdapter);
        if(list != null && list.size() > 0){
            mAdapter.notifyListItemsChanged();
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

	private void getAllPasswords(int type) {
		final PasswordDatabaseHandler db = PasswordDatabaseHandler.getInstance();

		if (type == NavigationDrawerConstants.NAVIGATION_ITEM_ALL) {
			db.getAllPasswords(this);
		} else if (type == NavigationDrawerConstants.NAVIGATION_ITEM_STARRED) {
			db.getAllStarredPasswords(this);
		} 
	}
	
	private void getAllPasswordsByGroup(String group){
		final PasswordDatabaseHandler db = PasswordDatabaseHandler.getInstance();
		
		if (db.isDatabaseOpen()) {
            db.getAllPasswordsByGroup(this, group);
		}
	}
	
	private void getAllPasswordsByLabel(int icon, String customLabel){
        final PasswordDatabaseHandler db = PasswordDatabaseHandler.getInstance();
		if (db.isDatabaseOpen()) {
            db.getAllPasswordsByLabel(this, icon, customLabel);
		}
	}

    @Override
    public void onSearchQueryTextSubmitted(String query) {
        super.onSearchQueryTextSubmitted(query);
        searchForPassword(query);
        if(mFab.isShown()) {
            mFab.hide();
        }
    }


    @Override
    public void onSearchActionCancelled() {
        super.onSearchActionCancelled();
        if(mAdapter != null){
            mAdapter.setSearchPattern(null);
        }
        mFab.show();
    }

    @Override
    public void onSearchActionStarted() {
        super.onSearchActionStarted();
        mFab.hide();
        hideBottomSheet();
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.main_list_fab:
                showBottomSheet();
                break;
            case R.id.main_list_bottom_sheet_shadow:
                hideBottomSheet();
                break;
        }

    }


    private void searchForPassword(String toSearch){
		if(mAdapter != null){
			mAdapter.setSearchPattern(toSearch);
		}
		
		final PasswordDatabaseHandler db = PasswordDatabaseHandler.getInstance();
		
		if(db.isDatabaseOpen())  {
			if(toSearch.isEmpty()){
				db.getAllPasswords(this);
			} else {
				db.searchFromPasswords(this, toSearch);
			}
		}
	}
	
	private void clearListAdapter(){
		if(mAdapter != null){
			mAdapter.removeAllListItems();
		}
	}

	
	@Override
	public void onDatabaseHasBeenEdited(String table, long rowid) {
		if(Constants.TABLE_ENTRIES.equals(table)){
			if(isVisible() && isAdded()){
                onNavigationItemSelection(getCurrentMenuItem());
			} else {
				mUpdateListOnResume = true;
			}
		} 
	}


	
	private void showWelcomeView(boolean visible){
		
		if(visible){
			mWelcomeView.show(true, null);
		} else if(mWelcomeView != null){
			mWelcomeView.hide(true, null);
		}
	}

	@Override
	public void onGetPasswords(ArrayList<Password> list) {
		if (list.size() == 0) {
			clearListAdapter();
            if(!isInSearchMode() && (mSelectedMenuItemId == -1 || mSelectedMenuItemId == R.id.navigation_item_all)){
                showWelcomeView(true);
            } else {
                showWelcomeView(false);
            }
		} else {
            showWelcomeView(false);
		}
		
		if(mAdapter == null){
			initAdapter(list);
			
		} else {
			mAdapter.setEntryList(list);
			mAdapter.notifyListItemsChanged();
			
		}
		if(mIsFromSavedInstanceState && !mSavedInstanceStateListSet && mLayoutManagerState != null){			
			mList.getLayoutManager().onRestoreInstanceState(mLayoutManagerState);
			mLayoutManagerState = null;
			mSavedInstanceStateListSet = true;
		}
		
	}


    private void initBottomSheet() {
        mBottomSheet.addBottomSheetItem(new BottomSheetItem(getString(R.string.title_new_password),
                R.id.bottom_sheet_new_password, R.drawable.ic_new_password, R.color.color_primary_500));

        mBottomSheet.addBottomSheetItem(new BottomSheetItem(getString(R.string.title_new_credit_card),
                R.id.bottom_sheet_new_credit_card, R.drawable.ic_new_credit_card, R.color.green));
		mBottomSheet.addBottomSheetItem(new BottomSheetItem(getString(R.string.title_new_note),
				R.id.bottom_sheet_new_credit_card, R.drawable.ic_new_note, R.color.orange));
    }

    private void showBottomSheet() {

        mBottomSheet.setOnBottomSheetItemClickedListener(new BottomSheet.OnBottomSheetItemClickedListener() {
            @Override
            public void onBottomSheetItemClicked(int id) {
                Intent i;

                if (id == R.id.bottom_sheet_new_password) {
                    final Bundle b = new Bundle();
                    i = new Intent(getContext(), ActivityAddPassword.class);
                    i.putExtras(b);
                    ApplicationBase.setActivityStatus(ApplicationBase.ACTIVITY_ADD_PASSWORD, ApplicationBase.STATUS_STARTING_ACTIVITY, false);
                    getContext().startActivity(i);
                } else if (id == R.id.bottom_sheet_new_credit_card) {
                    i = new Intent(getContext(), ActivityAddCreditCard.class);
                    ApplicationBase.setActivityStatus(ApplicationBase.ACTIVITY_ADD_CREDIT_CARD, ApplicationBase.STATUS_STARTING_ACTIVITY, false);
                    getContext().startActivity(i);
                }
                hideBottomSheet();
            }
        });

        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        showBottomSheetShadow(true);

    }

    private void hideBottomSheet() {
        if(mBottomSheetBehavior != null && mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }

    }

    private void showBottomSheetShadow(boolean show) {
        if(mBottomSheetShadow == null || (show == mShowingBottomSheet)) {
            return;
        }


        if(show) {
            mBottomSheetShadow.setAlpha(0);
            mBottomSheetShadow.setVisibility(View.VISIBLE);
            mBottomSheetShadow.animate().alpha(0.56f).setDuration(300).start();

        } else {
            mBottomSheetShadow.animate().alpha(0f).setDuration(300).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if(mBottomSheetShadow != null && !mShowingBottomSheet) {
                        mBottomSheetShadow.setVisibility(View.GONE);
                    }
                }

            }).start();
        }
        mShowingBottomSheet = show;
    }


    private void openFragment(Fragment frag, boolean animate, String tag){
		final FragmentTransaction fr = getFragmentManager().beginTransaction();
		if(animate && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
			fr.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out, R.anim.abc_fade_in, R.anim.abc_fade_out);
		} else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
			fr.setCustomAnimations(0, 0, 0, android.R.anim.fade_out);
		}
		fr.addToBackStack(null).replace(R.id.container, frag, tag).commit();
		
		
	}
	
	@Override
	public void onCardClick(View view, Password entry, int position) {
		final Bundle bundle = new Bundle();
		final FragmentPasswordDetails frag;
		final boolean creditCard = entry.isCreditCard();

		bundle.putInt(FragmentPasswordDetails.BUNDLE_KEY_ENTRY_ID, entry.getRowId());
		bundle.putInt(FragmentPasswordDetails.BUNDLE_KEY_TYPE, creditCard ? FragmentPasswordDetails.TYPE_CREDIT_CARD : FragmentPasswordDetails.TYPE_PASSWORD);
		
		final RobotoTextView title = (RobotoTextView) view.findViewById(creditCard ? R.id.credit_card_title : R.id.password_card_title);
		final ImageView icon = (ImageView) view.findViewById(creditCard ? R.id.credit_card_icon : R.id.password_card_icon);

		final boolean animate = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(getResources().getString(R.string.preference_key_show_animations), true);
		

		if (animate && title != null && icon != null) {
			int[] titlePoint = { 0, 0 };
			int[] iconPoint = { 0, 0 };

			title.getLocationOnScreen(titlePoint);
			icon.getLocationOnScreen(iconPoint);
			
			bundle.putInt(FragmentPasswordDetails.BUNDLE_KEY_TITLE_Y_POS, titlePoint[1]);
			bundle.putInt(FragmentPasswordDetails.BUNDLE_KEY_TITLE_X_POS, titlePoint[0]);
			bundle.putInt(FragmentPasswordDetails.BUNDLE_KEY_ICON_Y_POS, iconPoint[1]);
			bundle.putInt(FragmentPasswordDetails.BUNDLE_KEY_ICON_X_POS, iconPoint[0]);
			bundle.putBoolean(FragmentPasswordDetails.BUNDLE_KEY_IS_GRID_LAYOUT_MANAGER, mIsGridLayoutManager);
						
		}

		frag = new FragmentPasswordDetails();
		frag.setArguments(bundle);
		frag.getEntry((entry.getRowId()));
		frag.setRetainInstance(true);

		mFab.hide();
		
		if(!animate){
			openFragment(frag, true, FragmentPasswordDetails.TAG);
			return;
		}
		openFragment(frag, !animate, FragmentPasswordDetails.TAG);		
		
	}

	

	@Override
	public void onStarPressed(Password entry, int position) {
		if(entry != null){
			entry.setStarred(!entry.isStarred());
			PasswordDatabaseHandler.getInstance().updatePassword(entry);
		}
		
	}

	
	final ItemTouchHelper.SimpleCallback mSimpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
	    
		@Override
	    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
			onRowDeleted(viewHolder.getAdapterPosition());
	    }

		@Override
		public boolean onMove(RecyclerView arg0, ViewHolder arg1, ViewHolder arg2) {
			return false;
		}
	};

    public void onRowDeleted(final int position) {
        if (mSnack != null) {
            mSnack.dismiss();
            mSnack = null;
        }

        final Password entry = mAdapter.getItem(position);

        if (entry != null) {
            PasswordDatabaseHandler.getInstance().deletePassword(entry.getRowId());
            String deleted = entry.getDescription() + " " + getString(R.string.action_deleted);

            mSnack = Snackbar.make(mFab, deleted, Snackbar.LENGTH_LONG)
                    .setAction(R.string.action_undo, new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            PasswordDatabaseHandler.getInstance().addNewPassword(entry);
                        }
                    }).setActionTextColor(getResources().getColor(R.color.color_primary_500));
            mSnack.show();
        }

        if (mAdapter.getItemCount() == 1) {

        }

    }

    @Override
    public void onNavigationItemSelection(MenuItem item){
        super.onNavigationItemSelection(item);

        hideBottomSheet();

        mSelectedMenuItemId= item.getItemId();
        String group = item.getTitle().toString();
        
        if(mSelectedMenuItemId == -1 || (mSelectedMenuItemId == R.id.navigation_item_custom_group && group.isEmpty())){
        	getAllPasswords(NavigationDrawerConstants.NAVIGATION_ITEM_ALL);
        }


		switch(mSelectedMenuItemId){
		
		case R.id.navigation_item_all:
			getAllPasswords(NavigationDrawerConstants.NAVIGATION_ITEM_ALL);
			break;
		case R.id.navigation_item_starred:
			getAllPasswords(NavigationDrawerConstants.NAVIGATION_ITEM_STARRED);
			break;
		case R.id.navigation_item_social:
			getAllPasswordsByLabel(NavigationDrawerConstants.LABEL_SOCIAL, null);
			break;
		case R.id.navigation_item_email:
			getAllPasswordsByLabel(NavigationDrawerConstants.LABEL_E_MAIL, null);
			break;
		case R.id.navigation_item_cloud:
			getAllPasswordsByLabel(NavigationDrawerConstants.LABEL_CLOUD_STORAGE, null);
			break;
		case R.id.navigation_item_e_commerce:
			getAllPasswordsByLabel(NavigationDrawerConstants.LABEL_WEB_SHOP, null);
			break;
		case R.id.navigation_item_web:
			getAllPasswordsByLabel(NavigationDrawerConstants.LABEL_WEB_LOGIN, null);
			break;
		case R.id.navigation_item_work:
			getAllPasswordsByLabel(NavigationDrawerConstants.LABEL_WORK, null);
			break;
		case R.id.navigation_item_bank:
			getAllPasswordsByLabel(NavigationDrawerConstants.LABEL_BANK, null);
			break;
		case R.id.navigation_item_app:
			getAllPasswordsByLabel(NavigationDrawerConstants.LABEL_APP, null);
			break;
		case R.id.navigation_item_credit_card:
			getAllPasswordsByLabel(NavigationDrawerConstants.LABEL_CREDIT_CARD, null);
			break;
		case R.id.navigation_item_gaming:
			getAllPasswordsByLabel(NavigationDrawerConstants.LABEL_GAMING, null);
			break;
		case R.id.navigation_item_messaging:
			getAllPasswordsByLabel(NavigationDrawerConstants.LABEL_MESSAGING, null);
			break;
		case R.id.navigation_item_wifi:
			getAllPasswordsByLabel(NavigationDrawerConstants.LABEL_WIFI, null);
			break;
		case R.id.navigation_item_custom_group:
			getAllPasswordsByGroup(group);
			break;
		case R.id.navigation_item_manage_groups:
			ApplicationBase.setActivityStatus(ApplicationBase.ACTIVITY_MANAGE_GROUPS, ApplicationBase.STATUS_STARTING_ACTIVITY, false);
			final Intent groups = new Intent(getActivity(), ActivityManageGroups.class);
			startActivity(groups);
			break;
		case R.id.navigation_item_settings:
			ApplicationBase.setActivityStatus(ApplicationBase.ACTIVITY_SETTINGS, ApplicationBase.STATUS_STARTING_ACTIVITY, false);
			final Intent settings = new Intent(getActivity(), ActivitySettings.class);
			getActivity().startActivityForResult(settings, ActivityMain.REQUEST_RECREATE);
			break;
		case R.id.navigation_item_log_out:
			if (!((ActivityMain) getActivity()).isSw600dpLayout()) {
				getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
			}
            ((ActivityBase)getActivity()).logOut();
			
			break;
		}
		
	}



    
    @Override
    public void onSaveInstanceState(Bundle outState){
    	super.onSaveInstanceState(outState);
		if (mList != null) {
			mLayoutManagerState = mList.getLayoutManager().onSaveInstanceState();
		}

    }




    private class BottomSheetCallback extends BottomSheetBehavior.BottomSheetCallback {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            switch(newState) {
                case BottomSheetBehavior.STATE_COLLAPSED:
                case BottomSheetBehavior.STATE_HIDDEN:
                    mFab.show();
                    showBottomSheetShadow(false);
                    break;
                case BottomSheetBehavior.STATE_EXPANDED:
                    mFab.hide();
                    break;
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }
    }
}
