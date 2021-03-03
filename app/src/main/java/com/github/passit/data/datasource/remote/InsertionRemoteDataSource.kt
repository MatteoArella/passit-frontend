package com.github.passit.data.datasource.remote

import com.amplifyframework.api.aws.GsonVariablesSerializer
import com.amplifyframework.api.graphql.GraphQLRequest
import com.amplifyframework.api.graphql.SimpleGraphQLRequest
import com.amplifyframework.core.Amplify
import com.github.passit.data.datasource.remote.model.InsertionRemoteData
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class InsertionRemoteDataSource @Inject constructor() {
    suspend fun createInsertion(title: String, description: String, subject: String): InsertionRemoteData {
        return suspendCancellableCoroutine { continuation ->
            Amplify.API.mutate(
                createInsertionRequest(title, description, subject),
                { response -> continuation.resume(response.data) },
                { error -> continuation.resumeWithException(error) }
            )
        }
    }

    private fun createInsertionRequest(title: String, description: String, subject: String): GraphQLRequest<InsertionRemoteData> {
        val document = ("mutation CreateInsertion(\$title: String!, \$description: String!, \$subject: String!) { "
                + "createInsertion(insertion: {title: \$title, description: \$description, subject: \$subject}) { "
                    + "id "
                    + "title "
                    + "description "
                    + "subject "
                    + "tutor { "
                        + "id "
                        + "email "
                        + "familyName "
                        + "givenName "
                        + "phoneNumber "
                        + "birthDate "
                        + "picture "
                    + "} "
                    + "createdAt "
                    + "updatedAt "
                    + "} "
                + "}")
        return SimpleGraphQLRequest(
            document,
            mapOf("title" to title, "description" to description, "subject" to subject),
            InsertionRemoteData::class.java,
            GsonVariablesSerializer()
        )
    }
}