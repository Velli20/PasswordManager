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
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.velli.passwordmanager.database.OnDatabaseUnlockedListener;
import com.velli.passwordmanager.database.PasswordDatabaseHandler;
import com.velli.passwordmanager.widget.ConfirmPasswordView;
import com.velli.passwordmanager.widget.DialogTheme;

public class ActivityWebLogin extends Activity implements OnDatabaseUnlockedListener {
    private MaterialDialog mShowingDialog;
    private SpannableStringBuilder mLoginTo;

    private String mHost;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        if (intent.getExtras() != null && intent.getExtras().getString(Intent.EXTRA_TEXT) != null) {

            mHost = intent.getExtras().getString(Intent.EXTRA_TEXT);
            String to = getResources().getString(R.string.action_login_to_web_site);
            String domain = NewPasswordBase.getDomainName(mHost);

            final SpannableStringBuilder sb = new SpannableStringBuilder(to + " \"" + domain + "\"");
            final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
            sb.setSpan(bss, to.length(), sb.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

            mLoginTo = sb;

            if (!PasswordDatabaseHandler.getInstance().isDatabaseOpen()) {
                showLoginDialog(false);
            } else {
                onDatabaseUnlocked(true);
            }

        } else {
            finish();
        }

    }


    public void showLoginDialog(boolean showError) {
        final ConfirmPasswordView v = (ConfirmPasswordView) View.inflate(this, R.layout.dialog_layout_confirm_password, null);
        v.setTheme(DialogTheme.Light);
        v.setMessage(mLoginTo);


        if (showError) {
            v.setEnteredPasswordIncorrect();
        }

        MaterialDialog.Builder b = new MaterialDialog.Builder(this)
                .title(R.string.title_log_in)
                .customView(v, false)
                .cancelable(false)
                .negativeText(R.string.action_cancel)
                .positiveText(R.string.action_continue)
                .autoDismiss(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {

                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        if (!v.hasErrors()) {
                            PasswordDatabaseHandler.getInstance().openDatabase(v.getPassword().toCharArray(), ActivityWebLogin.this);
                            materialDialog.dismiss();
                            showProgressDialog();
                        }
                    }
                }).onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        materialDialog.dismiss();
                        finish();
                    }
                });


        mShowingDialog = b.build();
        mShowingDialog.show();
    }


    public void showProgressDialog() {
        mShowingDialog = new MaterialDialog.Builder(this)
                .title(R.string.title_log_in)
                .content(R.string.action_please_wait)
                .progress(true, 0)
                .cancelable(false)
                .show();
    }

    @Override
    public void onDatabaseUnlocked(boolean result) {
        if (mShowingDialog != null) {
            mShowingDialog.dismiss();
        }

        if (!result) {
            showLoginDialog(true);
        } else {
            final Intent i = new Intent(this, ServiceWebLogin.class);
            i.putExtra(Intent.EXTRA_TEXT, mHost);

            startService(i);
            finish();
        }

    }
}
