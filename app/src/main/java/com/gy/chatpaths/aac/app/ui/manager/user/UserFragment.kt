package com.gy.chatpaths.aac.app.ui.manager.user

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gy.chatpaths.aac.app.R
import com.gy.chatpaths.aac.app.databinding.DialogUserNameBinding
import com.gy.chatpaths.aac.app.databinding.UserFragmentBinding
import com.gy.chatpaths.aac.app.di.module.CurrentUser
import com.gy.chatpaths.aac.app.di.module.GuidedTour
import com.gy.chatpaths.model.PathUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class UserFragment : Fragment() {

    val viewModel: UserViewModel by viewModels()
    private var _binding: UserFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: SimpleItemRecyclerViewAdapter

    @Inject
    lateinit var currentUser: CurrentUser

    @Inject
    lateinit var guidedTour: GuidedTour

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = UserFragmentBinding.inflate(layoutInflater, container, false)
        context?.let {
            adapter = SimpleItemRecyclerViewAdapter(it, this, mutableListOf())
            binding.userList.adapter = adapter
        }

        lifecycleScope.launch {
            val users = viewModel.users
            users.observe(viewLifecycleOwner, {
                adapter.setUsers(it)
            })
        }

        this.setHasOptionsMenu(true)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        fun showGuidedHelp() {
            guidedTour.addGuidedEntry(
                R.id.addUserMenu,
                GuidedTour.Dialog.USER_ADD,
                getString(R.string.onboard_user_add_primary),
                getString(R.string.onboard_user_add_secondary),
                targetIcon = R.drawable.ic_baseline_person_add_24,
            ).show(this)
        }

        showGuidedHelp()
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.users_menu, menu)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.addUserMenu -> {
                lifecycleScope.launch {
                    viewModel.users.value?.let {
                        createAddUserDialog()
                    }
                }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun createAddUserDialog() {
        context?.apply {
            fun showAddUserDialog() {
                val binding = DialogUserNameBinding.inflate(layoutInflater)
                val madb = MaterialAlertDialogBuilder(this)
                    .setTitle("Add user")
                    .setMessage("Enter a name")
                    .setView(binding.root)
                    .setPositiveButton(getString(android.R.string.ok)) { _, _ ->
                        // if the insert fails, the username already exists
                    }
                    .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                    }
                    .show()
                setUsernameValidationOnClickListener(madb, binding, ::_addUser)
            }

            showAddUserDialog()
        }
    }

    private fun setUsernameValidationOnClickListener(
        dialog: AlertDialog,
        binding: DialogUserNameBinding,
        action: suspend (name: String) -> Boolean,
    ) {
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            if (binding.username.text.toString().isBlank()) {
                binding.errorMessage.text = getString(R.string.user_empty_not_allowed)
                binding.errorMessage.visibility = View.VISIBLE
            } else {
                lifecycleScope.launch {
                    withContext(Dispatchers.Main) {
                        if (action(binding.username.text.toString())) { // success
                            dialog.dismiss()
                        } else {
                            binding.errorMessage.text = getString(R.string.user_already_exists)
                            binding.errorMessage.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    /**
     * Wrapper for model access
     */
    private suspend fun _addUser(name: String): Boolean {
        var success = false

        context?.apply {
            val id = currentUser.addUser(name)
            if (id > 0) {
                success = true
                currentUser.setUser(id)
                val action =
                    UserFragmentDirections.actionGlobalNavHome()
                findNavController().navigate(action)
            }
        }

        return success
    }

    fun setUser(user: PathUser) {
        lifecycleScope.launch {
            // Set the user, then switch screens so the next screen doesn't load old date
            // as work for threads is queued up
            viewModel.setUser(user)
            val action =
                UserFragmentDirections.actionGlobalNavHome()
            findNavController().navigate(action)
        }
    }
}
