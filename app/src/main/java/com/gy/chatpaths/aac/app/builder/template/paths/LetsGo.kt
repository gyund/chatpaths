package com.gy.chatpaths.aac.app.builder.template.paths

import com.gy.chatpaths.aac.app.R
import com.gy.chatpaths.aac.app.builder.create.Collection
import com.gy.chatpaths.aac.app.builder.create.Path

class LetsGo(
    collection: Collection,
) : Path(collection) {

    init {
        setName(R.string.path_lets_go)
        setIcon(R.drawable.ic_lets_go)
        addChild(path().setName(R.string.path_home).setIcon(R.drawable.ic_home))
        addChild(PlacesAtHome(collection))
        addChild(PlacesAtSchool(collection)) }
}
