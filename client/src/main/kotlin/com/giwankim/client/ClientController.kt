package com.giwankim.client

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ClientController(private val client: Client) {
    @GetMapping("/client")
    fun request(): Message {
        return client.request()
    }
}
