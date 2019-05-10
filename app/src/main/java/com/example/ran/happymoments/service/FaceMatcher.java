package com.example.ran.happymoments.service;
import com.example.ran.happymoments.model.series.PhotoSeries;

import java.util.List;

interface FaceMatcher {
    void matchPersons(PhotoSeries series);
}
