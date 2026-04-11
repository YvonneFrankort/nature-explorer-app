package com.example.naturegame.utils

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
        else -> "#9E9E9E"        // gray
    }
}

