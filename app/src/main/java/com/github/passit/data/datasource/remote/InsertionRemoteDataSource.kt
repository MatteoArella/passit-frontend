package com.github.passit.data.datasource.remote

import com.amplifyframework.api.aws.GsonVariablesSerializer
import com.amplifyframework.api.graphql.GraphQLRequest
import com.amplifyframework.api.graphql.SimpleGraphQLRequest
import com.amplifyframework.core.Amplify
import com.github.passit.data.datasource.remote.model.InsertionPageRemoteData
import com.github.passit.data.datasource.remote.model.InsertionRemoteData
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class InsertionRemoteDataSource @Inject constructor() {
    suspend fun createInsertion(title: String, description: String, subject: String, city: String, state: String, country: String): InsertionRemoteData {
        return suspendCancellableCoroutine { continuation ->
            Amplify.API.mutate(
                createInsertionRequest(title, description, subject, city, state, country),
                { response -> continuation.resume(response.data) },
                { error -> continuation.resumeWithException(error) }
            )
        }
    }

    suspend fun getInsertions(subject: String, city: String, state: String, country: String, limit: Int?, pageNum: Int?): InsertionPageRemoteData {
        return suspendCancellableCoroutine { continuation ->
            Amplify.API.query(
                getInsertionsRequest(subject, city, state, country, limit, pageNum),
                { response -> continuation.resume(response.data) },
                { error -> continuation.resumeWithException(error) }
            )
        }
    }

    private fun getInsertionsRequest(subject: String, city: String, state: String, country: String, limit: Int?, pageNum: Int?): GraphQLRequest<InsertionPageRemoteData> {
        val document = (
                "query GetInsertions(\$subject: String!, \$city: String, \$state: String, \$country: String, \$first: Int, \$after: Int) { "
                    + "getInsertions(filter: {subject: \$subject, location: {city: \$city, state: \$state, country: \$country}}, first: \$first, after: \$after) { "
                        + "items { "
                            + "id "
                            + "title "
                            + "subject "
                            + "tutor { "
                                + "id "
                                + "familyName "
                                + "givenName "
                                + "picture "
                            + "} "
                            + "location { "
                                + "city "
                                + "country "
                                + "state "
                            + "} "
                            + "createdAt "
                        + "} "
                        + "pageInfo { "
                            + "hasNextPage "
                            + "total "
                        + "} "
                    + "} "
                + "}")

        return SimpleGraphQLRequest(
            document,
            mapOf("subject" to subject, "city" to city, "state" to state, "country" to country, "first" to limit, "after" to pageNum),
            InsertionPageRemoteData::class.java,
            GsonVariablesSerializer()
        )
    }

    private fun createInsertionRequest(title: String, description: String, subject: String, city: String, state: String, country: String): GraphQLRequest<InsertionRemoteData> {
        val document = (
                "mutation CreateInsertion(\$title: String!, \$description: String!, \$subject: String!, \$city: String!, \$state: String!, \$country: String!) { "
                    + "createInsertion(insertion: {title: \$title, description: \$description, subject: \$subject, location: {city: \$city, state: \$state, country: \$country}}) { "
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
                        + "location { "
                            + "city "
                            + "state "
                            + "country "
                        + "} "
                        + "createdAt "
                        + "updatedAt "
                    + "} "
                + "}")
        return SimpleGraphQLRequest(
            document,
            mapOf("title" to title, "description" to description, "subject" to subject,
                "city" to city, "state" to state, "country" to country),
            InsertionRemoteData::class.java,
            GsonVariablesSerializer()
        )
    }
}