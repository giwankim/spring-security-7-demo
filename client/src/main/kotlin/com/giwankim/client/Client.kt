package com.giwankim.client

import org.springframework.security.oauth2.client.annotation.ClientRegistrationId
import org.springframework.web.service.annotation.GetExchange

@ClientRegistrationId("spring")
fun interface Client {
    @GetExchange("http://localhost:8081")
    fun request(): Message
}

data class Message(val message: String)
