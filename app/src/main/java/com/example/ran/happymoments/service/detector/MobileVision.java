package com.example.ran.happymoments.service.detector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.media.ExifInterface;
import android.util.Log;
import android.util.SparseArray;

import com.example.ran.happymoments.common.Position;
import com.example.ran.happymoments.model.face.Eyes;
import com.example.ran.happymoments.model.face.Face;
import com.example.ran.happymoments.model.face.Smile;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Landmark;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;


public class MobileVision  implements FaceDetector {

    com.google.android.gms.vision.face.FaceDetector mDetector;

    public MobileVision(Context context){
        mDetector = new com.google.android.gms.vision.face.FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setLandmarkType(com.google.android.gms.vision.face.FaceDetector.ALL_LANDMARKS)
                .setClassificationType(com.google.android.gms.vision.face.FaceDetector.ALL_CLASSIFICATIONS)
                .build();
    }

    @Override
    public List<Face> detectFaces(Context context, String path,String orientation) {

        List<Face> foundFaces = new ArrayList<>();

        Bitmap bitmap = rotateBitmapIfRequired(path , orientation);

        if (!mDetector.isOperational()) {
            return null;
        }

        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<com.google.android.gms.vision.face.Face> faces = mDetector.detect(frame);

        for (int i = 0; i < faces.size(); i++) {
            foundFaces.add(new Face(new Position(faces.valueAt(i).getPosition().x, faces.valueAt(i).getPosition().y)
                    , faces.valueAt(i).getWidth(), faces.valueAt(i).getHeight()
                    , new Smile(faces.valueAt(i).getIsSmilingProbability())
                    ,new Eyes(faces.valueAt(i).getIsLeftEyeOpenProbability() , faces.valueAt(i).getIsRightEyeOpenProbability())));


//            Log.i("FACES", "==> Person #" + faces.valueAt(i).getId() + " Landmarks: ");
//            for(Landmark l : faces.valueAt(i).getLandmarks()) {
//                Log.i("FACES", "Position: " + l.getPosition() + " Type: " + l.getType());
//            }


        }

        return foundFaces;
    }

    @Override
    public void release() {
        mDetector.release();
    }


    private Bitmap rotateBitmapIfRequired(String path , String photoOrientation) {
        Bitmap bitmap = null;
        FileInputStream stream = null;


        try {
            stream = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        bitmap = BitmapFactory.decodeStream(stream);
        int orientation = photoOrientation != null ? Integer.parseInt(photoOrientation) : ExifInterface.ORIENTATION_NORMAL;

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateBitmapByDegrees(bitmap, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateBitmapByDegrees(bitmap, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateBitmapByDegrees(bitmap, 270);
            default:
                return bitmap;
        }
    }


    public Bitmap rotateBitmapByDegrees(Bitmap bmp, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedBitmap =  Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        bmp.recycle();
        return rotatedBitmap;
    }


}
