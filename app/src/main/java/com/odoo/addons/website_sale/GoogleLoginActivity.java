package com.odoo.addons.website_sale;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.odoo.App;
import com.odoo.R;
import com.odoo.core.service.OSyncAdapter;
import com.odoo.core.support.OUser;
import com.odoo.core.utils.OResource;
import com.odoo.datas.OConstants;

import org.json.JSONObject;

import java.util.HashMap;

import odoo.Odoo;
import odoo.helper.utils.gson.OdooResult;

public class GoogleLoginActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private GoogleApiClient google;
    private boolean mIntentInProgress = false;
    private static final int RC_SIGN_IN = 143;
    private boolean mClearingAccounts = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_google_login);
        setResult(RESULT_CANCELED);
        google = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
        findViewById(R.id.btnLoginWithGoogle).setOnClickListener(this);
        if (google != null) {
            google.connect();
            mClearingAccounts = true;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (!mClearingAccounts) {
            String email = Plus.AccountApi.getAccountName(google);
            Person person = Plus.PeopleApi.getCurrentPerson(google);
            if (person != null) {
                String personName = person.getDisplayName();
                Person.Image avatar = person.getImage();
                String photoURL = "false";
                if (person.hasImage() && avatar.hasUrl()) {
                    photoURL = avatar.getUrl().split("\\?")[0];
                }
                OUser user = new OUser();
                user.setUsername(email);
                user.setPassword("odoo_mobile_shop_google_user");
                user.setName(personName);
                user.setAvatar(photoURL);
                user.setDatabase(OConstants.publicUser().getDatabase());
                new AccountCreator().execute(user);
            }
        } else {
            Plus.AccountApi.clearDefaultAccount(google);
            mClearingAccounts = false;
        }
    }

    private class AccountCreator extends AsyncTask<OUser, Void, Boolean> {

        private ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(GoogleLoginActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(OResource.string(GoogleLoginActivity.this, R.string.status_logging_in));
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(OUser... params) {
            try {
                OUser user = params[0];
                String host = OConstants.publicUser().getHost();
                Odoo odoo = Odoo.createInstance(GoogleLoginActivity.this, host);
                String url = host + "/web/signup";
                HashMap<String, String> data = new HashMap<>();
                data.put("login", user.getUsername());
                data.put("password", user.getPassword());
                data.put("name", user.getName());
                data.put("confirm_password", user.getPassword());
                odoo.requestHTTPController(url, data);

                odoo.helper.OUser odooUser = odoo.authenticate(user.getUsername(),
                        user.getPassword(), user.getDatabase());
                user.fillFromBundle(odooUser.getAsBundle());
                return user.createNewAccount(GoogleLoginActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            reLaunch(result);
        }
    }

    private void reLaunch(Boolean result) {
        setResult(result ? RESULT_OK : RESULT_CANCELED);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (!mClearingAccounts && requestCode == RC_SIGN_IN) {
            mIntentInProgress = false;
            if (responseCode == Activity.RESULT_OK) {
                if (!google.isConnected()) {
                    google.reconnect();
                }
            } else {
                hideProgress();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        google.connect();
        showProgress();
    }

    @Override
    protected void onStop() {
        super.onStop();
        google.disconnect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!mClearingAccounts && !mIntentInProgress && result.hasResolution()) {
            try {
                mIntentInProgress = true;
                result.startResolutionForResult(this, RC_SIGN_IN);
            } catch (Exception e) {
                mIntentInProgress = false;
                google.connect();
                e.printStackTrace();
            }
        }
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                    0).show();
        }
    }

    private void showProgress() {
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLoginWithGoogle:
                mClearingAccounts = false;
                if (!google.isConnecting()) {
                    if (google.isConnected()) {
                        google.reconnect();
                    } else {
                        google.connect();
                    }
                    showProgress();
                }
                break;
        }
    }
}
