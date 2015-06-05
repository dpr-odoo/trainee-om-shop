package com.odoo.core.account;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.odoo.OdooActivity;
import com.odoo.R;
import com.odoo.base.addons.res.ResCompany;
import com.odoo.core.auth.OdooAccountManager;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.support.OUser;
import com.odoo.core.support.OdooLoginHelper;
import com.odoo.core.utils.OResource;
import com.odoo.datas.OConstants;

public class OdooLogin extends ActionBarActivity implements View.OnClickListener {

    private EditText edtUsername, edtPassword;
    private Boolean mForceConnect = false;
    private Boolean mRequestedForAccount = false;
    private LoginProcess loginProcess = null;
    private AccountCreater accountCreator = null;
    private OdooLoginHelper loginHelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_login);
        if (OdooAccountManager.anyActiveUser(this)) {
            startOdooActivity();
            return;
        }
        init();
    }

    private void init() {
        loginHelper = new OdooLoginHelper(this);
        findViewById(R.id.btnLogin).setOnClickListener(this);
    }

    private void startOdooActivity() {
        startActivity(new Intent(this, OdooActivity.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_base_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                loginUser();
                break;
        }
    }

    // User Login
    private void loginUser() {
        String serverURL = OConstants.SHOP_URL;
        String databaseName = OConstants.SHOP_DATABASE;
        edtUsername = (EditText) findViewById(R.id.edtUserName);
        edtPassword = (EditText) findViewById(R.id.edtPassword);

        edtUsername.setError(null);
        edtPassword.setError(null);
        if (TextUtils.isEmpty(edtUsername.getText())) {
            edtUsername.setError(OResource.string(this, R.string.error_provide_username));
            edtUsername.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(edtPassword.getText())) {
            edtPassword.setError(OResource.string(this, R.string.error_provide_password));
            edtPassword.requestFocus();
            return;
        }
        if (loginProcess != null) {
            loginProcess.cancel(true);
        }
        loginProcess = new LoginProcess();
        loginProcess.execute(databaseName, serverURL);
    }

    private class LoginProcess extends AsyncTask<String, Void, OUser> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//FIXME:            findViewById(R.id.login_progress).setVisibility(View.VISIBLE);
        }

        @Override
        protected OUser doInBackground(String... params) {
            try {
                String username = edtUsername.getText().toString();
                String password = edtPassword.getText().toString();
                return loginHelper.login(username, password, params[0], params[1], mForceConnect);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(OUser user) {
            super.onPostExecute(user);
            edtUsername.setError(null);
            if (user == null) {
//FIXME:                findViewById(R.id.login_progress).setVisibility(View.GONE);
                edtUsername.setError(OResource.string(OdooLogin.this, R.string.error_invalid_username_or_password));
            } else {
                accountCreator = new AccountCreater();
                accountCreator.execute(user);
            }
        }
    }

    private class AccountCreater extends AsyncTask<OUser, Void, Boolean> {

        private OUser mUser;

        @Override
        protected Boolean doInBackground(OUser... params) {
            mUser = params[0];
            if (OdooAccountManager.createAccount(OdooLogin.this, params[0])) {
                mUser = OdooAccountManager.getDetails(OdooLogin.this, mUser.getAndroidName());
                try {
                    // Syncing company details
                    ODataRow company_details = new ODataRow();
                    company_details.put("id", mUser.getCompany_id());
                    ResCompany company = new ResCompany(OdooLogin.this, mUser);
                    company.quickCreateRecord(company_details);

                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!mRequestedForAccount)
                        startOdooActivity();
                    else {
                        Intent intent = new Intent();
                        intent.putExtra(OdooActivity.KEY_NEW_USER_NAME, mUser.getAndroidName());
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
            }, 1500);
        }
    }
}