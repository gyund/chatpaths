package com.gy.chatpaths.builder.template.collection

import android.content.Context
import com.gy.chatpaths.builder.create.Collection
import com.gy.chatpaths.builder.create.Path
import com.gy.chatpaths.builder.template.paths.SomethingsWrong
import com.gy.chatpaths.model.source.CPRepository

class Problem(
    context: Context,
    repository: CPRepository,
    userId: Int,
) : Collection(context, repository, userId) {

    init {
        val path = SomethingsWrong(this)
        name = path.name
        iconUri = path.iconUri
    }

    override val id: Companion.Identifier
        get() = Companion.Identifier.Problem

    override fun initializeChildren(): MutableList<Path> {
        return SomethingsWrong(this).children
    }
}
