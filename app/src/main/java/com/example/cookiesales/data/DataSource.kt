package com.example.cookiesales.data

import com.example.cookiesales.R

object DataSource {
    val flavors = listOf(
        R.string.chocolate_chip,
        R.string.chocolate,
        R.string.holiday,
        R.string.sugar
    )

    val quantityOptions = listOf(
        Pair(R.string.one_cookie, 1),
        Pair(R.string.two_cookies, 2),
        Pair(R.string.six_cookies, 6),
        Pair(R.string.twelve_cookies, 12)
    )
}