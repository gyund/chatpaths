package com.gy.chatpaths.builder.template.paths

import com.gy.chatpaths.aac.resource.R
import com.gy.chatpaths.builder.create.Collection
import com.gy.chatpaths.builder.create.Path

class Drink(
    collection: Collection,
) : Path(collection) {

    init {
        setName(R.string.path_drink)
        setIcon(R.drawable.ic_drink)
        addChild(path().setName(R.string.path_milk_bottle).setIcon(R.drawable.ic_milk_bottle))
    }
}
