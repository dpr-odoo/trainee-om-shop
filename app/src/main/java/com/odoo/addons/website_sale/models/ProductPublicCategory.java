package com.odoo.addons.website_sale.models;

import android.content.Context;

import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OBlob;
import com.odoo.core.orm.fields.types.OInteger;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

public class ProductPublicCategory extends OModel {

    OColumn name = new OColumn("Name", OVarchar.class);
    OColumn parent_id = new OColumn("Parent category", ProductPublicCategory.class, OColumn.RelationType.ManyToOne);
    OColumn sequence = new OColumn("Sequence", OInteger.class).setDefaultValue(0);
    OColumn image_medium = new OColumn("Image", OBlob.class).setDefaultValue("false");

    public ProductPublicCategory(Context context) {
        super(context, "product.public.category");
    }
}
