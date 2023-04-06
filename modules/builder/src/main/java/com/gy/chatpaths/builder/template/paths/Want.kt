package com.gy.chatpaths.builder.template.paths

import com.gy.chatpaths.aac.resource.R
import com.gy.chatpaths.builder.create.Collection
import com.gy.chatpaths.builder.create.Path

class Want(
    collection: Collection,
) : Path(collection) {

    init {
        setName(R.string.path_i_want)
        setIcon(R.drawable.ic_i_want)
        addChild(Eat(collection))
        addChild(Drink(collection))
        addChild(path().setName(R.string.path_break_rest).setIcon(R.drawable.ic_break_rest))
        addChild(path().setName(R.string.path_hug).setIcon(R.drawable.ic_hug))
        addChild(path().setName(R.string.path_tablet).setIcon(R.drawable.ic_tablet))
        addChild(path().setName(R.string.path_play_with_toys).setIcon(R.drawable.ic_play_with_toys))
        addChild(path().setName(R.string.path_read_a_book).setIcon(R.drawable.ic_read_a_book))
        addChild(path().setName(R.string.path_music).setIcon(R.drawable.ic_music))
        addChild(path().setName(R.string.path_use_the_bathroom).setIcon(R.drawable.ic_bathroom))
    }
}
