package com.gy.chatpaths.aac.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index("name", unique = true),
    ],
)
data class PathUser(
    @PrimaryKey(autoGenerate = true) var userId: Int,
    var name: String,
    var displayImage: String?,
)
