package com.giwankim.client

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.web.client.support.OAuth2RestClientHttpServiceGroupConfigurer

@Configuration
class ClientConfig {
    @Bean
    fun oAuth2RestClientHttpServiceGroupConfigurer(
        authorizedClientManager: OAuth2AuthorizedClientManager,
    ): OAuth2RestClientHttpServiceGroupConfigurer {
        return OAuth2RestClientHttpServiceGroupConfigurer.from(authorizedClientManager)
    }
}
