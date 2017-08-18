package com.example.chance.inventoryapp;

/**
 * Created by chance on 8/16/17.
 */

public class Item {
    private int mImageResourceId;
    private String mProductName;
    private double mPrice;
    private int mQuantity;

    public Item(int mImageResourceId, String mProductName, double mPrice, int mQuantity) {
        this.mImageResourceId = mImageResourceId;
        this.mProductName = mProductName;
        this.mPrice = mPrice;
        this.mQuantity = mQuantity;
    }

    public int getImageResourceId() {
        return mImageResourceId;
    }

    public void setImageResourceId(int mImageResourceId) {
        this.mImageResourceId = mImageResourceId;
    }

    public String getProductName() {
        return mProductName;
    }

    public void setmProductName(String mProductName) {
        this.mProductName = mProductName;
    }

    public double getPrice() {
        return mPrice;
    }

    public void setPrice(double mPrice) {
        this.mPrice = mPrice;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public void setmQuantity(int mQuantity) {
        this.mQuantity = mQuantity;
    }
}
