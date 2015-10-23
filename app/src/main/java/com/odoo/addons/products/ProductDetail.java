package com.odoo.addons.products;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.odoo.R;
import com.odoo.addons.cart.models.ShopCart;
import com.odoo.addons.products.models.ProductProduct;
import com.odoo.addons.products.models.ProductTemplate;
import com.odoo.addons.website_sale.models.FavouriteProducts;
import com.odoo.addons.website_sale.models.RecentViewProducts;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.support.addons.fragment.BaseFragment;
import com.odoo.core.support.drawer.ODrawerItem;
import com.odoo.core.utils.BitmapUtils;
import com.odoo.core.utils.OAppBarUtils;
import com.odoo.core.utils.OControls;
import com.odoo.core.utils.OResource;

import java.util.ArrayList;
import java.util.List;

import odoo.controls.ExpandableListControl;

public class ProductDetail extends BaseFragment implements View.OnClickListener {

    public static final String KEY_OPEN_PRODUCT = "open_product_detail";
    private Bundle extra;
    private RecentViewProducts recentViewProducts;
    private int product_tmpl_id = -1;
    private ODataRow productTemplate;
    private ProductProduct productObj;
    private List<ODataRow> products = new ArrayList<>();
    private ODataRow currency = null;
    private FavouriteProducts favouriteProducts;
    private ExpandableListControl deliveryAndPaymentOptions;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.shop_product_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        extra = getArguments();
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back_dark);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
        toolbar.setTitle("");
        toolbar.inflateMenu(R.menu.menu_home);
        OAppBarUtils.bindShopMenu(parent(), false, toolbar.getMenu());
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                OAppBarUtils.onShopMenuItemClick(parent(), ProductDetail.this, item);
                return true;
            }
        });
        init();
    }


    private void init() {
        recentViewProducts = new RecentViewProducts(getContext());
        productObj = new ProductProduct(getContext());
        favouriteProducts = new FavouriteProducts(getContext());
        if (extra != null) {
            product_tmpl_id = extra.getInt(OColumn.ROW_ID);
            recentViewProducts.addToRecent(product_tmpl_id);
            productTemplate = db().browse(product_tmpl_id);
            products.addAll(productObj.select(null, "product_tmpl_id = ?", new String[]{product_tmpl_id + ""}));
        }
        if (productTemplate != null) {
            if (currency == null && !productTemplate.getString("company_id").equals("false")) {
                currency = productTemplate.getM2ORecord("company_id")
                        .browse().getM2ORecord("currency_id").browse();
            }
            bindProduct();
        }
    }

    private void bindProduct() {
        if (!productTemplate.getString("image").equals("false")) {
            OControls.setImage(getContainer(), R.id.productImage, BitmapUtils.getBitmapImage(getContext(),
                    productTemplate.getString("image")));
        }
        OControls.setText(getContainer(), R.id.productName, productTemplate.getString("name"));
        if (productTemplate.getFloat("warranty") > 0) {
            OControls.setText(getContainer(), R.id.productWarranty, String.format(
                    OResource.string(getContext(), R.string.product_warranty), productTemplate.getString("warranty")
            ));
        }

        String price = productTemplate.getString("list_price");
        float discountPrice = productTemplate.getFloat("price");
        if (discountPrice != productTemplate.getFloat("list_price")) {
            findViewById(R.id.layoutDiscount).setVisibility(View.VISIBLE);
            OControls.setTextViewStrikeThrough(getContainer(), R.id.productOriginalPrice);
            OControls.setText(getContainer(), R.id.productOriginalPrice, getPriceWithCurrency(price));
            float percent = Math.round(100 - (discountPrice / productTemplate.getFloat("list_price") * 100));
            OControls.setText(getContainer(), R.id.productDiscount, percent + "% OFF");
            price = discountPrice + "";
            float saved = productTemplate.getFloat("list_price") - discountPrice;
            OControls.setVisible(getContainer(), R.id.productSavedPrice);
            OControls.setText(getContainer(), R.id.productSavedPrice,
                    String.format(OResource.string(getContext(), R.string.you_saved), getPriceWithCurrency(saved + ""))
            );
        }
        OControls.setText(getContainer(), R.id.finalProductPrice, getPriceWithCurrency(price));
        bindPaymentAndDelivery();
        toggleFavIcon(favouriteProducts.isFavourite(product_tmpl_id));
        findViewById(R.id.toggleFavorite).setOnClickListener(this);
        findViewById(R.id.addToCart).setOnClickListener(this);
    }

    private void bindPaymentAndDelivery() {
        deliveryAndPaymentOptions = (ExpandableListControl) findViewById(R.id.deliveryAndPaymentOptions);
        final List<Object> items = new ArrayList<>();
        ExpandableListControl.ExpandableListAdapter adapter = deliveryAndPaymentOptions.getAdapter(R.layout.shop_payment_delivery_item, items,
                new ExpandableListControl.ExpandableListAdapterGetViewListener() {
                    @Override
                    public View getView(int position, View view, ViewGroup parent) {
                        OControls.setText(view, android.R.id.title, items.get(position));
                        return view;
                    }
                });
        items.add("Cash On Delivery Available");
        items.add("30-day money-back guarantee");
        items.add("Free Shipping in India");
        adapter.notifyDataSetChanged(items);
    }

    private String getPriceWithCurrency(String price) {
        if (currency != null) {
            if (currency.getString("position").equals("after")) {
                price += " " + currency.getString("symbol");
            } else {
                price = currency.getString("symbol") + " " + price;
            }
        }
        return price;
    }

    @Override
    public List<ODrawerItem> drawerMenus(Context context) {
        return null;
    }

    @Override
    public Class<ProductTemplate> database() {
        return ProductTemplate.class;
    }

    private void toggleFavIcon(boolean isFav) {
        int color = _c(R.color.theme_primary);
        if (isFav) {
            color = _c(R.color.android_orange_dark);
        }
        ImageView icon = (ImageView) findViewById(R.id.toggleFavorite);
        icon.setColorFilter(color);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toggleFavorite:
                boolean isFav = favouriteProducts.toggleFavourite(product_tmpl_id);
                toggleFavIcon(isFav);
                String message = (isFav) ? _s(R.string.toast_added_to_favourite_list) :
                        _s(R.string.toast_removed_from_favourite_list);
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                break;
            case R.id.addToCart:
                ShopCart shopCart = new ShopCart(getContext());
                shopCart.addToCart(product_tmpl_id);
                break;
        }
    }
}
