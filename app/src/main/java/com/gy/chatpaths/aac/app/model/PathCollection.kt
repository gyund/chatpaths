package com.gy.chatpaths.aac.app.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PathCollection(
    @PrimaryKey(autoGenerate = true) var collectionId: Int,
    var userId: Int,
    var name: String? = null,
    val imageUri: String? = null,
    var displayOrder: Int? = null,
    @ColumnInfo(defaultValue = "1") var enabled: Boolean = true,
    @ColumnInfo(defaultValue = "0") var autoSort: Boolean = false,
)
