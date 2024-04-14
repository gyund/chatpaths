package com.gy.chatpaths.aac.app.builder.template.paths

import com.gy.chatpaths.aac.app.R
import com.gy.chatpaths.aac.app.builder.create.Collection
import com.gy.chatpaths.aac.app.builder.create.Path

class SomethingsWrong(
    collection: Collection,
) : Path(collection) {

    init {
        setName(R.string.path_something_is_wrong)
        setIcon(R.drawable.ic_something_is_wrong)
        addChild(path().setName(R.string.path_mad).setIcon(R.drawable.ic_mad))
        addChild(path().setName(R.string.path_tired).setIcon(R.drawable.ic_tired))
        addChild(
            path().setName(R.string.path_something_hurts).setIcon(R.drawable.ic_something_hurts)
                .addChild(path().setName(R.string.path_ear).setIcon(R.drawable.ic_ear))
                .addChild(path().setName(R.string.path_head).setIcon(R.drawable.ic_head))
                .addChild(path().setName(R.string.path_nose).setIcon(R.drawable.ic_nose))
                .addChild(
                    path().setName(R.string.path_mouth_teeth_tongue).setIcon(R.drawable.ic_mouth_teeth_tongue)
                        .addChild(path().setName(R.string.path_mouth).setIcon(R.drawable.ic_mouth))
                        .addChild(path().setName(R.string.path_tooth).setIcon(R.drawable.ic_tooth))
                        .addChild(path().setName(R.string.path_tongue).setIcon(R.drawable.ic_tongue)),
                )
                .addChild(
                    path().setName(R.string.path_leg_foot_knees_toes).setIcon(R.drawable.ic_leg_foot_knees_toes)
                        .addChild(path().setName(R.string.path_foot).setIcon(R.drawable.ic_foot))
                        .addChild(path().setName(R.string.path_leg).setIcon(R.drawable.ic_leg)),
                ),
        )
        addChild(path().setName(R.string.path_hungry).setIcon(R.drawable.ic_hungry))
        addChild(path().setName(R.string.path_sick).setIcon(R.drawable.ic_sick))
        addChild(path().setName(R.string.path_hearing_aid).setIcon(R.drawable.ic_hearing_aid))
        addChild(path().setName(R.string.path_sad).setIcon(R.drawable.ic_sad))
        addChild(
            path().setName(R.string.path_uncomfortable).setIcon(R.drawable.ic_hot)
                .addChild(path().setName(R.string.path_cold).setIcon(R.drawable.ic_cold))
                .addChild(path().setName(R.string.path_hot).setIcon(R.drawable.ic_hot))
                .addChild(path().setName(R.string.path_wet).setIcon(R.drawable.ic_wet)),
        )
    }
}
