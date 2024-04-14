package com.gy.chatpaths.aac.app.builder.template.paths

import com.gy.chatpaths.aac.app.R
import com.gy.chatpaths.aac.app.builder.create.Collection
import com.gy.chatpaths.aac.app.builder.create.Path

class Drink(
    collection: Collection,
) : Path(collection) {

    init {
        setName(R.string.path_drink)
        setIcon(R.drawable.ic_drink)
        addChild(path().setName(R.string.path_milk_bottle).setIcon(R.drawable.ic_milk_bottle))
    }
}
