package com.odoo.addons.products.models;

import android.content.Context;

import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OFloat;

public class ProductAttributePrice extends OModel {
    OColumn product_tmpl_id = new OColumn("Product Template", ProductTemplate.class, OColumn.RelationType.ManyToOne);
    OColumn value_id = new OColumn("Product Attribute Value", ProductAttributeValue.class, OColumn.RelationType.ManyToOne);
    OColumn price_extra = new OColumn("Price Extra", OFloat.class).setDefaultValue(0);

    public ProductAttributePrice(Context context) {
        super(context, "product.attribute.price");
    }
}
