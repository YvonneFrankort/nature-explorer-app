package com.example.naturegame.utils

import android.content.Context
import android.graphics.PorterDuff
import androidx.core.content.ContextCompat
import com.example.naturegame.R
import android.graphics.drawable.Drawable

fun scaleDrawable(drawable: Drawable, scale: Float): Drawable {
    val width = (drawable.intrinsicWidth * scale).toInt()
    val height = (drawable.intrinsicHeight * scale).toInt()
    drawable.setBounds(0, 0, width, height)
    return drawable
}
fun getTintedDefaultMarker(context: Context, colorHex: String) =
    ContextCompat.getDrawable(context, R.drawable.ic_marker)?.apply {
        setColorFilter(android.graphics.Color.parseColor(colorHex), PorterDuff.Mode.SRC_IN)
    }
