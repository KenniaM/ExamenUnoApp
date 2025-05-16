package com.moviles.examenuno.repository

import android.content.Context
import com.moviles.examenuno.models.Course
import com.moviles.examenuno.data.DataBaseBuilder
import com.moviles.examenuno.models.CourseEntity

class CourseRepository(private val context: Context) {
    private val courseDao = DataBaseBuilder.getInstance(context).courseDao()





}