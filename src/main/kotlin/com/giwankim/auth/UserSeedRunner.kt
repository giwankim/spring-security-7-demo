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
    private val defaultUsers = listOf(
        SeedUser(username = DEFAULT_USER, rawPassword = "user123", authorities = listOf(ROLE_USER)),
        SeedUser(username = DEFAULT_ADMIN, rawPassword = "admin123", authorities = listOf(ROLE_USER, ROLE_ADMIN)),
    )

    override fun run(args: ApplicationArguments) = defaultUsers.forEach(::createUserIfMissing)

    private fun createUserIfMissing(seedUser: SeedUser) {
        if (userDetailsManager.userExists(seedUser.username)) {
            return
        }
        val userDetails = User.withUsername(seedUser.username)
            .password(passwordEncoder.encode(seedUser.rawPassword))
            .authorities(*seedUser.authorities.toTypedArray())
            .build()
        userDetailsManager.createUser(userDetails)
    }

    private companion object {
        const val DEFAULT_USER = "user"
        const val DEFAULT_ADMIN = "admin"
        const val ROLE_USER = "ROLE_USER"
        const val ROLE_ADMIN = "ROLE_ADMIN"
    }
}

private data class SeedUser(
    val username: String,
    val rawPassword: String,
    val authorities: List<String>,
)
