package com.example.ran.happymoments.common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;


import com.example.ran.happymoments.screens.detection.DetectionActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Utils {


    public Utils() {}


    public static File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES),"HappyMoments");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator + "IMG_" +timestamp +".jpg");
    }


    public static File createPhotoFile() {
        String name = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = null;

        try {
            image = File.createTempFile(name , ".jpg" , storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }


    public static void scanFile(Context context, String path) {
        MediaScannerConnection.scanFile(context, new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
            public void onScanCompleted(String path, Uri uri) {
                Log.i("ExternalStorage", "Scanned " + path + ":");
                Log.i("ExternalStorage", "-> uri=" + uri);
            }
        });
    }


    public static void createAlbumIfNotExist() {
        final String appDirectoryName = AppConstants.HAPPY_MOMENTS_ALBUM;

        //Create Path to save Image
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES+"/"+ appDirectoryName);

        //if there is no directory exist
        if (!path.isDirectory())
            path.mkdirs();

    }

    public static void copyFile(Context context, File srcFile , File destFile) {
        if (!srcFile.exists()) {
            return;
        }
        createAlbumIfNotExist();

        FileChannel src ,dest ;

        try {
            src = new FileInputStream(srcFile).getChannel();
            dest = new FileOutputStream(destFile).getChannel();

            if (dest != null && src != null) {
                dest.transferFrom(src , 0, src.size());
                scanFile(context ,destFile.getAbsolutePath());
            }

            if (src != null) {
                src.close();
            }

            if (dest != null) {
                dest.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}