package com.gy.chatpaths.aac.resource.template.paths

import com.gy.chatpaths.aac.resource.R
import com.gy.chatpaths.aac.resource.create.Collection
import com.gy.chatpaths.aac.resource.create.Path

class Vegetables(
    collection: Collection,
) : Path(collection) {

    init {
        setName(R.string.col_title_vegetables)
        setIcon(R.drawable.ic_carrot)
        addChild(path().setName(R.string.path_carrot).setIcon(R.drawable.ic_carrot))
        addChild(path().setName(R.string.path_potato_sweet).setIcon(R.drawable.ic_potato_sweet))
        addChild(path().setName(R.string.path_corn).setIcon(R.drawable.ic_corn))
        addChild(path().setName(R.string.path_pepper_red).setIcon(R.drawable.ic_pepper_red))
        addChild(path().setName(R.string.path_lettuce).setIcon(R.drawable.ic_lettuce))
        addChild(path().setName(R.string.path_cucumber).setIcon(R.drawable.ic_cucumber))
    }
}
