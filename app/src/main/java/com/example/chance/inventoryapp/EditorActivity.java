package com.example.chance.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.chance.inventoryapp.Data.InventoryContract.InventoryEntry;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    static final int REQUEST_TAKE_PHOTO = 1;
    private static final String LOG_TAG = EditorActivity.class.getSimpleName();
    private static final int EXISTING_INVENTORY_LOADER = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 60;
    private EditText mItemName, mItemPrice, mItemQuantity,
            mItemSupplier;
    private ImageView mItemImage;
    private Uri mCurrentItemUri;
    private String mCurrentPhotoPath;


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
        mItemImage = (ImageView) findViewById(R.id.image_view);
        Button btn = (Button) findViewById(R.id.btn_add_image);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });


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
                && TextUtils.isEmpty(mItemName.getText())
                || TextUtils.isEmpty(mItemSupplier.getText())
                || mItemImage.getDrawable() == null) {
            Toast.makeText(this, "One or more fields are empty.", Toast.LENGTH_SHORT).show();
            Log.e(LOG_TAG, "Error validating input.");
            return;
        }

        String itemName = mItemName.getText().toString().trim();
        double itemPrice = Double.parseDouble(mItemPrice.getText().toString().trim());
        int itemQuantity = Integer.parseInt(mItemQuantity.getText().toString().trim());
        String supplier = mItemSupplier.getText().toString().trim();
        if (TextUtils.isEmpty(String.valueOf(itemPrice)))
            itemPrice = 0.0f;
        if (TextUtils.isEmpty(String.valueOf(itemQuantity)))
            itemQuantity = 0;

        ContentValues cv = new ContentValues();
        cv.put(InventoryEntry.COLUMN_IMAGE_ID, mCurrentPhotoPath);
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

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

        try {
            createImageFile();
            Log.e(LOG_TAG, mCurrentPhotoPath);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error creating image file.", e);
        }
    }

    private void dispatchTakePictureIntent1() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e(LOG_TAG, "Error creating photoFile. ", ex);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.chance.inventoryapp",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mItemImage.setImageBitmap(imageBitmap);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }



}
