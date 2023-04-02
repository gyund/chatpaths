package com.gy.chatpaths.aac.resource.template.paths

import com.gy.chatpaths.aac.resource.R
import com.gy.chatpaths.aac.resource.create.Collection
import com.gy.chatpaths.aac.resource.create.Path

class Lunch(
    collection: Collection,
) : Path(collection) {

    init {
        setName(R.string.col_title_lunch)
        setIcon(R.drawable.ic_sandwich)
        addChild(path().setName(R.string.path_sandwich).setIcon(R.drawable.ic_sandwich))
        addChild(path().setName(R.string.path_pizza).setIcon(R.drawable.ic_pizza))
        addChild(path().setName(R.string.path_chicken_nuggets).setIcon(R.drawable.ic_chicken_nuggets))
        addChild(path().setName(R.string.path_fruit).setIcon(R.drawable.ic_fruit_red))
        addChild(path().setName(R.string.path_hot_dog).setIcon(R.drawable.ic_hot_dog))
    }
}
