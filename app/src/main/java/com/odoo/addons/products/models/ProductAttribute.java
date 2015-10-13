package com.odoo.addons.products.models;

import android.content.Context;

import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OSelection;
import com.odoo.core.orm.fields.types.OVarchar;

public class ProductAttribute extends OModel {

    OColumn name = new OColumn("Name", OVarchar.class);
    OColumn type = new OColumn("Type", OSelection.class)
            .addSelection("radio", "Radio")
            .addSelection("select", "Select")
            .addSelection("color", "Color")
            .addSelection("hidden", "Hidden");

    public ProductAttribute(Context context) {
        super(context, "product.attribute");
    }
}
