package com.gy.chatpaths.builder.template.paths

import com.gy.chatpaths.aac.resource.R
import com.gy.chatpaths.builder.create.Collection
import com.gy.chatpaths.builder.create.Path

class ChatWords(
    collection: Collection,
) : Path(collection) {

    init {
        setName(R.string.path_chat_words)
        setIcon(R.drawable.ic_chat_words)
        addChild(path().setName(R.string.path_all_done).setIcon(R.drawable.ic_all_done))
        addChild(path().setName(R.string.path_more).setIcon(R.drawable.ic_more))
        addChild(path().setName(R.string.path_help).setIcon(R.drawable.ic_help))
        addChild(path().setName(R.string.path_bathroom).setIcon(R.drawable.ic_bathroom))
        addChild(path().setName(R.string.path_dont_know).setIcon(R.drawable.ic_dont_know))
        addChild(path().setName(R.string.path_something_different).setIcon(R.drawable.ic_different))
    }
}
