package com.makeitez.acsalesapp.screens.products.productlist

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.makeitez.acsalesapp.models.Product
import com.makeitez.acsalesapp.utils.extensions.clearAndAddAll


class ProductListAdapter(private val productListListener: ProductViewHolder.Listener) : RecyclerView.Adapter<ProductViewHolder>() {

    private val productList: MutableList<Product> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder = ProductViewHolder.newInstance(parent, productListListener)

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) =
        holder.bind(productList[position])

    override fun getItemCount() = productList.size

    fun setList(newList: List<Product>) {
        productList.clearAndAddAll(newList)
        notifyDataSetChanged()
    }
}