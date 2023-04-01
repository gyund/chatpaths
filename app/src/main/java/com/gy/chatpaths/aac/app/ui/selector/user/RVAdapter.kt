package com.gy.chatpaths.aac.app.ui.selector.user

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gy.chatpaths.aac.app.DrawableUtils
import com.gy.chatpaths.aac.app.databinding.UserListContentBinding
import com.gy.chatpaths.aac.data.PathUser

class RVAdapter(
    private val context: Context,
    private val fragment: UserSelectorFragment,
    private var values: List<PathUser>
) :
    RecyclerView.Adapter<RVAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = UserListContentBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.bind(item)
    }

    override fun getItemCount() = values.size

    fun setUsers(users: List<PathUser>) {
        values = users
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: UserListContentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: PathUser) {
            binding.idText.text = user.userId.toString()
            binding.image.setImageDrawable(
                DrawableUtils.getDrawableImage(
                    context,
                    user.displayImage
                )
            )
            binding.content.text = user.name

            binding.root.setOnClickListener {
                fragment.onUserSelected(user)
            }
        }
    }
}