package com.abrown.KotlinCalculator.web.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping




@Controller
class DashboardController {
    @GetMapping("/")
    fun hello(): String {
        return "index.html"
    }
}