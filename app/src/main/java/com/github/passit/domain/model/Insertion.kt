package com.github.passit.domain.model

import com.github.passit.domain.model.auth.User
import java.util.*

data class Insertion(
    var id: String,
    var title: String?,
    var description: String?,
    var subject: String?,
    var tutor: User? = null,
    var location: Location?,
    var createdAt: Date?,
    var updatedAt: Date?
)