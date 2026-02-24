package com.giwankim.service

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
class MeController {
    @GetMapping
    fun me(principal: Principal): Map<String, String> {
        return mapOf("message" to "hello, ${principal.name}!")
    }
}
