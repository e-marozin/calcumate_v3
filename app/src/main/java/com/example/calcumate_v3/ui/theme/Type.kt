package com.example.calcumate_v3.ui.theme

import android.content.res.Resources
import androidx.compose.material.Typography
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.calcumate_v3.R

val SourceSansPro = FontFamily(
    Font(R.font.sourcesanspro_black, FontWeight.Black),
    Font(R.font.sourcesanspro_regular, FontWeight.Normal),
    Font(R.font.sourcesanspro_semibold, FontWeight.SemiBold),
    Font(R.font.sourcesanspro_bold, FontWeight.Bold)
)

// Set of Material typography styles to start with
val Typography = Typography(
    h1 = TextStyle(
        fontFamily = SourceSansPro,
        fontWeight = FontWeight.SemiBold,
        fontSize = 35.sp //??? how to ref dimen ???
    ),
    body1 = TextStyle(
        fontFamily = SourceSansPro,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    body2 = TextStyle(
        fontFamily = SourceSansPro,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    )
    /* Other default text styles to override
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    */
)