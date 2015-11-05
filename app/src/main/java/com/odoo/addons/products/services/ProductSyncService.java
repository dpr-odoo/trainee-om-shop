package com.odoo.addons.products.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.odoo.App;
import com.odoo.addons.products.models.ProductProduct;

public class ProductSyncService extends IntentService {
    public static final String TAG = ProductSyncService.class.getSimpleName();
    public static final String KEY_RESULT_RECEIVER = "result_receiver";
    private App app;

    public ProductSyncService() {
        super(TAG);
        Log.i(TAG, "ProductSyncService()");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "ProductSyncService START");
        app = (App) getApplicationContext();
        if (app.inNetwork()) {
            try {
                ProductProduct products = new ProductProduct(getApplicationContext());
                products.quickSyncRecords(null, true); // FIXME: What if there are more than 500 products ??
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
        this.stopSelf();
        Log.i(TAG, "ProductSyncService END");
    }
}
