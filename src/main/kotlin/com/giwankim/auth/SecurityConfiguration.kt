package com.giwankim.auth

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.security.authorization.AuthorizationManagerFactories
import org.springframework.security.authorization.AuthorizationManagerFactory
import org.springframework.security.authorization.DefaultAuthorizationManagerFactory
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
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

private val logger = KotlinLogging.logger {}

@Configuration
class SecurityConfiguration {
    @Bean
    fun authorizationManagerFactory(): AuthorizationManagerFactory<Any> {
        return AuthorizationManagerFactories.multiFactor<Any>()
            .requireFactor { b -> b.ottAuthority().validDuration(10.minutes.toJavaDuration()) }
            .requireFactor { b -> b.passwordAuthority().validDuration(10.minutes.toJavaDuration()) }
            .build()
    }

    @Bean
    fun securityCustomizer(globalAmf: AuthorizationManagerFactory<Any>): Customizer<HttpSecurity> {
        val permissiveAma = DefaultAuthorizationManagerFactory<Any>()
        return Customizer { http ->
            http {
                authorizeHttpRequests {
                    authorize("/admin", globalAmf.authenticated())
                    authorize("/user", permissiveAma.authenticated())
                }
                oneTimeTokenLogin {
                    oneTimeTokenGenerationSuccessHandler =
                        OneTimeTokenGenerationSuccessHandler { request, response, oneTimeToken ->
                            response.writer.println("you've got console mail!")
                            response.contentType = MediaType.TEXT_PLAIN_VALUE

                            logger.info { "please go to http://localhost:${request.serverPort}/login/ott?token=${oneTimeToken.tokenValue}" }
                        }
                }
                webAuthn {
                    allowedOrigins = setOf("http://localhost:8080")
                    rpId = "localhost"
                    rpName = "bootiful"
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
