package com.makeitez.acsalesapp.screens.products.productlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.models.Product
import kotlinx.android.synthetic.main.item_product_list.view.*

class ProductViewHolder(itemView: View, productListListener: Listener) : RecyclerView.ViewHolder(itemView) {

    interface Listener {
        fun onProductClick(product: Product)
    }

    private val productCard = itemView.productCard
    private val productImage = itemView.productImage
    private val productNameText = itemView.productNameText
    private val productCodeText = itemView.productCodeText
    private val productQuotationIndicatorImage = itemView.productQuotationIndicatorImage

    private var product: Product? = null

    init {
        productCard.setOnClickListener {
            product?.let {
                productListListener.onProductClick(it)
            }
        }
    }

    fun bind(boundProduct: Product) {
        product = boundProduct
        Glide.with(itemView.context).load(boundProduct.imageUrl).placeholder(R.drawable.app_ac_logo).into(productImage)
        productNameText.text = boundProduct.description
        productCodeText.text = boundProduct.itemCode
        productQuotationIndicatorImage.isVisible = boundProduct.hasQuotationOnUom
    }

    companion object {

        fun newInstance(parent: ViewGroup, listener: Listener) =
            ProductViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_product_list, parent, false),
                listener
            )
    }
}