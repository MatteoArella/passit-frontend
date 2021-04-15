package com.github.passit.data.datasource.remote

import com.amplifyframework.api.aws.GsonVariablesSerializer
import com.amplifyframework.api.graphql.GraphQLRequest
import com.amplifyframework.api.graphql.SimpleGraphQLRequest
import com.amplifyframework.core.Amplify
import com.github.passit.data.datasource.remote.model.conversations.ConvLinkPageRemoteData
import com.github.passit.data.datasource.remote.model.conversations.ConvLinkRemoteData
import com.github.passit.data.datasource.remote.model.conversations.MessagePageRemoteData
import com.github.passit.data.datasource.remote.model.conversations.MessageRemoteData
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ConversationRemoteDataSource @Inject constructor() {
    suspend fun createConversation(tutorId: String): ConvLinkRemoteData {
        return suspendCancellableCoroutine { continuation ->
            Amplify.API.mutate(
                createConversationRequest(tutorId),
                { response -> continuation.resume(response.data) },
                { error -> continuation.resumeWithException(error) }
            )
        }
    }

    suspend fun getConversations(first: Int?, nextToken: String?): ConvLinkPageRemoteData {
        return suspendCancellableCoroutine { continuation ->
            Amplify.API.query(
                    getConversationsRequest(first, nextToken),
                    { response -> continuation.resume(response.data) },
                    { error -> continuation.resumeWithException(error) }
            )
        }
    }

    suspend fun createMessage(to: String, conversationId: String, content: String): MessageRemoteData {
        return suspendCancellableCoroutine { continuation ->
            Amplify.API.mutate(
                    createMessageRequest(to, conversationId, content),
                    { response -> continuation.resume(response.data) },
                    { error -> continuation.resumeWithException(error) }
            )
        }
    }

    suspend fun getMessages(conversationId: String, first: Int?, nextToken: String?): MessagePageRemoteData {
        return suspendCancellableCoroutine { continuation ->
            Amplify.API.query(
                    getMessagesRequest(conversationId, first, nextToken),
                    { response -> continuation.resume(response.data) },
                    { error -> continuation.resumeWithException(error) }
            )
        }
    }

    fun subscribeConversations(userId: String): Flow<ConvLinkRemoteData> = callbackFlow {
        Amplify.API.subscribe(
                subscribeConversationsRequest(userId),
                {},
                { data -> offer(data.data) },
                { error -> close(error) },
                { close() }
        )
        awaitClose { cancel() }
    }

    fun subscribeMessages(userId: String): Flow<MessageRemoteData> = callbackFlow {
        val subscription = Amplify.API.subscribe(
                subscribeMessagesRequest(userId),
                {},
                { data -> sendBlocking(data.data) },
                { error -> close(error) },
                { close() }
        )
        awaitClose { subscription?.cancel() }
    }

    private fun getConversationsRequest(first: Int?, nextToken: String? = ""): GraphQLRequest<ConvLinkPageRemoteData> {
        val document = (
                "query GetConversations(\$first: Int, \$nextToken: String) { "
                    + "getConversations(first: \$first, after: \$nextToken) { "
                        + "items { "
                            + "id "
                            + "conversation { "
                                + "id "
                                + "associated { "
                                    + "id "
                                    + "user { "
                                        + "id "
                                        + "familyName "
                                        + "givenName "
                                        + "picture "
                                    + "} "
                                + "} "
                                + "createdAt "
                                + "updatedAt "
                            + "} "
                            + "user { "
                                + "id "
                            + "} "
                        + "} "
                        + "pageInfo { "
                            + "hasNextPage "
                            + "nextToken "
                        + "} "
                    + "} "
                + "}")

        return SimpleGraphQLRequest(
                document,
                mapOf("first" to first, "after" to nextToken),
                ConvLinkPageRemoteData::class.java,
                GsonVariablesSerializer()
        )
    }

    private fun createConversationRequest(tutorId: String): GraphQLRequest<ConvLinkRemoteData> {
        val document = (
                "mutation CreateConversation(\$tutorId: ID!) { "
                    + "createConversation(input: {tutorId: \$tutorId}) { "
                        + "id "
                        + "userId " // really important to trigger the subscription
                        + "conversation { "
                            + "id "
                            + "associated { "
                                + "id "
                                + "user { "
                                    + "id "
                                    + "familyName "
                                    + "givenName "
                                    + "picture "
                                + "} "
                            + "} "
                            + "createdAt "
                            + "updatedAt "
                        + "} "
                        + "user { "
                            + "id "
                        + "} "
                    + "} "
                + "}")
        return SimpleGraphQLRequest(
            document,
            mapOf("tutorId" to tutorId),
            ConvLinkRemoteData::class.java,
            GsonVariablesSerializer()
        )
    }

    private fun subscribeConversationsRequest(userId: String): GraphQLRequest<ConvLinkRemoteData> {
        val document = (
                "subscription SubscribeConversation(\$userId: ID!) { "
                    + "onCreateConversation(userId: \$userId) { "
                        + "id "
                        + "conversation { "
                            + "id "
                            + "associated { "
                                + "id "
                                + "user { "
                                    + "id "
                                    + "familyName "
                                    + "givenName "
                                    + "picture "
                                + "} "
                            + "} "
                            + "createdAt "
                            + "updatedAt "
                        + "} "
                        + "user { "
                            + "id "
                        + "} "
                    + "} "
                + "}")
        return SimpleGraphQLRequest(
                document,
                mapOf("userId" to userId),
                ConvLinkRemoteData::class.java,
                GsonVariablesSerializer()
        )
    }

    private fun createMessageRequest(to: String, conversationId: String, content: String): GraphQLRequest<MessageRemoteData> {
        val document = (
                "mutation CreateMessage(\$to: ID!, \$conversationId: ID!, \$content: String!) { "
                    + "createMessage(input: {to: \$to, conversationId: \$conversationId, content: \$content}) { "
                        + "id "
                        + "content "
                        + "conversationId "
                        + "to " // really important to trigger the subscription
                        + "author { "
                            + "id "
                            + "familyName "
                            + "givenName "
                            + "picture "
                        + "} "
                        + "createdAt "
                    + "}"
                + "}")
        return SimpleGraphQLRequest(
                document,
                mapOf("to" to to, "conversationId" to conversationId, "content" to content),
                MessageRemoteData::class.java,
                GsonVariablesSerializer()
        )
    }

    private fun subscribeMessagesRequest(userId: String): GraphQLRequest<MessageRemoteData> {
        val document = (
                "subscription SubscribeMessage(\$to: ID!) { "
                    + "onCreateMessageFilterByDest(to: \$to) { "
                        + "id "
                        + "content "
                        + "conversationId "
                        + "author { "
                            + "id "
                            + "familyName "
                            + "givenName "
                            + "picture "
                        + "} "
                        + "createdAt "
                    + "} "
                + "}")
        return SimpleGraphQLRequest(
                document,
                mapOf("to" to userId),
                MessageRemoteData::class.java,
                GsonVariablesSerializer()
        )
    }

    private fun getMessagesRequest(conversationId: String, first: Int?, nextToken: String?): GraphQLRequest<MessagePageRemoteData> {
        val document = (
                "query GetMessages(\$conversationId: ID!, \$first: Int, \$nextToken: String) { "
                    + "getMessages(filter: {conversationId: \$conversationId}, first: \$first, after: \$nextToken) { "
                        + "items { "
                            + "id "
                            + "content "
                            + "author { "
                                + "id "
                            + "} "
                            + "conversation { "
                                + "id "
                            + "} "
                            + "createdAt "
                        + "} "
                        + "pageInfo { "
                            + "hasNextPage "
                            + "nextToken "
                        + "} "
                    + "} "
                + "}")

        return SimpleGraphQLRequest(
                document,
                mapOf("conversationId" to conversationId, "first" to first, "after" to nextToken),
                MessagePageRemoteData::class.java,
                GsonVariablesSerializer()
        )
    }
}