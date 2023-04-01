package com.gy.chatpaths.aac.resource.create

import android.content.Context
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.gy.chatpaths.aac.data.PathCollection
import com.gy.chatpaths.aac.data.source.CPRepository
import com.gy.chatpaths.aac.resource.UriHelper
import com.gy.chatpaths.aac.resource.template.collection.*


/**
 * This is a class for creating a collection
 */
abstract class Collection (
    val context: Context,
    val repository: CPRepository,
    val userId: Int) {

    abstract val id : Identifier

    var name: String? = null
        protected set
    var iconUri: Uri? = null
        protected set

    private val children by lazy {
        initializeChildren()
    }

    abstract fun initializeChildren() : MutableList<Path>

    /**
     * Creates the Collection
     */
    open suspend fun build() {
        // Create collection
        val pc = PathCollection(
            collectionId = 0, // new
            name = name,
            imageUri = iconUri.toString(),
            userId = userId
        )
        val id = repository.addCollection(pc)

        // Add paths depth first
        children.forEachIndexed { index, path ->
            path.build(id,index)
        }

    }

    protected fun path() : Path {
        return Path(this)
    }

    /**
     * Name of the collection
     */
    fun setName(name: String) : Collection {
        this.name = name
        return this
    }

    fun setName(@StringRes name: Int) : Collection {
        this.name = context.getString(name)
        return this
    }

    /**
     * Set the icon to a drawable resource. We want to use an ID
     * so that android studio knows the resource ID is being used.
     *
     * This method will create a drawable URI that can be used
     * store the drawable information in a database.
     */
    fun setIcon(@DrawableRes res: Int) : Collection {
        iconUri = UriHelper.getUriToDrawable(context, res)
        return this
    }

    fun addPath(path: Path) : Collection {
        children.add(path)
        return this
    }

    companion object {
        /**
         * Retrieve the builder for the specified object
         */
        fun getBuilder(identifier: Identifier, context: Context, repository: CPRepository, userId: Int) : Collection {
            return when(identifier) {
                Identifier.Essentials -> Essentials(context, repository,userId)
                Identifier.Starter -> Starter(context, repository,userId)
                Identifier.Breakfast -> Breakfast(context, repository,userId)
                Identifier.Lunch -> Lunch(context, repository,userId)
                Identifier.Dinner -> Dinner(context, repository,userId)
                Identifier.Problem -> Problem(context, repository,userId)
                Identifier.Family -> Family(context, repository,userId)
            }
        }

        enum class Identifier {
            Essentials,
            Starter,
            Breakfast,
            Lunch,
            Dinner,
            Problem,
            Family
        }
    }
}
