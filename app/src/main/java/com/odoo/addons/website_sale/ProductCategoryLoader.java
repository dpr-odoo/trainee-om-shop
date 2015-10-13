package com.odoo.addons.website_sale;

import android.content.Context;
import android.os.AsyncTask;

import com.odoo.App;
import com.odoo.addons.website_sale.models.ProductPublicCategory;
import com.odoo.core.utils.logger.OLog;

public class ProductCategoryLoader extends AsyncTask<Void, Void, Void> {
    private Context mContext;
    private ProductPublicCategory category;
    private OnCategoryLoadListener mOnCategoryLoadListener;
    private App mApp;

    public ProductCategoryLoader(Context context, OnCategoryLoadListener callback) {
        mContext = context;
        mApp = (App) mContext.getApplicationContext();
        category = new ProductPublicCategory(context);
        mOnCategoryLoadListener = callback;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            if (mApp.inNetwork()) {
                category.quickSyncRecords(null, true);
            }
            Thread.sleep(1000);
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (mOnCategoryLoadListener != null) {
            mOnCategoryLoadListener.categoryLoaded();
        }
    }

    public interface OnCategoryLoadListener {
        void categoryLoaded();
    }
}
