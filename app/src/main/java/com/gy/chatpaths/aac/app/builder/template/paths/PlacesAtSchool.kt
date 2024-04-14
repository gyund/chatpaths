package com.gy.chatpaths.aac.app.builder.template.paths

import com.gy.chatpaths.aac.app.R
import com.gy.chatpaths.aac.app.builder.create.Collection
import com.gy.chatpaths.aac.app.builder.create.Path

class PlacesAtSchool(
    collection: Collection,
) : Path(collection) {
    init {
        setName(R.string.path_somewhere_at_school)
        setIcon(R.drawable.ic_school)
        addChild(path().setName(R.string.path_bathroom_school).setIcon(R.drawable.ic_bathroom_school))
        addChild(path().setName(R.string.path_classroom).setIcon(R.drawable.ic_classroom))
        addChild(path().setName(R.string.path_outside).setIcon(R.drawable.ic_outside))
        addChild(path().setName(R.string.path_table).setIcon(R.drawable.ic_table))
        addChild(path().setName(R.string.path_rug_carpet).setIcon(R.drawable.ic_rug_carpet))
        addChild(path().setName(R.string.path_gym).setIcon(R.drawable.ic_gym))
    }
}
