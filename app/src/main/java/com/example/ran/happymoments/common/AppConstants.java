package com.example.ran.happymoments.common;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class AppConstants {

    //Number of columns in RecycleView
    public static final int NUM_OF_COLUMNS = 3;

    //GridView image padding in dp
    public static final int GRID_PADDING = 8;

    //SD card image directory
    public static final String HAPPY_MOMENTS_ALBUM = "HappyMoments";

    //supported file formats
    public static final List<String> SUPPORTED_FILE_EXTENSIONS = Arrays.asList("jpg" , "jpeg" , "png");


    public static final String POSITION = "POSITION";

    public static final String PHOTOS_PATH = "PHOTOS_PATH";

    public static final String IMPORTED_IMAGES = "INPUT";

    public static final String OUTPUT_PHOTOS = "OUTPUT";

    public static final double MAX_ANGLE = 360;

    public static final double EYES_WEIGHT = 0.4;

    public static final double SMILE_WEIGHT = 0.6;

    public static final int CAMERA_REQUEST_CODE = 888;

    public static final int READ_STORAGE_PERMISSION = 1;
    public static final int CAMERA_PERMISSION = 2;
    public static final int NUM_IMAGE_CHOSEN_LIMIT = 20;
}
