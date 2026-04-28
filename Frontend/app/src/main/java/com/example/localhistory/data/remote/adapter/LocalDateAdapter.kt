package com.example.localhistory.data.remote.adapter

import com.google.gson.*
import kotlinx.datetime.LocalDate
import java.lang.reflect.Type

// kotlin can not parse the backend's LocalDate format automatically
class LocalDateAdapter : JsonDeserializer<LocalDate>, JsonSerializer<LocalDate> {

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): LocalDate {
        return LocalDate.parse(json.asString)
    }

    override fun serialize(
        src: LocalDate,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return JsonPrimitive(src.toString())
    }
}