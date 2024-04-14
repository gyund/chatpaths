package com.gy.chatpaths.aac.app.builder.template.paths

import com.gy.chatpaths.aac.app.R
import com.gy.chatpaths.aac.app.builder.create.Collection
import com.gy.chatpaths.aac.app.builder.create.Path

class PlacesAtHome(
    collection: Collection,
) : Path(collection) {
    init {
        setName(R.string.col_title_places_home)
        setIcon(R.drawable.ic_somewhere_in_house_yard)
        addChild(path().setName(R.string.path_bathroom).setIcon(R.drawable.ic_bathroom))
        addChild(path().setName(R.string.path_kitchen).setIcon(R.drawable.ic_refrigerator))
        addChild(path().setName(R.string.path_living_room).setIcon(R.drawable.ic_living_room))
        addChild(path().setName(R.string.path_bathtub_shower).setIcon(R.drawable.ic_bath))
        addChild(path().setName(R.string.path_bedroom).setIcon(R.drawable.ic_bed))
    }
}
