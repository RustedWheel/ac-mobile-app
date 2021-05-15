package com.makeitez.acsalesapp.utils

import androidx.lifecycle.LiveData
import io.realm.RealmChangeListener
import io.realm.RealmObject

class RealmObjectLiveData<T : RealmObject>(val realmObject: T) : LiveData<T>(realmObject) {

    private val listener = RealmChangeListener<T> { result ->
        if (result.isValid) {
            value = result
        }
    }

    override fun onActive() {
        realmObject.addChangeListener(listener)
    }

    override fun onInactive() {
        realmObject.removeChangeListener(listener)
    }
}