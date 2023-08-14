package com.sampleweatherapp.ui.components.bottomNav

import com.sampleweatherapp.R

sealed class BottomNavItem(
    var title: Int,
    var icon: Int,
    var icon_focused: Int,
    var screen_route: String
) {

    object Home : BottomNavItem(
        R.string.home,
        R.drawable.baseline_home_24,
        R.drawable.baseline_home_24,
        "home"
    )

    object Favourites : BottomNavItem(
        R.string.favourites,
        R.drawable.baseline_favorite_24,
        R.drawable.baseline_favorite_24,
        "favourite"
    )

}