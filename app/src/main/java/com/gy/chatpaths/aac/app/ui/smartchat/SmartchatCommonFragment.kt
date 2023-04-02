package com.gy.chatpaths.aac.app.ui.smartchat

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import com.gy.chatpaths.aac.app.DrawableUtils
import com.gy.chatpaths.aac.app.MainActivity
import com.gy.chatpaths.aac.app.R
import com.gy.chatpaths.aac.app.databinding.FragmentSmartchatRootBinding
import com.gy.chatpaths.aac.app.di.module.CurrentUser
import com.gy.chatpaths.aac.app.di.module.PathReader
import com.gy.chatpaths.aac.data.Path
import com.gy.chatpaths.aac.data.PathUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * An example full-screen fragment that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
@AndroidEntryPoint
open class SmartchatCommonFragment : Fragment() {
    private var _binding: FragmentSmartchatRootBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var user: CurrentUser

    @Inject
    lateinit var pathReader: PathReader

    private val viewmodel: SmartchatViewModel by viewModels()

    private val args: SmartchatCommonFragmentArgs by navArgs()

    private fun getThemeAccentColor(context: Context): Int {
        val value = TypedValue()
        context.theme.resolveAttribute(androidx.appcompat.R.attr.colorAccent, value, true)
        return value.data
    }

    private fun getThemeColorOnBackground(context: Context): Int {
        val value = TypedValue()
        context.theme.resolveAttribute(
            com.google.android.material.R.attr.colorOnBackground,
            value,
            true,
        )
        return value.data
    }

    private fun getThemeColorPrimaryVariant(context: Context): Int {
        val value = TypedValue()
        context.theme.resolveAttribute(
            com.google.android.material.R.attr.colorPrimaryVariant,
            value,
            true,
        )
        return value.data
    }

    private val colors: Array<Int> by lazy {
        arrayOf(
            getThemeColorOnBackground(requireContext()),
            getThemeAccentColor(requireContext()),
            getThemeColorPrimaryVariant(requireContext()),
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSmartchatRootBinding.inflate(layoutInflater)

        // Use lifecycle scopes to keep things sequential when necessary but async
        lifecycleScope.launch {
            context?.apply {
                user.getUser().also {
                    viewmodel.setCurrentCollection(this, args.collectionId)

                    // Observe the UI Changes
                    setupObservers(user.getUserLive())
                    setupClickListeners()
                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        pathReader.release()
        super.onDestroy()
    }

    private fun setupObservers(userLive: LiveData<PathUser?>) {
        viewmodel.pathNavigator.observe(viewLifecycleOwner, {
            if (null == it.currentPath) {
                binding.pathImage.setImageDrawable(null)
                binding.pathDescription.text = null
                binding.overlay.overlayTextView.text = null
                // binding.imageOverlay.text = getString(R.string.hint_add_first_path)
            } else {
                updateViewWithAnimations(it.currentPath)
            }
            if (it.parent == null) {
                binding.parentTitle.text = null
            } else {
                binding.parentTitle.text = getStringTitle(it.parent)
                viewmodel.setTextColorFromCachedDrawable(binding.parentTitle)
            }
            when (it.hasPrevious) {
                true -> binding.prevPath.visibility = View.VISIBLE
                false -> binding.prevPath.visibility = View.INVISIBLE
            }
            when (it.hasNext) {
                true -> binding.nextPath.visibility = View.VISIBLE
                false -> binding.nextPath.visibility = View.INVISIBLE
            }
        })

        // Keep the userID in sync
        userLive.observe(viewLifecycleOwner, {
            it?.apply {
                lifecycleScope.launch {
                    // NAvigator is bound to the user
                    // pathNavigator.dataHasChanged()
                    binding.name.text = name
                }
            }
        })
    }

    private fun setupClickListeners() {
        binding.pathImage.setOnClickListener {
            // it.playSoundEffect(NAVIGATION_DOWN)
            lifecycleScope.launch {
                pathReader.readAfter(viewmodel.pathNavigator) {
                    preAnimation = R.anim.flyup
                    postAnimation = R.anim.flyupfrombottom
                    viewmodel.goCurrentPath()
                }
            }
        }

        binding.prevPath.setOnClickListener {
            preAnimation = R.anim.flyright
            postAnimation = R.anim.flyrightfromleft
            lifecycleScope.launch { viewmodel.previousPath() }
        }

        binding.nextPath.setOnClickListener {
            preAnimation = R.anim.flyleft
            postAnimation = R.anim.flyleftfromright
            lifecycleScope.launch { viewmodel.nextPath() }
        }
        binding.oopsButton.setOnClickListener {
            preAnimation = android.R.anim.fade_out
            postAnimation = android.R.anim.fade_in
            gotoParentPath()
        }
        binding.configureButton.setOnClickListener {
            viewmodel.pathNavigator.currentPath.let { cp ->
                // We need to accept null for this so if current path is null, then we can still configure it
                val action =
                    SmartchatCommonFragmentDirections.actionSmartchatCommonFragmentToPathsFragment(
                        collectionId = args.collectionId,
                        parentId = cp?.parentId ?: 0,
                    )
                findNavController().navigate(action)
            }

//            lifecycleScope.launch {
//                viewmodel.pathNavigator.setShowDisabled(!viewmodel.pathNavigator.showDisabled)
//                setupToolbar()
//            }
        }

        binding.name.setOnClickListener {
            lifecycleScope.launch {
                user.getUser()?.apply {
                    val action =
                        SmartchatCommonFragmentDirections.actionSmartchatCommonFragmentToUserDetailFragment(
                            userId,
                            true,
                        )
                    it.findNavController().navigate(action)
                }
            }
        }
    }

    private fun updateViewWithAnimations(path: Path) {
        activity?.invalidateOptionsMenu()

        if (path.pathId != binding.pathImage.tag) {
            if (preAnimation == android.R.anim.fade_out) {
                // this is our default case, but if we can guess the direction, we can make
                // this smarter
                if (path.parentId == binding.parentTitle.tag) {
                    preAnimation = R.anim.flyright
                    postAnimation = R.anim.flyrightfromleft
                } else {
                    preAnimation = R.anim.flyup
                    postAnimation = R.anim.flyupfrombottom
                }
            }

            // Different image, animage
            val preAnim = preAnimation
            if (preAnim != null) {
                setNavEnabled(false)
                preAnimation = null
                val loadedAnim = AnimationUtils.loadAnimation(requireContext(), preAnim)
                loadedAnim.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationRepeat(animation: Animation?) {
                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        updatePathUI(path)
                        val postAnim = postAnimation
                        if (postAnim != null) {
                            postAnimation = null
                            val exitanim =
                                AnimationUtils.loadAnimation(requireContext(), postAnim)
                            exitanim.setAnimationListener(object :
                                Animation.AnimationListener {
                                override fun onAnimationRepeat(animation: Animation?) {
                                }

                                override fun onAnimationEnd(animation: Animation?) {
                                    binding.pathImage.clearAnimation()
                                    setNavEnabled(true)
                                }

                                override fun onAnimationStart(animation: Animation?) {
                                }
                            })
                            binding.pathImage.startAnimation(exitanim)
                        } else {
                            animation?.reset()
                            binding.pathImage.clearAnimation()
                            setNavEnabled(true)
                        }
                    }

                    override fun onAnimationStart(animation: Animation?) {
                    }
                })
                binding.pathImage.startAnimation(loadedAnim)
            } else {
                preAnimation = null
                postAnimation = null
                updatePathUI(path)
            }
        } else {
            // just for the counts
            preAnimation = null
            postAnimation = null
            updatePathUI(path)
        }
    }

    private fun setNavEnabled(enabled: Boolean) {
        binding.pathImage.isEnabled = enabled
        binding.prevPath.isEnabled = enabled
        binding.nextPath.isEnabled = enabled
        binding.oopsButton.isEnabled = enabled
    }

    override fun onResume() {
        super.onResume()
        setupToolbar()
    }

    private fun setupToolbar() {
        if (!viewmodel.pathNavigator.showDisabled) {
            // activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            (activity as MainActivity).disableToolbar()
            binding.configureButton.text = getString(R.string.configure)
            binding.configureButton.setCompoundDrawables(null, null, null, null)
        } else {
            activity?.window?.decorView?.systemUiVisibility = 0
            (activity as MainActivity).enableToolbar()
            if (ORIENTATION_PORTRAIT == activity?.resources?.configuration?.orientation) {
                binding.configureButton.text = null
            } else {
                binding.configureButton.text = getString(R.string.leave_configure)
            }

            binding.configureButton.setCompoundDrawablesWithIntrinsicBounds(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_baseline_keyboard_return_24,
                    requireContext().theme,
                ),
                null,
                null,
                null,
            )
        }
    }

    @SuppressLint("SetTextI18n")
    fun updatePathUI(current: Path) {
        context?.let { context ->

            val prefs: SharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context)
            val fixedTxtColor = prefs.getBoolean("fixed_path_text_color", false)

            // HIDE DURING MODIFICATIONS
            binding.pathDescription.visibility = View.INVISIBLE
            binding.overlay.cardViewOverlay.visibility = View.GONE
            // NOTE: If the values aren't cleared, then edit text can prepopulate the wrong value
            binding.pathDescription.setText(
                getStringTitle(current),
                TextView.BufferType.SPANNABLE,
            )
            binding.overlay.overlayTextView.text = null

            val drawableImg = DrawableUtils.getDrawableImage(context, current)

            if (drawableImg != null) {
                binding.pathImage.setImageDrawable(drawableImg)
                if (!fixedTxtColor) {
                    viewmodel.setTextColorFromDrawable(
                        binding.pathImage.drawable,
                        binding.pathDescription,
                        colors[current.pathId % colors.size],
                    )
                    // binding.overlay.overlayTextView.setTextColor(binding.pathDescription.currentTextColor.xor(0x00ffffff))
                }

                binding.pathDescription.visibility = View.VISIBLE

                // If admin, we'll use the empty text to tell the user whether the image is
                // shared or not shared
                if (viewmodel.pathNavigator.showDisabled) {
                    if (current.imageUri.isNullOrEmpty()) {
                        binding.overlay.overlayTextView.text = getString(R.string.default_text)
                    }
                    binding.overlay.cardViewOverlay.visibility = View.VISIBLE
                } else {
                    binding.overlay.cardViewOverlay.visibility = View.GONE
                }
            } else {
                binding.pathImage.setImageDrawable(null)

                if (!fixedTxtColor) {
                    // Get the default image, since no image was found for this (or we're text only)

                    val drawable = DrawableUtils.getDrawableImage(
                        context,
                        null,
                        R.drawable.ic_baseline_image_24,
                    )

                    if (null != drawable) { // make sure we aren't missing the default image too instead of a theme
                        viewmodel.setTextColorFromDrawable(
                            drawable,
                            binding.pathDescription,
                            colors[current.pathId % colors.size],
                        )
                    } else {
                        // The alternate color is derived off a moduli of the path id (deterministic)
                        binding.pathDescription.setTextColor(colors[current.pathId % colors.size])
                    }
                }

                binding.pathDescription.visibility = View.VISIBLE
                binding.overlay.cardViewOverlay.visibility = View.GONE
            }
            binding.pathImage.tag = current.pathId
            binding.parentTitle.tag = current.parentId
            binding.wordCount.text = current.timesUsed.toString()
        }
    }

    private fun getStringTitle(pud: Path): String? {
        return pud.name
    }

    private fun gotoParentPath() {
        lifecycleScope.launch {
            viewmodel.goParentPath()
        }
    }

    companion object {

        private var preAnimation: Int? = null
        private var postAnimation: Int? = null
    }
}
