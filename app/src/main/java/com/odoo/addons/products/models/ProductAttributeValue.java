package com.odoo.addons.products.models;

import android.content.Context;

import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OVarchar;

public class ProductAttributeValue extends OModel {

    OColumn name = new OColumn("Name", OVarchar.class);
    OColumn attribute_id = new OColumn("Attribute", ProductAttribute.class, OColumn.RelationType.ManyToOne);


    public ProductAttributeValue(Context context) {
        super(context, "product.attribute.value");
    }
}
