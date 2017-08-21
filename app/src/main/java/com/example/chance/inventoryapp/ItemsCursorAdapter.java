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
import com.example.chance.inventoryapp.Data.InventoryProvider;

/**
 * Created by chance on 8/16/17.
 */

public class ItemsCursorAdapter extends CursorAdapter {

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

        String itemName = cursor.getString(nameColumnIndex);
        int itemPrice = cursor.getInt(priceColumnIndex);
        int itemQuantity = cursor.getInt(quantityColumnIndex);

        int currentId = cursor.getInt(cursor.getColumnIndex(InventoryEntry._ID));
        final Uri currentUriId = Uri.withAppendedPath(InventoryEntry.CONTENT_URI, String.valueOf(currentId)); // Current URI
        nameTextView.setText(itemName);
        priceTextView.setText(String.valueOf(itemPrice) + "$");
        quantityTextView.setText(String.valueOf(itemQuantity));

        Button btn = (Button) view.findViewById(R.id.sell_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = (int) ContentUris.parseId(currentUriId);
                if (InventoryProvider.isExist(id)) {
                    cursor.moveToFirst();
                    int row = id - 1;
                    if (cursor.moveToPosition(row)) {
                        int columnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_QUANTITY);
                        int rowQuan = cursor.getInt(columnIndex);
                        if (rowQuan > 0) {
                            ContentValues cv = new ContentValues();
                            cv.put(InventoryEntry.COLUMN_ITEM_QUANTITY, rowQuan - 1);
                            String where = "WHERE _id = " + ContentUris.parseId(currentUriId);
                            context.getContentResolver().update(currentUriId, cv, where, null);
                            notifyDataSetChanged();
                        } else {
                            Toast.makeText(context, R.string.negative_values, Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Log.e(getClass().getSimpleName(), "Error fetching row from database");
                }
            }
        });
    }

}
