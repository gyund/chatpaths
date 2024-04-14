package com.gy.chatpaths.aac.app.ui.manager.path

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.gy.chatpaths.aac.app.BindableAdapter
import com.gy.chatpaths.aac.app.DrawableUtils
import com.gy.chatpaths.aac.app.R
import com.gy.chatpaths.aac.app.databinding.PathItemBinding
import com.gy.chatpaths.aac.app.model.Path
import com.gy.chatpaths.aac.app.ui.helper.ItemTouchHelperAdapter
import com.gy.chatpaths.aac.app.ui.helper.ItemTouchHelperViewHolder
import com.gy.chatpaths.aac.app.ui.helper.OnStartDragListener
import java.util.*

/**
 * [RecyclerView.Adapter] that can display a [UserPathCollectionPrefsView].
 */
class MyPathsRecyclerViewAdapter(
    private val listener: PathManagerListener,
    private val viewmodel: PathsViewModel,
    private val dragStartListener: OnStartDragListener,
) : RecyclerView.Adapter<MyPathsRecyclerViewAdapter.ViewHolder>(),
    BindableAdapter<Path>,
    ItemTouchHelperAdapter {

    private var values: MutableList<Path> = mutableListOf()

    override fun getItemId(position: Int): Long {
        return values[position].pathId.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return values[position].pathId
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        // avoid exception
        if (holder.bindingAdapterPosition < 0) return
        if (holder.bindingAdapterPosition > values.lastIndex) return
        val item = values[holder.bindingAdapterPosition]
        item.position = holder.bindingAdapterPosition
        listener.setPathPosition(item.pathId, item.position)
    }

    /**
     * When we set the [RecyclerView] adapter to null, this will get called.
     * This allows us to save the state once when items are reordered when the
     * fragment managing the view is closed.
     */
//    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
//        super.onDetachedFromRecyclerView(recyclerView)
//        Log.d("RV", "detached from RV")
//        updateAllPreferences()
//    }
//
    protected fun updateAllPreferences() {
        listener.updatePathOrder(values)
//        values.forEachIndexed { index, path ->
//            listener.setPathPosition(path.pathId, index)
//        }
    }

    /**
     * Standard way of setting data for this adapter
     */
    override fun setData(items: List<Path>) {
        // do a dumb find
        // var dataChanged = false
//        items.forEachIndexed() {index, newitem->
//            // if the item is in the list, ignore it, because the user probably has the latest value
//            val existingItem = values.find {existing->
//                existing.pathId == newitem.pathId
//            }
//            if(null == existingItem) {
//                // add it
//                values.add(newitem)
//                notifyItemInserted(values.lastIndex)
//            } else {
//                existingItem.position?.let {existingPosition->
//                    if (index != newitem.position) {
//                        notifyItemMoved(existingPosition, index)
//                    }
//                    if (existingItem.enabled != newitem.enabled) {
//                        notifyItemChanged(existingPosition)
//                    }
//                }
//            }
//        }
//        //values = items.toMutableList()
        // if(dataChanged) notifyDataSetChanged()
        values.clear()
        values.addAll(items)
        notifyDataSetChanged()
    }

    suspend fun removeAt(position: Int) {
        val item = values.removeAt(position)
        viewmodel.removePath(item.pathId)
        notifyItemRemoved(position)
    }

    /**
     * This works by using a function pointer to notify each individual element of its new position
     * so an update can be performed
     */
    override fun changedPositions(positions: Set<Int>) {
        positions.forEach(this::notifyItemChanged)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        // .inflate(R.layout.fragment_collections, parent, false)
        val binding = PathItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(values[position])
    }

    override fun getItemCount(): Int = values.size

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(values, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        // notifyDataSetChanged()
    }

    override fun onItemDismiss(position: Int) {
        Log.d("RV", "onItemDismiss")
        // mItems.remove(position);
        notifyItemRemoved(position)
    }

    inner class ViewHolder(val binding: PathItemBinding) :
        RecyclerView.ViewHolder(
            binding.root,
        ),
        ItemTouchHelperViewHolder {

        fun bind(path: Path) {
            binding.pathImage.setImageDrawable(
                DrawableUtils.getDrawableImage(
                    binding.root.context,
                    path,
                    R.drawable.ic_baseline_image_24,
                ),
            )

            binding.content.text = path.name
            binding.switchWidget.isChecked = path.enabled
            binding.root.setTag(R.id.path_id, path.pathId)
            binding.anchor.visibility = if (path.anchored) View.VISIBLE else View.INVISIBLE

            binding.switchWidget.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked != path.enabled) {
                    path.enabled = isChecked
                    listener.setIsEnabled(path.pathId, isChecked)
                }
            }

            binding.root.setOnClickListener {
                val action =
                    PathsFragmentDirections.actionPathsFragmentToPathsDetailFragment(
                        userId = path.userId,
                        collectionId = path.collectionId,
                        parentId = path.parentId ?: 0,
                        pathId = path.pathId,
                    )
                it.findNavController().navigate(action)
            }
        }

        override fun onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY)
            Log.d("RV", "onItemSelected")
        }

        override fun onItemClear() {
            itemView.setBackgroundColor(0)
            Log.d("RV", "onItemClear")
            updateAllPreferences()
        }

        override fun toString(): String {
            return super.toString() + " '" + binding.content.text + "'"
        }
    }

    init {
        setHasStableIds(true)
    }
}
