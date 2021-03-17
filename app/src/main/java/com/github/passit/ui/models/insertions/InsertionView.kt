package com.github.passit.ui.models.insertions

import java.util.Date

data class InsertionView(
    var id: String?,
    var title: String?,
    var description: String?,
    var location: LocationView?,
    var tutor: UserView?,
    var subject: String?,
    var createdAt: Date?,
    var updatedAt: Date?
)