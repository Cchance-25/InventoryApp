package com.example.chance.inventoryapp.Data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by chance on 8/17/17.
 */

public class InventoryContract {

    // Content authority, the path for the entire app content provider
    public static final String CONTENT_AUTHORITY = "com.example.chance.inventoryapp";

    // Database name
    public static final String INVENTORY_PATH = "inventory_items";

    // Base URI content
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    private InventoryContract() {
        // No one to make an object of this class
    }

    // Make a class for each table
    public static final class InventoryEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, INVENTORY_PATH);


        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + INVENTORY_PATH;


        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + INVENTORY_PATH;

        // Table name
        public static final String TABLE_NAME = "inventory_items";
        // Id column
        public static final String _ID = BaseColumns._ID;
        // Image source id column
        public static final String COLUMN_IMAGE_ID = "image_id";
        // Name column
        public static final String COLUMN_ITEM_NAME = "item_name";
        public static final String COLUMN_ITEM_PRICE = "item_price";
        public static final String COLUMN_ITEM_QUANTITY = "item_quantity";
        public static final String COLUMN_ITEM_SUPPLIER = "item_supplier";
        public static final String COLUMN_ITEM_SALES = "item_sales";
        public static final String COLUMN_ITEM_SHIPMENT = "item_shipment";

    }

}
