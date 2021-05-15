package com.makeitez.acsalesapp.utils.moshiadapter

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import io.realm.RealmList
import io.realm.RealmObject


/**
 * Derived from this example:
 * https://github.com/square/moshi/blob/master/moshi/src/main/java/com/squareup/moshi/CollectionJsonAdapter.java
 */
class RealmListAdapter<T : RealmObject?>(private val elementAdapter: JsonAdapter<T>) : JsonAdapter<RealmList<T>?>() {

    override fun toJson(writer: JsonWriter, value: RealmList<T>?) {
        value?.let {
            writer.beginArray()
            for (element in it) {
                elementAdapter.toJson(writer, element)
            }
            writer.endArray()
        }
    }

    override fun fromJson(reader: JsonReader): RealmList<T> {
        val result = RealmList<T>()
        reader.beginArray()
        while (reader.hasNext()) {
            result.add(elementAdapter.fromJson(reader))
        }
        reader.endArray()
        return result
    }

}