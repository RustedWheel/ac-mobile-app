package com.makeitez.acsalesapp.components.tableview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import com.makeitez.acsalesapp.R
import kotlinx.android.synthetic.main.item_general_table_view_column_header.view.*

class ACColumnHeaderViewHolder(itemView: View) : AbstractViewHolder(itemView) {
    private val tableColumnHeaderLayout: FrameLayout = itemView.tableColumnHeaderLayout
    private val tableColumnHeaderText: AppCompatTextView = itemView.tableColumnHeaderText

    fun bind(columnHeaderText: String?) {
        tableColumnHeaderText.text = columnHeaderText

        // remeasure for auto-resize
        tableColumnHeaderLayout.layoutParams.width = FrameLayout.LayoutParams.WRAP_CONTENT
        tableColumnHeaderText.requestLayout()
    }

    companion object {
        fun newInstance(parent: ViewGroup) = ACColumnHeaderViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_general_table_view_column_header, parent, false)
        )
    }

}