package com.gy.chatpaths.aac.app.ui.manager.path

import com.gy.chatpaths.model.Path

interface PathManagerListener {
    fun showAddPathDialog()
    fun showEditCollectionTitleDialog()

    fun setIsEnabled(pathId: Int, enabled: Boolean)
    fun setPathPosition(pathId: Int, position: Int?)
    fun updatePathOrder(paths: List<Path>)
}
