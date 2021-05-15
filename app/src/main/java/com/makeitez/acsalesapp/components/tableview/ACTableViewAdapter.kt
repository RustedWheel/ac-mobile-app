package com.makeitez.acsalesapp.components.tableview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.evrencoskun.tableview.TableView
import com.evrencoskun.tableview.adapter.AbstractTableAdapter
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import com.makeitez.acsalesapp.R
import kotlinx.android.synthetic.main.item_general_table_view_corner.view.*


class ACTableViewAdapter(private val rowHeaderColumnHeader: String) : AbstractTableAdapter<String?, String?, String?>() {

    // Top Left Corner
    override fun onCreateCornerView(parent: ViewGroup): View {
        val topLeftCorner = LayoutInflater.from(parent.context).inflate(R.layout.item_general_table_view_corner, parent, false)
        return topLeftCorner.apply {
            tableTopLeftCornerText.text = rowHeaderColumnHeader
        }
    }

    // Cell Column Header
    override fun onCreateColumnHeaderViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder
        = ACColumnHeaderViewHolder.newInstance(parent)

    override fun onBindColumnHeaderViewHolder(holder: AbstractViewHolder, columnHeaderText: String?, position: Int)
        = (holder as ACColumnHeaderViewHolder).bind(columnHeaderText)

    override fun getColumnHeaderItemViewType(columnPosition: Int): Int = 0

    // Row Header
    override fun onCreateRowHeaderViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder
            = ACRowHeaderViewHolder.newInstance(parent)

    override fun onBindRowHeaderViewHolder(holder: AbstractViewHolder, rowHeaderText: String?, position: Int)
            = (holder as ACRowHeaderViewHolder).bind(rowHeaderText)

    override fun getRowHeaderItemViewType(rowPosition: Int): Int = 0

    // Cell
    override fun onCreateCellViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder
        = ACCellViewHolder.newInstance(parent)

    override fun onBindCellViewHolder(holder: AbstractViewHolder, cellText: String?, columnPosition: Int, rowPosition: Int)
        = (holder as ACCellViewHolder).bind(cellText)

    override fun getCellItemViewType(columnPosition: Int): Int = 0


    fun setACTableData(acTableData: ACTableData) {
        with(acTableData) {
            setAllItems(
                columnHeader,
                rowHeaderData,
                columnData
            )
        }
    }

    companion object {
        fun setAdapterForTable(tableView: TableView, acTableData: ACTableData) {
            val adapter = ACTableViewAdapter(acTableData.rowHeaderHeader)
            tableView.setAdapter(adapter)
            adapter.setACTableData(acTableData)
        }
    }
}