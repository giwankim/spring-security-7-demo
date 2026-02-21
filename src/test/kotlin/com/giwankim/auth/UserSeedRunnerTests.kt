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
        assertThat(count("select count(*) from users where username in (?, ?)", "user", "admin")).isEqualTo(2)
        assertThat(count("select count(*) from authorities where username = ? and authority = ?", "user", "ROLE_USER")).isEqualTo(1)
        assertThat(count("select count(*) from authorities where username = ? and authority = ?", "admin", "ROLE_USER")).isEqualTo(1)
        assertThat(count("select count(*) from authorities where username = ? and authority = ?", "admin", "ROLE_ADMIN")).isEqualTo(1)

        val encodedPassword = jdbcClient.sql("select password from users where username = :username")
            .param("username", "user")
            .query(String::class.java)
            .single()
        assertThat(encodedPassword).startsWith("{password4j-argon2}")
    }

    @Test
    fun `seeding is idempotent`() {
        val userCountBefore = count("select count(*) from users where username in (?, ?)", "user", "admin")
        val roleCountBefore = count("select count(*) from authorities where username in (?, ?)", "user", "admin")

        userSeedRunner.run(DefaultApplicationArguments(*emptyArray<String>()))

        val userCountAfter = count("select count(*) from users where username in (?, ?)", "user", "admin")
        val roleCountAfter = count("select count(*) from authorities where username in (?, ?)", "user", "admin")

        assertThat(userCountAfter).isEqualTo(userCountBefore)
        assertThat(roleCountAfter).isEqualTo(roleCountBefore)
    }

    private fun count(sql: String, vararg args: Any): Int {
        return jdbcClient.sql(sql).params(*args).query(Int::class.java).single()
    }
}
