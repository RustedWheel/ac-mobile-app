package com.makeitez.acsalesapp.screens.salesorder.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.makeitez.acsalesapp.R
import kotlinx.android.synthetic.main.item_exceeded_limit.view.*

class ExceededLimitViewHolder(listItemView: View, listener: Listener) : RecyclerView.ViewHolder(listItemView) {

    interface Listener {
        fun onCreditLimitExceededPressed() {}
        fun onOverdueLimitExceededPressed() {}
    }

    private val exceededCreditLimitButton = itemView.cartExceededCreditLimitButton
    private val exceededOverdueLimitButton = itemView.cartExceededOverdueLimitButton

    init {
        exceededCreditLimitButton.setOnClickListener {
            listener.onCreditLimitExceededPressed()
        }
        exceededOverdueLimitButton.setOnClickListener {
            listener.onOverdueLimitExceededPressed()
        }
    }

    fun bind(isCreditLimitExceeded: Boolean, isOverdueLimitExceeded: Boolean) {
        exceededCreditLimitButton.isVisible = isCreditLimitExceeded
        exceededOverdueLimitButton.isVisible = isOverdueLimitExceeded
    }

    companion object {
        fun newInstance(parent: ViewGroup, listener: Listener) =
            ExceededLimitViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_exceeded_limit, parent, false),
                listener
            )
    }
}