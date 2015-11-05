package com.odoo.addons.products.models;

import android.content.Context;

import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OFloat;
import com.odoo.core.orm.fields.types.OVarchar;

public class ProductAttributeValue extends OModel {

    OColumn name = new OColumn("Name", OVarchar.class);
    OColumn attribute_id = new OColumn("Attribute", ProductAttribute.class, OColumn.RelationType.ManyToOne);
    OColumn price_ids = new OColumn("Prices", ProductAttributePrice.class, OColumn.RelationType.OneToMany).setRelatedColumn("value_id")
            .setRecordSyncLimit(-1);
    OColumn product_ids = new OColumn("Products", ProductProduct.class, OColumn.RelationType.ManyToMany)
            .setRecordSyncLimit(-1);
    OColumn price_extra = new OColumn("Price Extra", OFloat.class).setDefaultValue(0);

    public ProductAttributeValue(Context context) {
        super(context, "product.attribute.value");
    }
}
