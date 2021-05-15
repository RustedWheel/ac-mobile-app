package com.makeitez.acsalesapp.screens.products.pricereference

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.makeitez.acsalesapp.models.Uom
import com.makeitez.acsalesapp.utils.extensions.clearAndAddAll

class UomMinPriceListAdapter() : RecyclerView.Adapter<UomMinPriceViewHolder>() {

    private var currency: String = ""
    private val uomList: MutableList<Uom> = arrayListOf()

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UomMinPriceViewHolder =
        UomMinPriceViewHolder.newInstance(parent)

    override fun onBindViewHolder(holder: UomMinPriceViewHolder, position: Int) = holder.bind(uomList[position], currency)

    override fun getItemCount() = uomList.size

    override fun getItemId(position: Int): Long = uomList[position].description.hashCode().toLong()

    fun setList(newList: List<Uom>, newCurrency: String) {
        uomList.clearAndAddAll(newList)

        currency = newCurrency

        notifyDataSetChanged()
    }
}