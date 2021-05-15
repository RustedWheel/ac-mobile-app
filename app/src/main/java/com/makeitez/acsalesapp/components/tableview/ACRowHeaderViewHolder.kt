package com.makeitez.acsalesapp.components.tableview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import com.makeitez.acsalesapp.R
import kotlinx.android.synthetic.main.item_general_table_view_row_header.view.*

class ACRowHeaderViewHolder(itemView: View) : AbstractViewHolder(itemView) {
    private val tableRowHeaderText: AppCompatTextView = itemView.tableRowHeaderText

    fun bind(rowHeaderText: String?) {
        tableRowHeaderText.text = rowHeaderText
    }

    companion object {
        fun newInstance(parent: ViewGroup) = ACRowHeaderViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_general_table_view_row_header, parent, false)
        )
    }
}