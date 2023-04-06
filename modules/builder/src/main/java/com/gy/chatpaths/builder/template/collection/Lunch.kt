package com.gy.chatpaths.builder.template.collection

import android.content.Context
import com.gy.chatpaths.builder.create.Collection
import com.gy.chatpaths.builder.create.Path
import com.gy.chatpaths.model.source.CPRepository
import com.gy.chatpaths.builder.template.paths.Lunch as PathLunch

class Lunch(
    context: Context,
    repository: CPRepository,
    userId: Int,
) : Collection(context, repository, userId) {

    init {
        val path = PathLunch(this)
        name = path.name
        iconUri = path.iconUri
    }

    override val id: Companion.Identifier
        get() = Companion.Identifier.Lunch

    override fun initializeChildren(): MutableList<Path> {
        return PathLunch(this).children
    }
}
