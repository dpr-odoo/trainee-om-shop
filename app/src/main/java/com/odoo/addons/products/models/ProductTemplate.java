package com.odoo.addons.products.models;

import android.content.Context;

import com.odoo.addons.website_sale.models.ProductPublicCategory;
import com.odoo.base.addons.res.ResCompany;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OBlob;
import com.odoo.core.orm.fields.types.OBoolean;
import com.odoo.core.orm.fields.types.OFloat;
import com.odoo.core.orm.fields.types.OInteger;
import com.odoo.core.orm.fields.types.OText;
import com.odoo.core.orm.fields.types.OVarchar;

import odoo.helper.ODomain;

public class ProductTemplate extends OModel {

    OColumn name = new OColumn("Name", OVarchar.class);
    OColumn image_medium = new OColumn("Image", OBlob.class).setDefaultValue("false");
    OColumn list_price = new OColumn("Sale price", OFloat.class).setDefaultValue(0);
    OColumn price = new OColumn("Sale price", OFloat.class).setDefaultValue(0);
    OColumn description = new OColumn("Description", OText.class).setDefaultValue("");
    OColumn warranty = new OColumn("Warranty", OFloat.class).setDefaultValue("0");
    OColumn sale_delay = new OColumn("Lead time", OFloat.class).setDefaultValue("7");

    OColumn website_sequence = new OColumn("Sequence", OInteger.class).setDefaultValue(0);
    OColumn website_published = new OColumn("Published", OBoolean.class).setDefaultValue("true");

    //Relation columns
    OColumn public_categ_ids = new OColumn("Public category", ProductPublicCategory.class,
            OColumn.RelationType.ManyToMany);

    OColumn company_id = new OColumn("Company", ResCompany.class, OColumn.RelationType.ManyToOne);

    public ProductTemplate(Context context) {
        super(context, "product.template");
    }

    @Override
    public ODomain defaultDomain() {
        ODomain domain = new ODomain();
        domain.add("website_published", "=", true);
        return domain;
    }
}
