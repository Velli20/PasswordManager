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
import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.velli.passwordmanager.database.PasswordDatabaseHandler;

public class ApplicationBase extends Application implements Application.ActivityLifecycleCallbacks {

    public static final String TAG = "ApplicationBase ";
    public static final int NOTIFICATION_ID = 2049394;

    public static final int ACTIVITY_MAIN = 0;
    public static final int ACTIVITY_MANAGE_GROUPS = 1;
    public static final int ACTIVITY_SETTINGS = 2;
    public static final int ACTIVITY_LICENCES = 3;
    public static final int ACTIVITY_ADD_PASSWORD = 4;
    public static final int ACTIVITY_GENERATE_PASSWORD = 5;
    public static final int ACTIVITY_LOCK_SCREEN = 6;
    public static final int ACTIVITY_ADD_CREDIT_CARD = 7;
    public static final int ACTIVITY_FILE_PICKER = 8;
    public static final int ACTIVITY_IMPORT_PASSWORDS = 9;
    public static final int SERVICE_WEB_LOGIN = 10;

    public static final int STATUS_VISIBLE = 0;
    public static final int STATUS_ON_PAUSE = 1;
    public static final int STATUS_ON_STOP = 2;
    public static final int STATUS_ON_DESTROY = 3;
    public static final int STATUS_STARTING_ACTIVITY = -1;

    private static int mMainActivityStatus = STATUS_ON_DESTROY;
    private static int mManageGroupsActivityStatus = STATUS_ON_DESTROY;
    private static int mSettingsActivityStatus = STATUS_ON_DESTROY;
    private static int mLicencesActivityStatus = STATUS_ON_DESTROY;
    private static int mAddPasswordActivityStatus = STATUS_ON_DESTROY;
    private static int mGeneratePasswordActivityStatus = STATUS_ON_DESTROY;
    private static int mLockScreenActivityStatus = STATUS_ON_DESTROY;
    private static int mAddCreditCardActivityStatus = STATUS_ON_DESTROY;
    private static int mServiceWebLoginStatus = STATUS_ON_DESTROY;
    private static int mFilePickerActivityStatus = STATUS_ON_DESTROY;
    private static int mImportPasswordsActivityStatus = STATUS_ON_DESTROY;

    private static boolean sLogoutAutomatically = false;
    private static boolean sMainActivityIsChangingConfigurations = false;


    private static ApplicationBase instance;

    public static ApplicationBase get() {
        return instance;
    }

    public static Context getAppContext() {
        return ApplicationBase.instance;
    }

    public static void logoutAutomatically(boolean logout) {
        Log.i(TAG, TAG + "logoutAutomatically(" + String.valueOf(logout) + ")");
        sLogoutAutomatically = logout;
    }

    public static void setActivityStatus(int activity, int status, boolean isChangingConfigurations) {
        String name = "none name";
        switch (activity) {

            case ACTIVITY_MAIN:
                mMainActivityStatus = status;
                sMainActivityIsChangingConfigurations = isChangingConfigurations;
                name = "ACTIVITY_MAIN";
                break;
            case ACTIVITY_MANAGE_GROUPS:
                mManageGroupsActivityStatus = status;
                name = "ACTIVITY_MANAGE_GROUPS";
                break;
            case ACTIVITY_SETTINGS:
                mSettingsActivityStatus = status;
                name = "ACTIVITY_SETTINGS";
                break;
            case ACTIVITY_LICENCES:
                mLicencesActivityStatus = status;
                name = "ACTIVITY_LICENCES";
                break;
            case ACTIVITY_ADD_PASSWORD:
                mAddPasswordActivityStatus = status;
                name = "ACTIVITY_ADD_PASSWORD";
                break;
            case ACTIVITY_GENERATE_PASSWORD:
                mGeneratePasswordActivityStatus = status;
                name = "ACTIVITY_GENERATE_PASSWORD";
                break;
            case ACTIVITY_LOCK_SCREEN:
                mLockScreenActivityStatus = status;
                name = "ACTIVITY_LOCK_SCREEN";
                break;
            case ACTIVITY_ADD_CREDIT_CARD:
                mAddCreditCardActivityStatus = status;
                name = "ACTIVITY_ADD_CREDIT_CARD";
                break;
            case SERVICE_WEB_LOGIN:
                mServiceWebLoginStatus = status;
                name = "SERVICE_WEB_LOGIN";
                break;
            case ACTIVITY_FILE_PICKER:
                mFilePickerActivityStatus = status;
                name = "ACTIVITY_FILE_PICKER";
                break;
            case ACTIVITY_IMPORT_PASSWORDS:
                mImportPasswordsActivityStatus = status;
                name = "ACTIVITY_IMPORT_PASSWORDS";
                break;
        }

        Log.i(TAG, TAG + "setActivityStatus(" + name + "," + getNameForStatus(status) + ")");
        if (checkIfNotificationNeedsToBeHidden()) {
            hideLogOutNotification();
        }
        lockAppIfNeeded();
    }

    public static int getActivityStatus(int activity) {
        switch (activity) {

            case ACTIVITY_MAIN:
                return mMainActivityStatus;
            case ACTIVITY_MANAGE_GROUPS:
                return mManageGroupsActivityStatus;
            case ACTIVITY_SETTINGS:
                return mSettingsActivityStatus;
            case ACTIVITY_LICENCES:
                return mLicencesActivityStatus;
            case ACTIVITY_ADD_PASSWORD:
                return mAddPasswordActivityStatus;
            case ACTIVITY_LOCK_SCREEN:
                return mLockScreenActivityStatus;
            case ACTIVITY_FILE_PICKER:
                return mFilePickerActivityStatus;
            case ACTIVITY_IMPORT_PASSWORDS:
                return mImportPasswordsActivityStatus;
        }

        return -1;
    }

    public static boolean checkIfNotificationNeedsToBeHidden() {
        return mAddCreditCardActivityStatus == STATUS_VISIBLE
                || mAddPasswordActivityStatus == STATUS_VISIBLE
                || mGeneratePasswordActivityStatus == STATUS_VISIBLE
                || mLicencesActivityStatus == STATUS_VISIBLE
                || mLockScreenActivityStatus == STATUS_VISIBLE
                || mMainActivityStatus == STATUS_VISIBLE
                || mManageGroupsActivityStatus == STATUS_VISIBLE
                || mSettingsActivityStatus == STATUS_VISIBLE
                || mFilePickerActivityStatus == STATUS_VISIBLE;
    }

    public static void lockAppIfNeeded() {
        if (needToLockApp()) {
            forceToLockApp();
        } else {
            if (PasswordDatabaseHandler.getInstance().isDatabaseOpen()
                    && (mMainActivityStatus == STATUS_ON_STOP
                    || mAddCreditCardActivityStatus == STATUS_ON_STOP
                    || mAddPasswordActivityStatus == STATUS_ON_STOP
                    || mSettingsActivityStatus == STATUS_ON_STOP
                    || mLicencesActivityStatus == STATUS_ON_STOP
                    || mGeneratePasswordActivityStatus == STATUS_ON_STOP
                    || mManageGroupsActivityStatus == STATUS_ON_STOP
                    || mFilePickerActivityStatus == STATUS_ON_STOP)) {
                if (!checkIfNotificationNeedsToBeHidden()) {
                    showLogoutNotification();
                }
            }
        }
    }

    public static void forceToLockApp() {
        PasswordDatabaseHandler.getInstance().closeDatabase();
        hideLogOutNotification();
    }

    public static void hideLogOutNotification() {
        NotificationManager notificationManager = (NotificationManager) getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    public static void showLogoutNotification() {
        final Intent logout = new Intent(getAppContext(), IntentServiceLogOut.class);

        final NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getAppContext())
                        .setSmallIcon(R.drawable.ic_action_fingerprint)
                        .setContentTitle(getAppContext().getString(R.string.notification_log_out_title))
                        .setContentText(getAppContext().getString(R.string.notification_log_out_content))
                        .setColor(getAppContext().getResources().getColor(R.color.color_primary_500))
                        .setShowWhen(false)
                        .setContentIntent(PendingIntent.getService(getAppContext(), -1, logout, PendingIntent.FLAG_ONE_SHOT));

        NotificationManager notificationManager = (NotificationManager) getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());

    }

    public static boolean needToLockApp() {
        if (!sLogoutAutomatically) {
            return false;
        }
        if ((mMainActivityStatus == STATUS_ON_STOP || mMainActivityStatus == STATUS_ON_PAUSE) && !sMainActivityIsChangingConfigurations) {
            if (mManageGroupsActivityStatus == STATUS_ON_DESTROY
                    && mSettingsActivityStatus == STATUS_ON_DESTROY
                    && mLicencesActivityStatus == STATUS_ON_DESTROY
                    && mAddPasswordActivityStatus == STATUS_ON_DESTROY
                    && mAddCreditCardActivityStatus == STATUS_ON_DESTROY
                    && mGeneratePasswordActivityStatus == STATUS_ON_DESTROY
                    && mServiceWebLoginStatus == STATUS_ON_DESTROY
                    && mFilePickerActivityStatus == STATUS_ON_DESTROY) {
                return true;
            }
        }
        return false;
    }

    public static String getNameForStatus(int status) {
        switch (status) {
            case STATUS_ON_DESTROY:
                return " STATUS_ON_DESTROY ";
            case STATUS_ON_PAUSE:
                return " STATUS_ON_PAUSE ";
            case STATUS_ON_STOP:
                return " STATUS_ON_STOP ";
            case STATUS_STARTING_ACTIVITY:
                return " STATUS_STARTING_ACTIVITY ";
            case STATUS_VISIBLE:
                return " STATUS_VISIBLE ";
        }

        return " UNKNOWN STATUS ";
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onTerminate() {
        Log.i(TAG, TAG + "onTerminate()");
        hideLogOutNotification();
        super.onTerminate();
    }

    @Override
    protected void finalize() throws Throwable {
        Log.i(TAG, TAG + "finalize()");
        hideLogOutNotification();
        super.finalize();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
