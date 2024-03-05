package com.abrown.KotlinCalculator.web.controller

import com.abrown.KotlinCalculator.util.TaxCalculator
import com.abrown.KotlinCalculator.web.form.TaxForm
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping


@Controller
class DashboardController {
    @GetMapping("/")
    fun hello(): String {
        return "index.html"
    }

    @PostMapping("/tax-submission")
    fun greetingSubmit(@ModelAttribute taxForm: TaxForm?, model: Model?): String {
        if (taxForm == null || model == null) {
            print("Did I die? A")
            return "index.html"
        }
        val marital: TaxCalculator.MaritalStatus
        try {
            print(taxForm.marital)
            marital = TaxCalculator.MaritalStatus.valueOf(taxForm.marital)
        } catch(e: IllegalArgumentException) {
            print("Did I die? B")
            return "index.html"
        }

        val (tax, net) = TaxCalculator().calculateIncomeTax(taxForm?.salary?:0.0, marital, taxForm?.state?:"Washington")
        model.addAttribute("tax", tax);
        model.addAttribute("net", net);
        model.addAttribute("salary", taxForm.salary);
        model.addAttribute("state", taxForm.state);
        model.addAttribute("marital", taxForm.marital);

        println("Tax: $tax")
        print("Net: $net")
        return "index.html"
    }
}