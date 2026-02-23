package com.giwankim.auth

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.DefaultApplicationArguments
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.simple.JdbcClient

@SpringBootTest
@Import(TestcontainersConfiguration::class)
class UserSeedRunnerTests(
    val jdbcClient: JdbcClient,
    val userSeedRunner: UserSeedRunner,
) {
    @Test
    fun `seeds default users and roles`() {
        assertThat(count(COUNT_USERS_BY_USERNAME, USER, ADMIN)).isEqualTo(2)

        expectedAuthorities.forEach { (username, authority) ->
            assertThat(count(COUNT_AUTHORITIES_BY_USER_AND_ROLE, username, authority)).isEqualTo(1)
        }

        val encodedPassword = encodedPasswordFor(USER)
        assertThat(encodedPassword).startsWith("{password4j-argon2}")
    }

    @Test
    fun `seeding is idempotent`() {
        val userCountBefore = count(COUNT_USERS_BY_USERNAME, USER, ADMIN)
        val roleCountBefore = count(COUNT_AUTHORITIES_BY_USERNAMES, USER, ADMIN)

        userSeedRunner.run(DefaultApplicationArguments())

        val userCountAfter = count(COUNT_USERS_BY_USERNAME, USER, ADMIN)
        val roleCountAfter = count(COUNT_AUTHORITIES_BY_USERNAMES, USER, ADMIN)

        assertThat(userCountAfter).isEqualTo(userCountBefore)
        assertThat(roleCountAfter).isEqualTo(roleCountBefore)
    }

    private fun encodedPasswordFor(username: String): String {
        return jdbcClient.sql(SELECT_PASSWORD_BY_USERNAME)
            .param("username", username)
            .query(String::class.java)
            .single()
    }

    private fun count(sql: String, vararg args: Any): Int {
        return jdbcClient.sql(sql).params(*args).query(Int::class.java).single()
    }

    private companion object {
        const val USER = "user"
        const val ADMIN = "admin"
        const val COUNT_USERS_BY_USERNAME = "select count(*) from users where username in (?, ?)"
        const val COUNT_AUTHORITIES_BY_USERNAMES = "select count(*) from authorities where username in (?, ?)"
        const val COUNT_AUTHORITIES_BY_USER_AND_ROLE = "select count(*) from authorities where username = ? and authority = ?"
        const val SELECT_PASSWORD_BY_USERNAME = "select password from users where username = :username"
        val expectedAuthorities = listOf(
            USER to "ROLE_USER",
            ADMIN to "ROLE_USER",
            ADMIN to "ROLE_ADMIN",
        )
    }
}
