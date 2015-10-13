package com.odoo.addons.website_sale;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.odoo.R;
import com.odoo.addons.products.Products;
import com.odoo.addons.website_sale.models.ProductPublicCategory;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.support.addons.fragment.BaseFragment;
import com.odoo.core.support.drawer.ODrawerItem;
import com.odoo.core.utils.BitmapUtils;
import com.odoo.core.utils.OAppBarUtils;
import com.odoo.core.utils.OControls;

import java.util.ArrayList;
import java.util.List;

import odoo.controls.BezelImageView;
import odoo.controls.recycler.EasyRecyclerView;
import odoo.controls.recycler.EasyRecyclerViewAdapter;

public class ProductSubCategories extends BaseFragment implements EasyRecyclerViewAdapter.OnViewBindListener,
        EasyRecyclerViewAdapter.OnItemViewClickListener {

    private Bundle data;
    private ODataRow category;
    private EasyRecyclerView subCategories;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.shop_home_sub_category, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
        toolbar.inflateMenu(R.menu.menu_home);
        OAppBarUtils.bindShopMenu(parent(), false, toolbar.getMenu());
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                OAppBarUtils.onShopMenuItemClick(parent(), ProductSubCategories.this, item);
                return true;
            }
        });
        data = getArguments();
        category = db().browse(data.getInt(OColumn.ROW_ID));
        setTitle(toolbar);
        init();
    }

    private void init() {
        List<Object> items = new ArrayList<>();
        subCategories = (EasyRecyclerView) findViewById(R.id.subCategoriesList);
        subCategories.setLayout(R.layout.shop_home_sub_category_item);
        subCategories.changeCursor(items);
        subCategories.setOnViewBindListener(this);
        subCategories.setOnItemViewClickListener(this);
        items.addAll(findAllChild(category.getInt(OColumn.ROW_ID)));
        subCategories.changeCursor(items);
    }

    private List<Object> findAllChild(int parent_id) {
        List<Object> items = new ArrayList<>();
        List<ODataRow> rows = db().select(new String[]{"name", "parent_id", "image_medium"}, "parent_id = ?",
                new String[]{parent_id + ""}, "sequence");
        for (ODataRow row : rows) {
            boolean hasChild = db().count("parent_id = ?", new String[]{row.getInt(OColumn.ROW_ID) + ""}) > 0;
            row.put("has_child", hasChild);
            items.add(row);
            if (hasChild) {
                items.addAll(findAllChild(row.getInt(OColumn.ROW_ID)));
            }
        }
        return items;
    }

    private void setTitle(Toolbar toolbar) {
        CollapsingToolbarLayout collapsing_toolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsing_toolbar.setTitle(category.getString("name"));
        toolbar.setTitle(category.getString("name"));
        Bitmap cover;
        if (category.getString("image_medium").equals("false")) {
            cover = BitmapUtils.getAlphabetImage(getContext(), category.getString("name"));
        } else {
            cover = BitmapUtils.getBitmapImage(getContext(), category.getString("image_medium"));
        }

        Palette.Builder paletteBuilder = new Palette.Builder(cover);
        Palette palette = paletteBuilder.generate();
        ImageView imageView = (ImageView) findViewById(R.id.image);
        imageView.setImageBitmap(cover);
        if (palette != null) {
            int color = palette.getDarkVibrantColor(_c(R.color.theme_secondary_dark));
            imageView.setBackgroundColor(color);
        }

    }

    @Override
    public List<ODrawerItem> drawerMenus(Context context) {
        return null;
    }

    @Override
    public Class<ProductPublicCategory> database() {
        return ProductPublicCategory.class;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getChildFragmentManager().popBackStack();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewBind(int position, View view, Object data) {
        ODataRow row = (ODataRow) data;
        String[] display_names = row.getString("name").split(" / ");
        OControls.setText(view, android.R.id.text1, display_names[display_names.length - 1]);
        view.findViewById(R.id.subCategoryPadding).setVisibility(
                row.getInt("parent_id") != category.getInt(OColumn.ROW_ID) ? View.VISIBLE : View.GONE);
        BezelImageView icon = (BezelImageView) view.findViewById(R.id.categoryIcon);
        if (row.getString("image_medium").equals("false")) {
            icon.setImageResource(R.drawable.ic_action_image_lens);
        } else {
            icon.setImageBitmap(BitmapUtils.getBitmapImage(getContext(), row.getString("image_medium")));
        }
        icon.setVisibility(row.getInt("parent_id") == category.getInt(OColumn.ROW_ID) ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onItemViewClick(int position, View view, Object data) {
        ODataRow row = (ODataRow) data;
        Bundle extra = new Bundle();
        extra.putBoolean(Products.KEY_FILTER_BY_CATEGORY, true);
        extra.putBundle(Products.KEY_FILTER_DATA, row.getPrimaryBundleData());
        startFragment(new Products(), true, extra);
    }
}
