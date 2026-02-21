package com.giwankim.auth

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

private val logger = KotlinLogging.logger {}

@RestController
class MeController {
    private val strategy = SecurityContextHolder.getContextHolderStrategy()

    @GetMapping("/admin")
    fun admin(principal: Principal): Map<String, String> {
        return mapOf("adminName" to principal.name)
    }

    @GetMapping("/user")
    fun me(principal: Principal): Map<String, String> {
        val authentication = strategy.context.authentication
        logger.info { "name: ${authentication?.name}" }
        return mapOf("name" to principal.name)
    }
}
