package com.giwankim.client

import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

@Component
class Client(private val restClient: RestClient) {
    fun request(accessToken: String): Message {
        return restClient.get()
            .uri("http://localhost:8081")
            .headers { it.setBearerAuth(accessToken) }
            .retrieve()
            .body<Message>()!!
    }
}

data class Message(val message: String)
