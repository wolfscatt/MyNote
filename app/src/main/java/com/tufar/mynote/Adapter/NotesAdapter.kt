package com.tufar.mynote.Adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.tufar.mynote.Entities.Notes
import com.tufar.mynote.R
import com.tufar.mynote.databinding.ItemRvNotesBinding

class NotesAdapter() : RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {
    var noteList = ArrayList<Notes>()
    var listener : OnItemClickListener? = null
    class NotesViewHolder(val binding: ItemRvNotesBinding) : RecyclerView.ViewHolder(binding.root){

    }
    fun setData(arrNotesList : List<Notes>){
        noteList = arrNotesList as ArrayList<Notes>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        var binding = ItemRvNotesBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return NotesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.binding.tvTitle.text = noteList[position].title
        holder.binding.tvDesc.text = noteList[position].noteText
        holder.binding.tvDateTime.text = noteList[position].dateTime

        if (noteList[position].color != null){
            holder.binding.cardView.setCardBackgroundColor(Color.parseColor(noteList[position].color))
        }else{
            holder.binding.cardView.setCardBackgroundColor(R.color.lightBlack.toInt())
        }
        if (noteList[position].imgPath != null){
            holder.binding.roundImgNote.setImageBitmap(BitmapFactory.decodeFile(noteList[position].imgPath))
            holder.binding.roundImgNote.visibility = View.VISIBLE
        }else{
            holder.binding.roundImgNote.visibility = View.GONE
        }
        if (noteList[position].webLink != null){
            holder.binding.tvWebLinkRV.text = noteList[position].webLink
            holder.binding.tvWebLinkRV.visibility = View.VISIBLE
        }else{
            holder.binding.tvWebLinkRV.visibility = View.GONE
        }
        holder.binding.cardView.setOnClickListener {
            listener!!.onClicked(noteList[position].id)
        }

    }
    override fun getItemCount(): Int {
        return noteList.size
    }
    fun setOnClickListener(listener1:OnItemClickListener){
        listener = listener1
    }

    interface OnItemClickListener{
        fun onClicked(notesId : Int?)
    }

}