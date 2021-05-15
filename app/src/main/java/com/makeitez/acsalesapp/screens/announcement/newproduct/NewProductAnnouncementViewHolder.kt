package com.makeitez.acsalesapp.screens.announcement.newproduct

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.models.NewProductAnnouncement
import com.makeitez.acsalesapp.utils.extensions.setNonEmptyText
import com.makeitez.acsalesapp.utils.helper.FormattingHelper
import kotlinx.android.synthetic.main.item_new_product_announcement_list.view.*

class NewProductAnnouncementViewHolder(listItemView: View, isEditing: Boolean, listener: Listener) : RecyclerView.ViewHolder(listItemView) {

    interface Listener {
        fun onNewProductAnnouncementDelete(announcementId: String, description: String)
    }

    private val newProductImage = itemView.newProductImage
    private val newProductNameText = itemView.newProductNameText
    private val newProductCodeText = itemView.newProductCodeText
    private val newProductBarcodeText = itemView.newProductBarcodeText

    private val newProductAnnouncementDateText = itemView.newProductAnnouncementDateText
    private val newProductAnnouncementDeleteButton = itemView.newProductAnnouncementDeleteButton

    private val newProductDefaultUomText = itemView.newProductDefaultUomText
    private val newProductCartonSizeText = itemView.newProductCartonSizeText
    private val newProductM3Text = itemView.newProductM3Text
    private val newProductNetWeightText = itemView.newProductNetWeightText
    private val newProductPackingUnitText = itemView.newProductPackingUnitText

    private val newProductRemarksText = itemView.newProductRemarksText

    private var announcement: NewProductAnnouncement? = null

    init {
        newProductAnnouncementDeleteButton.isVisible = isEditing
        newProductAnnouncementDeleteButton.setOnClickListener {
            announcement?.let { announcement ->
                listener.onNewProductAnnouncementDelete(
                    announcement.id, announcement.productDetails?.description ?: ""
                )
            }
        }
    }

    fun bind(announcement: NewProductAnnouncement) {
        this.announcement = announcement
        newProductAnnouncementDateText.text = FormattingHelper.formatDate(announcement.date)
        newProductRemarksText.setNonEmptyText(announcement.remarks)

        announcement.productDetails?.let {
            Glide.with(itemView.context).load(it.imageUrl).placeholder(R.drawable.app_ac_logo).into(newProductImage)
            newProductNameText.text = it.description
            newProductCodeText.text = it.itemCode

            it.getDefaultUom().let { defaultUom ->
                newProductBarcodeText.setNonEmptyText(defaultUom.barcode)
                newProductDefaultUomText.text = defaultUom.description
                newProductCartonSizeText.setNonEmptyText(defaultUom.cartonSize)
                newProductM3Text.setNonEmptyText(defaultUom.m3)
                newProductNetWeightText.text = if(defaultUom.weight != null) {
                    "${defaultUom.weight}${defaultUom.weightUom}"
                } else {
                    itemView.context.getString(R.string.generic_dash)
                }
                newProductPackingUnitText.setNonEmptyText(defaultUom.packagingUnit)
            }
        }
    }

    companion object {
        fun newInstance(parent: ViewGroup, isEditing: Boolean, listener: Listener) =
            NewProductAnnouncementViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_new_product_announcement_list, parent, false),
                isEditing,
                listener
            )
    }
}