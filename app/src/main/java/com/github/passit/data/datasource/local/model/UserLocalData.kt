package com.github.passit.data.datasource.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "users")
data class UserLocalData(
    @PrimaryKey @ColumnInfo(name = "user_id") var userId: String,
    var email: String? = null,
    @ColumnInfo(name = "family_name") var familyName: String? = null,
    @ColumnInfo(name = "given_name") var givenName: String? = null,
    @ColumnInfo(name = "phone_number") var phoneNumber: String? = null,
    @ColumnInfo(name = "birthdate") var birthDate: String? = null,
    var picture: String? = null
): Serializable