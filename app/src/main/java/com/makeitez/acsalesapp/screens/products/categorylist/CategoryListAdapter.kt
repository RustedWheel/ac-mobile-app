package com.makeitez.acsalesapp.screens.products.categorylist

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.makeitez.acsalesapp.models.ProductCategory
import com.makeitez.acsalesapp.utils.extensions.clearAndAddAll

class CategoryListAdapter(private val categoryListListener: CategoryViewHolder.Listener) : RecyclerView.Adapter<CategoryViewHolder>() {

    private val categoryList: MutableList<ProductCategory> = arrayListOf()

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder =
        CategoryViewHolder.newInstance(parent, categoryListListener)

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) = holder.bind(categoryList[position])

    override fun getItemCount() = categoryList.size

    override fun getItemId(position: Int): Long = categoryList[position].type.hashCode().toLong()

    fun setList(newList: List<ProductCategory>) {
        categoryList.clearAndAddAll(newList)
        notifyDataSetChanged()
    }
}