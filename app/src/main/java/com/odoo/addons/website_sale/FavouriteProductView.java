package com.odoo.addons.website_sale;

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
import com.odoo.addons.products.ProductDetail;
import com.odoo.addons.products.models.ProductTemplate;
import com.odoo.addons.website_sale.models.FavouriteProducts;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.support.addons.fragment.BaseFragment;
import com.odoo.core.support.drawer.ODrawerItem;
import com.odoo.core.utils.BitmapUtils;
import com.odoo.core.utils.OAppBarUtils;
import com.odoo.core.utils.OControls;

import java.util.ArrayList;
import java.util.List;

import odoo.controls.recycler.EasyRecyclerView;
import odoo.controls.recycler.EasyRecyclerViewAdapter;

public class FavouriteProductView extends BaseFragment implements
        EasyRecyclerViewAdapter.OnViewBindListener, EasyRecyclerViewAdapter.OnItemViewClickListener {
    public static final String KEY_FILTER_BY_CATEGORY = "filter_by_category";
    public static final String KEY_FILTER_DATA = "filter_data";
    private Bundle extras;
    private EasyRecyclerView productList;
    private ODataRow currency = null;
    private FavouriteProducts favouriteProducts;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.shop_favourite_products, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back_dark);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
        toolbar.setTitle("Products");
        toolbar.inflateMenu(R.menu.menu_home);
        OAppBarUtils.bindShopMenu(parent(), new int[]{R.id.menu_favourite_list}, toolbar.getMenu());
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                OAppBarUtils.onShopMenuItemClick(parent(), FavouriteProductView.this, item);
                return true;
            }
        });
        init();
    }


    private void init() {
        favouriteProducts = new FavouriteProducts(getContext());
        productList = (EasyRecyclerView) findViewById(R.id.productsList);
        List<Object> items = new ArrayList<>();
        items.addAll(db().select(null, "is_fav = ?", new String[]{"true"}));
        productList.setLayout(R.layout.shop_product_item);
        productList.changeCursor(items);
        productList.setOnViewBindListener(this);
        productList.setOnItemViewClickListener(this);
        productList.grid(2);
        productList.changeCursor(items);
    }

    @Override
    public List<ODrawerItem> drawerMenus(Context context) {
        return null;
    }

    @Override
    public Class<ProductTemplate> database() {
        return ProductTemplate.class;
    }

    @Override
    public void onViewBind(int position, View view, Object data) {
        ODataRow row = (ODataRow) data;

        if (currency == null && !row.getString("company_id").equals("false")) {
            currency = row.getM2ORecord("company_id")
                    .browse().getM2ORecord("currency_id").browse();
        }

        if (!row.getString("image_medium").equals("false")) {
            OControls.setImage(view, R.id.productImage,
                    BitmapUtils.getBitmapImage(getActivity(), row.getString("image_medium")));
        } else {
            OControls.setImage(view, R.id.productImage, R.drawable.no_image);
        }
        OControls.setText(view, R.id.productName, row.getString("name"));
        String price = row.getString("list_price");
        float discountPrice = row.getFloat("price");

        if (discountPrice != row.getFloat("list_price")) {
            view.findViewById(R.id.discountView).setVisibility(View.VISIBLE);
            OControls.setTextViewStrikeThrough(view, R.id.productOriginalPrice);
            OControls.setText(view, R.id.productOriginalPrice, getPriceWithCurrency(price));
            float percent = Math.round(100 - (discountPrice / row.getFloat("list_price") * 100));
            OControls.setText(view, R.id.productDiscount, percent + "% off");
            price = discountPrice + "";
        } else {
            view.findViewById(R.id.discountView).setVisibility(View.INVISIBLE);
        }
        OControls.setText(view, R.id.productPrice, getPriceWithCurrency(price));
        toggleFavIcon(favouriteProducts.isFavourite(row.getInt(OColumn.ROW_ID)),
                view.findViewById(R.id.favorite_layout));
        view.findViewById(R.id.favorite_layout).setTag(row);
        view.findViewById(R.id.favorite_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ODataRow r = (ODataRow) v.getTag();
                boolean isFav = favouriteProducts.toggleFavourite(r.getInt(OColumn.ROW_ID));
                toggleFavIcon(isFav, v);
                String message = (isFav) ? _s(R.string.toast_added_to_favourite_list) :
                        _s(R.string.toast_removed_from_favourite_list);
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleFavIcon(boolean isFav, View parent) {
        int color = _c(R.color.theme_primary);
        if (isFav) {
            color = _c(R.color.android_orange_dark);
        }
        ImageView icon = (ImageView) parent.findViewById(R.id.favorite_icon);
        icon.setColorFilter(color);
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
    public void onItemViewClick(int position, View view, Object data) {
        ODataRow row = (ODataRow) data;
        startFragment(new ProductDetail(), true, row.getPrimaryBundleData());
    }
}
