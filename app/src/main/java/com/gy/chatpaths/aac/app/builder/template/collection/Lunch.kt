package com.gy.chatpaths.aac.app.builder.template.collection

import android.content.Context
import com.gy.chatpaths.aac.app.builder.create.Collection
import com.gy.chatpaths.aac.app.builder.create.Path
import com.gy.chatpaths.aac.app.model.source.CPRepository
import com.gy.chatpaths.aac.app.builder.template.paths.Lunch as PathLunch

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
