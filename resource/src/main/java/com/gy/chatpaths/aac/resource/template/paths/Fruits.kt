package com.gy.chatpaths.aac.resource.template.paths

import com.gy.chatpaths.aac.resource.R
import com.gy.chatpaths.aac.resource.create.Collection
import com.gy.chatpaths.aac.resource.create.Path

class Fruits (collection: Collection
) : Path (collection) {

    init {
        setName(R.string.col_title_fruits)
        setIcon(R.drawable.ic_apple)
        addChild(path().setName(R.string.path_banana).setIcon(R.drawable.ic_banana))
        addChild(path().setName(R.string.path_orange).setIcon(R.drawable.ic_orange))
        addChild(path().setName(R.string.path_grapes).setIcon(R.drawable.ic_grapes))
        addChild(path().setName(R.string.path_apple).setIcon(R.drawable.ic_apple))
        addChild(path().setName(R.string.path_watermelon).setIcon(R.drawable.ic_cherry))
        addChild(path().setName(R.string.path_pineapple).setIcon(R.drawable.ic_pineapple))
        addChild(path().setName(R.string.path_peach).setIcon(R.drawable.ic_peach))
        addChild(path().setName(R.string.path_pear_green).setIcon(R.drawable.ic_pear_green))
        addChild(path().setName(R.string.path_avocado).setIcon(R.drawable.ic_avocado))
        addChild(path().setName(R.string.path_kiwi).setIcon(R.drawable.ic_kiwi))
    }
}