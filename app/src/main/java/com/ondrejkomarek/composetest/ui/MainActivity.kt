package com.ondrejkomarek.composetest.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.ondrejkomarek.composetest.R
import com.ondrejkomarek.composetest.ui.MoviesActivity
import com.ondrejkomarek.composetest.ui.theme.ComposeTestTheme

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			ComposeTestTheme {
				// A surface container using the 'background' color from the theme
				Surface(color = MaterialTheme.colors.background) {
					MyApp(onSecondButtonClicked = {startActivity(Intent(this, SecondActivity::class.java))}, onThirdButtonClicked = {startActivity(Intent(this, ThirdActivity::class.java))}, onMovieButtonClicked = {startActivity(Intent(this, MoviesActivity::class.java))})
				}
			}
		}
	}
}

@Composable
fun MyApp(onSecondButtonClicked: () -> Unit, onThirdButtonClicked: () -> Unit, onMovieButtonClicked: () -> Unit) {
	var shouldShowOnboarding by rememberSaveable { mutableStateOf(true) }


	if (shouldShowOnboarding) { // Where does this come from?
		OnboardingScreen(onContinueClicked = { shouldShowOnboarding = false }, onSecondButtonClicked = onSecondButtonClicked, onThirdButtonClicked = onThirdButtonClicked, onMovieButtonClicked = onMovieButtonClicked)
	} else {
		Greetings()
	}
}

@Composable
private fun Greetings(names: List<String> = List(1000) { "$it" } ) {
	LazyColumn(modifier = Modifier.padding(vertical = 4.dp)) {
		items(items = names) { name ->
			Greeting(name = name)
		}
	}
}

@Composable
private fun Greeting(name: String) {

	val expanded = rememberSaveable { mutableStateOf(false) }
	/*val extraPadding by animateDpAsState(
		if (expanded.value) 48.dp else 0.dp,
		animationSpec = spring(
			dampingRatio = Spring.DampingRatioHighBouncy,
			stiffness = Spring.StiffnessVeryLow
		)
	)*/

	Card(
		backgroundColor = MaterialTheme.colors.primary,
		modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
	) {
		Row(modifier = Modifier
			.padding(Dp(24f))
			.animateContentSize(
				animationSpec = spring(
					dampingRatio = Spring.DampingRatioHighBouncy,
					stiffness = Spring.StiffnessVeryLow
				)
			)) {
			Column(modifier = Modifier.weight(1f)) {
				Text(text = "Hello, ")
				Text(text = name, style = MaterialTheme.typography.h4)
				if (expanded.value) Text(text = ("Composem ipsum color sit lazy, " +
						"padding theme elit, sed do bouncy. ").repeat(4), style = MaterialTheme.typography.h6)
			}
			IconButton(
				onClick = { expanded.value = !expanded.value }
			) {
				Icon(if(expanded.value) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore, contentDescription = if (expanded.value) stringResource(
					R.string.show_less) else stringResource(R.string.show_more))
			}
		}
	}
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun DefaultPreview() {
	ComposeTestTheme {
		Greeting("Android")
	}
}

@Composable
fun OnboardingScreen(onContinueClicked: () -> Unit, onSecondButtonClicked: () -> Unit, onThirdButtonClicked: () -> Unit, onMovieButtonClicked: () -> Unit) {
	// TODO: This state should be hoisted

	Surface {
		Column(
			modifier = Modifier.fillMaxSize(),
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Text("Welcome to the Basics Codelab!")
			Button(
				modifier = Modifier.padding(vertical = 24.dp),
				onClick = onContinueClicked
			) {
				Text("Continue")
			}
			Button(
				modifier = Modifier.padding(vertical = 24.dp),
				onClick = onSecondButtonClicked
			) {
				Text("Open Second Codelab A")
			}
			Button(
				modifier = Modifier.padding(vertical = 24.dp),
				onClick = onThirdButtonClicked
			) {
				Text("Open Second Codelab B")
			}
			Button(
				modifier = Modifier.padding(vertical = 24.dp),
				onClick = onMovieButtonClicked
			) {
				Text("Open Popular movies")
			}
		}
	}
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun OnboardingPreview() {
	ComposeTestTheme {
		OnboardingScreen(onContinueClicked = {}, onSecondButtonClicked = {}, onThirdButtonClicked = {}, onMovieButtonClicked = {})
	}
}