package com.example.chance.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chance.inventoryapp.Data.InventoryContract.InventoryEntry;

import java.io.File;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = EditorActivity.class.getSimpleName();
    private static final int EXISTING_INVENTORY_LOADER = 0;
    private TextView mItemName;
    private TextView mItemPrice;
    private TextView mItemQuantity;
    private TextView mItemSupplier;
    private Button addBtn;
    private Button removeBtn;
    private Uri mCurrentItemUri;
    private Cursor mCurrentCursor;
    private int mCurrentQuantity;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);


        mItemName = (TextView) findViewById(R.id.product_name);
        mItemPrice = (TextView) findViewById(R.id.price_value);
        mItemQuantity = (TextView) findViewById(R.id.quantity_value);
        mItemSupplier = (TextView) findViewById(R.id.supplier_name);
        addBtn = (Button) findViewById(R.id.add_quantity);
        removeBtn = (Button) findViewById(R.id.remove_quantity);
        image = (ImageView) findViewById(R.id.product_image);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addQuantity();
            }
        });

        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeQuantity();
            }
        });

        Intent i = getIntent();
        mCurrentItemUri = i.getData();

        getLoaderManager().initLoader(EXISTING_INVENTORY_LOADER, null, this);

        Button order = (Button) findViewById(R.id.order);
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_EMAIL, "inventroy@app.com");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Order");
                intent.putExtra(Intent.EXTRA_TEXT, mCurrentItemUri.toString());
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }

            }
        });
    }

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
                updateQuantity();
                Toast.makeText(this, "Quantity Updated. ", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.delete_item:
                AlertDialog diaBox = deleteDialog();
                diaBox.show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteItem() {
        if (mCurrentItemUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);
            Log.e(LOG_TAG, "Deleting: " + rowsDeleted);
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
                InventoryEntry.COLUMN_ITEM_SUPPLIER,
        };

        return new CursorLoader(this,
                mCurrentItemUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        mCurrentCursor = cursor;
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_SUPPLIER);
            int imageColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_IMAGE_ID);

            String name = cursor.getString(nameColumnIndex);
            double price = cursor.getDouble(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            String img = cursor.getString(imageColumnIndex);
            mCurrentQuantity = quantity;
            mItemName.setText(name);
            mItemPrice.setText(Double.toString(price));
            mItemQuantity.setText(Integer.toString(quantity));
            mItemSupplier.setText(supplier);
            //image.setImageURI(Uri.parse(getImage(img)));
            // image.setImageResource(R.drawable.ic_add_black_24dp);
            image.setImageBitmap(getImage(img));
            //Log.e("TAG", getImage(img));


        }
    }

    Bitmap getImage(String path) {

        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        Bitmap bitmap = BitmapFactory.decodeFile(contentUri.toString());

        return bitmap;

    }

    void updateQuantity() {
        ContentValues cv = new ContentValues();
        cv.put(InventoryEntry.COLUMN_ITEM_QUANTITY, mCurrentQuantity);
        getContentResolver().update(mCurrentItemUri,
                cv,
                InventoryEntry.COLUMN_ITEM_QUANTITY,
                null);
    }

    void addQuantity() {
        mCurrentQuantity++;
        mItemQuantity.setText(String.valueOf(mCurrentQuantity));
    }

    void removeQuantity() {
        if (mCurrentQuantity > 0) {
            mCurrentQuantity--;
            mItemQuantity.setText(String.valueOf(mCurrentQuantity));
        } else {
            Toast.makeText(getApplicationContext(), "Quantity can't be negative!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mItemName.setText("");
        mItemPrice.setText("");
        mItemQuantity.setText("");
        mItemSupplier.setText("");
    }

    private AlertDialog deleteDialog() {
        AlertDialog deleteConfirmation = new AlertDialog.Builder(this)
                .setTitle("Delete")
                .setMessage("Are you sure you want to delete this item?")
                .setIcon(android.R.drawable.ic_menu_delete)

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteItem();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return deleteConfirmation;

    }


}
