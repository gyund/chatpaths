package com.gy.chatpaths.aac.resource.create

import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.gy.chatpaths.aac.resource.UriHelper

open class Path (val collection: Collection) {

    var name: String? = null
    var iconUri: Uri? = null
    var anchored: Boolean = false
    private var parentPath: Int? = null

    val children by lazy {
        initializeChildren()
    }

    protected fun initializeChildren() : MutableList<Path> {
        return mutableListOf()
    }

    suspend fun build(collectionId: Int, position: Int) {
        // create path
        val id = collection.repository.addPath(
            userId = collection.userId,
            collectionId = collectionId,
            title = name,
            imageUri = iconUri.toString(),
            parentId = parentPath,
            position = position,
            anchored = false
        )

        collection.repository.setPathIsAnchored(
            userId = collection.userId,
            collectionId = collectionId,
            pathId = id,
            anchored = anchored
        )


        // add children
        children.forEachIndexed { index, path ->
            path.parentPath = id
            path.build(collectionId, index)
        }
    }

    fun path() : Path {
        return Path(collection)
    }

    /**
     * Name of the collection
     */
    fun setName(name: String) : Path {
        this.name = name
        return this
    }

    fun setName(@StringRes name: Int) : Path {
        this.name = collection.context.getString(name)
        return this
    }

    /**
     * Set the icon to a drawable resource. We want to use an ID
     * so that android studio knows the resource ID is being used.
     *
     * This method will create a drawable URI that can be used
     * store the drawable information in a database.
     */
    fun setIcon(@DrawableRes res: Int) : Path {
        iconUri = UriHelper.getUriToDrawable(collection.context, res)
        return this
    }

    fun anchored() : Path {
        anchored = true
        return this
    }

    fun addChild(path: Path) : Path {
        children.add(path)
        return this
    }


    constructor(path: Path) :this(path.collection)
}