package com.makeitez.acsalesapp.models

import com.squareup.moshi.Json
import io.realm.RealmObject

open class CustomerBranch : RealmObject() {

    @field:Json(name = "branchCode")
    var branchCode: String = "" ; private set

    @field:Json(name = "branchName")
    var branchName: String = ""; private set

    @field:Json(name = "salesAgent")
    var salesAgent: String? = null; private set

    companion object {
        fun createUntracked(fromBranch: CustomerBranch, newName: String? = null): CustomerBranch {
            return CustomerBranch().apply {
                branchCode = fromBranch.branchCode
                branchName = newName ?: fromBranch.branchName
                salesAgent = fromBranch.salesAgent
            }
        }
    }
}