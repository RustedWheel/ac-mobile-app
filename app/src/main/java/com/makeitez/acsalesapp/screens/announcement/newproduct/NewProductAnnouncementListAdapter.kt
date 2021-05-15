package com.makeitez.acsalesapp.screens.announcement.newproduct

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.makeitez.acsalesapp.models.NewProductAnnouncement
import com.makeitez.acsalesapp.utils.extensions.clearAndAddAll

class NewProductAnnouncementListAdapter(private val isEditing: Boolean,
    private val listener: NewProductAnnouncementViewHolder.Listener) : RecyclerView.Adapter<NewProductAnnouncementViewHolder>() {

    private val announcementList: MutableList<NewProductAnnouncement> = arrayListOf()

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewProductAnnouncementViewHolder = NewProductAnnouncementViewHolder.newInstance(parent, isEditing, listener)

    override fun onBindViewHolder(holder: NewProductAnnouncementViewHolder, position: Int) = holder.bind(announcementList[position])

    override fun getItemCount() = announcementList.size

    override fun getItemId(position: Int): Long = announcementList[position].id.hashCode().toLong()

    fun setList(newList: List<NewProductAnnouncement>){
        announcementList.clearAndAddAll(newList)
        notifyDataSetChanged()
    }

}