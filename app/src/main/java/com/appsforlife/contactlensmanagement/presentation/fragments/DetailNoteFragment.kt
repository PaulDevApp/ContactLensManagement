package com.appsforlife.contactlensmanagement.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.transition.TransitionInflater
import com.appsforlife.contactlensmanagement.databinding.LayoutDetailFragmentBinding
import com.appsforlife.contactlensmanagement.domain.entities.NoteItem
import com.appsforlife.contactlensmanagement.presentation.viewmodelfactories.noteitemviewmodelfactory.DetailNoteItemViewModelFactory
import com.appsforlife.contactlensmanagement.presentation.viewmodels.noteitemviewmodels.DetailNoteItemViewModel


class DetailNoteFragment : Fragment() {

    private lateinit var detailNoteViewModel: DetailNoteItemViewModel

    private var _binding: LayoutDetailFragmentBinding? = null
    private val binding: LayoutDetailFragmentBinding
        get() = _binding ?: throw RuntimeException("LayoutDetailFragmentBinding is null")

    private var noteId: Int = NoteItem.UNDEFINED_ID
    private var screenMode: String = MODE_UNKNOWN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        parseParams()
        setFabTransition()
    }

    private fun setFabTransition() {
        sharedElementEnterTransition = activity?.let {
            TransitionInflater.from(it)
                .inflateTransition(com.appsforlife.contactlensmanagement.R.transition.fragment_fab_transition)
        }
        sharedElementReturnTransition = activity?.let {
            TransitionInflater.from(it)
                .inflateTransition(com.appsforlife.contactlensmanagement.R.transition.fragment_fab_transition)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = LayoutDetailFragmentBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        detailNoteViewModel = ViewModelProvider(
            this,
            DetailNoteItemViewModelFactory(requireContext())
        )[DetailNoteItemViewModel::class.java]

        with(binding) {
            viewModel = detailNoteViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        setUpToolbar()

        onBackPressedCallBack()

        launchRightMode()

    }

    private fun parseParams() {
        val args = requireArguments()
        if (!args.containsKey(MODE)) {
            throw RuntimeException("Param screen mode is absent")
        }
        val mode = args.getString(MODE)
        if (mode != MODE_EDIT && mode != MODE_ADD) {
            throw RuntimeException("Unknown screen mode $mode")
        }

        screenMode = mode

        if (screenMode == MODE_EDIT) {
            if (!args.containsKey(NOTE_ID)) {
                throw RuntimeException("Param shop item id is absent")
            }
            noteId = args.getInt(NOTE_ID, NoteItem.UNDEFINED_ID)
        }
    }

    private fun launchRightMode() {
        when (screenMode) {
            MODE_EDIT -> launchEditMode()
            MODE_ADD -> launchAddMode()
        }
    }

    private fun launchAddMode() {
        with(binding) {
            fabSave.setOnClickListener {
                detailNoteViewModel.addNoteItem(
                    inputLeftOpticalPower = etOpticalPowerLeft.text?.toString(),
                    inputLeftRadiusOfCurvature = etRadiusOfCurvatureLeft.text?.toString(),
                    inputLeftCylinderPower = etCylinderPowerLeft.text?.toString(),
                    inputLeftAxis = etAxisLeft.text?.toString(),
                    inputRightOpticalPower = etOpticalPowerRight.text?.toString(),
                    inputRightRadiusOfCurvature = etRadiusOfCurvatureRight.text?.toString(),
                    inputRightCylinderPower = etCylinderPowerRight.text?.toString(),
                    inputRightAxis = etAxisRight.text?.toString(),
                    inputTitle = etTitle.text?.toString()
                )
                backPresses()
            }
        }
    }

    private fun launchEditMode() {
        with(binding) {
            detailNoteViewModel.getNoteItem(noteId)
            fabSave.setOnClickListener {
                detailNoteViewModel.editNoteItem(
                    inputLeftOpticalPower = etOpticalPowerLeft.text?.toString(),
                    inputLeftRadiusOfCurvature = etRadiusOfCurvatureLeft.text?.toString(),
                    inputLeftCylinderPower = etCylinderPowerLeft.text?.toString(),
                    inputLeftAxis = etAxisLeft.text?.toString(),
                    inputRightOpticalPower = etOpticalPowerRight.text?.toString(),
                    inputRightRadiusOfCurvature = etRadiusOfCurvatureRight.text?.toString(),
                    inputRightCylinderPower = etCylinderPowerRight.text?.toString(),
                    inputRightAxis = etAxisRight.text?.toString(),
                    inputTitle = etTitle.text?.toString()
                )
                backPresses()
            }
        }
    }

    private fun setUpToolbar() {
        binding.toolbarNoteList.tvToolbarTitle.text =
            requireActivity().resources.getString(com.appsforlife.contactlensmanagement.R.string.parameters)
    }

    private fun onBackPressedCallBack() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    backPresses()
                }
            })
    }

    private fun backPresses() {
        requireActivity().supportFragmentManager.popBackStack(NoteListFragment.NAME, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        private const val NOTE_ID = "extra_note_id"

        private const val MODE = "mode"
        private const val MODE_UNKNOWN = ""
        private const val MODE_EDIT = "mode_edit"
        private const val MODE_ADD = "mode_add"

        fun newInstance(): DetailNoteFragment {
            return DetailNoteFragment().apply {
                arguments = Bundle().apply {
                    putString(MODE, MODE_ADD)
                }
            }
        }

        fun newInstanceEdit(noteId: Int): DetailNoteFragment {
            return DetailNoteFragment().apply {
                arguments = Bundle().apply {
                    putString(MODE, MODE_EDIT)
                    putInt(NOTE_ID, noteId)
                }
            }
        }
    }
}