package com.github.passit.data.datasource.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "users")
data class UserLocalData(
    @PrimaryKey @ColumnInfo(name = "user_id") var userId: String,
    var email: String?,
    @ColumnInfo(name = "family_name") var familyName: String?,
    @ColumnInfo(name = "given_name") var givenName: String?,
    @ColumnInfo(name = "phone_number") var phoneNumber: String?,
    @ColumnInfo(name = "birthdate") var birthDate: String?,
    var picture: String?
): Serializable