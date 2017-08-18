package com.example.chance.inventoryapp.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.chance.inventoryapp.Data.InventoryContract.InventoryEntry;

/**
 * Created by chance on 8/17/17.
 */

public class InventoryProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();

    /**
     * URI matcher code for the content URI for the inventory table
     */
    private static final int INVENTORY_ITEMS = 100;

    /**
     * URI matcher code for the content URI for a single item in the inventory table
     */
    private static final int INVENTORY_ITEM_ID = 101;


    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY,
                InventoryContract.INVENTORY_PATH, INVENTORY_ITEMS);


        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY,
                InventoryContract.INVENTORY_PATH + "/#", INVENTORY_ITEM_ID);
    }


    private InventoryDbHelper mDbHelper;


    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);

        switch (match) {

            case INVENTORY_ITEMS:
                cursor = db.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case INVENTORY_ITEM_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = db.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);

        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY_ITEMS:
                return insertItem(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertItem(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(InventoryEntry.COLUMN_ITEM_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Item requires a name");
        }

        double price = values.getAsDouble(InventoryEntry.COLUMN_ITEM_PRICE);

        if (price < 0) {
            throw new IllegalArgumentException("Not a valid price");
        }

        Integer quantity = values.getAsInteger(InventoryEntry.COLUMN_ITEM_QUANTITY);
        if (quantity < 0) {
            throw new IllegalArgumentException("Not a valid quantity");
        }

        String shipment = values.getAsString(InventoryEntry.COLUMN_ITEM_SHIPMENT);
        if (shipment == null) {
            throw new IllegalArgumentException("Shipment empty");
        }
        String supplier = values.getAsString(InventoryEntry.COLUMN_ITEM_SUPPLIER);
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier empty");
        }

        String sales = values.getAsString(InventoryEntry.COLUMN_ITEM_SALES);
        if (sales == null) {
            throw new IllegalArgumentException("Shipment empty");
        }

        String imageId = values.getAsString(InventoryEntry.COLUMN_IMAGE_ID);

//        if (imageId == null) {
//            throw new IllegalArgumentException("wrong image empty");
//        }

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new pet with the given values
        long id = database.insert(InventoryEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY_ITEMS:
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case INVENTORY_ITEM_ID:
                // Delete a single row given by the ID in the URI
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY_ITEMS:
                return updateItem(uri, values, selection, selectionArgs);
            case INVENTORY_ITEM_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateItem(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateItem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(InventoryEntry.COLUMN_ITEM_NAME)) {
            String name = values.getAsString(InventoryEntry.COLUMN_ITEM_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Item requires a name");
            }
        }

        if (values.containsKey(InventoryEntry.COLUMN_ITEM_PRICE)) {
            double price = values.getAsDouble(InventoryEntry.COLUMN_ITEM_PRICE);
            if (price < 0) {
                throw new IllegalArgumentException("Not a valid price");
            }
        }

        if (values.containsKey(InventoryEntry.COLUMN_ITEM_QUANTITY)) {
            Integer quantity = values.getAsInteger(InventoryEntry.COLUMN_ITEM_QUANTITY);
            if (quantity < 0) {
                throw new IllegalArgumentException("Not a valid quantity");
            }
        }

        if (values.containsKey(InventoryEntry.COLUMN_ITEM_SHIPMENT)) {
            String shipment = values.getAsString(InventoryEntry.COLUMN_ITEM_SHIPMENT);
            if (shipment == null) {
                throw new IllegalArgumentException("Shipment empty");
            }
        }
        if (values.containsKey(InventoryEntry.COLUMN_ITEM_SUPPLIER)) {
            String supplier = values.getAsString(InventoryEntry.COLUMN_ITEM_SUPPLIER);
            if (supplier == null) {
                throw new IllegalArgumentException("Supplier empty");
            }
        }
        if (values.containsKey(InventoryEntry.COLUMN_ITEM_SALES)) {
            String sales = values.getAsString(InventoryEntry.COLUMN_ITEM_SALES);
            if (sales == null) {
                throw new IllegalArgumentException("Shipment empty");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(InventoryEntry.TABLE_NAME, values, selection, selectionArgs);


        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }


}
