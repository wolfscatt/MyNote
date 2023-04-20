package com.tufar.mynote

import android.app.Activity.RESULT_OK
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.icu.util.TimeZone.SystemTimeZoneType
import android.net.Uri
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.tufar.mynote.DataBase.NotesDatabase
import com.tufar.mynote.Entities.Notes
import com.tufar.mynote.Util.NoteBottomSheetFragment
import com.tufar.mynote.databinding.FragmentCreateNoteBinding
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class CreateNoteFragment : BaseFragment() , EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks{

    private var _binding : FragmentCreateNoteBinding? = null
    private val binding get() = _binding!!

    private var currentDate: String? = null
    private var selectedColor = "#171c26"
    private var READ_STORAGE_PERM = 123
    private var REQUEST_CODE_IMAGE = 456
    private var selectedImagePath = ""
    private var webLink = ""
    private var noteId = -1



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        noteId = requireArguments().getInt("noteId",-1)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateNoteBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            CreateNoteFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (noteId != -1){
            launch {
                context?.let {
                    var notes = NotesDatabase.getDatabase(it).noteDao().getSpecificNote(noteId)
                    binding.colorView.setBackgroundColor(Color.parseColor(notes.color))
                    binding.etNoteTitle.setText(notes.title)
                    binding.etNoteSubTitle.setText(notes.subTitle)
                    binding.etNoteDescription.setText(notes.noteText)

                    if (notes.imgPath != ""){
                        selectedImagePath = notes.imgPath!!
                        binding.imgNote.setImageBitmap(BitmapFactory.decodeFile(notes.imgPath))
                        binding.imgNote.visibility = View.VISIBLE
                        binding.imgDelete.visibility = View.VISIBLE
                        binding.relativeLayoutImage.visibility = View.VISIBLE
                    }else{
                        binding.imgNote.visibility = View.GONE
                        binding.imgDelete.visibility = View.GONE
                        binding.relativeLayoutImage.visibility = View.GONE
                    }
                    if (notes.webLink != ""){
                        webLink = notes.webLink!!
                        binding.tvWebLink.text = notes.webLink
                        binding.lWebUrl.visibility = View.VISIBLE
                        binding.urlDelete.visibility = View.VISIBLE
                        binding.etWebLink.setText(notes.webLink)
                    }else{
                        binding.urlDelete.visibility = View.GONE
                        binding.lWebUrl.visibility = View.GONE
                    }
                    //requireActivity().supportFragmentManager.popBackStack()

                }
            }
        }

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(broadcastReceiver,
            IntentFilter("bottom_sheet_action")
        )
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O ){
            val sdf = SimpleDateFormat("dd/M/yyyy HH:mm:ss")
            currentDate = sdf.format(Date())
            binding.tvDateTime.text = currentDate
        }else{
            val formatter = DateTimeFormatter.ofPattern("dd/M/yyyy HH:mm:ss")
            currentDate = LocalDateTime.now().format(formatter)
            binding.tvDateTime.text = currentDate
        }

        binding.colorView.setBackgroundColor(Color.parseColor(selectedColor))
        binding.imgDone.setOnClickListener {
            // Save Note
            if (noteId != -1){
                updateNote()
            }else{
                saveNote()
            }
            //replaceFragment(HomeFragment.newInstance(),false)
        }
        binding.imgBack.setOnClickListener {
            //replaceFragment(HomeFragment.newInstance(),false)
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.imgMore.setOnClickListener {
            var noteBottomSheetFragment = NoteBottomSheetFragment.newInstance(noteId)
            noteBottomSheetFragment.show(requireActivity().supportFragmentManager,"Note Bottom Sheet Fragment")
        }
        binding.imgDelete.setOnClickListener {
            selectedImagePath = ""
            binding.relativeLayoutImage.visibility = View.GONE
        }
        binding.btnOk.setOnClickListener {
            if (binding.etWebLink.text.toString().trim().isNotEmpty()){
                checkWebUrl()
            }else{
                Toast.makeText(requireContext(),"Url is required",Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnCancel.setOnClickListener {
            if(noteId != -1){
                binding.tvWebLink.visibility = View.VISIBLE
                binding.lWebUrl.visibility = View.GONE
            }else{
                binding.lWebUrl.visibility = View.GONE
            }

        }
        binding.urlDelete.setOnClickListener {
            webLink = ""
            binding.tvWebLink.visibility = View.GONE
            binding.lWebUrl.visibility = View.GONE
            binding.urlDelete.visibility = View.GONE

        }
        binding.tvWebLink.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW,Uri.parse(binding.etWebLink.text.toString()))
            startActivity(intent)
        }
    }
    private fun updateNote(){
        launch {
            context?.let {
                var notes = NotesDatabase.getDatabase(it).noteDao().getSpecificNote(noteId)
                notes.title = binding.etNoteTitle.text.toString()
                notes.subTitle = binding.etNoteSubTitle.text.toString()
                notes.noteText = binding.etNoteDescription.text.toString()
                notes.dateTime = currentDate
                notes.color = selectedColor
                notes.imgPath = selectedImagePath
                notes.webLink = webLink
                NotesDatabase.getDatabase(it).noteDao().updateNote(notes)
                binding.etNoteTitle.setText("")
                binding.etNoteSubTitle.setText("")
                binding.etNoteDescription.setText("")
                binding.relativeLayoutImage.visibility = View.GONE
                binding.imgNote.visibility = View.GONE
                binding.tvWebLink.visibility = View.GONE
                requireActivity().supportFragmentManager.popBackStack()

            }
        }
    }
    private fun saveNote(){
        if (binding.etNoteTitle.text.isNullOrEmpty()){
            Toast.makeText(context,"Note Title is Required",Toast.LENGTH_SHORT).show()
        }
        else if (binding.etNoteSubTitle.text.isNullOrEmpty()){
            Toast.makeText(context,"Note Sub Title is Required",Toast.LENGTH_SHORT).show()
        }
        else if (binding.etNoteDescription.text.isNullOrEmpty()){
            Toast.makeText(context,"Note Description must not be null",Toast.LENGTH_SHORT).show()
        }else {
            launch {
                val notes = Notes()
                notes.title = binding.etNoteTitle.text.toString()
                notes.subTitle = binding.etNoteSubTitle.text.toString()
                notes.noteText = binding.etNoteDescription.text.toString()
                notes.dateTime = currentDate
                notes.color = selectedColor
                notes.imgPath = selectedImagePath
                notes.webLink = webLink
                context?.let {
                    NotesDatabase.getDatabase(it).noteDao().insertNotes(notes)
                    binding.etNoteTitle.setText("")
                    binding.etNoteSubTitle.setText("")
                    binding.etNoteDescription.setText("")
                    binding.relativeLayoutImage.visibility = View.GONE
                    binding.imgNote.visibility = View.GONE
                    binding.tvWebLink.visibility = View.GONE
                    requireActivity().supportFragmentManager.popBackStack()

                }

            }
        }
    }
    private fun deleteNote(){
        launch {
            context?.let {
                NotesDatabase.getDatabase(it).noteDao().deleteSpecificNote(noteId)
                requireActivity().supportFragmentManager.popBackStack()

            }
        }
    }

    private fun checkWebUrl(){
        if (Patterns.WEB_URL.matcher(binding.etWebLink.text.toString()).matches()){
            binding.lWebUrl.visibility = View.GONE
            binding.etWebLink.isEnabled = false
            webLink = binding.etWebLink.text.toString()
            binding.tvWebLink.visibility = View.VISIBLE
            binding.tvWebLink.text = binding.etWebLink.text.toString()
        }else{
            Toast.makeText(requireContext(),"Url is not valid",Toast.LENGTH_SHORT).show()
        }
    }
    private val broadcastReceiver : BroadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            var actionColor = p1!!.getStringExtra("action")

            when(actionColor){
                "blue" ->{
                    selectedColor = p1.getStringExtra("selectedColor")!!
                    binding.colorView.setBackgroundColor(Color.parseColor(selectedColor))
                }
                "black" ->{
                    selectedColor = p1.getStringExtra("selectedColor")!!
                    binding.colorView.setBackgroundColor(Color.parseColor(selectedColor))
                }
                "green" ->{
                    selectedColor = p1.getStringExtra("selectedColor")!!
                    binding.colorView.setBackgroundColor(Color.parseColor(selectedColor))
                }
                "orange" ->{
                    selectedColor = p1.getStringExtra("selectedColor")!!
                    binding.colorView.setBackgroundColor(Color.parseColor(selectedColor))
                }
                "yellow" ->{
                    selectedColor = p1.getStringExtra("selectedColor")!!
                    binding.colorView.setBackgroundColor(Color.parseColor(selectedColor))
                }
                "purple" ->{
                    selectedColor = p1.getStringExtra("selectedColor")!!
                    binding.colorView.setBackgroundColor(Color.parseColor(selectedColor))
                }
                "white" ->{
                    selectedColor = p1.getStringExtra("selectedColor")!!
                    binding.colorView.setBackgroundColor(Color.parseColor(selectedColor))
                }
                "Image" ->{
                    readStorageTask()
                    binding.lWebUrl.visibility = View.GONE
                }
                "WebUrl"->{
                    binding.lWebUrl.visibility = View.VISIBLE
                }
                "deleteNote" ->{
                    deleteNote()
                }
                else ->{
                    binding.relativeLayoutImage.visibility = View.GONE
                    binding.imgNote.visibility = View.GONE
                    binding.lWebUrl.visibility = View.GONE
                    selectedColor = p1.getStringExtra("selectedColor")!!
                    binding.colorView.setBackgroundColor(Color.parseColor(selectedColor))
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver)
    }

    private fun hasReadStoragePerm() : Boolean{
        return EasyPermissions.hasPermissions(requireContext(),android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    private fun hasWriteStoragePerm() : Boolean{
        return EasyPermissions.hasPermissions(requireContext(),android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    private fun readStorageTask(){
        if (hasReadStoragePerm()){
            pickImageFromGallery()
        }else{
            EasyPermissions.requestPermissions(requireActivity(),getString(R.string.storage_permission_text),READ_STORAGE_PERM,android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }
    private fun pickImageFromGallery(){
        var intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (intent.resolveActivity(requireActivity().packageManager) != null){
            startActivityForResult(intent,REQUEST_CODE_IMAGE)
        }
    }
    private fun getPathFromUri(contentUri:Uri) : String{
        var filePath : String? = null
        var cursor = requireActivity().contentResolver.query(contentUri,null,null,null,null)
        if (cursor == null){
            filePath = contentUri.path
        }else{
            cursor.moveToFirst()
            var index = cursor.getColumnIndex("_data")
            filePath = cursor.getString(index)
            cursor.close()
        }
        return filePath!!
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK){
            if (data != null){
                var selectedImageUrl = data.data
                selectedImageUrl?.let {
                    try {
                        var inputStream = requireActivity().contentResolver.openInputStream(it)
                        var bitmap = BitmapFactory.decodeStream(inputStream)
                        binding.imgNote.setImageBitmap(bitmap)
                        binding.imgNote.visibility = View.VISIBLE
                        binding.relativeLayoutImage.visibility = View.VISIBLE

                        selectedImagePath = getPathFromUri(selectedImageUrl)
                    }catch (e: Exception){
                        Toast.makeText(requireContext(),e.message,Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,requireActivity())
    }
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(requireActivity(),perms)){
            AppSettingsDialog.Builder(requireActivity()).build().show()
        }
    }

    override fun onRationaleAccepted(requestCode: Int) {
    }

    override fun onRationaleDenied(requestCode: Int) {
    }
}