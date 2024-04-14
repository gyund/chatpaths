package com.gy.chatpaths.aac.app.builder.template.collection

import android.content.Context
import com.gy.chatpaths.aac.app.R
import com.gy.chatpaths.aac.app.builder.create.Collection
import com.gy.chatpaths.aac.app.builder.create.Path
import com.gy.chatpaths.aac.app.model.source.CPRepository

class Essentials(
    context: Context,
    repository: CPRepository,
    userId: Int,
) : Collection(context, repository, userId) {
    init {
        setName(R.string.col_title_essentials)
        setIcon(R.drawable.ic_crescent_moon_right)
    }

    override val id: Companion.Identifier
        get() = Companion.Identifier.Essentials

    override fun initializeChildren(): MutableList<Path> {
        return mutableListOf(
            path().setName(R.string.path_eat).setIcon(R.drawable.ic_eat),
            path().setName(R.string.path_drink).setIcon(R.drawable.ic_drink),
            path().setName(R.string.path_bathroom).setIcon(R.drawable.ic_bathroom),
            path().setName(R.string.path_more).setIcon(R.drawable.ic_more),
            path().setName(R.string.path_all_done).setIcon(R.drawable.ic_all_done),
        )
    }
}
