package com.makeitez.acsalesapp.screens.salesorder.cart

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.makeitez.acsalesapp.components.tableview.ACTableData
import com.makeitez.acsalesapp.core.ACViewModel
import com.makeitez.acsalesapp.models.ExceededOverdueLimitSummary
import com.makeitez.acsalesapp.models.OverdueDoc
import com.makeitez.acsalesapp.utils.Config
import com.makeitez.acsalesapp.utils.ListContentState
import com.makeitez.acsalesapp.utils.extensions.toNdpString
import com.makeitez.acsalesapp.utils.helper.FormattingHelper

class OverdueDocDetailsViewModel(application: Application) : ACViewModel(application) {
    private var hasInit: Boolean = false

    val overdueDocTableData = MutableLiveData<ACTableData>()
    val contentState = MutableLiveData<ListContentState>()

    fun init(exceededOverdueLimitSummary: ExceededOverdueLimitSummary) {
        if (!hasInit) {
            exceededOverdueLimitSummary.overdueDocList.let { overdueDocList ->
                overdueDocTableData.value = mapOverdueDocListToTableData(overdueDocList)
                contentState.value = if (!overdueDocList.isNullOrEmpty()) {
                    ListContentState.NotEmpty
                } else {
                    ListContentState.EmptyData
                }
            }
            hasInit = true
        }
    }

    private fun mapOverdueDocListToTableData(overdueDocList: List<OverdueDoc>): ACTableData {
        return ACTableData(
            rowHeaderHeader = "Doc No",
            columnHeader = listOf(
                "Doc Date",
                "Credit Term",
                "Due Date",
                "Curr. Code",
                "Curr. Rate",
                "Outstanding"
            ),
            rowHeaderData = overdueDocList.map { it.docNo },
            columnData = overdueDocList.map {
                listOf(
                    FormattingHelper.formatDate(it.docDate),
                    it.creditTerm,
                    FormattingHelper.formatDate(it.dueDate),
                    it.currencyCode,
                    it.currencyRate.toNdpString(Config.EXCHANGE_RATE_DPS),
                    it.outstanding.toNdpString(Config.NORMAL_PRICE_DPS)
                    )
            }

        )
    }

}