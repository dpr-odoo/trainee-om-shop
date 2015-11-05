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
 * Created on 19/12/14 2:36 PM
 */
package com.odoo.core.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.odoo.R;
import com.odoo.addons.cart.CartFragment;
import com.odoo.addons.cart.models.ShopCart;
import com.odoo.addons.products.ProductDetail;
import com.odoo.addons.website_sale.FavouriteProductView;
import com.odoo.addons.website_sale.RecentViewedItems;
import com.odoo.addons.website_sale.SearchItemsActivity;
import com.odoo.core.support.addons.fragment.BaseFragment;
import com.odoo.core.utils.sys.IOnActivityResultListener;

public class OAppBarUtils {
    public static final int REQUEST_PRODUCT_SEARCH = 115;

    public static void setAppBar(AppCompatActivity activity, Boolean withHomeButtonEnabled) {
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        if (toolbar != null) {
            activity.setSupportActionBar(toolbar);
            ActionBar actionBar = activity.getSupportActionBar();
            if (withHomeButtonEnabled && actionBar != null) {
                actionBar.setHomeButtonEnabled(true);
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    public static void setChildAppBar(AppCompatActivity parent, Toolbar toolbar,
                                      boolean withHomeButtonEnabled) {
        if (toolbar != null) {
            parent.setSupportActionBar(toolbar);
            ActionBar actionBar = parent.getSupportActionBar();
            if (withHomeButtonEnabled && actionBar != null) {
                actionBar.setHomeButtonEnabled(true);
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    public static void setCounter(int counter, RelativeLayout cartBadge) {
        if (counter > 0) {
            OControls.setVisible(cartBadge, R.id.counter);
            OControls.setText(cartBadge, R.id.counter, counter + "");
        } else {
            OControls.setGone(cartBadge, R.id.counter);
        }
    }

    public static void bindShopMenu(final AppCompatActivity activity, int[] hideMenuIds, Menu menu) {
        final ShopCart shopCart = new ShopCart(activity);
        MenuItem cart = menu.findItem(R.id.menu_show_cart);
        for (int menuId : hideMenuIds) {
            Log.v("", "Hiding menu " + menu.findItem(menuId).getTitle());
            menu.findItem(menuId).setVisible(false);
        }
//        menu.findItem(R.id.menu_search_product).setVisible(!isHome);
        // Binding cart and its badge
        final RelativeLayout cartBadge = (RelativeLayout) cart.getActionView();
        int counter = shopCart.counter();
        setCounter(counter, cartBadge);
        cart.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                OAppBarUtils.onShopMenuItemClick(activity, null, item);
                return true;
            }
        });
        cartBadge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OFragmentUtils.get(activity, null).startFragment(new CartFragment(), true, null);
            }
        });
    }

    public static void onShopMenuItemClick(AppCompatActivity activity, final BaseFragment fragment, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_home:
                int count = fragment.getFragmentManager().getBackStackEntryCount();
                if (count > 0) {
                    int frag_id = fragment.getFragmentManager().getBackStackEntryAt(0).getId();
                    fragment.getFragmentManager().popBackStack(frag_id, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                } else {
                    fragment.getFragmentManager().popBackStack();
                }
                break;
            case R.id.menu_search_product:
                Intent searchProduct = new Intent(activity, SearchItemsActivity.class);
                fragment.parent().setOnActivityResultListener(new IOnActivityResultListener() {
                    @Override
                    public void onOdooActivityResult(int requestCode, int resultCode, Intent data) {
                        if (requestCode == REQUEST_PRODUCT_SEARCH && resultCode == Activity.RESULT_OK) {
                            //TODO: Handle result and start product detail view if selected from search
                            Bundle extra = new Bundle();
                            extra.putBoolean(ProductDetail.KEY_OPEN_PRODUCT, true);
                            fragment.startFragment(new ProductDetail(), true, extra);
                        }
                    }
                });
                fragment.parent().startActivityForResult(searchProduct, REQUEST_PRODUCT_SEARCH);
                break;
            case R.id.menu_show_cart:
                OFragmentUtils.get(activity, null).startFragment(new CartFragment(), true, null);
                break;
            case R.id.menu_recent_view:
                fragment.startFragment(new RecentViewedItems(), true);
                break;
            case R.id.menu_favourite_list:
                fragment.startFragment(new FavouriteProductView(), true);
                break;
        }
    }

}
