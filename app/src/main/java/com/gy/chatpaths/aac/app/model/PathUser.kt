package com.gy.chatpaths.aac.app.model

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
