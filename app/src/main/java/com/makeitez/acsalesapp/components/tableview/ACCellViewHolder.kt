package com.makeitez.acsalesapp.components.tableview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import com.makeitez.acsalesapp.R
import kotlinx.android.synthetic.main.item_general_table_view_cell.view.*

class ACCellViewHolder(itemView: View) : AbstractViewHolder(itemView) {
    private val tableCellLayout: FrameLayout = itemView.tableCellLayout
    private val tableCellText: AppCompatTextView = itemView.tableCellText

    fun bind(cellText: String?) {
        tableCellText.text = cellText

        // remeasure for auto-resize
        tableCellLayout.layoutParams.width = FrameLayout.LayoutParams.WRAP_CONTENT
        tableCellText.requestLayout()
    }

    companion object {
        fun newInstance(parent: ViewGroup) = ACCellViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_general_table_view_cell, parent, false)
        )
    }
}