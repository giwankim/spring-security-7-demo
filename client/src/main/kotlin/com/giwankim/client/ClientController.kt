package com.giwankim.client

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ClientController(private val client: Client) {
    @GetMapping("/client")
    fun request(@RegisteredOAuth2AuthorizedClient oauth2AuthorizedClient: OAuth2AuthorizedClient): Message {
        return client.request(oauth2AuthorizedClient.accessToken.tokenValue)
    }
}
