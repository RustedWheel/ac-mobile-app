package com.makeitez.acsalesapp.screens.salesorder.cart

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.makeitez.acsalesapp.models.ProductOrder
import com.makeitez.acsalesapp.screens.salesorder.ViewSalesOrderViewModel

class ProductOrderListAdapter(private val listener: ProductOrderListener,
                              private val isReadOnly: Boolean = false,
                              private val indicateQuotations: Boolean = false,
                              private val indicateViolations: Boolean = false
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private enum class ViewType {
        ExceededLimit,
        SubmissionRecord,
        ProductOrder
    }

    private var currency: String = ""
    private val adapterItems: MutableList<AdapterItem> = arrayListOf()

    init {
        setHasStableIds(true)
    }

    override fun getItemViewType(position: Int): Int = adapterItems[position].viewType

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when(viewType) {
        ViewType.ExceededLimit.ordinal -> ExceededLimitViewHolder.newInstance(parent, listener)
        ViewType.SubmissionRecord.ordinal -> SubmissionRecordViewHolder.newInstance(parent)
        else -> ProductOrderViewHolder.newInstance(parent, listener, isReadOnly, indicateQuotations, indicateViolations)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val adapterItem = adapterItems[position]
        when(holder) {
            is ExceededLimitViewHolder -> holder.bind(adapterItem.exceededCreditLimit, adapterItem.exceededOverdueLimit)
            is SubmissionRecordViewHolder -> adapterItem.submissionRecord?.let { holder.bind(it) }
            is ProductOrderViewHolder -> adapterItem.productOrder?.let { holder.bind(it, currency) }
        }
    }

    override fun getItemCount() = adapterItems.size

    override fun getItemId(position: Int): Long = when(adapterItems[position].viewType) {
        ViewType.ProductOrder.ordinal -> adapterItems[position].productOrder?.hashCode()?.toLong() ?: adapterItems[position].hashCode().toLong()
        else -> adapterItems[position].hashCode().toLong()
    }

    fun setList(productOrders: List<ProductOrder>, newCurrency: String?, submissionRecord: ViewSalesOrderViewModel.SubmissionRecord? = null, exceededCreditLimit: Boolean = false, exceededOverdueLimit: Boolean = false) {
        adapterItems.clear()

        if (indicateViolations && (exceededCreditLimit || exceededOverdueLimit)) {
            adapterItems.add(AdapterItem(ViewType.ExceededLimit.ordinal, null, null, exceededCreditLimit, exceededOverdueLimit))
        }

        if (submissionRecord != null) {
            adapterItems.add(AdapterItem(ViewType.SubmissionRecord.ordinal, null, submissionRecord))
        }

        adapterItems.addAll(productOrders.map {
            AdapterItem(ViewType.ProductOrder.ordinal, it, null)
        })

        currency = newCurrency ?: ""

        notifyDataSetChanged()
    }

    private inner class AdapterItem(
        val viewType: Int,
        val productOrder: ProductOrder?,
        val submissionRecord: ViewSalesOrderViewModel.SubmissionRecord?,
        val exceededCreditLimit: Boolean = false,
        val exceededOverdueLimit: Boolean = false
    )
}

interface ProductOrderListener: ProductOrderViewHolder.Listener, ExceededLimitViewHolder.Listener