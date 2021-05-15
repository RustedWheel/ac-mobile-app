package com.makeitez.acsalesapp.screens.products.productdetails

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.makeitez.acsalesapp.models.ProductHistory
import com.makeitez.acsalesapp.utils.extensions.clearAndAddAll

class ProductHistoryAdapter(private val listener: ProductHistoryViewHolder.Listener) : RecyclerView.Adapter<ProductHistoryViewHolder>() {

    init {
        setHasStableIds(true)
    }

    private val productHistoryList: MutableList<ProductHistory> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHistoryViewHolder = ProductHistoryViewHolder.newInstance(parent, listener)

    override fun onBindViewHolder(holder: ProductHistoryViewHolder, position: Int){
        holder.bind(productHistoryList[position])
    }

    override fun getItemCount() = productHistoryList.size

    override fun getItemId(position: Int): Long = productHistoryList[position].uuid.hashCode().toLong()

    fun setList(newList: List<ProductHistory>) {
        productHistoryList.clearAndAddAll(newList)
        notifyDataSetChanged()
    }

}