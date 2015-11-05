package com.odoo.addons.products.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.odoo.R;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.utils.OControls;
import com.odoo.core.utils.OResource;

import java.util.ArrayList;
import java.util.List;

import odoo.controls.ExpandableListControl;

public class ProductAttributeValuesView implements View.OnClickListener {
    private Context mContext;
    private ODataRow mAttr = null;
    private List<ODataRow> mAttrValues = new ArrayList<>();
    private int selectedValue = -1;
    private View selectedView = null;
    private ExpandableListControl optionsView;
    private LinearLayout layout;
    private OnAttributeSelectListener mOnAttributeSelectListener;

    public ProductAttributeValuesView(Context context, ViewGroup parent, ODataRow attribute, List<ODataRow> values) {
        mContext = context;
        mAttr = attribute;
        mAttrValues = values;
        layout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.shop_cart_item_options, parent, false);
        optionsView = (ExpandableListControl) layout.findViewById(R.id.options);
    }

    public View getView() {
        OControls.setText(layout, R.id.title, String.format(OResource.string(mContext, R.string.title_select),
                mAttr.getString("name")));
        bindOptions();
        return layout;
    }

    private void bindOptions() {
        optionsView.removeAllViews();
        for (ODataRow val : mAttrValues) {
            LinearLayout item = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.shop_cart_attr_option_value_item, optionsView, false);
            OControls.setText(item, R.id.attrValueName, val.getString("name"));
            item.setTag(val);
            item.setOnClickListener(this);
            optionsView.addView(item);
        }
    }

    @Override
    public void onClick(View v) {
        ODataRow attr = (ODataRow) v.getTag();
        if (selectedValue != -1 && selectedView != null) {
            selectedView.setBackgroundResource(R.drawable.attr_value_item);
        }
        selectedValue = attr.getInt(OColumn.ROW_ID);
        v.setBackgroundResource(R.drawable.attr_value_item_selected);
        selectedView = v;
        if (mOnAttributeSelectListener != null) {
            mOnAttributeSelectListener.onAttributeValueSelect(mAttr, attr);
        }
    }

    public void setOnAttributeSelectListener(OnAttributeSelectListener callback) {
        mOnAttributeSelectListener = callback;
    }

    public interface OnAttributeSelectListener {
        void onAttributeValueSelect(ODataRow attribute, ODataRow value);
    }


}
