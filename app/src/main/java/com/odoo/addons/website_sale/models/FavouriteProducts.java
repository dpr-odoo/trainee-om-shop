package com.odoo.addons.website_sale.models;

import android.content.Context;

import com.odoo.addons.products.models.ProductTemplate;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.fields.OColumn;

public class FavouriteProducts extends OModel {

    OColumn product_id = new OColumn("Product ID", ProductTemplate.class,
            OColumn.RelationType.ManyToOne);

    public FavouriteProducts(Context context) {
        super(context, "favourite.products");
    }

    public boolean isFavourite(int product_id) {
        return (count("product_id = ?", new String[]{product_id + ""}) > 0);
    }

    public boolean toggleFavourite(int product_id) {
        if (isFavourite(product_id)) {
            delete("product_id = ?", new String[]{product_id + ""}, true);
            return false;
        }
        OValues values = new OValues();
        values.put("product_id", product_id);
        insert(values);
        return true;
    }
}
