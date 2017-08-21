package com.example.chance.inventoryapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

/**
 * Created by chance on 8/21/17.
 */

public class ImageUtils {

    private ImageUtils() {

    }

    public static byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream bytsStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bytsStream);
        return bytsStream.toByteArray();
    }

    public static Bitmap convertByteArrayToBitmap(byte[] imageByteArray) {
        return BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
    }


}
