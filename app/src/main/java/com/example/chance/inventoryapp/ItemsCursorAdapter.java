package com.example.chance.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.chance.inventoryapp.Data.InventoryContract.InventoryEntry;

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
    public void bindView(View view, Context context, Cursor cursor) {

        TextView nameTextView = (TextView) view.findViewById(R.id.product_name_text_view);
        TextView priceTextView = (TextView) view.findViewById(R.id.product_price_value_text_view);
        TextView quantityTextView = (TextView) view.findViewById(R.id.product_quantity_value_text_view);

        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_QUANTITY);

        String itemName = cursor.getString(nameColumnIndex);
        double itemPrice = cursor.getDouble(priceColumnIndex);
        int itemQuantity = cursor.getInt(quantityColumnIndex);


        nameTextView.setText(itemName);
        priceTextView.setText(String.valueOf(itemPrice)+"$");
        quantityTextView.setText(String.valueOf(itemQuantity));

    }



}
