package amrk000.salehunter.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageEncoder {
    private static ImageEncoder instance;

    //My Encoder Config
    private final boolean centerCrop = true;
    private final int imageWidth = 500, imageHeight = 500;
    private final int imageQuality = 100;
    private final Bitmap.CompressFormat imageFormat = Bitmap.CompressFormat.WEBP;
    private final String metaData = "data:image/webp;base64,";

    public static ImageEncoder get(){
        if(instance == null){
            instance = new ImageEncoder();
        }
        return instance;
    }

    public String encode(Drawable drawable){

        BitmapDrawable bitmapDrawable;
        try {
            bitmapDrawable = (BitmapDrawable) drawable;
        } catch (ClassCastException e){
            bitmapDrawable = (BitmapDrawable) ((TransitionDrawable)drawable).getDrawable(1);
        }

        Bitmap bitmap = bitmapDrawable.getBitmap();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        if(centerCrop) {
            if (bitmap.getWidth() > bitmap.getHeight())
                bitmap = Bitmap.createBitmap(bitmap, bitmap.getWidth() / 2 - bitmap.getHeight() / 2, 0, bitmap.getHeight(), bitmap.getHeight());
            else
                bitmap = Bitmap.createBitmap(bitmap, 0, bitmap.getHeight() / 2 - bitmap.getWidth() / 2, bitmap.getWidth(), bitmap.getWidth());
        }

        bitmap = Bitmap.createScaledBitmap(bitmap,imageWidth,imageHeight,false);

        bitmap.compress(imageFormat, imageQuality, byteArrayOutputStream);

        String image = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.NO_WRAP);

        return metaData + image;
    }

    public String encode(Context context, Uri uri){
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        if(centerCrop) {
            if (bitmap.getWidth() > bitmap.getHeight())
                bitmap = Bitmap.createBitmap(bitmap, bitmap.getWidth() / 2 - bitmap.getHeight() / 2, 0, bitmap.getHeight(), bitmap.getHeight());
            else
                bitmap = Bitmap.createBitmap(bitmap, 0, bitmap.getHeight() / 2 - bitmap.getWidth() / 2, bitmap.getWidth(), bitmap.getWidth());
        }

        bitmap = Bitmap.createScaledBitmap(bitmap,imageWidth,imageHeight,false);

        bitmap.compress(imageFormat, imageQuality, byteArrayOutputStream);

        String image = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.NO_WRAP);

        return  metaData + image;
    }


}
