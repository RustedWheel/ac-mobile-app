package com.makeitez.acsalesapp.screens.announcement.general

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.makeitez.acsalesapp.models.GeneralAnnouncement
import com.makeitez.acsalesapp.utils.extensions.clearAndAddAll

class GeneralAnnouncementListAdapter(private val isEditing: Boolean,
    private val listener: GeneralAnnouncementViewHolder.Listener) : RecyclerView.Adapter<GeneralAnnouncementViewHolder>() {

    private val announcementList: MutableList<GeneralAnnouncement> = arrayListOf()

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GeneralAnnouncementViewHolder = GeneralAnnouncementViewHolder.newInstance(parent, isEditing, listener)

    override fun onBindViewHolder(holder: GeneralAnnouncementViewHolder, position: Int) = holder.bind(announcementList[position])

    override fun getItemCount() = announcementList.size

    override fun getItemId(position: Int): Long = announcementList[position].id.hashCode().toLong()

    fun setList(newList: List<GeneralAnnouncement>){
        announcementList.clearAndAddAll(newList)
        notifyDataSetChanged()
    }

}