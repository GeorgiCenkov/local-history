package com.example.localhistory.model.response

import kotlinx.datetime.LocalDate

// This file defines the User sealed class, which represents a user in the system. It has two subclasses: Student and Teacher.
// The Gson deserializer decides which one to deserialize into based on the role field
sealed class User {
    abstract val id: Long
    abstract val firstName: String
    abstract val lastName: String
    abstract val email: String
    abstract val birthDate: LocalDate
    abstract val role: Role

    data class Student(
        override val id: Long,
        override val firstName: String,
        override val lastName: String,
        override val email: String,
        override val birthDate: LocalDate,
        override val role: Role,
        val level: Int,
        val points: Int,
        val pointsRequired: Int
    ) : User()

    data class Teacher(
        override val id: Long,
        override val firstName: String,
        override val lastName: String,
        override val email: String,
        override val birthDate: LocalDate,
        override val role: Role
    ) : User()
}
