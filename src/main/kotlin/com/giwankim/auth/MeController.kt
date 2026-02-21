package com.giwankim.auth

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.security.Principal

@Controller
@ResponseBody
class MeController {
    private val strategy = SecurityContextHolder.getContextHolderStrategy()

    @GetMapping("/admin")
    fun admin(principal: Principal): Map<String, String> {
        return mapOf("adminName" to principal.name)
    }

    @GetMapping("/user")
    fun me(principal: Principal): Map<String, String> {
        val authentication = strategy.context.authentication
        println("name: ${authentication?.name}")
        return mapOf("name" to principal.name)
    }
}
