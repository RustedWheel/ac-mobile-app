package com.makeitez.acsalesapp.utils

import androidx.lifecycle.LiveData
import io.realm.RealmChangeListener
import io.realm.RealmModel
import io.realm.RealmResults

class RealmResultsLiveData <T : RealmModel>(val realmResults: RealmResults<T>) : LiveData<RealmResults<T>>(realmResults) {

    private val listener = RealmChangeListener<RealmResults<T>> { results ->
        if (results.isValid) {
            value = results
        }
    }

    override fun onActive() {
        realmResults.addChangeListener(listener)
    }

    override fun onInactive() {
        realmResults.removeChangeListener(listener)
    }
}