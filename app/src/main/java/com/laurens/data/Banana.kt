package com.laurens.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Banana(
    val name: String,
    val description: String,
    val photo: Int,
    val detail: String
): Parcelable
