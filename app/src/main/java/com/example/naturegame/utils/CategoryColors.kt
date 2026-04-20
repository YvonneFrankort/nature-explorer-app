package com.example.naturegame.utils

import androidx.compose.ui.graphics.Color
import android.graphics.Color as AndroidColor

fun getCategoryColorHex(category: String): String {
    return when (category.lowercase()) {
        "bird" -> "#4DA3FF"      // blue
        "animal" -> "#FF9A4D"    // orange
        "insect" -> "#FFD93B"    // yellow
        "tree" -> "#4CAF50"      // green
        "flower" -> "#FF6FB1"    // pink
        "leaf" -> "#66E0A3"      // green
        "mushroom" -> "#FF6B6B"  // red
        "landscape" -> "#A98BFF" // purple
        "rock" -> "#8D6E63"     // brown
        "plant" -> "#81C784"     // soft plant green
        else -> "#90A4AE"      // blue-gray
    }
}

fun getCategoryColor(category: String): Color {
    return Color(android.graphics.Color.parseColor(getCategoryColorHex(category)))
}

