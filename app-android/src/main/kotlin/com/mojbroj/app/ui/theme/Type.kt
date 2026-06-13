package com.mojbroj.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.mojbroj.app.R

@OptIn(ExperimentalTextApi::class)
private fun jakarta(weight: Int) = Font(
    resId = R.font.plus_jakarta_sans,
    weight = FontWeight(weight),
    variationSettings = FontVariation.Settings(FontVariation.weight(weight))
)

@OptIn(ExperimentalTextApi::class)
private fun grotesk(weight: Int) = Font(
    resId = R.font.space_grotesk,
    weight = FontWeight(weight),
    variationSettings = FontVariation.Settings(FontVariation.weight(weight))
)

val PlusJakarta = FontFamily(
    jakarta(400), jakarta(500), jakarta(600), jakarta(700), jakarta(800)
)

val SpaceGrotesk = FontFamily(
    grotesk(400), grotesk(500), grotesk(700)
)

/** Named styles taken directly from the design spec (kinetic_logic/DESIGN.md). */
object MojBrojType {
    val displayTarget = TextStyle(
        fontFamily = PlusJakarta,
        fontWeight = FontWeight(800),
        fontSize = 72.sp,
        lineHeight = 80.sp,
        letterSpacing = (-0.04).em
    )
    val headlineLg = TextStyle(
        fontFamily = PlusJakarta,
        fontWeight = FontWeight(700),
        fontSize = 32.sp,
        lineHeight = 40.sp
    )
    val headlineLgMobile = TextStyle(
        fontFamily = PlusJakarta,
        fontWeight = FontWeight(700),
        fontSize = 28.sp,
        lineHeight = 36.sp
    )
    val numberTile = TextStyle(
        fontFamily = PlusJakarta,
        fontWeight = FontWeight(700),
        fontSize = 24.sp,
        lineHeight = 32.sp
    )
    val operatorTile = TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight(500),
        fontSize = 28.sp,
        lineHeight = 32.sp
    )
    val labelCaps = TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight(700),
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.1.em
    )
    val bodyMd = TextStyle(
        fontFamily = PlusJakarta,
        fontWeight = FontWeight(400),
        fontSize = 16.sp,
        lineHeight = 24.sp
    )
}

val MojBrojTypography = Typography(
    displayLarge = MojBrojType.displayTarget,
    headlineLarge = MojBrojType.headlineLg,
    headlineMedium = MojBrojType.headlineLgMobile,
    titleLarge = MojBrojType.numberTile,
    titleMedium = TextStyle(
        fontFamily = PlusJakarta,
        fontWeight = FontWeight(700),
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    titleSmall = TextStyle(
        fontFamily = PlusJakarta,
        fontWeight = FontWeight(600),
        fontSize = 15.sp,
        lineHeight = 20.sp
    ),
    bodyLarge = MojBrojType.bodyMd,
    bodyMedium = TextStyle(
        fontFamily = PlusJakarta,
        fontWeight = FontWeight(400),
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = PlusJakarta,
        fontWeight = FontWeight(400),
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelLarge = MojBrojType.labelCaps,
    labelMedium = MojBrojType.labelCaps,
    labelSmall = MojBrojType.labelCaps
)
