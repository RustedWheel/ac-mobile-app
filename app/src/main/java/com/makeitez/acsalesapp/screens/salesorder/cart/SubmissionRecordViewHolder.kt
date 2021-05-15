package com.makeitez.acsalesapp.screens.salesorder.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.screens.salesorder.ViewSalesOrderViewModel
import com.makeitez.acsalesapp.utils.helper.FormattingHelper
import kotlinx.android.synthetic.main.item_submission_record.view.*

class SubmissionRecordViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {

    private val submissionDocNoText = itemView.cartSubmissionDocNoText
    private val submissionTimeText = itemView.cartSubmissionTimeText

    fun bind(submissionRecord: ViewSalesOrderViewModel.SubmissionRecord) {
        submissionDocNoText.text = submissionRecord.submissionDocNo
        submissionTimeText.text = FormattingHelper.formatDate(submissionRecord.submissionDateTime, FormattingHelper.datetimeFormatLong)
    }

    companion object {
        fun newInstance(parent: ViewGroup) =
            SubmissionRecordViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_submission_record, parent, false)
            )
    }
}