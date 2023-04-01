package com.gy.chatpaths.aac.app.ui.manager.userdetail

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gy.chatpaths.aac.app.BindableAdapter
import com.gy.chatpaths.aac.app.R
import com.gy.chatpaths.aac.app.databinding.FragmentCollectionsBinding
import com.gy.chatpaths.aac.app.ui.helper.ItemTouchHelperAdapter
import com.gy.chatpaths.aac.app.ui.helper.ItemTouchHelperViewHolder
import com.gy.chatpaths.aac.app.ui.helper.OnStartDragListener
import com.gy.chatpaths.aac.data.PathCollection
import java.util.*


/**
 * [RecyclerView.Adapter] that can display a [PathCollection].
 */
class MyCollectionsRecyclerViewAdapter(
    private val listener: CollectionManagerListener,
    private val viewmodel: UserCollectionsViewModel,
    private val dragStartListener: OnStartDragListener
) : RecyclerView.Adapter<MyCollectionsRecyclerViewAdapter.ViewHolder>(),
    BindableAdapter<PathCollection>,
    ItemTouchHelperAdapter {

    private var values: MutableList<PathCollection> = mutableListOf()

    override fun getItemId(position: Int): Long {
        return values[position].collectionId.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return values[position].collectionId
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        // avoid exception
        if (holder.bindingAdapterPosition < 0) return
        if (holder.bindingAdapterPosition > values.lastIndex) return
        val item = values[holder.bindingAdapterPosition]
        item.displayOrder = holder.bindingAdapterPosition
        item.enabled = holder.binding.switchWidget.isChecked
    }

    /**
     * When we set the [RecyclerView] adapter to null, this will get called.
     * This allows us to save the state once when items are reordered when the
     * fragment managing the view is closed.
     */
    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        Log.d("RV", "detached from RV")
        //updateAllPreferences()
    }

    private fun updateAllPreferences() {
        listener.updateCollectionOrder(values)
    }

    /**
     * Standard way of setting data for this adapter
     */
    override fun setData(items: List<PathCollection>) {
        // do a dumb find
        items.forEach { newitem ->
            // if the item is in the list, ignore it, because the user probably has the latest value
            val existingItem = values.find { existing ->
                existing.collectionId == newitem.collectionId
            }
            if (null == existingItem) {
                // add it
                values.add(newitem)
                notifyItemInserted(values.lastIndex)
            }
        }
        //values = items.toMutableList()
        //notifyDataSetChanged()
    }

    fun removeAt(position: Int) {
        val item = values.removeAt(position)
        listener.showDeleteCollectionDialog(item.collectionId) {
            when(it) {
                true -> {
                    // If we don't navigate away, then remove this collection from the RV
                    viewmodel.removeCollection(item.collectionId)
                    notifyItemRemoved(position)
                }
                false -> {
                    // TODO fix item is missing but not really deleted
                    notifyItemChanged(position)
                }
            }
        }
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
        //.inflate(R.layout.fragment_collections, parent, false)
        val binding = FragmentCollectionsBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(values[position])
    }

    override fun getItemCount(): Int = values.size

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(values, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        //notifyDataSetChanged()
    }

    override fun onItemDismiss(position: Int) {
        Log.d("RV", "onItemDismiss")
        //mItems.remove(position);
        notifyItemRemoved(position)
    }

    inner class ViewHolder(val binding: FragmentCollectionsBinding) : RecyclerView.ViewHolder(
        binding.root
    ),
        ItemTouchHelperViewHolder {

        fun bind(collection: PathCollection) {
            binding.pathImage.setImageDrawable(
                UserCollectionsViewModel.getDrawable(
                    binding.root.context,
                    collection
                )
            )
            binding.content.text =
                UserCollectionsViewModel.getTitle(binding.root.context, collection)
            binding.switchWidget.isChecked = collection.enabled
            binding.root.setTag(R.id.collection_id, collection.collectionId)

            binding.switchWidget.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked != collection.enabled) {
                    collection.enabled = isChecked
                    listener.setIsEnabled(collection.collectionId, isChecked)
                }
            }

            binding.root.setOnClickListener {
                listener.editCollection(collection.collectionId)
            }

            binding.handle.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    dragStartListener.onStartDrag(this)
                }
                return@setOnTouchListener false
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