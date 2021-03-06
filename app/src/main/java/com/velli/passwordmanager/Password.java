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

public class Password {

    private String mDescription;
    private String mUrl;
    private String mUsername;
    private String mPassword;
    private String mNote;
    private String mNetworkSSID;
    private String mWifiSecurity;

    private String mLoginType;
    private String mGroup;
    private String mAppPackageName;

    private int mLoginIcon = 0;
    private int mGroupPos = -1;
    private int rowId = -1;

    private boolean mStarred = false;
    private boolean mIsWifiPassword = false;
    private boolean mIsSelected = false;

    private CreditCardInfo mCard;

    public Password() {

    }

    public Password(CreditCardInfo card) {
        mCard = card;
    }

    public void setInfo(String description, String url, String username, String password, String note) {
        this.mDescription = description;
        this.mUrl = url;
        this.mUsername = username;
        this.mPassword = password;
        this.mNote = note;
    }

    public void setLoginType(String type, int icon) {
        mLoginType = type;
        mLoginIcon = icon;
    }

    public void setIsWifiPassword(boolean wifiPassword) {
        mIsWifiPassword = wifiPassword;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String pass) {
        mPassword = pass;
    }

    public String getNote() {
        return mNote;
    }

    public void setNote(String note) {
        mNote = note;
    }

    public String getGroup() {
        return mGroup;
    }

    public void setGroup(String group) {
        mGroup = group;
    }

    public String getLoginType() {
        return mLoginType;
    }

    public int getLoginIcon() {
        return mLoginIcon;
    }

    public int getRowId() {
        return rowId;
    }

    public void setRowId(int id) {
        rowId = id;
    }

    public boolean isStarred() {
        return mStarred;
    }

    public void setStarred(boolean starred) {
        mStarred = starred;
    }

    public boolean isCreditCard() {
        return mCard != null;
    }

    public boolean isWifiPassword() {
        return mIsWifiPassword;
    }

    public boolean isSelected() {
        return mIsSelected;
    }

    public void setSelected(boolean selected) {
        mIsSelected = selected;
    }

    public int getGroupPosition() {
        return mGroupPos;
    }

    public void setGroupPosition(int pos) {
        mGroupPos = pos;
    }

    public String getAppPackageName() {
        return mAppPackageName;
    }

    public void setAppPackageName(String name) {
        mAppPackageName = name;
    }

    public String getNetworkSSID() {
        return mNetworkSSID;
    }

    public void setNetworkSSID(String ssid) {
        mNetworkSSID = ssid;
    }

    public String getWifiSecurity() {
        return mWifiSecurity;
    }

    public void setWifiSecurity(String security) {
        mWifiSecurity = security;
    }

    public CreditCardInfo getCreditCard() {
        return mCard;
    }

    public void setCreditCard(CreditCardInfo card) {
        mCard = card;
    }

}
