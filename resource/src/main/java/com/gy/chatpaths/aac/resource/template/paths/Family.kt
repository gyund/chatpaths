package com.gy.chatpaths.aac.resource.template.paths

import com.gy.chatpaths.aac.resource.R
import com.gy.chatpaths.aac.resource.create.Collection
import com.gy.chatpaths.aac.resource.create.Path

class Family (collection: Collection
) : Path (collection) {

    init {
        setName(R.string.col_title_family)
        setIcon(R.drawable.ic_family)
        addChild(path().setName(R.string.path_mother).setIcon(R.drawable.ic_mother))
        addChild(path().setName(R.string.path_father).setIcon(R.drawable.ic_father))
        addChild(path().setName(R.string.path_sister).setIcon(R.drawable.ic_sister))
        addChild(path().setName(R.string.path_brother).setIcon(R.drawable.ic_brother))
        addChild(path().setName(R.string.path_baby).setIcon(R.drawable.ic_baby))
        addChild(path().setName(R.string.path_grandma).setIcon(R.drawable.ic_grandma))
        addChild(path().setName(R.string.path_grandpa).setIcon(R.drawable.ic_grandpa))
    }
}