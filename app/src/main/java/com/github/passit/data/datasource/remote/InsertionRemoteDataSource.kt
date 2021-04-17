package com.github.passit.data.datasource.remote

import android.util.Log
import com.amplifyframework.api.aws.GsonVariablesSerializer
import com.amplifyframework.api.graphql.GraphQLRequest
import com.amplifyframework.api.graphql.SimpleGraphQLRequest
import com.amplifyframework.core.Amplify
import com.github.passit.data.datasource.remote.model.InsertionPageRemoteData
import com.github.passit.data.datasource.remote.model.InsertionRemoteData
import com.github.passit.data.datasource.remote.model.LocationRemoteData
import com.github.passit.domain.model.InsertionStatus
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class InsertionRemoteDataSource @Inject constructor() {
    suspend fun createInsertion(title: String, description: String, subject: String, city: String, state: String, country: String): InsertionRemoteData {
        return suspendCancellableCoroutine { continuation ->
            val operation = Amplify.API.mutate(
                createInsertionRequest(title, description, subject, city, state, country),
                { response -> continuation.resume(response.data) },
                { error -> continuation.resumeWithException(error) }
            )
            continuation.invokeOnCancellation { operation?.cancel() }
        }
    }

    suspend fun getInsertions(subject: String, city: String, state: String, country: String, limit: Int?, pageNum: Int?): InsertionPageRemoteData {
        return suspendCancellableCoroutine { continuation ->
            val operation = Amplify.API.query(
                getInsertionsRequest(subject, city, state, country, limit, pageNum),
                { response -> continuation.resume(response.data) },
                { error -> continuation.resumeWithException(error) }
            )
            continuation.invokeOnCancellation { operation?.cancel() }
        }
    }

    suspend fun getUserInsertions(userID: String): InsertionPageRemoteData {
        return suspendCancellableCoroutine { continuation ->
            val operation = Amplify.API.query(
                    getUserInsertionsRequest(userID),
                    { response -> continuation.resume(response.data) },
                    { error -> continuation.resumeWithException(error) }
            )
            continuation.invokeOnCancellation { operation?.cancel() }
        }
    }

    suspend fun getInsertion(insertionId: String): InsertionRemoteData {
        return suspendCancellableCoroutine { continuation ->
            val operation = Amplify.API.query(
                getInsertionRequest(insertionId),
                { response -> continuation.resume(response.data) },
                { error -> continuation.resumeWithException(error) }
            )
            continuation.invokeOnCancellation { operation?.cancel() }
        }
    }

    suspend fun updateInsertion(insertionId: String, status: InsertionStatus?, title: String?, description: String?, subject: String?,
                               location: LocationRemoteData?): InsertionRemoteData {
        return suspendCancellableCoroutine { continuation ->
            val operation = Amplify.API.mutate(
                    updateInsertionRequest(insertionId, status, title, description, subject, location),
                    { response -> continuation.resume(response.data) },
                    { error -> continuation.resumeWithException(error) }
            )
            continuation.invokeOnCancellation { operation?.cancel() }
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
                            + "updatedAt "
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

    private fun getInsertionRequest(insertionId: String): GraphQLRequest<InsertionRemoteData> {
        val document = (
            "query GetInsertion(\$insertionId: ID!) { "
                + "getInsertion(insertion: \$insertionId) { "
                    + "id "
                    + "createdAt "
                    + "description "
                    + "subject "
                    + "status "
                    + "title "
                    + "location { "
                        + "city "
                        + "country "
                        + "state "
                    + "} "
                    + "updatedAt "
                    + "tutor { "
                        + "familyName "
                        + "givenName "
                        + "picture "
                        + "id "
                    + "} "
                + "} "
            + "} "
        )

        return SimpleGraphQLRequest(
            document,
            mapOf("insertionId" to insertionId),
            InsertionRemoteData::class.java,
            GsonVariablesSerializer()
        )
    }

    private fun getUserInsertionsRequest(userID: String): GraphQLRequest<InsertionPageRemoteData> {
        val document = (
                "query GetUserInsertions(\$userID: ID!) { "
                    + "getUserInsertions(userID: \$userID) { "
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
                            + "updatedAt "
                        + "} "
                        + "pageInfo { "
                            + "hasNextPage "
                            + "total "
                        + "} "
                    + "} "
                + "}")

        return SimpleGraphQLRequest(
                document,
                mapOf("userID" to userID),
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

    private fun updateInsertionRequest(insertionId: String, status: InsertionStatus?, title: String?, description: String?, subject: String?,
                                       location: LocationRemoteData?): GraphQLRequest<InsertionRemoteData> {
        val document = (
                "mutation UpdateInsertion(\$insertionId: ID!, \$status: InsertionStatus, \$title: String, \$description: String, \$subject: String, \$location: LocationInput) { "
                    + "updateInsertion(insertion: {id: \$insertionId, status: \$status, title: \$title, description: \$description, subject: \$subject, location: \$location}) { "
                        + "id "
                        + "title "
                        + "description "
                        + "subject "
                        + "status "
                        + "tutor { "
                            + "id "
                            + "familyName "
                            + "givenName "
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
                mapOf("insertionId" to insertionId, "status" to status?.status, "title" to title, "description" to description, "subject" to subject,
                        "location" to location),
                InsertionRemoteData::class.java,
                GsonVariablesSerializer()
        )
    }
}