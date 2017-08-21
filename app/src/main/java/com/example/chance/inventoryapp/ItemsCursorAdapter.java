package com.example.chance.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chance.inventoryapp.Data.InventoryContract.InventoryEntry;

/**
 * Created by chance on 8/16/17.
 */

public class ItemsCursorAdapter extends CursorAdapter {


    // chmod 777 /data /data/data /data/data/com.application.package /data/data/com.application.package/*su

    public ItemsCursorAdapter(Context context, Cursor c) {
        super(context, c, false);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        TextView nameTextView = (TextView) view.findViewById(R.id.product_name_text_view);
        TextView priceTextView = (TextView) view.findViewById(R.id.product_price_value_text_view);
        TextView quantityTextView = (TextView) view.findViewById(R.id.product_quantity_value_text_view);


        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_QUANTITY);


        final String itemName = cursor.getString(nameColumnIndex);
        double itemPrice = cursor.getDouble(priceColumnIndex);
        int itemQuantity = cursor.getInt(quantityColumnIndex);

        Button btn = (Button) view.findViewById(R.id.sell_button);
        final int currentId = cursor.getInt(cursor.getColumnIndex(InventoryEntry._ID));
        final Uri currentUriId = Uri.withAppendedPath(InventoryEntry.CONTENT_URI, String.valueOf(currentId)); // Current URI
        final Uri currentUri = Uri.withAppendedPath(InventoryEntry.CONTENT_URI, String.valueOf(currentId)); // Current URI
        nameTextView.setText(itemName);
        priceTextView.setText(String.valueOf(itemPrice) + "$");
        quantityTextView.setText(String.valueOf(itemQuantity));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] projection = {
                        InventoryEntry._ID,
                        InventoryEntry.COLUMN_ITEM_QUANTITY
                };


                //cursor.moveToFirst();
                if (cursor.moveToPosition((int) ContentUris.parseId(currentUri) - 1)) {
                    Toast.makeText(context, "moved to postiion " + ContentUris.parseId(currentUri), Toast.LENGTH_SHORT).show();
                    Log.e("TAG", cursor.getColumnName(0) + " -> " + cursor.getColumnName(1));
                    Log.e("TAG", cursor.getInt(0) + " -> " + cursor.getInt(1));
                    int columnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_QUANTITY);
                    int rowQuan = cursor.getInt(columnIndex);
                    Log.e("TAG", cursor.getInt(0) + " IS " + rowQuan);
                    if (rowQuan > 0) {
                        ContentValues cv = new ContentValues();
                        cv.put(InventoryEntry.COLUMN_ITEM_QUANTITY, rowQuan - 1);
                        String where = "WHERE _id = " + ContentUris.parseId(currentUriId);
                        context.getContentResolver().update(currentUriId, cv, where, null);
                        notifyDataSetChanged();
                    } else {
                        Toast.makeText(context, "Can't go below 0 items", Toast.LENGTH_SHORT).show();
                    }
                }
//
//                int rowQuan = current.getInt(1);
//                Log.e("ID", "Current ID: " + currentId + " with quaintiy: " + rowQuan);
//                Log.e("CURRENT URI ", currentUri.toString());
//                Log.e("CURRENT QUANTITY FOR  ", itemName + " is: " + rowQuan);
//
//                if (rowQuan > 0) {
//                    rowQuan--;
//                    ContentValues cv = new ContentValues();
//                    cv.put(InventoryEntry.COLUMN_ITEM_QUANTITY, rowQuan);
//                    String where = "WHERE _id = " + ContentUris.parseId(currentUriId);
//                    context.getContentResolver().update(currentUriId, cv, where, null);
//                    Log.e("NEW QUANTITY FOR  ", itemName + " is: " + rowQuan);
//                } else Toast.makeText(context, "Can't go below 0 items", Toast.LENGTH_SHORT).show();

//                int rowQuan = cursor.getInt(cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_QUANTITY));
//                Log.e("ID", "Current ID: " + currentId + " with quaintiy: " + rowQuan);
//                Log.e("CURRENT URI ", currentUri.toString());
//                //Log.e("CURRENT QUANTITY FOR  ", itemName + " is: " + rowQuan);
//                rowQuan--;
//                ContentValues cv = new ContentValues();
//                cv.put(InventoryEntry.COLUMN_ITEM_QUANTITY, rowQuan);
//                context.getContentResolver().update(currentUriId, cv, String.valueOf(ContentUris.parseId(currentUri)), null);
//                Log.e("NEW QUANTITY FOR  ", itemName + " is: " + rowQuan);
            }
        });
    }
}
