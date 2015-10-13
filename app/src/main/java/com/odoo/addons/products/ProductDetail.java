package com.odoo.addons.products;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.odoo.addons.products.models.ProductTemplate;
import com.odoo.core.support.addons.fragment.BaseFragment;
import com.odoo.core.support.drawer.ODrawerItem;

import java.util.List;

public class ProductDetail extends BaseFragment {

    public static final String KEY_OPEN_PRODUCT = "open_product_detail";

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public List<ODrawerItem> drawerMenus(Context context) {
        return null;
    }

    @Override
    public Class<ProductTemplate> database() {
        return ProductTemplate.class;
    }
}
