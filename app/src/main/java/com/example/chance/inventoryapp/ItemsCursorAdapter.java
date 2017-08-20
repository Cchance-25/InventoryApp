package com.example.chance.inventoryapp;

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

import com.example.chance.inventoryapp.Data.InventoryContract.InventoryEntry;

/**
 * Created by chance on 8/16/17.
 */

public class ItemsCursorAdapter extends CursorAdapter {


    // chmod 777 /data /data/data /data/data/com.application.package /data/data/com.application.package/*su
    int itemQuantity;
    int quantityColumnIndex;

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
        final TextView quantityTextView = (TextView) view.findViewById(R.id.product_quantity_value_text_view);

        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_PRICE);
        quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_QUANTITY);

        final String itemName = cursor.getString(nameColumnIndex);
        double itemPrice = cursor.getDouble(priceColumnIndex);
        itemQuantity = cursor.getInt(quantityColumnIndex);

        Button btn = (Button) view.findViewById(R.id.sell_button);
        Object object = cursor.getString(cursor.getColumnIndex(InventoryEntry._ID));
        btn.setTag(object);
        final int currentId = cursor.getInt(cursor.getColumnIndex(InventoryEntry._ID));
        final Uri currentUriId = Uri.withAppendedPath(InventoryEntry.CONTENT_URI, String.valueOf(currentId)); // Current URI
        final Uri currentUri = Uri.withAppendedPath(InventoryEntry.CONTENT_URI, String.valueOf(currentId)); // Current URI
        nameTextView.setText(itemName);
        priceTextView.setText(String.valueOf(itemPrice) + "$");
        quantityTextView.setText(String.valueOf(itemQuantity));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int rowQuan = cursor.getInt(cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_QUANTITY));

                Log.e("ID", "Current ID: " + currentId + " with quaintiy: " + rowQuan);
                Log.e("CURRENT URI ", currentUri.toString());
                Log.e("CURRENT QUANTITY FOR  ", itemName + " is: " + rowQuan);

                rowQuan--;

                ContentValues cv = new ContentValues();
                cv.put(InventoryEntry.COLUMN_ITEM_QUANTITY, rowQuan);
                String where = "_id = " + currentId;
                context.getContentResolver().update(currentUri, cv, where, null);
                Log.e("NEW QUANTITY FOR  ", itemName + " is: " + rowQuan);
                //quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_QUANTITY);
//                int quantity = cursor.getInt(quantityColumnIndex);
//                if (quantity > 0) {
//                    ContentValues cv = new ContentValues();
//                    cv.put(InventoryEntry.COLUMN_ITEM_QUANTITY, quantity - 1);
//                    String selectionArgs[] = {String.valueOf(currentId)};
//                    context.getContentResolver().update(
//                            currentUri,
//                            cv,
//                            null,
//                            null);
//                    Log.e("TAG", currentUri.toString());
//                    Log.e("TAG", "Qua" + quantity);
//                    Log.e("TAG", "ID" + ContentUris.parseId(currentUri));
//                    notifyDataSetChanged();
            }
        });
    }
}
