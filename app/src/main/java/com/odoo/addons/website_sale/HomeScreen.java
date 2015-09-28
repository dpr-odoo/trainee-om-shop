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

    private EasyRecyclerView layoutContainer = null;
    public static final int ITEM_CATEGORY_LIST = 1;

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

    private List<Object> getContainerItems() {
        List<Object> items = new ArrayList<>();
        items.add(ITEM_CATEGORY_LIST);
        return items;
    }

    private void init(View view) {
        final List<Object> rows = getContainerItems();
        layoutContainer = (EasyRecyclerView) view.findViewById(R.id.layoutContainer);
        layoutContainer.setLayout(R.layout.shop_recycler_container);
        layoutContainer.changeCursor(rows);
        layoutContainer.setOnViewBindListener(new EasyRecyclerViewAdapter.OnViewBindListener() {
            @Override
            public void onViewBind(int position, View view, Object data) {
                int item = (int) data;
                ViewGroup container = (ViewGroup) view;
                switch (item) {
                    case ITEM_CATEGORY_LIST:
                        container.addView(getCategoryItemView(container));
                        break;
                }
            }
        });

    }

    private View getCategoryItemView(ViewGroup container) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.shop_home_category_list_view, container, false);
        final List<Object> items = new ArrayList<>();
        items.addAll(db().select(null, "parent_id is NULL", new String[]{}, "sequence"));

        ExpandableListControl listControl = (ExpandableListControl) view.findViewById(R.id.categoriesList);
        listControl.setExpandableListItemClickListener(new ExpandableListControl.ExpandableListItemClickListener() {
            @Override
            public void onItemViewClick(int position, View view) {

            }
        });
        ExpandableListControl.ExpandableListAdapter adapter = listControl.getAdapter(R.layout.shop_home_category_item, items, new ExpandableListControl.ExpandableListAdapterGetViewListener() {
            @Override
            public View getView(int position, View view, ViewGroup parent) {
                ODataRow row = (ODataRow) items.get(position);
                if (!row.getString("image_medium").equals("false")) {
                    OControls.setImage(view, R.id.categoryIcon,
                            BitmapUtils.getBitmapImage(getActivity(), row.getString("image_medium")));
                } else {
                    OControls.setImage(view, R.id.categoryIcon, R.drawable.ic_action_arrow_right);
                }
                OControls.setText(view, R.id.title, row.getString("name"));
                return view;
            }
        });
        adapter.notifyDataSetChanged(items);
        return view;
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
