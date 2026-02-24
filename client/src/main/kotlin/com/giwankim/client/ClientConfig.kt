package com.giwankim.client

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

@Configuration
class ClientConfig {
    @Bean
    fun restClient(restClientBuilder: RestClient.Builder): RestClient {
        return restClientBuilder.build()
    }
}
