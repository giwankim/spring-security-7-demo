package com.giwankim.auth

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.DelegatingPasswordEncoder
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.crypto.password.StandardPasswordEncoder
import org.springframework.security.crypto.password4j.Argon2Password4jPasswordEncoder
import org.springframework.security.provisioning.JdbcUserDetailsManager
import org.springframework.security.web.authentication.ott.OneTimeTokenGenerationSuccessHandler
import javax.sql.DataSource

private val logger = KotlinLogging.logger {}

@Configuration
class SecurityConfiguration {
    @Bean
    fun securityCustomizer(): Customizer<HttpSecurity> {
        return { http ->
            http {
                oneTimeTokenLogin {
                    oneTimeTokenGenerationSuccessHandler =
                        OneTimeTokenGenerationSuccessHandler { request, response, oneTimeToken ->
                            response.writer.println("you've got console mail!")
                            response.contentType = MediaType.TEXT_PLAIN_VALUE

                            logger.info { "please go to http://localhost:${request.serverPort}/login/ott?token=${oneTimeToken.tokenValue}" }
                        }
                }
            }
        }
    }

    @Bean
    fun jdbcUserDetailsManager(ds: DataSource): JdbcUserDetailsManager {
        val userDetailsManager = JdbcUserDetailsManager(ds)
        userDetailsManager.setEnableUpdatePassword(true)
        return userDetailsManager
    }

    @Bean
    fun delegatingPasswordEncoder(): DelegatingPasswordEncoder {
        val encodingId = "password4j-argon2"
        val encoders = mutableMapOf<String, PasswordEncoder>()
        encoders[encodingId] = Argon2Password4jPasswordEncoder()
        encoders["bcrypt"] = BCryptPasswordEncoder()
        encoders["noop"] = NoOpPasswordEncoder.getInstance()
        encoders["sha256"] = StandardPasswordEncoder()
        return DelegatingPasswordEncoder(encodingId, encoders)
    }
}
