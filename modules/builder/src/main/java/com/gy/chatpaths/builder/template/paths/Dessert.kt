package com.gy.chatpaths.builder.template.paths

import com.gy.chatpaths.aac.resource.R
import com.gy.chatpaths.builder.create.Collection
import com.gy.chatpaths.builder.create.Path

class Dessert(
    collection: Collection,
) : Path(collection) {

    init {
        setName(R.string.col_title_dessert)
        setIcon(R.drawable.ic_cookie)
        addChild(path().setName(R.string.path_cookie).setIcon(R.drawable.ic_cookie))
        addChild(path().setName(R.string.path_cake).setIcon(R.drawable.ic_cake))
        addChild(path().setName(R.string.path_cupcake).setIcon(R.drawable.ic_cupcake))
        addChild(path().setName(R.string.path_pie).setIcon(R.drawable.ic_pie))
        addChild(path().setName(R.string.path_ice_cream_cone).setIcon(R.drawable.ic_ice_cream_cone))
        addChild(path().setName(R.string.path_ice_cream_dish).setIcon(R.drawable.ic_ice_cream_dish))
    }
}
