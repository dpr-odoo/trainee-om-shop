package com.odoo.addons.cart.models;

import android.content.Context;

import com.odoo.addons.products.models.ProductProduct;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.fields.OColumn;

public class ShopCart extends OModel {

    OColumn product_id = new OColumn("Product Id", ProductProduct.class, OColumn.RelationType.ManyToOne);

    public ShopCart(Context context) {
        super(context, "shop.cart");
    }

    public int counter() {
        return count(null, null);
    }

    public void addToCart(int product_tmpl_id) {
        if (count("product_id = ?", new String[]{product_tmpl_id + ""}) == 0) {
            OValues values = new OValues();
            values.put("product_id", product_tmpl_id);
            insert(values);
        }
    }
}
