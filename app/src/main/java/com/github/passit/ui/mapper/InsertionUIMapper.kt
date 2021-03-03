package com.github.passit.ui.mapper

import com.github.passit.core.domain.Mapper
import com.github.passit.domain.model.Insertion
import com.github.passit.ui.models.insertions.InsertionView

object InsertionEntityToUIMapper: Mapper<Insertion, InsertionView>() {
    override fun map(from: Insertion): InsertionView {
        return InsertionView(
                from.id,
                from.title,
                from.description,
                from.subject,
                from.createdAt,
                from.updatedAt
        )
    }
}
