package com.example.chance.inventoryapp.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.chance.inventoryapp.Data.InventoryContract.InventoryEntry;

/**
 * Created by chance on 8/17/17.
 */

public class InventoryDbHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG=  InventoryDbHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 1;

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        /*
        public static final String COLUMN_ITEM_QUANTITY = "item_quantity";
        public static final String COLUMN_ITEM_SUPPLIER = "item_supplier";
        public static final String COLUMN_ITEM_SALES = "item_sales";
        public static final String COLUMN_ITEM_SHIPMENT = "item_shipment";

        *  CREATE TABLE inventory ( _ID INTEGER PRIMARY KEY AUTOINCREMENT, PICTURE INTEGER NOT NULL
        *  DEFAULT -1, NAME TEXT NOT NULL, PRICE DOUBLE NOT NULL DEFAULT 0, QUANTITY INTEGER NOT NULL
        *  DEFAULT 0, ITEM_SALES TEXT NOT NULL, SHIPMENT TEXT NOT NULL
        *
        * */

        String CREATE_INVENTORY_TABLES = "CREATE TABLE "+ InventoryEntry.TABLE_NAME + "("
                + InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryEntry.COLUMN_IMAGE_ID + " BLOB, "
                + InventoryEntry.COLUMN_ITEM_NAME + " TEXT NOT NULL, "
                + InventoryEntry.COLUMN_ITEM_PRICE + " DOUBLE NOT NULL DEFAULT 0, "
                + InventoryEntry.COLUMN_ITEM_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + InventoryEntry.COLUMN_ITEM_SUPPLIER + " TEXT NOT NULL);";

        db.execSQL(CREATE_INVENTORY_TABLES);

        Log.e(LOG_TAG, "Database created. ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
