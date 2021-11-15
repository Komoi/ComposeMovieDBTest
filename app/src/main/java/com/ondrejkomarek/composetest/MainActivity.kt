package com.ondrejkomarek.composetest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.ondrejkomarek.composetest.ui.theme.ComposeTestTheme

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			ComposeTestTheme {
				// A surface container using the 'background' color from the theme
				Surface(color = MaterialTheme.colors.background) {
					MyApp()
				}
			}
		}
	}
}

@Composable
fun MyApp() {
	var shouldShowOnboarding by rememberSaveable { mutableStateOf(true) }


	if (shouldShowOnboarding) { // Where does this come from?
		OnboardingScreen(onContinueClicked = { shouldShowOnboarding = false })
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
	val extraPadding by animateDpAsState(
		if (expanded.value) 48.dp else 0.dp,
		animationSpec = spring(
			dampingRatio = Spring.DampingRatioHighBouncy,
			stiffness = Spring.StiffnessVeryLow
		)
	)

	Surface(
		color = MaterialTheme.colors.primary,
		modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
	) {
		Row(modifier = Modifier.padding(Dp(24f))) {
			Column(modifier = Modifier.weight(1f).padding(bottom = extraPadding.coerceAtLeast(0.dp))) {
				Text(text = "Hello, ")
				Text(text = name)
			}
			OutlinedButton(
				onClick = { expanded.value = !expanded.value }
			) {
				Text(if(expanded.value) "Show less" else "Show more")
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
fun OnboardingScreen(onContinueClicked: () -> Unit) {
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
		}
	}
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun OnboardingPreview() {
	ComposeTestTheme {
		OnboardingScreen(onContinueClicked = {})
	}
}