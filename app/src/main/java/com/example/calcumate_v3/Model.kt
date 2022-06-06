package com.example.calcumate_v3

data class CurrencyValues(
    val id: Int,
    val regionCode: String,
    val notes: List<String>,
    val coins: List<String>
)

fun getCurrencyValuesByRegionCode(regionCode: String) = currencyValues().find{it.regionCode == regionCode}

fun currencyValues() = listOf(
    CurrencyValues(
        id = 101,
        regionCode = "AU",
        notes = listOf("5", "10", "20", "50", "100"),
        coins = listOf("5", "10", "20", "50", "100", "200")
    )
)