package com.gy.chatpaths.aac.app.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.gy.chatpaths.aac.app.BindableAdapter
import com.gy.chatpaths.aac.app.R
import com.gy.chatpaths.aac.app.databinding.FragmentHomeCollectionsBinding
import com.gy.chatpaths.aac.app.ui.manager.userdetail.UserCollectionsViewModel
import com.gy.chatpaths.aac.app.model.PathCollection

class ActiveCollectionsRVAdapter(
    private val context: Context,
) : RecyclerView.Adapter<ActiveCollectionsRVAdapter.ViewHolder>(), BindableAdapter<PathCollection> {

    private var values: List<PathCollection> = emptyList()

    /**
     * Standard way of setting data for this adapter
     */
    override fun setData(items: List<PathCollection>) {
        values = items
        notifyDataSetChanged()
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
        val binding = FragmentHomeCollectionsBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(values[position])
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(val binding: FragmentHomeCollectionsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(collection: PathCollection) {
            binding.pathImage.setImageDrawable(
                UserCollectionsViewModel.getDrawable(
                    binding.root.context,
                    collection,
                ),
            )
            binding.content.text =
                UserCollectionsViewModel.getTitle(binding.root.context, collection)

            binding.outercardview.setCardBackgroundColor(
                getColor(
                    context,
                    R.color.primaryDarkColor,
                ),
            )
            binding.potwView.visibility = View.GONE

            fun onPathClicked(it: View) {
                val action =
                    HomeFragmentDirections.actionNavHomeToSmartchatCommonFragment(collection.collectionId)
                it.findNavController().navigate(action)
            }

            binding.innercardview.setOnClickListener {
                onPathClicked(it)
            }
        }

        override fun toString(): String {
            return super.toString() + " '" + binding.content.text + "'"
        }
    }

    override fun getItemId(position: Int): Long {
        return values[position].collectionId.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return values[position].collectionId
    }

    init {
        setHasStableIds(true)
    }
}
