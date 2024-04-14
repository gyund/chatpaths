package com.gy.chatpaths.aac.app.ui.imageselect

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gy.chatpaths.aac.app.BindableAdapter
import com.gy.chatpaths.aac.app.DrawableUtils
import com.gy.chatpaths.aac.app.databinding.FragmentImageselectImageBinding

/**
 * [RecyclerView.Adapter] that can display a [UserPathCollectionPrefsView].
 */
class RVAdapter(
    private val listener: ImageSelectListener,
) : RecyclerView.Adapter<RVAdapter.ViewHolder>(), BindableAdapter<String> {
    private var values: List<String> = emptyList()

    /**
     * Standard way of setting data for this adapter
     */
    override fun setData(items: List<String>) {
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

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        // .inflate(R.layout.fragment_collections, parent, false)
        val binding = FragmentImageselectImageBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        holder.bind(values[position])
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(val binding: FragmentImageselectImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(resourceOrUri: String) {
            binding.pathImage.setImageDrawable(
                DrawableUtils.getDrawableImage(
                    binding.root.context,
                    resourceOrUri,
                    null,
                ),
            )

            if (binding.pathImage.drawable == null) {
                Log.d("RVAdapter", "image is not available: $resourceOrUri")
            }

            // binding.outercardview.setCardBackgroundColor(getColor(context, R.color.primaryDarkColor))
            fun onPathClicked() {
                listener.selectImage(resourceOrUri)
            }

            binding.innercardview.setOnClickListener {
                onPathClicked()
            }
        }
    }
//
//    override fun getItemId(position: Int): Long {
//        return values[position].collectionId.toLong()
//    }
//
//    override fun getItemViewType(position: Int): Int {
//        return values[position].collectionId
//    }

    init {
        setHasStableIds(false)
    }
}
