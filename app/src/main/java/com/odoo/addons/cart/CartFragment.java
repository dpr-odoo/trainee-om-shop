package com.odoo.addons.cart;

import android.content.Context;

import com.odoo.core.support.addons.fragment.BaseFragment;
import com.odoo.core.support.drawer.ODrawerItem;

import java.util.List;

public class CartFragment extends BaseFragment{
    @Override
    public List<ODrawerItem> drawerMenus(Context context) {
        return null;
    }

    @Override
    public <T> Class<T> database() {
        return null;
    }
}
