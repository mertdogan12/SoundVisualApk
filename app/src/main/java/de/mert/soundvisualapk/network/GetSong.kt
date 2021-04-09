package de.mert.soundvisualapk.network

data class GetSong(
    val playing: Boolean,
    val currentSong: String,
    val pic: String,
    val pause: Boolean
)