package com.odoo.addons.website_sale.models;

import android.content.Context;

import com.odoo.addons.products.models.ProductTemplate;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;

public class RecentViewProducts extends OModel {

    OColumn product_id = new OColumn("Product", ProductTemplate.class, OColumn.RelationType.ManyToOne);

    public RecentViewProducts(Context context) {
        super(context, "recent.products");
    }
}
