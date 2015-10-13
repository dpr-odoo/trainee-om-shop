package com.odoo.addons.products.services;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class ProductSyncReceiver extends ResultReceiver {

    private Receiver receiver;

    public ProductSyncReceiver(Handler handler, Receiver receiver) {
        super(handler);
        this.receiver = receiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        super.onReceiveResult(resultCode, resultData);
        if (receiver != null) {
            receiver.onReceiveResult(resultCode, resultData);
        }
    }

    public interface Receiver {
        void onReceiveResult(int resultCode, Bundle data);
    }


}
