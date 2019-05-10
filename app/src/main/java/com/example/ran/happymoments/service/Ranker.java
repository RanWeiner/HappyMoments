package com.example.ran.happymoments.service;

import android.util.Log;

import com.example.ran.happymoments.model.photo.Photo;

interface Ranker {
     double rankPhoto(Photo photo);
}
