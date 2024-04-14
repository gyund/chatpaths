package com.gy.chatpaths.aac.app.builder.template.paths

import com.gy.chatpaths.aac.app.R
import com.gy.chatpaths.aac.app.builder.create.Collection
import com.gy.chatpaths.aac.app.builder.create.Path

class Breakfast(
    collection: Collection,
) : Path(collection) {
    init {
        setName(R.string.col_title_breakfast)
        setIcon(R.drawable.ic_egg_cooked)
        addChild(path().setName(R.string.path_banana).setIcon(R.drawable.ic_banana))
        addChild(path().setName(R.string.path_pancakes).setIcon(R.drawable.ic_pancakes))
        addChild(path().setName(R.string.path_waffle).setIcon(R.drawable.ic_waffle))
        addChild(path().setName(R.string.path_cereal).setIcon(R.drawable.ic_cereal))
        addChild(path().setName(R.string.path_egg_cooked).setIcon(R.drawable.ic_egg_cooked))
        addChild(path().setName(R.string.path_doughnut).setIcon(R.drawable.ic_doughnut))
        addChild(path().setName(R.string.path_milk_glass).setIcon(R.drawable.ic_milk_glass))
    }
}
