package com.makeitez.acsalesapp.utils.moshiadapter

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.realm.RealmList
import io.realm.RealmObject
import java.lang.reflect.Type


class RealmListJsonAdapterFactory : JsonAdapter.Factory {

    override fun create(type: Type, annotations: Set<Annotation?>, moshi: Moshi): JsonAdapter<*>? {
        val rawType: Class<*> = Types.getRawType(type)
        if (!annotations.isEmpty()) return null
        return if (rawType == RealmList::class.java) { newRealmListAdapter<RealmObject>(type, moshi).nullSafe() } else null
    }

    companion object {
        private fun <T : RealmObject?> newRealmListAdapter(type: Type, moshi: Moshi): RealmListAdapter<T> {
            val elementType: Type = Types.collectionElementType(type, RealmList::class.java)
            val elementAdapter: JsonAdapter<T> = moshi.adapter(elementType)
            return RealmListAdapter(elementAdapter)
        }
    }
}