package com.makeitez.acsalesapp.models

import com.squareup.moshi.Json
import io.realm.RealmList
import io.realm.RealmObject

open class ExceededOverdueLimitSummary: RealmObject() {
    @field:Json(name = "currentOverdue")
    var currentOverdue: Double = 0.0; private set

    @field:Json(name = "originalOverdueLimit")
    var originalOverdueLimit: Double = 0.0; private set

    @field:Json(name = "overdueLimitIncrement")
    var overdueLimitIncrement: Double = 0.0; private set

    @field:Json(name = "increaseOverdueLimit")
    var increasedOverdueLimit: Double = 0.0; private set

    @field:Json(name = "exceededOverdue")
    var exceededOverdue: Double = 0.0; private set

    @field:Json(name = "overdueDocList")
    var overdueDocList: RealmList<OverdueDoc> = RealmList(); private set
}