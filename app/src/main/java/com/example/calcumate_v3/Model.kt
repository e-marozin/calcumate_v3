package com.example.calcumate_v3

data class CurrencyValues(
    val id: Int,
    val regionCode: String,
    val notes: List<String>,
    val coinValues: List<Double>,
    val coinLabels: List<String>,
    val coinMap: Map<String,Double>
)

fun getCurrencyValuesByRegionCode(regionCode: String) = currencyValues().find{it.regionCode == regionCode}

fun currencyValues() = listOf(
    CurrencyValues(
        id = 101,
        regionCode = "AU",
        notes = listOf("5", "10", "20", "50", "100"),
        coinValues = listOf(0.05, 0.10, 0.20, 0.50, 1.00, 2.00),
        coinLabels = listOf("5 cents", "10 cents", "20 cents", "50 cents", "1 dollar", "2 dollars"),
        coinMap = mapOf("5 cents" to 0.05, "10 cents" to 0.10, "20 cents" to 0.20, "50 cents" to 0.50, "1 dollar" to 1.00, "2 dollars" to 2.00)
    )
)