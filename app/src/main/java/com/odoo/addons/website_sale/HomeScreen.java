package com.odoo.addons.website_sale;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.odoo.R;
import com.odoo.addons.website_sale.models.ProductPublicCategory;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.support.addons.fragment.BaseFragment;
import com.odoo.core.support.drawer.ODrawerItem;
import com.odoo.core.utils.BitmapUtils;
import com.odoo.core.utils.OAppBarUtils;
import com.odoo.core.utils.OControls;
import com.odoo.core.utils.logger.OLog;

import java.util.ArrayList;
import java.util.List;

import odoo.controls.ExpandableListControl;
import odoo.controls.recycler.EasyRecyclerView;
import odoo.controls.recycler.EasyRecyclerViewAdapter;

public class HomeScreen extends BaseFragment {

    private EasyRecyclerView categoriesList = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.shop_home_screen, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_menu_dark);
        OAppBarUtils.setChildAppBar(parent(), toolbar, true);
        setTitle(_s(R.string.label_welcome));
        init(view);
    }

    private void init(View view) {
        final List<Object> rows = new ArrayList<>();
        rows.addAll(db().select(null, "parent_id is NULL", new String[]{}, "sequence"));
        categoriesList = (EasyRecyclerView) view.findViewById(R.id.categories);
        categoriesList.setLayout(R.layout.shop_home_category_item);
        categoriesList.changeCursor(rows);
        categoriesList.setOnItemViewClickListener(new EasyRecyclerViewAdapter.OnItemViewClickListener() {
            @Override
            public void onItemViewClick(int position, View view, Object data) {

            }
        });
        categoriesList.setOnViewBindListener(new EasyRecyclerViewAdapter.OnViewBindListener() {
            @Override
            public void onViewBind(int position, View view, Object data) {
                ODataRow row = (ODataRow) rows.get(position);
                if (!row.getString("image_medium").equals("false")) {
                    OControls.setImage(view, R.id.categoryIcon,
                            BitmapUtils.getBitmapImage(getActivity(), row.getString("image_medium")));
                } else {
                    OControls.setImage(view, R.id.categoryIcon, R.drawable.ic_action_arrow_right);
                }
                OControls.setText(view, R.id.title, row.getString("name"));
            }
        });

//        categoriesList = (ExpandableListControl) view.findViewById(R.id.categories);
//
//        ExpandableListControl.ExpandableListAdapter adapter = categoriesList.getAdapter(
//                R.layout.shop_home_category_item, rows, new ExpandableListControl.ExpandableListAdapterGetViewListener() {
//                    @Override
//                    public View getView(int position, View view, ViewGroup parent) {
//                        ODataRow row = (ODataRow) rows.get(position);
//                        OControls.setText(view, R.id.title, row.getString("name"));
//                        return view;
//                    }
//                });
//        adapter.notifyDataSetChanged(rows);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home, menu);
    }
}
