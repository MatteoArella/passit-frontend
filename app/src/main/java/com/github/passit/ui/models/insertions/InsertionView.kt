package com.github.passit.ui.models.insertions

import com.github.passit.domain.model.InsertionStatus
import java.util.*

data class InsertionView(
    var id: String,
    var title: String?,
    var description: String?,
    var location: LocationView?,
    var tutor: UserView?,
    var subject: String?,
    var status: InsertionStatus?,
    var createdAt: Date?,
    var updatedAt: Date?
)