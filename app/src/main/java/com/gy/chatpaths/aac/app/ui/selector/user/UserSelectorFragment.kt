package com.gy.chatpaths.aac.app.ui.selector.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gy.chatpaths.aac.app.databinding.UserFragmentBinding
import com.gy.chatpaths.aac.app.di.module.CurrentUser
import com.gy.chatpaths.aac.app.di.module.Firebase
import com.gy.chatpaths.aac.app.di.module.GuidedTour
import com.gy.chatpaths.aac.app.model.PathUser
import com.gy.chatpaths.aac.app.ui.manager.user.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UserSelectorFragment : Fragment() {
    val viewModel: UserViewModel by viewModels()

    @Suppress("ktlint:standard:property-naming")
    private var _binding: UserFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: RVAdapter

    @Inject
    lateinit var currentUser: CurrentUser

    @Inject
    lateinit var firebase: Firebase

    @Inject
    lateinit var guidedTour: GuidedTour

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = UserFragmentBinding.inflate(layoutInflater, container, false)
        context?.let {
            adapter = RVAdapter(it, this, mutableListOf())
            binding.userList.adapter = adapter
        }

        lifecycleScope.launch {
            val users = viewModel.users
            users.observe(viewLifecycleOwner, { pu ->
                val uList = pu.filter { it.userId != currentUser.userId }
                adapter.setUsers(uList)
            })
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun onUserSelected(user: PathUser) {
        setNavigationResult(user.userId, "user_id")
        findNavController().navigateUp()
    }

    private fun <T> Fragment.setNavigationResult(
        result: T,
        key: String,
    ) {
        findNavController().previousBackStackEntry?.savedStateHandle?.set(key, result)
    }
}
