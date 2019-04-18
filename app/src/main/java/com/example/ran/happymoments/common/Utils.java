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

    private Context mContext; //not in use! Utils is more static class, so we never create instance of Utils

    public Utils(Context context) {
        this.mContext = context;
    }


    public static void connectToNetwork(final Context context) {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        Intent intent=new Intent(Settings.ACTION_SETTINGS);
                        context.startActivity(intent);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("No Internet").setMessage("This App requires Internet connections ")
                .setPositiveButton("Connect", dialogClickListener).setNegativeButton("Exit", dialogClickListener).show();
    }

    private boolean IsSupportedFile(String filePath) {
        String extension = filePath.substring((filePath.lastIndexOf(".") + 1),
                filePath.length());

        if (AppConstants.SUPPORTED_FILE_EXTENSIONS.contains(extension.toLowerCase(Locale.getDefault())))
            return true;
        else
            return false;

    }


    public double calcEuclidDistance(double x1 , double y1 , double x2 , double y2) {
        double x ,y;

        x = x1 - x2;
        y = y1 - y2;

        return Math.sqrt(x*x + y*y);
    }


    // Getting screen width
    public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();
        try {
            display.getSize(point);
        } catch (java.lang.NoSuchMethodError ignore) { // Older device
            point.x = display.getWidth();
            point.y = display.getHeight();
        }
        columnWidth = point.x;
        return columnWidth;
    }



    //https://stackoverflow.com/questions/11983654/android-how-to-add-an-image-to-an-album
    public void saveImageToExternal(Bitmap bm) throws IOException {

        final String appDirectoryName = AppConstants.HAPPY_MOMENTS_ALBUM;

        File imageFile = getOutputMediaFile();

        //Create Path to save Image
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES+"/"+ appDirectoryName);

        //if there is no directory exist
        if (!path.isDirectory())
            path.mkdirs();

        FileOutputStream out = new FileOutputStream(imageFile);
        try{
            bm.compress(Bitmap.CompressFormat.PNG, 100, out); // Compress Image
            out.flush();
            out.close();

            // Tell the media scanner about the new file so that it is
            // immediately available to the user.
            MediaScannerConnection.scanFile(mContext,new String[] { imageFile.getAbsolutePath() }, null,new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                    Log.i("ExternalStorage", "Scanned " + path + ":");
                    Log.i("ExternalStorage", "-> uri=" + uri);
                }
            });
        } catch(Exception e) {
            throw new IOException();
        }
    }


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
//        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/Camera");
        File image = null;

        try {
            image = File.createTempFile(name , ".jpg" , storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }



    public boolean isExternalStorageWritable(){
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)){
            return true;
        }
        return false;
    }


    ///////////////////////////
    public static void scanFile(Context context, String path) {

        MediaScannerConnection.scanFile(context,new String[] { path }, null,new MediaScannerConnection.OnScanCompletedListener() {
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