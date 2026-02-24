package com.giwankim.client

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.service.registry.ImportHttpServices

@SpringBootApplication
@ImportHttpServices(Client::class)
class ClientApplication

fun main(args: Array<String>) {
    runApplication<ClientApplication>(*args)
}
