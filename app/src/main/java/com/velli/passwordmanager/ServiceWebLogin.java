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

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.Toast;

import com.velli.passwordmanager.collections.CustomLinearLayoutManager;
import com.velli.passwordmanager.collections.Utils;
import com.velli.passwordmanager.database.OnGetPasswordListener;
import com.velli.passwordmanager.database.OnGetPasswordsListener;
import com.velli.passwordmanager.database.PasswordDatabaseHandler;
import com.velli.passwordmanager.roboto.RobotoButton;
import com.velli.passwordmanager.roboto.RobotoTextView;
import com.velli.passwordmanager.widget.CreditCard;

import java.util.ArrayList;

public class ServiceWebLogin extends Service implements OnClickListener, OnMenuItemClickListener {
    public static final String Tag = "ServiceWebLogin ";
    public static final String INTENT_EXTRA_PASSWORD_ID = "password id";

    private static final boolean DEBUG = false;
    int width;
    private ImageButton mMore;
    private RecyclerView mList;
    private PasswordAdapter mAdapter;
    private WindowManager mWindowManager;
    private LinearLayout mNoPasswordsContainer;
    private RobotoTextView mText;
    private RobotoButton mViewAll;
    private View view;
    private String mHost;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (DEBUG) {
            Log.i(Tag, Tag + "onStartCommand()");
        }

        if (intent.getExtras() != null && intent.getExtras().getString(Intent.EXTRA_TEXT) != null) {
            mHost = intent.getExtras().getString(Intent.EXTRA_TEXT);
            PasswordDatabaseHandler.getInstance().searchFromPasswords(new OnGetPasswordsListener() {
                @Override
                public void onGetPasswords(ArrayList<Password> list) {
                    if (DEBUG) {
                        Log.i(Tag, Tag + "onGetPasswords() host: " + mHost);
                    }
                    if (list != null && !list.isEmpty()) {
                        mAdapter = new PasswordAdapter(list);
                        mList.setAdapter(mAdapter);
                    } else {
                        mNoPasswordsContainer.setVisibility(View.VISIBLE);
                        mText.setText(getTitle(getResources().getString(R.string.title_no_password_for_the_site), NewPasswordBase.getDomainName(mHost)));
                    }

                }
            }, NewPasswordBase.getDomainName(mHost));

        } else if (intent.getExtras() != null) {
            int id = intent.getIntExtra(INTENT_EXTRA_PASSWORD_ID, -1);

            if (id != -1) {
                PasswordDatabaseHandler.getInstance().getPassword(id, new OnGetPasswordListener() {

                    @Override
                    public void onGetPassword(Password entry) {
                        if (entry != null) {
                            ArrayList<Password> list = new ArrayList<>();
                            list.add(entry);
                            mAdapter = new PasswordAdapter(list);
                            mList.setAdapter(mAdapter);
                        } else {
                            mNoPasswordsContainer.setVisibility(View.VISIBLE);
                        }

                    }
                });
            }
        }
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationBase.setActivityStatus(ApplicationBase.SERVICE_WEB_LOGIN, ApplicationBase.STATUS_VISIBLE, false);

        if (DEBUG) {
            Log.i(Tag, Tag + "onCreate()");
        }

        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.service_web_login, null);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.BOTTOM;
        width = getScreenSize().x - getResources().getDimensionPixelSize(R.dimen.service_web_login_width_offset);

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(view, params);


        mList = (RecyclerView) view.findViewById(R.id.service_web_login_pass_list);
        mList.setLayoutManager(new CustomLinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mList.setHasFixedSize(true);
        mList.setHorizontalScrollBarEnabled(true);

        mNoPasswordsContainer = (LinearLayout) view.findViewById(R.id.service_web_login_text_container);

        mText = (RobotoTextView) view.findViewById(R.id.service_web_login_text);
        mViewAll = (RobotoButton) view.findViewById(R.id.service_web_login_view_all);
        mViewAll.setOnClickListener(this);

        mMore = (ImageButton) view.findViewById(R.id.service_web_login_more);
        mMore.setOnClickListener(this);


        view.requestLayout();

        if (mAdapter != null) {
            mList.setAdapter(mAdapter);
        }

    }

    private Point getScreenSize() {
        final Point point = new Point();
        final WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        final Display display = wm.getDefaultDisplay();

        display.getSize(point);
        return point;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ApplicationBase.setActivityStatus(ApplicationBase.SERVICE_WEB_LOGIN, ApplicationBase.STATUS_ON_DESTROY, false);
        if (DEBUG) {
            Log.i(Tag, Tag + "onDestroy()");
        }

    }

    private SpannableStringBuilder getTitle(String title, String host) {
        final SpannableStringBuilder sb = new SpannableStringBuilder(title + " \"" + host + "\"");
        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
        sb.setSpan(bss, title.length(), sb.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        return sb;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.service_web_login_view_all:
                PasswordDatabaseHandler.getInstance().getAllPasswords(new OnGetPasswordsListener() {

                    @Override
                    public void onGetPasswords(ArrayList<Password> list) {
                        if (list == null || list.isEmpty()) {
                            mText.setText(getResources().getString(R.string.title_no_passwords));
                            mViewAll.setVisibility(View.GONE);
                        } else {
                            mNoPasswordsContainer.setVisibility(View.GONE);
                            mAdapter = new PasswordAdapter(list);
                            mList.setAdapter(mAdapter);
                        }

                    }
                });
                break;
            case R.id.service_web_login_more:
                showPopup();
                break;


        }

    }

    public void showPopup() {
        PopupMenu popup = new PopupMenu(this, mMore);
        MenuInflater inflate = popup.getMenuInflater();
        inflate.inflate(R.menu.service_web_login_options, popup.getMenu());
        popup.setOnMenuItemClickListener(this);
        popup.show();

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {


            case R.id.service_web_login_new_pass:
                mWindowManager.removeView(view);
                final Intent i = new Intent(this, ActivityAddPassword.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (mHost != null) {
                    i.putExtra(Intent.EXTRA_TEXT, mHost);
                }
                ApplicationBase.setActivityStatus(ApplicationBase.ACTIVITY_ADD_PASSWORD, ApplicationBase.STATUS_STARTING_ACTIVITY, false);
                startActivity(i);
                stopSelf();
                return true;
            case R.id.service_web_login_new_credit_card:
                mWindowManager.removeView(view);
                final Intent a = new Intent(this, ActivityAddCreditCard.class);
                a.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ApplicationBase.setActivityStatus(ApplicationBase.ACTIVITY_ADD_CREDIT_CARD, ApplicationBase.STATUS_STARTING_ACTIVITY, false);
                startActivity(a);
                stopSelf();
                return true;
            case R.id.service_web_login_close:
                mWindowManager.removeView(view);
                stopSelf();
                return true;
        }
        return false;
    }

    private class PasswordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int VIEW_TYPE_PASSWORD = 0;
        private static final int VIEW_TYPE_CREDIT_CARD = 1;
        private static final int VIEW_TYPE_WIFI_PASSWORD = 2;
        private final Resources mRes = getResources();
        private ArrayList<Password> mEntryList;


        public PasswordAdapter(ArrayList<Password> list) {
            if (DEBUG) {
                Log.i(Tag, Tag + "PasswordAdapter()");
            }
            mEntryList = list;
            setHasStableIds(true);

        }

        @Override
        public long getItemId(int position) {
            return mEntryList.get(position).getRowId();
        }

        @Override
        public int getItemCount() {
            if (mEntryList == null) {
                return 0;
            }
            return mEntryList.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (mEntryList.get(position).isCreditCard()) {
                return VIEW_TYPE_CREDIT_CARD;
            } else if (mEntryList.get(position).isWifiPassword()) {
                return VIEW_TYPE_WIFI_PASSWORD;
            } else {
                return VIEW_TYPE_PASSWORD;
            }
        }

        public Drawable getDrawable(int icon) {
            Drawable d = mRes.getDrawable(Utils.getLoginLabelIconArray()[icon]);
            if (d != null) {
                d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            }
            return d;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final int viewType = getItemViewType(position);
            final Password pass = mEntryList.get(position);

            if (viewType == VIEW_TYPE_PASSWORD) {
                ((PasswordViewHolder) holder).mPassword.setText(pass.getPassword());
                ((PasswordViewHolder) holder).mUserName.setText(pass.getUsername());
                ((PasswordViewHolder) holder).mTitle.setText(pass.getDescription());
                ((PasswordViewHolder) holder).mTitle.setCompoundDrawables(getDrawable(pass.getLoginIcon()), null, null, null);
                ((PasswordViewHolder) holder).mCopyUsername.setOnClickListener(new CopyTextClickListener(pass.getUsername()));
                ((PasswordViewHolder) holder).mCopyPassword.setOnClickListener(new CopyTextClickListener(pass.getPassword()));
            } else if (viewType == VIEW_TYPE_CREDIT_CARD) {
                final CreditCardInfo card = pass.getCreditCard();
                ((CreditCardViewHolder) holder).mCardNumber.setText(CreditCard.formatCreditCardNumber(card.getCardNumber()));
                ((CreditCardViewHolder) holder).mCSV.setText(card.getCardCSV());
                ((CreditCardViewHolder) holder).mExpDate.setText(card.getCardExpirationDate());
                ((CreditCardViewHolder) holder).mTitle.setText(pass.getDescription());
                ((CreditCardViewHolder) holder).mCopyCardNumber.setOnClickListener(new CopyTextClickListener(card.getCardNumber()));
                ((CreditCardViewHolder) holder).mCopyCSV.setOnClickListener(new CopyTextClickListener(card.getCardCSV()));
            } else if (viewType == VIEW_TYPE_WIFI_PASSWORD) {
                ((PasswordViewHolder) holder).mPassword.setText(pass.getPassword());
                ((PasswordViewHolder) holder).mUserName.setText(pass.getNetworkSSID());
                ((PasswordViewHolder) holder).mTitle.setText(pass.getDescription());
                ((PasswordViewHolder) holder).mTitle.setCompoundDrawables(getDrawable(pass.getLoginIcon()), null, null, null);
                ((PasswordViewHolder) holder).mCopyUsername.setOnClickListener(new CopyTextClickListener(pass.getUsername()));
                ((PasswordViewHolder) holder).mCopyPassword.setOnClickListener(new CopyTextClickListener(pass.getPassword()));
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View itemView;

            if (viewType == VIEW_TYPE_PASSWORD) {
                itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.floating_password_view_new, viewGroup, false);
                itemView.getLayoutParams().width = width;

                return new PasswordViewHolder(itemView);
            } else if (viewType == VIEW_TYPE_WIFI_PASSWORD) {
                itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.floating_password_view_new, viewGroup, false);
                itemView.getLayoutParams().width = width;

                final PasswordViewHolder holder = new PasswordViewHolder(itemView);
                holder.mUserNameTitle.setText(R.string.new_entry_wifi_ssid);
                return holder;
            } else {
                itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.floating_credit_card_view, viewGroup, false);
                itemView.getLayoutParams().width = width;

                return new CreditCardViewHolder(itemView);
            }
        }

    }

    public class PasswordViewHolder extends RecyclerView.ViewHolder {
        RobotoTextView mUserName;
        RobotoTextView mUserNameTitle;
        RobotoTextView mPassword;
        RobotoTextView mTitle;

        ImageButton mCopyUsername;
        ImageButton mCopyPassword;

        public PasswordViewHolder(View itemView) {
            super(itemView);
            mUserNameTitle = (RobotoTextView) itemView.findViewById(R.id.floating_pass_view_username_title);
            mUserName = (RobotoTextView) itemView.findViewById(R.id.floating_pass_view_username);
            mPassword = (RobotoTextView) itemView.findViewById(R.id.floating_pass_view_password);
            mTitle = (RobotoTextView) itemView.findViewById(R.id.floating_pass_view_description);

            mCopyUsername = (ImageButton) itemView.findViewById(R.id.floating_pass_view_copy_username);
            mCopyPassword = (ImageButton) itemView.findViewById(R.id.floating_pass_view_copy_password);
        }

    }

    public class CopyTextClickListener implements OnClickListener {
        private String mTextToCopy;

        public CopyTextClickListener(String textToCopy) {
            mTextToCopy = textToCopy;
        }

        @Override
        public void onClick(View v) {
            if (mTextToCopy == null) {
                return;
            }
            final ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            final ClipData clip = ClipData.newPlainText("Text", mTextToCopy);

            Toast.makeText(ServiceWebLogin.this, getString(R.string.action_copied_on_clip_board), Toast.LENGTH_SHORT).show();
            clipboard.setPrimaryClip(clip);

        }

    }

    public class CreditCardViewHolder extends RecyclerView.ViewHolder {
        RobotoTextView mCardNumber;
        RobotoTextView mExpDate;
        RobotoTextView mCSV;
        RobotoTextView mTitle;

        ImageButton mCopyCardNumber;
        ImageButton mCopyCSV;

        public CreditCardViewHolder(View itemView) {
            super(itemView);
            mCardNumber = (RobotoTextView) itemView.findViewById(R.id.floating_pass_view_card_number);
            mExpDate = (RobotoTextView) itemView.findViewById(R.id.floating_pass_view_card_exp);
            mCSV = (RobotoTextView) itemView.findViewById(R.id.floating_pass_view_card_csv);
            mTitle = (RobotoTextView) itemView.findViewById(R.id.floating_pass_view_description);

            mCopyCardNumber = (ImageButton) itemView.findViewById(R.id.floating_pass_view_card_copy_number);
            mCopyCSV = (ImageButton) itemView.findViewById(R.id.floating_pass_view_card_copy_csv);
        }

    }

}
