/**
 * Odoo, Open Source Management Solution
 * Copyright (C) 2012-today Odoo SA (<http:www.odoo.com>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http:www.gnu.org/licenses/>
 * <p/>
 * Created on 17/12/14 6:19 PM
 */
package com.odoo.core.support;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.odoo.core.auth.OdooAccountManager;
import com.odoo.core.utils.OPreferenceManager;
import com.odoo.datas.OConstants;

import odoo.helper.OdooVersion;

public class OUser extends odoo.helper.OUser {

    public static final int USER_ACCOUNT_VERSION = 2;
    private Account account;
    private OdooVersion odooVersion;
    private Boolean dummyUser = false;
    private OPreferenceManager pref = null;

    public Boolean isDummyUser() {
        return dummyUser;
    }

    public void setDummyUser(Boolean dummyUser) {
        this.dummyUser = dummyUser;
    }

    public static OUser currentUser(Context context) {
        return OConstants.publicUser();
    }

    public static OUser getUser(Context context) {
        OPreferenceManager pref = new OPreferenceManager(context);
        return pref.getUserValues();
    }

    public boolean createNewAccount(Context context) {
        pref = new OPreferenceManager(context);
        pref.setUserValues(this);
        return true;
    }

    @Override
    public Bundle getAsBundle() {
        Bundle data = super.getAsBundle();
        // Converting each value to string. Account supports only string values
        for (String key : data.keySet()) {
            data.putString(key, data.get(key) + "");
        }
        return data;
    }

    public void setFromBundle(Bundle data) {
        if (data != null) {
            data.putInt("version_type_number", Integer.parseInt(data.getString("version_type_number")));
            data.putInt("version_number", Integer.parseInt(data.getString("version_number")));
            data.putBoolean("oauth_login", Boolean.parseBoolean(data.getString("oauth_login")));
            data.putInt("user_id", Integer.parseInt(data.getString("user_id")));
            data.putInt("partner_id", Integer.parseInt(data.getString("partner_id")));
            data.putInt("company_id", Integer.parseInt(data.getString("company_id")));
            data.putBoolean("allow_self_signed_ssl", false);
            data.putBoolean("is_active",true);
            data.putBoolean("allow_force_connect", Boolean.parseBoolean(data.getString("allow_force_connect")));
        }
        fillFromBundle(data);
    }

    public void fillFromAccount(AccountManager accMgr, Account account) {

        setName(accMgr.getUserData(account, "name"));
        setUsername(accMgr.getUserData(account, "username"));
        setUserId(Integer.parseInt(accMgr.getUserData(account, "user_id")));
        setPartnerId(Integer.parseInt(accMgr.getUserData(account, "partner_id")));
        setTimezone(accMgr.getUserData(account, "timezone"));
        setIsActive(Boolean.parseBoolean(accMgr.getUserData(account, "isactive")));
        setAvatar(accMgr.getUserData(account, "avatar"));
        setDatabase(accMgr.getUserData(account, "database"));
        setHost(accMgr.getUserData(account, "host"));
        setAndroidName(accMgr.getUserData(account, "android_name"));
        setPassword(accMgr.getUserData(account, "password"));
        setCompanyId(Integer.parseInt(accMgr.getUserData(account, "company_id")));
        setAllowForceConnect(Boolean.parseBoolean(accMgr.getUserData(account, "allow_self_signed_ssl")));
        try {
            OdooVersion version = new OdooVersion();
            version.setServerSerie(accMgr.getUserData(account, "server_serie"));
            version.setVersionType(accMgr.getUserData(account, "version_type"));
            version.setVersionRelease(accMgr.getUserData(account, "version_release"));
            version.setVersionNumber(Integer.parseInt(accMgr.getUserData(account, "version_number")));
            version.setVersionTypeNumber(Integer.parseInt(accMgr.getUserData(account, "version_type_number")));
            version.setServerVersion(accMgr.getUserData(account, "server_version"));
            setOdooVersion(version);
        } catch (Exception e) {
            e.printStackTrace();
            Log.w(TAG, e.getMessage());
        }
        // If oAuth login
        setOAuthLogin(Boolean.parseBoolean(accMgr.getUserData(account, "oauth_login")));
        setInstanceDatabase(accMgr.getUserData(account, "instance_database"));
        setInstanceURL(accMgr.getUserData(account, "instance_url"));
        setClientId(accMgr.getUserData(account, "client_id"));
    }

    public String getDBName() {
        String db_name = "OdooSQLite";
        db_name += "_" + getUsername();
        db_name += "_" + getDatabase();
        return db_name + ".db";
    }

    @Override
    public String toString() {
        return getAndroidName();
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public OdooVersion getOdooVersion() {
        return odooVersion;
    }

    @Override
    public void setOdooVersion(OdooVersion odooVersion) {
        this.odooVersion = odooVersion;
    }
}
