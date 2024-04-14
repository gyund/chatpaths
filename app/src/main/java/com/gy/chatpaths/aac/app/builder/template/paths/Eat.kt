package com.gy.chatpaths.aac.app.builder.template.paths

import com.gy.chatpaths.aac.app.R
import com.gy.chatpaths.aac.app.builder.create.Collection
import com.gy.chatpaths.aac.app.builder.create.Path

class Eat(
    collection: Collection,
) : Path(collection) {

    init {
        setName(R.string.path_eat)
        setIcon(R.drawable.ic_eat)
        addChild(path().setName(R.string.path_pizza).setIcon(R.drawable.ic_pizza))
        addChild(path().setName(R.string.path_pasta).setIcon(R.drawable.ic_pasta))
        addChild(path().setName(R.string.path_sandwich).setIcon(R.drawable.ic_sandwich))
        addChild(path().setName(R.string.path_pancakes).setIcon(R.drawable.ic_pancakes))
        addChild(path().setName(R.string.path_chicken_nuggets).setIcon(R.drawable.ic_chicken_nuggets))
        addChild(path().setName(R.string.path_strawberry).setIcon(R.drawable.ic_strawberry))
        addChild(path().setName(R.string.path_tomato).setIcon(R.drawable.ic_tomato))
        addChild(path().setName(R.string.path_grapes).setIcon(R.drawable.ic_grapes))
        addChild(path().setName(R.string.path_eggplant).setIcon(R.drawable.ic_eggplant))
        addChild(path().setName(R.string.path_watermelon).setIcon(R.drawable.ic_watermelon))
        addChild(path().setName(R.string.path_hot_dog).setIcon(R.drawable.ic_hot_dog))
        addChild(path().setName(R.string.path_stew).setIcon(R.drawable.ic_stew))
        addChild(path().setName(R.string.path_soup).setIcon(R.drawable.ic_soup))
        addChild(path().setName(R.string.path_egg_cooked).setIcon(R.drawable.ic_egg_cooked))
        addChild(path().setName(R.string.path_steak).setIcon(R.drawable.ic_steak))
        addChild(path().setName(R.string.path_pita).setIcon(R.drawable.ic_pita))
        addChild(path().setName(R.string.path_fries).setIcon(R.drawable.ic_fries))
        addChild(path().setName(R.string.path_cheese).setIcon(R.drawable.ic_cheese))
    }
}
