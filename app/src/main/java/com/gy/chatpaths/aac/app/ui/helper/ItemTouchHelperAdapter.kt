package com.gy.chatpaths.aac.app.ui.helper

/**
 * Interface to notify a [RecyclerView.Adapter] of moving and dismissal event from a [ ].
 *
 * @author Paul Burke (ipaulpro)
 */
interface ItemTouchHelperAdapter {
    /**
     * Called when an item has been dragged far enough to trigger a move. This is called every time
     * an item is shifted, and not at the end of a "drop" event.
     *
     * @param fromPosition The start position of the moved item.
     * @param toPosition   Then end position of the moved item.
     * @see RecyclerView.getAdapterPositionFor
     * @see RecyclerView.ViewHolder.getAdapterPosition
     */
    fun onItemMove(
        fromPosition: Int,
        toPosition: Int,
    )

    /**
     * Called when an item has been dismissed by a swipe.
     *
     * @param position The position of the item dismissed.
     * @see RecyclerView.getAdapterPositionFor
     * @see RecyclerView.ViewHolder.getAdapterPosition
     */
    fun onItemDismiss(position: Int)
}
