package com.gy.chatpaths.aac.resource.template.paths

import com.gy.chatpaths.aac.resource.R
import com.gy.chatpaths.aac.resource.create.Collection
import com.gy.chatpaths.aac.resource.create.Path

class Dinner(
    collection: Collection,
) : Path(collection) {

    init {
        setName(R.string.col_title_dinner)
        setIcon(R.drawable.ic_stew)
        addChild(path().setName(R.string.path_potato_sweet).setIcon(R.drawable.ic_potato_sweet))
        addChild(path().setName(R.string.path_chicken_nuggets).setIcon(R.drawable.ic_chicken_nuggets))
        addChild(path().setName(R.string.path_pasta).setIcon(R.drawable.ic_pasta))
        addChild(path().setName(R.string.path_pizza).setIcon(R.drawable.ic_pizza))
        addChild(path().setName(R.string.path_fruit).setIcon(R.drawable.ic_fruit_red))
        addChild(path().setName(R.string.path_hot_dog).setIcon(R.drawable.ic_hot_dog))
        addChild(path().setName(R.string.path_egg_cooked).setIcon(R.drawable.ic_egg_cooked))
        addChild(path().setName(R.string.path_steak).setIcon(R.drawable.ic_steak))
        addChild(path().setName(R.string.path_stew).setIcon(R.drawable.ic_stew))
    }
}
