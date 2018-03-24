package com.inopek.duvana.sink.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.util.Log;
import android.widget.ImageView;

import org.springframework.util.support.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public final class ImageUtils {

    private static final int MAX_HEIGHT = 512;
    private static final int MAX_QUALITY = 100;
    private static final float MAX_WIDTH = 512.0F;
    private static final int THUMBNAIL_SIZE = 250;

    private ImageUtils() {
    }

    public static void createBipMapFromFile(String path, ImageView imageView) {
        File imageFile = new File(path);
        if (imageFile.exists()) {
            Bitmap bitmap = getThumbnailBitmap(imageFile.getAbsolutePath());
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
                imageView.setDrawingCacheEnabled(true);
            } else {
                Log.e("ImageUtils", "Error creating bitmap");
            }
        }
    }

    public static Bitmap getThumbnailBitmap(String imageFilePath) {
        return ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(imageFilePath),
                THUMBNAIL_SIZE, THUMBNAIL_SIZE);
    }

    public static Bitmap getBipMapFromFile(String path) {
        File imageFile = new File(path);
        if (imageFile.exists()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            return BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
        }
        return null;
    }

    public static String convertBitmapToSmallerSizetoString(String image) {
        File imageFile = new File(image);
        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        int nh = (int) (bitmap.getHeight() * (MAX_WIDTH / bitmap.getWidth()));
        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, MAX_HEIGHT, nh, true);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        scaled.compress(Bitmap.CompressFormat.JPEG, MAX_QUALITY, stream);
        byte[] imageByte = stream.toByteArray();
        return Base64.encodeBytes(imageByte);
    }
}
