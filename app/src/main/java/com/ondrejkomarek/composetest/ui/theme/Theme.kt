package com.ondrejkomarek.composetest.ui.theme


import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
	primary = Purple700,
	primaryVariant = Purple500,
	onPrimary = Color.White,
	secondary = Yellow200,
	onSecondary = Color.Black,
	surface = Navy,
	onSurface = LightBlue,
	background = Color.Black
)

private val LightColorPalette = lightColors(
	primary = Purple700,
	primaryVariant = Purple500,
	onPrimary = Color.White,
	secondary = Yellow200,
	onSecondary = Color.Black,
	surface = Color(244, 246, 246), // Very light gray
	onSurface = Color.Black,
	background = Color.White

	/* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)
/*
private val LightBlueColorPalette = lightColors(
	background = Color.White,
	surface = Color.White,
	onSurface = Color.Black,
	primary = Purple500,
	primaryVariant = Purple200,
	onPrimary = Color.White,
	secondary = Purple700,
	secondaryVariant = Purple200,
	onSecondary = Color.White
)*/

@Composable
fun ComposeTestTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
	val colors = if (darkTheme) {
		DarkColorPalette
	} else {
		LightColorPalette
	}

	MaterialTheme(
		colors = colors,
		typography = Typography,
		shapes = Shapes,
		content = content
	)
}

@Composable
fun AnimationCodelabTheme(content: @Composable () -> Unit) {
	val colors = lightColors(
		primary = Purple500,
		primaryVariant = Purple700,
		secondary = Teal200
	)
	MaterialTheme(
		colors = colors,
		content = content
	)
}