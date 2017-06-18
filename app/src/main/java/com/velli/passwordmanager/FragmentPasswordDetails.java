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
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.Toolbar.OnMenuItemClickListener;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.velli.passwordmanager.collections.NavigationDrawerConstants;
import com.velli.passwordmanager.collections.Utils;
import com.velli.passwordmanager.database.OnDatabaseEditedListener;
import com.velli.passwordmanager.database.OnGetPasswordListener;
import com.velli.passwordmanager.database.PasswordDatabaseHandler;
import com.velli.passwordmanager.listeners.OnScreenshotSavedListener;
import com.velli.passwordmanager.roboto.RobotoTextView;
import com.velli.passwordmanager.widget.CreditCard;
import com.velli.passwordmanager.widget.HardwareAccelerateListener;

import java.io.File;
import java.util.Locale;

public class FragmentPasswordDetails extends Fragment implements OnGetPasswordListener, OnDatabaseEditedListener, OnMenuItemClickListener, ActivityMain.OnBackPressedListener {
    public static final int TYPE_PASSWORD = 0;
    public static final int TYPE_CREDIT_CARD = 1;

    public static final String BUNDLE_KEY_ENTRY_ID = "entry id";
    public static final String BUNDLE_KEY_IS_GRID_LAYOUT_MANAGER = "is grid layout manager";

    public static final String BUNDLE_KEY_ICON_X_POS = "icon x pos";
    public static final String BUNDLE_KEY_ICON_Y_POS = "icon y pos";

    public static final String BUNDLE_KEY_TITLE_X_POS = "title x pos";
    public static final String BUNDLE_KEY_TITLE_Y_POS = "title y pos";

    public static final String BUNDLE_KEY_TYPE = "type";

    public static final String TAG = "DetailsFragment ";
    private static final boolean DEBUG = false;

    private RobotoTextView mTitle;
    private RobotoTextView mLoginType;
    private RobotoTextView mUrl;
    private RobotoTextView mUsername;
    private RobotoTextView mPassword;
    private RobotoTextView mNotes;
    private RobotoTextView mGroup;

    private RobotoTextView mCardType;
    private RobotoTextView mCardNumber;
    private RobotoTextView mCardExpDate;
    private RobotoTextView mCardCSV;

    private RobotoTextView mNotesTitle;
    private RobotoTextView mUrlTitle;
    private RobotoTextView mGroupTitle;
    private RobotoTextView mUsernameTitle;

    private RobotoTextView mWifiSSIDTitle;
    private RobotoTextView mWifiSSID;
    private RobotoTextView mWifiSecurityTitle;
    private RobotoTextView mWifiSecurity;

    private ImageView mLoginIcon;
    private DetailsTitleAnimator mTitleAnimator;
    private LinearLayout mInfoContainer;
    private Snackbar mSnack;

    private Toolbar mBottomToolbar;
    private View mBottomBarDivider;

    private Password mEntry;

    private int mEntryId = 0;

    private boolean mShowingPassword = true;
    private boolean mAnimated = false;
    private boolean mAnimationReady = false;
    private boolean mAnimationStarted = false;
    private boolean mIsPassword = true;
    private boolean mViewsSet = false;
    private boolean mIsGridLayoutManager;

    public static void openApp(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        Intent i = manager.getLaunchIntentForPackage(packageName);
        if (i != null) {
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            context.startActivity(i);
        }

    }

    public static boolean openWebPage(Context context, String url) {
        try {
            if (!url.startsWith("https://") && !url.startsWith("http://")) {
                url = "http://" + url;
            }
            Intent login = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(login);

        } catch (ActivityNotFoundException e) {
            return false;
        }
        return true;
    }

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle savedInstanceState) {
        final View v;
        final Bundle bundle = savedInstanceState != null ? savedInstanceState : getArguments();


        if (bundle != null) {
            mIsPassword = bundle.getInt(BUNDLE_KEY_TYPE, TYPE_PASSWORD) == TYPE_PASSWORD;
            mEntryId = bundle.getInt(BUNDLE_KEY_ENTRY_ID, -1);
        }

        v = inflater.inflate(mIsPassword ? R.layout.fragment_layout_details : R.layout.fragment_layout_details_credit_card, root, false);


        mBottomToolbar = (Toolbar) v.findViewById(R.id.details_view_bottom_toolbar);
        mBottomBarDivider = v.findViewById(R.id.details_view_toolbar_divider);

        mTitle = (RobotoTextView) v.findViewById(R.id.details_view_description);
        mLoginType = (RobotoTextView) v.findViewById(R.id.details_view_login_type);
        mUrl = (RobotoTextView) v.findViewById(R.id.details_view_url);
        mUrlTitle = (RobotoTextView) v.findViewById(R.id.details_view_url_title);
        mUsername = (RobotoTextView) v.findViewById(R.id.details_view_username);
        mUsernameTitle = (RobotoTextView) v.findViewById(R.id.details_view_username_title);
        mPassword = (RobotoTextView) v.findViewById(R.id.details_view_password);
        mNotes = (RobotoTextView) v.findViewById(R.id.details_view_notes);
        mNotesTitle = (RobotoTextView) v.findViewById(R.id.details_view_notes_title);
        mGroup = (RobotoTextView) v.findViewById(R.id.details_view_group);
        mGroupTitle = (RobotoTextView) v.findViewById(R.id.details_view_group_title);
        mLoginIcon = (ImageView) v.findViewById(R.id.details_view_login_icon);
        mInfoContainer = (LinearLayout) v.findViewById(R.id.details_info_container);


        mCardType = (RobotoTextView) v.findViewById(R.id.details_view_card_type);
        mCardNumber = (RobotoTextView) v.findViewById(R.id.details_view_card_number);
        mCardExpDate = (RobotoTextView) v.findViewById(R.id.details_view_card_exp_date);
        mCardCSV = (RobotoTextView) v.findViewById(R.id.details_view_card_csv);

        mWifiSecurity = (RobotoTextView) v.findViewById(R.id.details_view_wifi_security);
        mWifiSecurityTitle = (RobotoTextView) v.findViewById(R.id.details_view_wifi_security_title);
        mWifiSSID = (RobotoTextView) v.findViewById(R.id.details_view_wifi_ssid);
        mWifiSSIDTitle = (RobotoTextView) v.findViewById(R.id.details_view_wifi_ssid_title);

        mViewsSet = true;

        if (mEntry != null) {
            onGetPassword(mEntry);
        }


        if (bundle != null && !mAnimated) {

            final int titleY = bundle.getInt(BUNDLE_KEY_TITLE_Y_POS, -1);
            final int titleX = bundle.getInt(BUNDLE_KEY_TITLE_X_POS, -1);

            final int iconY = bundle.getInt(BUNDLE_KEY_ICON_Y_POS, -1);
            final int iconX = bundle.getInt(BUNDLE_KEY_ICON_X_POS, -1);
            mIsGridLayoutManager = bundle.getBoolean(BUNDLE_KEY_IS_GRID_LAYOUT_MANAGER, false);

            if (titleY != -1 && titleX != -1 && iconY != -1 && iconX != -1) {

                final ViewTreeObserver viewTreeObserver = getActivity().getWindow().getDecorView().getViewTreeObserver();
                viewTreeObserver.addOnPreDrawListener(new OnPreDrawListener() {

                    @SuppressLint("NewApi")
                    @Override
                    public boolean onPreDraw() {
                        if (viewTreeObserver.isAlive()) {
                            viewTreeObserver.removeOnPreDrawListener(this);
                        }
                        if (mTitleAnimator == null) {
                            mTitleAnimator = new DetailsTitleAnimator();
                        }
                        mTitleAnimator.animate(titleY, titleX, iconY, iconX, v.getHeight());

                        return false;
                    }
                });
            } else if (mBottomToolbar != null) {
                mBottomToolbar.setVisibility(View.VISIBLE);
            }

        } else if (mBottomToolbar != null) {
            mBottomToolbar.setVisibility(View.VISIBLE);
        }

        return v;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (DEBUG) {
            Log.i(TAG, TAG + "onActivityCreated()");
        }
        final ActivityMain activity = (ActivityMain) getActivity();
        final ActionBar bar = activity.getSupportActionBar();

        if (bar != null) {
            bar.setTitle(activity.getString(R.string.title_details));
        }

        final boolean unlockOnClick = activity.lockDrawer(true, true);
        activity.toolbarShowBackButton(true, new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (unlockOnClick) {
                    activity.lockDrawer(false, true);
                }
                doBack();
            }
        });
        setHasOptionsMenu(true);

        mBottomToolbar.inflateMenu(mIsPassword ? R.menu.details_fragment_bottom_bar_options : R.menu.details_fragment_bottom_bar_op_credit_card);
        mBottomToolbar.setOnMenuItemClickListener(this);

        if (mEntry == null) {
            getEntry(mEntryId);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (DEBUG) {
            Log.i(TAG, TAG + "onResume()");
        }
        setHasOptionsMenu(true);

        PasswordDatabaseHandler.getInstance().registerOnDatabaseEditedListener(this);
        ((ActivityMain) getActivity()).setOnBackListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (DEBUG) {
            Log.i(TAG, TAG + "onPause()");
        }

        if (mSnack != null) {
            mSnack.dismiss();
        }
        ((ActivityMain) getActivity()).setOnBackListener(null);
    }

    public void getEntry(int id) {
        if (DEBUG) {
            Log.i(TAG, TAG + "getEntry()");
        }
        PasswordDatabaseHandler.getInstance().getPassword(id, this);
    }

    @Override
    public void onGetPassword(Password entry) {
        if (DEBUG) {
            Log.i(TAG, TAG + "onGetPassword() is null? " + String.valueOf(entry == null));
        }
        mEntry = entry;

        if (entry == null || !mViewsSet) {
            return;
        }

        try {
            mTitle.setText(String.format("%s%s", mEntry.getDescription().substring(0, 1).toUpperCase(Locale.getDefault()), mEntry.getDescription().substring(1)));
        } catch (IndexOutOfBoundsException e) {
            mTitle.setText(mEntry.getDescription());
        } catch (NullPointerException e) {
            mTitle.setText("-");
        }

        mGroup.setText(mEntry.getGroup());
        mGroupTitle.setVisibility(mEntry.getGroup().isEmpty() ? View.GONE : View.VISIBLE);

        if (entry.isCreditCard()) {
            setCreditCardInfo(entry);
            return;
        }

        final int labels[] = Utils.getLoginLabelArray();
        final Drawable ic = mEntry.getLoginIcon() == -1 ? null : getResources().getDrawable(Utils.getLoginLabelIconArray()[mEntry.getLoginIcon()]);
        final String url = mEntry.getUrl();

        mNotes.setVisibility(mEntry.getNote().isEmpty() ? View.GONE : View.VISIBLE);
        mNotesTitle.setVisibility(mEntry.getNote().isEmpty() ? View.GONE : View.VISIBLE);

        mPassword.setText(mEntry.getPassword());
        mNotes.setText(mEntry.getNote());

        mLoginIcon.setImageDrawable(ic);
        mLoginType.setText(mEntry.getLoginIcon() == -1 ? "" : getResources().getString(labels[mEntry.getLoginIcon()]));

        if (mEntry.isWifiPassword()) {
            setWifiPasswordInfo(mEntry);
            return;
        } else {
            mUsername.setVisibility(View.VISIBLE);
            mUsernameTitle.setVisibility(View.VISIBLE);

            mWifiSecurityTitle.setVisibility(View.GONE);
            mWifiSecurity.setVisibility(View.GONE);
            mWifiSSIDTitle.setVisibility(View.GONE);
            mWifiSSID.setVisibility(View.GONE);
        }
        mUrl.setText(mEntry.getUrl());
        mUsername.setText(mEntry.getUsername());


        if (url == null || url.isEmpty() || mEntry.getLoginIcon() == NavigationDrawerConstants.LABEL_APP) {
            mUrlTitle.setVisibility(View.GONE);
            mUrl.setVisibility(View.GONE);
        } else {
            mUrlTitle.setVisibility(View.VISIBLE);
            mUrl.setVisibility(View.VISIBLE);
        }
        getActivity().invalidateOptionsMenu();
        updateToolbarItems();

    }

    private void setWifiPasswordInfo(Password pass) {
        mUsername.setVisibility(View.GONE);
        mUsernameTitle.setVisibility(View.GONE);
        mUrl.setVisibility(View.GONE);
        mUrlTitle.setVisibility(View.GONE);

        mWifiSecurityTitle.setVisibility(View.VISIBLE);
        mWifiSecurity.setVisibility(View.VISIBLE);
        mWifiSSIDTitle.setVisibility(View.VISIBLE);
        mWifiSSID.setVisibility(View.VISIBLE);

        mWifiSecurity.setText(pass.getWifiSecurity());
        mWifiSSID.setText(pass.getNetworkSSID());
    }

    private void setCreditCardInfo(Password pass) {
        final CreditCardInfo card = pass.getCreditCard();

        mLoginIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_type_credit_card));
        mLoginType.setText(R.string.label_credit_card);
        mCardType.setText(card.getCardType());
        mCardNumber.setText(CreditCard.formatCreditCardNumber(card.getCardNumber()));
        mCardExpDate.setText(card.getCardExpirationDate());
        mCardCSV.setText(card.getCardCSV());
    }

    @Override
    public void onDatabaseHasBeenEdited(String table, long rowid) {
        if (mEntryId != -1) {
            getEntry(mEntryId);
        }

    }

    private void updateToolbarItems() {
        if (mBottomToolbar != null) {
            final Menu menuBottom = mBottomToolbar.getMenu();

            if (menuBottom != null) {
                onPrepareOptionsMenu(menuBottom);
            }
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        final MenuItem starred = menu.findItem(R.id.menu_starred);
        final MenuItem passwordVisibility = menu.findItem(R.id.menu_view_hide_password);

        if (starred != null) {
            if (mEntry == null) {
                starred.setIcon(R.drawable.ic_action_starred_off);
            } else {
                final int res = mEntry.isStarred() ? R.drawable.ic_action_starred_on : R.drawable.ic_action_starred_off;
                starred.setIcon(res);
            }

        }


        if (passwordVisibility != null) {
            passwordVisibility.setIcon(mShowingPassword ? R.drawable.ic_action_view : R.drawable.ic_action_hide);
            passwordVisibility.setTitle(mShowingPassword ? R.string.menu_action_view_password : R.string.menu_action_hide_password);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.details_fragment, menu);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (DEBUG) {
            Log.i(TAG, TAG + "onDestroyView()");
        }
        mViewsSet = false;
        PasswordDatabaseHandler.getInstance().unregisterOnDatabaseEditedListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PasswordDatabaseHandler.getInstance().unregisterOnDatabaseEditedListener(this);
    }

    private void edit() {
        ApplicationBase.setActivityStatus(mIsPassword ? ApplicationBase.ACTIVITY_ADD_PASSWORD : ApplicationBase.ACTIVITY_ADD_CREDIT_CARD, ApplicationBase.STATUS_STARTING_ACTIVITY, false);

        final Bundle bundle = new Bundle();
        final Intent intent = new Intent(getActivity(), mIsPassword ? ActivityAddPassword.class : ActivityAddCreditCard.class);

        bundle.putInt(ActivityAddPassword.INTENT_EXTRA_ROW_ID, mEntry.getRowId());
        intent.putExtras(bundle);

        getActivity().startActivity(intent);

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        onOptionsItemSelected(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_starred:
                mEntry.setStarred(!mEntry.isStarred());
                PasswordDatabaseHandler.getInstance().updatePassword(mEntry);
                getActivity().invalidateOptionsMenu();
                return true;
            case R.id.menu_edit:
                edit();
                return true;
            case android.R.id.home:
                getActivity().getSupportFragmentManager().popBackStack();
                return true;
            case R.id.menu_delete:
                new MaterialDialog.Builder(getActivity())
                        .title(R.string.title_delete_password)
                        .content(R.string.dialog_message_delete_password)
                        .positiveText(R.string.action_ok)
                        .negativeText(R.string.action_cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                                PasswordDatabaseHandler.getInstance().deletePassword(mEntryId);
                                getActivity().getSupportFragmentManager().popBackStack();
                            }
                        })
                        .show();
                return true;
            case R.id.menu_show_as_toolbar:
                showFloatingCard();
                return true;
            case R.id.menu_open_web_page:
                showFloatingCard();
                if (mEntry.getLoginIcon() == NavigationDrawerConstants.LABEL_APP) {
                    openApp(getActivity(), mEntry.getAppPackageName());
                } else {
                    openWebPage(getActivity(), mEntry.getUrl());
                }
                return true;
            case R.id.menu_share_email:
                sharePasswordEmail();
                return true;
            case R.id.menu_view_hide_password:
                if (mPassword != null) {
                    mPassword.setInputType(mShowingPassword ? InputType.TYPE_TEXT_VARIATION_PASSWORD : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else if (mCardCSV != null) {
                    mCardCSV.setInputType(mShowingPassword ? InputType.TYPE_TEXT_VARIATION_PASSWORD : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                mShowingPassword = !mShowingPassword;

                updateToolbarItems();
                return true;
            case R.id.menu_share_screenshot:
                createScreenshot();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showFloatingCard() {
        if (DEBUG) {
            Log.i(TAG, TAG + "showFloatingCard()");
        }
        ApplicationBase.setActivityStatus(ApplicationBase.SERVICE_WEB_LOGIN, ApplicationBase.STATUS_STARTING_ACTIVITY, false);
        final Intent intent = new Intent(getActivity(), ServiceWebLogin.class);
        intent.putExtra(ServiceWebLogin.INTENT_EXTRA_PASSWORD_ID, mEntry.getRowId());
        getActivity().startService(intent);

    }

    private void sharePasswordEmail() {
        if (mEntry == null) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_SUBJECT, "Password: " + mEntry.getDescription());
        i.putExtra(Intent.EXTRA_TEXT, Utils.compositeEmailPassword(getActivity().getResources(), mEntry));

        try {
            getActivity().startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void createScreenshot() {
        Utils.createScreenshot(mEntry, getActivity(), new OnScreenshotSavedListener() {

            @Override
            public void onScreenshotSaved(final String path) {
                if (path != null && getView() != null) {
                    final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    scanIntent.setData(Uri.parse(path));

                    getActivity().sendBroadcast(scanIntent);

                    mSnack = Snackbar.make(getView(), getActivity().getText(R.string.action_screenshot_saved), Snackbar.LENGTH_LONG)
                            .setAction(getActivity().getText(R.string.menu_action_open), new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent();
                                    intent.setAction(android.content.Intent.ACTION_VIEW);
                                    intent.setDataAndType(Uri.fromFile(new File(path)), "image/jpg");
                                    try {
                                        startActivity(intent);
                                    } catch (ActivityNotFoundException ex) {
                                        Toast.makeText(getActivity(), "There are no gallery application installed.", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            })
                            .setActionTextColor(getActivity().getResources().getColor(R.color.color_primary_500));


                    mSnack.show();
                }

            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (DEBUG) {
            Log.i(TAG, TAG + "onSaveInstanceState()");
        }

        if (mTitleAnimator != null) {
            outState.putInt(BUNDLE_KEY_ICON_X_POS, mTitleAnimator.mIconPosInList[0]);
            outState.putInt(BUNDLE_KEY_ICON_Y_POS, mTitleAnimator.mIconPosInList[1]);
            outState.putInt(BUNDLE_KEY_TITLE_X_POS, mTitleAnimator.mTitlePosInList[0]);
            outState.putInt(BUNDLE_KEY_TITLE_X_POS, mTitleAnimator.mTitlePosInList[1]);
        }
        outState.putInt(BUNDLE_KEY_ENTRY_ID, mEntryId);
        outState.putInt(BUNDLE_KEY_TYPE, mIsPassword ? TYPE_PASSWORD : TYPE_CREDIT_CARD);
    }

    @Override
    public boolean doBack() {
        if (mTitleAnimator != null) {
            mTitleAnimator.animateBack();
        } else {
            getActivity().getSupportFragmentManager().popBackStack();
        }
        return true;
    }

    public class DetailsTitleAnimator {
        private static final int ANIMATION_DURATION = 344;
        private static final int ANIMATION_REVERSE_DURATION = 344;
        public int[] mIconPosInList;
        public int[] mTitlePosInList;
        private int iconDeltaY;
        private int iconDeltaX;
        private int titleDeltaY;
        private int titleDeltaX;
        private float mViewHeight = 0;

        public void setViewHeight(int viewHeight) {
            mViewHeight = viewHeight;
        }

        public void animate(int titleY, int titleX, int iconY, int iconX, int viewHeight) {

            mIconPosInList = new int[]{iconX, iconY};
            mTitlePosInList = new int[]{titleX, titleY};
            mViewHeight = viewHeight;

            calculateValues();

            mAnimationReady = true;
            start();

        }

        private void calculateValues() {
            final int[] titleCurrent = new int[2];
            final int[] iconCurrent = new int[2];

            mAnimationReady = false;

            mTitle.getLocationOnScreen(titleCurrent);
            mLoginIcon.getLocationOnScreen(iconCurrent);

            iconDeltaY = (mIconPosInList[1] - iconCurrent[1]);
            iconDeltaX = (mIconPosInList[0] - iconCurrent[0]);
            titleDeltaY = (mTitlePosInList[1] - titleCurrent[1]);
            titleDeltaX = (mTitlePosInList[0] - titleCurrent[0]);

            mAnimationReady = true;
        }

        private void calculateValuesBack() {
            final int[] titleCurrent = {0, 0};
            final int[] iconCurrent = {0, 0};

            mAnimationReady = false;

            mTitle.getLocationInWindow(titleCurrent);
            mLoginIcon.getLocationInWindow(iconCurrent);

            iconDeltaY = (mIconPosInList[1] - iconCurrent[1]);
            titleDeltaY = (mTitlePosInList[1] - titleCurrent[1]);

            mAnimationReady = true;
        }

        private void start() {
            if (mAnimated) {
                mBottomToolbar.setVisibility(View.VISIBLE);
                mBottomBarDivider.setVisibility(View.VISIBLE);
                return;
            } else {
                mAnimated = true;
            }

            mTitle.setTranslationX(titleDeltaX);
            mTitle.setTranslationY(titleDeltaY);

            mLoginIcon.setTranslationX(iconDeltaX);
            mLoginIcon.setTranslationY(iconDeltaY);
            mLoginType.setTranslationY(titleDeltaY);
            if (mIsGridLayoutManager) {
                mLoginType.setTranslationX(titleDeltaX);
            }

            mLoginType.setAlpha(0);
            mInfoContainer.setAlpha(0);

            if (mBottomToolbar != null && mBottomBarDivider != null) {
                mBottomToolbar.setTranslationY(mBottomToolbar.getHeight());
                mBottomBarDivider.setTranslationY(mBottomToolbar.getHeight());
            }
            final HardwareAccelerateListener list = new HardwareAccelerateListener(mTitle);
            list.addAnimatorListenerAdapter(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(Animator animation) {
                    mInfoContainer.animate().alpha(1).setInterpolator(new DecelerateInterpolator()).setDuration(150).start();
                    if (mBottomToolbar != null && mBottomBarDivider != null) {
                        mBottomToolbar.setVisibility(View.VISIBLE);
                        mBottomBarDivider.setVisibility(View.VISIBLE);

                        mBottomToolbar.animate().translationY(0).setDuration(150).setListener(new HardwareAccelerateListener(mBottomToolbar)).setInterpolator(new DecelerateInterpolator()).start();
                        mBottomBarDivider.animate().translationY(0).setDuration(150).setListener(new HardwareAccelerateListener(mBottomBarDivider)).setInterpolator(new DecelerateInterpolator()).start();
                    }
                }
            });
            mLoginIcon.animate().translationX(0).translationY(0).setDuration(ANIMATION_DURATION).setListener(new HardwareAccelerateListener(mLoginIcon)).setInterpolator(new DecelerateInterpolator()).start();
            mTitle.animate().translationX(0).translationY(0).setDuration(ANIMATION_DURATION).setListener(list).setInterpolator(new DecelerateInterpolator()).start();
            mLoginType.animate().translationX(0).translationY(0).alpha(1).setDuration(ANIMATION_DURATION).setListener(new HardwareAccelerateListener(mLoginType)).setInterpolator(new DecelerateInterpolator()).start();


            mAnimationStarted = true;
        }

        public void animateBack() {
            if (!mAnimated || !mAnimationStarted || !mAnimationReady) {
                getActivity().getSupportFragmentManager().popBackStack();
                return;
            }

            calculateValuesBack();
            mLoginIcon.animate().translationX(iconDeltaX).translationY(iconDeltaY).setDuration(ANIMATION_REVERSE_DURATION).setListener(new HardwareAccelerateListener(mLoginIcon)).setInterpolator(new DecelerateInterpolator()).start();
            mTitle.animate().translationX(titleDeltaX).translationY(titleDeltaY).setDuration(ANIMATION_REVERSE_DURATION).setListener(new HardwareAccelerateListener(mTitle)).setInterpolator(new DecelerateInterpolator()).start();
            mInfoContainer.animate().translationY(mViewHeight).setInterpolator(new DecelerateInterpolator()).setDuration(ANIMATION_REVERSE_DURATION).start();

            final HardwareAccelerateListener contList = new HardwareAccelerateListener(mLoginType);
            contList.addAnimatorListenerAdapter(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });
            if (mIsGridLayoutManager) {
                mLoginType.animate().translationX(titleDeltaX).translationY(titleDeltaY).alpha(0).setDuration(ANIMATION_REVERSE_DURATION).setListener(contList).setInterpolator(new DecelerateInterpolator()).start();
            } else {
                mLoginType.animate().translationY(titleDeltaY).alpha(0).setDuration(ANIMATION_REVERSE_DURATION).setListener(contList).setInterpolator(new DecelerateInterpolator()).start();
            }


            if (mBottomToolbar != null && mBottomBarDivider != null) {
                mBottomToolbar.animate().translationY(mBottomToolbar.getHeight()).setDuration(ANIMATION_REVERSE_DURATION).setListener(new HardwareAccelerateListener(mBottomToolbar)).setInterpolator(new DecelerateInterpolator()).start();
                mBottomBarDivider.animate().translationY(mBottomToolbar.getHeight()).setDuration(ANIMATION_REVERSE_DURATION).setListener(new HardwareAccelerateListener(mBottomBarDivider)).setInterpolator(new DecelerateInterpolator()).start();
            }


        }


    }


}
