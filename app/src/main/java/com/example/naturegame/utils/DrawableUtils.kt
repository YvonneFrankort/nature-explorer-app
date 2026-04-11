package com.example.naturegame.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat

fun getScaledDrawable(context: Context, resId: Int, size: Int): Drawable? {
    val drawable = ContextCompat.getDrawable(context, resId) ?: return null

    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    drawable.setBounds(0, 0, size, size)
    drawable.draw(canvas)

    return BitmapDrawable(context.resources, bitmap)
}
