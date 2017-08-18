package com.example.chance.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.chance.inventoryapp.Data.InventoryContract.InventoryEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = EditorActivity.class.getSimpleName();
    private static final int EXISTING_INVENTORY_LOADER = 0;
    private EditText mItemName, mItemPrice, mItemQuantity,
            mItemSupplier;
    private ImageView mItemImage;
    private Uri mCurrentItemUri;
    private String mImagePath;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.save_item:
            saveItem();
            return true;
            case R.id.delete_item:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        setTitle("Add item");
        mItemName = (EditText) findViewById(R.id.item_name_edit_text);
        mItemPrice = (EditText) findViewById(R.id.item_price_edit_text);
        mItemQuantity = (EditText) findViewById(R.id.item_quantity_edit_text);
        mItemSupplier = (EditText) findViewById(R.id.item_supplier_edit_text);


    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItem = menu.findItem(R.id.delete_item);
        menuItem.setVisible(false);
        return true;
    }

    private void saveItem() {

        if (mCurrentItemUri == null
                && TextUtils.isEmpty(mItemName.getText()) || TextUtils.isEmpty(mItemSupplier.getText())) {
            Toast.makeText(this, "One or more fields are empty.", Toast.LENGTH_SHORT).show();
            Log.e(LOG_TAG, "Error validating input.");
            return;
        }

        String itemName = mItemName.getText().toString().trim();
        double itemPrice = Double.parseDouble(mItemPrice.getText().toString().trim());
        int itemQuantity = Integer.parseInt(mItemQuantity.getText().toString().trim());
        String imageResourceId = mImagePath;
        String supplier = mItemSupplier.getText().toString().trim();
        if (TextUtils.isEmpty(String.valueOf(itemPrice)))
            itemPrice = 0.0f;
        if (TextUtils.isEmpty(String.valueOf(itemQuantity)))
            itemQuantity = 0;

        ContentValues cv = new ContentValues();
        cv.put(InventoryEntry.COLUMN_IMAGE_ID, imageResourceId);
        cv.put(InventoryEntry.COLUMN_ITEM_NAME, itemName);
        cv.put(InventoryEntry.COLUMN_ITEM_PRICE, itemPrice);
        cv.put(InventoryEntry.COLUMN_ITEM_QUANTITY, itemQuantity);
        cv.put(InventoryEntry.COLUMN_ITEM_SUPPLIER, supplier);


        if (mCurrentItemUri == null) {
            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, cv);

            if (newUri == null) {
                Toast.makeText(this, "Error, error inserting",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "item saved",
                        Toast.LENGTH_SHORT).show();
            }
        } else {

            int rowsAffected = getContentResolver().update(mCurrentItemUri, cv, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, "Item not saved",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Item edited",
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    private void deleteItem() {
        if (mCurrentItemUri != null) {
               int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);
                Log.e(LOG_TAG, "Deleting: "+rowsDeleted);
               if (rowsDeleted == 0) {
                   Toast.makeText(this, "Error deleting item",
                           Toast.LENGTH_SHORT).show();
               } else {
                   Toast.makeText(this, "Item deleted",
                           Toast.LENGTH_SHORT).show();
               }

        }
        finish();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_IMAGE_ID,
                InventoryEntry.COLUMN_ITEM_NAME,
                InventoryEntry.COLUMN_ITEM_PRICE,
                InventoryEntry.COLUMN_ITEM_QUANTITY,
                InventoryEntry.COLUMN_ITEM_SHIPMENT,
                InventoryEntry.COLUMN_ITEM_SUPPLIER,
                InventoryEntry.COLUMN_ITEM_SALES
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentItemUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_NAME);
            int imageColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_IMAGE_ID);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_SUPPLIER);

            String image = cursor.getColumnName(imageColumnIndex);
            String name = cursor.getString(nameColumnIndex);
            double price = cursor.getDouble(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);

            ImageView img = (ImageView) findViewById(R.id.image_view);



            mItemName.setText(name);
            mItemPrice.setText(Double.toString(price));
            mItemQuantity.setText(Integer.toString(quantity));
            mItemSupplier.setText(supplier);

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mItemName.setText("");
        mItemPrice.setText("");
        mItemQuantity.setText("");
        mItemSupplier.setText("");

    }


}
