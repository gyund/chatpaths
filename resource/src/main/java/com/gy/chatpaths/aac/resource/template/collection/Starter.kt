package com.gy.chatpaths.aac.resource.template.collection

import android.content.Context
import com.gy.chatpaths.aac.data.source.CPRepository
import com.gy.chatpaths.aac.resource.R
import com.gy.chatpaths.aac.resource.create.Collection
import com.gy.chatpaths.aac.resource.create.Path
import com.gy.chatpaths.aac.resource.template.paths.ChatWords
import com.gy.chatpaths.aac.resource.template.paths.LetsGo
import com.gy.chatpaths.aac.resource.template.paths.SomethingsWrong
import com.gy.chatpaths.aac.resource.template.paths.Want

class Starter(
    context: Context,
    repository: CPRepository,
    userId: Int,
) : Collection(context, repository, userId) {

    init {
        setName(R.string.col_title_smartchat)
        setIcon(R.drawable.ic_chat)
    }

    override val id: Companion.Identifier
        get() = Companion.Identifier.Starter

    override fun initializeChildren(): MutableList<Path> {
        return mutableListOf(
            path().setName(R.string.path_something_to_say).setIcon(R.drawable.ic_question).anchored(),
            ChatWords(this),
            Want(this),
            SomethingsWrong(this),
            LetsGo(this),
        )
    }
}
