package com.makeitez.acsalesapp.screens.products.categorylist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.models.ProductCategory
import kotlinx.android.synthetic.main.item_category_list.view.*

class CategoryViewHolder(listItemView: View, categoryListListener: Listener) :
    RecyclerView.ViewHolder(listItemView) {

    interface Listener {
        fun onCategoryClick(category: ProductCategory)
    }

    private val categoryCard = itemView.categoryCard
    private val categoryLabelText = itemView.categoryLabelText

    private var category: ProductCategory? = null

    init {
        categoryCard.setOnClickListener {
            category?.let(categoryListListener::onCategoryClick)
        }
    }

    fun bind(boundCategory: ProductCategory) {
        category = boundCategory
        categoryLabelText.text = boundCategory.description
    }

    companion object {
        fun newInstance(parent: ViewGroup, listener: Listener) =
            CategoryViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_category_list, parent, false),
                listener
            )
    }
}