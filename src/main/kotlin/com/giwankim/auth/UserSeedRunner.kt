package com.giwankim.auth

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.JdbcUserDetailsManager
import org.springframework.stereotype.Component

@Component
class UserSeedRunner(
    private val userDetailsManager: JdbcUserDetailsManager,
    private val passwordEncoder: PasswordEncoder,
) : ApplicationRunner {
    override fun run(args: ApplicationArguments) {
        createUserIfMissing("user", "user123", listOf("ROLE_USER"))
        createUserIfMissing("admin", "admin123", listOf("ROLE_USER", "ROLE_ADMIN"))
    }

    private fun createUserIfMissing(
        username: String,
        rawPassword: String,
        authorities: List<String>,
    ) {
        if (userDetailsManager.userExists(username)) {
            return
        }
        val userDetails = User.withUsername(username)
            .password(passwordEncoder.encode(rawPassword))
            .authorities(*authorities.toTypedArray())
            .build()
        userDetailsManager.createUser(userDetails)
    }
}
