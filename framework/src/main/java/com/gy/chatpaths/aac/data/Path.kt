package com.gy.chatpaths.aac.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// @DatabaseView("SELECT PU.userId, P.pathId, P.imageResource as defaultImageResource, P.defaultTitleStringResource, P2C.parentId, P2C.defaultPosition, PC.collectionId, COALESCE(PUML.enabled,1) as enabled, PUML.anchored, COALESCE(PUML.timesUsed,0) as timesUsed, COALESCE(PUML.position,P2C.defaultPosition,null) as position, UPC.title AS userTitle, UPC.imageResource AS userSharedImageUri, UPUI.imageResource AS userIndividualImageUri FROM PathUser AS PU INNER JOIN Path AS P INNER JOIN PathToCollections AS P2C ON P2C.pathId = P.pathId LEFT JOIN UserPathCustomizations AS UPC ON P.pathId = UPC.pathId LEFT JOIN PathUserML AS PUML ON PUML.userId = PU.userId AND PUML.pathId = P.pathId AND PUML.pathCollectionId = P2C.pathCollectionId LEFT JOIN PathCollection AS PC ON PC.collectionId = P2C.pathCollectionId LEFT JOIN UserPathUniqueImages AS UPUI ON UPUI.userId = PU.userId AND UPUI.pathId = P.pathId")

@Entity(
    indices = [
        Index("pathId", unique = true),
        Index("collectionId"),
        Index("userId", "collectionId", "parentId"),
    ],
    foreignKeys = [
        ForeignKey(
            entity = PathUser::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = PathCollection::class,
            parentColumns = ["collectionId"],
            childColumns = ["collectionId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class Path(
    @PrimaryKey(autoGenerate = true) var pathId: Int,
    var userId: Int,
    var collectionId: Int,
    var parentId: Int? = null,
    var defaultPosition: Int? = null,
    var name: String? = null,
    var imageUri: String? = null, // TODO: Index or make FTS4 table
    var audioPromptUri: String? = null, // TODO: Index or make FTS4 table
    @ColumnInfo(defaultValue = "1") var enabled: Boolean = true, // modifyable so we don't need to request the whole view
    @ColumnInfo(defaultValue = "0") var anchored: Boolean = true,
    @ColumnInfo(defaultValue = "0") var timesUsed: Int = 0,
    var position: Int?,

)
