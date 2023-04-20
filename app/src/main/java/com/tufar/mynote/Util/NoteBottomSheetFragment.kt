package com.tufar.mynote.Util

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tufar.mynote.R
import com.tufar.mynote.databinding.FragmentCreateNoteBinding
import com.tufar.mynote.databinding.FragmentNotesBottomSheetBinding

class NoteBottomSheetFragment : BottomSheetDialogFragment() {
    private var _binding : FragmentNotesBottomSheetBinding? = null
    private val binding get() = _binding!!

    private var selectedColor = "#171c26"

    companion object{
        var noteId = -1
        fun newInstance(id : Int) : NoteBottomSheetFragment{
            val args = Bundle()
            val fragment = NoteBottomSheetFragment()
            fragment.arguments = args
            noteId = id
            return fragment
        }
    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)

        val view = LayoutInflater.from(context).inflate(R.layout.fragment_notes_bottom_sheet,null)
        dialog.setContentView(view)

        val param = (view.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = param.behavior

        if (behavior is BottomSheetBehavior<*>){
            behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    var state = ""
                    when(newState){
                        BottomSheetBehavior.STATE_DRAGGING -> {
                            state = "DRAGGING"
                        }
                        BottomSheetBehavior.STATE_SETTLING -> {
                            state = "SETTLING"
                        }
                        BottomSheetBehavior.STATE_COLLAPSED -> {
                            state = "COLLAPSED"
                        }
                        BottomSheetBehavior.STATE_EXPANDED -> {
                            state = "EXPANDED"
                        }
                        BottomSheetBehavior.STATE_HIDDEN -> {
                            state = "HIDDEN"
                            dismiss()
                            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        }
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    TODO("Not yet implemented")
                }

            })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotesBottomSheetBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (noteId != -1){
            binding.layoutDeleteNote.visibility = View.VISIBLE
        }else{
            binding.layoutDeleteNote.visibility = View.GONE
        }
        setListener()
    }
    private fun setListener(){
        binding.flNoteBlue.setOnClickListener {
            binding.imgNoteBlue.setImageResource(R.drawable.ic_tick)
            binding.imgNoteBlack.setImageResource(0)
            binding.imgNoteWhite.setImageResource(0)
            binding.imgNotePurple.setImageResource(0)
            binding.imgNoteOrange.setImageResource(0)
            binding.imgNoteYellow.setImageResource(0)
            binding.imgNoteGreen.setImageResource(0)
            selectedColor = "#4e33ff"
            val intent = Intent("bottom_sheet_action")
            intent.putExtra("action","blue")
            intent.putExtra("selectedColor",selectedColor)
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
        }
        binding.flNoteBlack.setOnClickListener {
            binding.imgNoteBlue.setImageResource(0)
            binding.imgNoteBlack.setImageResource(R.drawable.ic_tick)
            binding.imgNoteWhite.setImageResource(0)
            binding.imgNotePurple.setImageResource(0)
            binding.imgNoteOrange.setImageResource(0)
            binding.imgNoteYellow.setImageResource(0)
            binding.imgNoteGreen.setImageResource(0)
            selectedColor = "#202734"
            val intent = Intent("bottom_sheet_action")
            intent.putExtra("action","black")
            intent.putExtra("selectedColor",selectedColor)
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
        }
        binding.flNoteYellow.setOnClickListener {
            binding.imgNoteBlue.setImageResource(0)
            binding.imgNoteBlack.setImageResource(0)
            binding.imgNoteWhite.setImageResource(0)
            binding.imgNotePurple.setImageResource(0)
            binding.imgNoteOrange.setImageResource(0)
            binding.imgNoteYellow.setImageResource(R.drawable.ic_tick)
            binding.imgNoteGreen.setImageResource(0)
            selectedColor = "#FFd633"
            val intent = Intent("bottom_sheet_action")
            intent.putExtra("action","yellow")
            intent.putExtra("selectedColor",selectedColor)
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
        }
        binding.flNotePurple.setOnClickListener {
            binding.imgNoteBlue.setImageResource(0)
            binding.imgNoteBlack.setImageResource(0)
            binding.imgNoteWhite.setImageResource(0)
            binding.imgNotePurple.setImageResource(R.drawable.ic_tick)
            binding.imgNoteOrange.setImageResource(0)
            binding.imgNoteYellow.setImageResource(0)
            binding.imgNoteGreen.setImageResource(0)
            selectedColor = "#ae3b76"
            val intent = Intent("bottom_sheet_action")
            intent.putExtra("action","purple")
            intent.putExtra("selectedColor",selectedColor)
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
        }
        binding.flNoteGreen.setOnClickListener {
            binding.imgNoteBlue.setImageResource(0)
            binding.imgNoteBlack.setImageResource(0)
            binding.imgNoteWhite.setImageResource(0)
            binding.imgNotePurple.setImageResource(0)
            binding.imgNoteOrange.setImageResource(0)
            binding.imgNoteYellow.setImageResource(0)
            binding.imgNoteGreen.setImageResource(R.drawable.ic_tick)
            selectedColor = "#0aebaf"
            val intent = Intent("bottom_sheet_action")
            intent.putExtra("action","green")
            intent.putExtra("selectedColor",selectedColor)
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
        }
        binding.flNoteOrange.setOnClickListener {
            binding.imgNoteBlue.setImageResource(0)
            binding.imgNoteBlack.setImageResource(0)
            binding.imgNoteWhite.setImageResource(0)
            binding.imgNotePurple.setImageResource(0)
            binding.imgNoteOrange.setImageResource(R.drawable.ic_tick)
            binding.imgNoteYellow.setImageResource(0)
            binding.imgNoteGreen.setImageResource(0)
            selectedColor = "#FF7746"
            val intent = Intent("bottom_sheet_action")
            intent.putExtra("action","orange")
            intent.putExtra("selectedColor",selectedColor)
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
        }
        binding.flNoteWhite.setOnClickListener {
            binding.imgNoteBlue.setImageResource(0)
            binding.imgNoteBlack.setImageResource(0)
            binding.imgNoteWhite.setImageResource(R.drawable.ic_tick)
            binding.imgNotePurple.setImageResource(0)
            binding.imgNoteOrange.setImageResource(0)
            binding.imgNoteYellow.setImageResource(0)
            binding.imgNoteGreen.setImageResource(0)
            selectedColor = "#FFFFFFFF"
            val intent = Intent("bottom_sheet_action")
            intent.putExtra("action","white")
            intent.putExtra("selectedColor",selectedColor)
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
        }

        binding.layoutImage.setOnClickListener {
            val intent = Intent("bottom_sheet_action")
            intent.putExtra("action","Image")
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
            dismiss()
        }
        binding.layoutWebUrl.setOnClickListener {
            val intent = Intent("bottom_sheet_action")
            intent.putExtra("action","WebUrl")
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
            dismiss()
        }
        binding.layoutDeleteNote.setOnClickListener {
            val intent = Intent("bottom_sheet_action")
            intent.putExtra("action","deleteNote")
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
            dismiss()
        }
    }
}