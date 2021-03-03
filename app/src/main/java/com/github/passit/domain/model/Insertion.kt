package com.github.passit.domain.model

import com.github.passit.domain.model.auth.User
import java.util.Date

data class Insertion(
    var id: String?,
    var title: String?,
    var description: String?,
    var subject: String?,
    var tutor: User? = null,
    var createdAt: Date?,
    var updatedAt: Date?
)