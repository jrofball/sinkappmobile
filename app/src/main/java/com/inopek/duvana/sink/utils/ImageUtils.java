package com.inopek.duvana.sink.utils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.inopek.duvana.sink.constants.SinkConstants;

import org.springframework.util.support.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public final class ImageUtils {

    private static final float MAX_HEIGHT = 816.0F;
    private static final float MAX_WIDTH = 612.0F;
    private static final int BYTE_MEMORY_SIZE = 16 * 1024;

    private ImageUtils() {
    }

    public static Bitmap processBitMap(Intent data) {
        Bitmap bitmap = null;

        String fileName = data.getStringExtra(SinkConstants.INTENT_EXTRA_FILE_NAME);
        File file = new File(fileName);
        file.mkdir();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        //  max Height and width values of the compressed image is taken as 816x612
        float maxHeight = MAX_HEIGHT;
        float maxWidth = MAX_WIDTH;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }

        // setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = calculateSize(options, actualWidth, actualHeight);

        // inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

        //  this options allow android to claim the bitmap memory if it runs low on memory
        options.inTempStorage = new byte[BYTE_MEMORY_SIZE];

        bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        try {
            bytes.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public static int calculateSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public static void createBipMapFromFile(String path, ImageView imageView) {
        File imageFile = new File(path);
        if (imageFile.exists()) {
            Bitmap bitmap = getBipMapFromFile(imageFile.getAbsolutePath());
            if(bitmap != null) {
                imageView.setImageBitmap(bitmap);
                imageView.setDrawingCacheEnabled(true);
            } else {
                Log.e("ImageUtils", "Error creating bitmap");
            }
        }
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

    public static String convertBitmaptoSmallerSizetoString(String image){
        File imageFile = new File(image);
        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        scaled.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageByte = stream.toByteArray();
        return  Base64.encodeBytes(imageByte);
    }
}
