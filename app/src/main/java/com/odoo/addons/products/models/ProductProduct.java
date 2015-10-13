package com.odoo.addons.products.models;

import android.content.Context;

import com.odoo.addons.website_sale.models.ProductPublicCategory;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OBoolean;
import com.odoo.core.orm.fields.types.OFloat;
import com.odoo.core.orm.fields.types.OInteger;
import com.odoo.core.orm.fields.types.OVarchar;

public class ProductProduct extends OModel {

    OColumn name = new OColumn("Name", OVarchar.class);
    OColumn lst_price = new OColumn("Price", OFloat.class).setDefaultValue(0);
    OColumn website_published = new OColumn("Published", OBoolean.class).setDefaultValue("true");
    OColumn website_sequence = new OColumn("Sequence", OInteger.class).setDefaultValue(0);

    // Relation columns
    OColumn product_tmpl_id = new OColumn("Template", ProductTemplate.class,
            OColumn.RelationType.ManyToOne);

    OColumn public_categ_ids = new OColumn("Public category", ProductPublicCategory.class,
            OColumn.RelationType.ManyToMany);

    OColumn attribute_value_ids = new OColumn("Attributes", ProductAttributeValue.class, OColumn.RelationType.ManyToMany);

    public ProductProduct(Context context) {
        super(context, "product.product");
    }
}
