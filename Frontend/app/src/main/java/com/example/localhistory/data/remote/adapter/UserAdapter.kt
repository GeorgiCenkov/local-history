package com.example.localhistory.data.remote.adapter

import com.example.localhistory.model.response.Role
import com.example.localhistory.model.response.User
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import kotlinx.datetime.LocalDate
import java.lang.reflect.Type

// Describe which user type to deserialize based on the "role" field in the JSON. This adapter handles both Student and Teacher user types.
class UserAdapter : JsonDeserializer<User>, JsonSerializer<User> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): User {
        val roleValue = json.asJsonObject["role"]?.asString
            ?: throw JsonParseException("User role is missing")

        val user = json.asJsonObject

        return when (Role.valueOf(roleValue)) {
            Role.STUDENT -> User.Student(
                id = user["id"].asLong,
                firstName = user["firstName"].asString,
                lastName = user["lastName"].asString,
                email = user["email"].asString,
                birthDate = context.deserialize(user["birthDate"], LocalDate::class.java),
                role = Role.STUDENT,
                level = user["level"]?.takeUnless { it.isJsonNull }?.asInt ?: 1,
                points = user["points"]?.takeUnless { it.isJsonNull }?.asInt ?: 0,
                pointsRequired = user["pointsRequired"]?.takeUnless { it.isJsonNull }?.asInt ?: 100
            )

            Role.TEACHER -> context.deserialize(json, User.Teacher::class.java)
        }
    }

    override fun serialize(
        src: User,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return when (src) {
            is User.Student -> context.serialize(src, User.Student::class.java)
            is User.Teacher -> context.serialize(src, User.Teacher::class.java)
        }
    }
}
