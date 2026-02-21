package com.giwankim.auth

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.JdbcUserDetailsManager
import javax.sql.DataSource

@Configuration
class SecurityConfiguration {
    @Bean
    fun securityCustomizer(): Customizer<HttpSecurity> {
        return { http ->
            http {
                authorizeHttpRequests {
                    authorize("/admin", authenticated)
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
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }

//    @Bean
//    fun delegatingPasswordEncoder(): DelegatingPasswordEncoder {
//        val encodingId = "password4j-argon2"
//        val encoders = mutableMapOf<String, PasswordEncoder>()
//        encoders.put(encodingId, Argon2Password4jPasswordEncoder())
//        encoders.put("bcrypt", BCryptPasswordEncoder())
//        encoders.put("noop", NoOpPasswordEncoder.getInstance())
//        encoders.put("sha256", StandardPasswordEncoder())
//        return DelegatingPasswordEncoder(encodingId, encoders)
//    }
}
