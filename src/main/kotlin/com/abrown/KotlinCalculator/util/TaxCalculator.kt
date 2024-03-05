package com.abrown.KotlinCalculator.util

import java.io.File

/**
 * @author abbybrown
 * @filename Taxation.kt
 * @date 02/26/24
 *
 *      The following class gets the income brackets for each filling status for use in the GUI.
 *      Calculations are made based off of income that the user will enter within the GUI.
 */


class TaxCalculator {
    // define tax bracket rate for a single filer
    private val singleTaxBrackets = listOf(
        TaxBracket(0.0, 11000.0, 0.1),
        TaxBracket(11001.0, 44725.0, 0.12),
        TaxBracket(44726.0, 95375.0, 0.22),
        TaxBracket(95376.0, 182100.0, 0.24),
        TaxBracket(182101.0, 231250.0, 0.32),
        TaxBracket(231251.0, 578125.0, 0.35),
        TaxBracket(578126.0, Double.MAX_VALUE, 0.37)
    )

    private val marriedJointlyTaxBrackets = listOf(
        TaxBracket(0.0, 22000.0, 0.1),
        TaxBracket(22001.0, 89450.0, 0.12),
        TaxBracket(89451.0, 190750.0, 0.22),
        TaxBracket(190751.0, 364200.0, 0.24),
        TaxBracket(364201.0, 462500.0, 0.32),
        TaxBracket(462501.0, 693750.0, 0.35),
        TaxBracket(693751.0, Double.MAX_VALUE, 0.37)
    )

    private val marriedSeparatelyTaxBrackets = listOf(
        TaxBracket(0.0, 11000.0, 0.1),
        TaxBracket(11001.0, 44725.0, 0.12),
        TaxBracket(44726.0, 95375.0, 0.22),
        TaxBracket(95376.0, 182100.0, 0.24),
        TaxBracket(182101.0, 231250.0, 0.32),
        TaxBracket(231251.0, 346875.0, 0.35),
        TaxBracket(346875.0, Double.MAX_VALUE, 0.37)
    )

    // define the allowable marital statuses
    enum class MaritalStatus {
        SINGLE,
        MARRIED_JOINTLY,
        MARRIED_SEPARATELY
    }

    /**
     * @param income - the income of our user
     * @param maritalStatus - the marital status of our user
     *
     * @returns Pair<Double, Double> - returns both the total tax taken and the remaining net income
     *
     * This function calculates how much tax is taken from the user based on income, tax bracket, and
     * marital status.
     */
    fun calculateFederalIncomeTax(income: Double, maritalStatus: MaritalStatus): Pair<Double, Double> {
        val taxBrackets = getTaxBracketsForMaritalStatus(maritalStatus)
        var remainingIncome = income
        var totalTax = 0.0
        var netPay = income

        for (bracket in taxBrackets) {
            // you can do this fun one line thing... kinda weird but neat
            if (remainingIncome <= 0) break

            // calculate the tax on income that lies only within bracket bounds
            val taxableAmountInBracket = minOf(remainingIncome, bracket.upperBound - bracket.lowerBound)
            val taxInBracket = taxableAmountInBracket * bracket.taxRate
            totalTax += taxInBracket

            // take away the taxable income we just taxed and move on to potential next bracket for taxing
            remainingIncome -= taxableAmountInBracket
        }
        // calculate the net pay once taxes are removed
        netPay -= totalTax

        // return both tax amount and pay amount
        return Pair(totalTax, netPay)
    }

    /**
     * @param maritalStatus - the marital status of filer
     *
     * @returns List of tax bracket information
     *
     * Function gets the tax bracket per marital status and returns the information
     */
    private fun getTaxBracketsForMaritalStatus(maritalStatus: MaritalStatus): List<TaxBracket> {
        return when (maritalStatus) {
            MaritalStatus.SINGLE -> singleTaxBrackets
            MaritalStatus.MARRIED_JOINTLY -> marriedJointlyTaxBrackets
            MaritalStatus.MARRIED_SEPARATELY -> marriedSeparatelyTaxBrackets
        }
    }

    // Load state income tax brackets from CSV file
    private val stateTaxBrackets: List<StateBracket> by lazy {
        val stateTaxData = mutableListOf<StateBracket>()
        val csvFile = File("state_tax_rates.csv")
        val lines = csvFile.readLines().drop(1) // Skip header line

        lines.forEach { line ->
            val columns = line.split("\t")
            if (columns.size == 5) {
                val state = columns[0]
                val maritalStatus = when (columns[1]) {
                    "Single", "Married Filing Separately" -> MaritalStatus.SINGLE
                    "Married Filing Jointly" -> MaritalStatus.MARRIED_JOINTLY
                    else -> MaritalStatus.SINGLE
                }
                val lowerBound = columns[2].toDouble()
                val upperBound = if (columns[3] == "-") 0.00 else columns[3].toDouble()
                val taxRate = columns[4].toDouble()

                stateTaxData.add(StateBracket(state, maritalStatus, lowerBound, upperBound, taxRate))
            }
        }
        stateTaxData
    }

    // this is cool! you can make a class for only data and no logic
    data class TaxBracket(val lowerBound: Double, val upperBound: Double, val taxRate: Double)

    data class StateBracket(val state: String, val marital: MaritalStatus, val lowerBound: Double, val upperBound: Double, val taxRate: Double)
}