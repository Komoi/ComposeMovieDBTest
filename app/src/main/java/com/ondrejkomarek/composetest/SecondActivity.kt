package com.ondrejkomarek.composetest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.ondrejkomarek.composetest.ui.theme.ComposeTestTheme
import kotlinx.coroutines.launch


@ExperimentalCoilApi
class SecondActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			ComposeTestTheme {
				// A surface container using the 'background' color from the theme
				Surface(color = MaterialTheme.colors.background) {
					LayoutsCodelab()
				}
			}
		}
	}
}

@ExperimentalCoilApi
@Composable
fun LayoutsCodelab() {
	// We save the coroutine scope where our animated scroll will be executed
	val coroutineScope = rememberCoroutineScope()

	// We save the scrolling position with this state
	val scrollState = rememberLazyListState()
	val listSize = 5000

	Scaffold(
		topBar = {
			TopAppBar(
				title = {
					Text(text = "LayoutsCodelab")
				},
				actions = {
					IconButton(onClick = { /* doSomething() */ }) {
						Icon(Icons.Filled.Favorite, contentDescription = null)
					}

					IconButton(onClick = { /* doSomething() */ }) {
						Icon(Icons.Filled.DeleteForever, contentDescription = "Delete")
					}
				}
			)
		}
	) { innerPadding ->
		Column() {
			Text(text = "Hi there!", modifier = Modifier.padding(innerPadding))
			Row {
				Button(onClick = {
					coroutineScope.launch {
						// 0 is the first item index
						scrollState.animateScrollToItem(0)
					}
				}) {
					Text("Scroll to the top")
				}

				Button(onClick = {
					coroutineScope.launch {
						// listSize - 1 is the last index of the list
						scrollState.animateScrollToItem(listSize - 1)
					}
				}) {
					Text("Scroll to the end")
				}
			}
			LazyList(scrollState, listSize)
		}
	}

	/*Card(
		backgroundColor = MaterialTheme.colors.primary,
		modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
	) {
		PhotographerCard()
	}*/

}

fun Modifier.firstBaselineToTop(
	firstBaselineToTop: Dp
) = this.then(
	layout { measurable, constraints ->
		val placeable = measurable.measure(constraints)

		// Check the composable has a first baseline
		check(placeable[FirstBaseline] != AlignmentLine.Unspecified)
		val firstBaseline = placeable[FirstBaseline]

		// Height of the composable with padding - first baseline
		val placeableY = firstBaselineToTop.roundToPx() - firstBaseline
		val height = placeable.height + placeableY
		layout(placeable.width, height) {
			placeable.placeRelative(0, placeableY)
		}
	}
)

// Crashing with OOM, not sure why
@Composable
fun MyOwnColumn(
	modifier: Modifier = Modifier,
	content: @Composable () -> Unit
) {
	Layout(
		modifier = modifier,
		content = content
	) { measurables, constraints ->
		// Don't constrain child views further, measure them with given constraints
		// List of measured children
		val placeables = measurables.map { measurable ->
			// Measure each child
			measurable.measure(constraints)
		}

		// Track the y co-ord we have placed children up to
		var yPosition = 0

		// Set the size of the layout as big as it can
		layout(constraints.maxWidth, constraints.maxHeight) {
			// Place children in the parent layout
			placeables.forEach { placeable ->
				// Position item on the screen
				placeable.placeRelative(x = 0, y = yPosition)

				// Record the y co-ord placed up to
				yPosition += placeable.height
			}
		}
	}
}

@Composable
fun SimpleList() {
	// We save the scrolling position with this state that can also
	// be used to programmatically scroll the list
	val scrollState = rememberScrollState()

	Column(Modifier.verticalScroll(scrollState)) {
		repeat(5000) {
			Text("Item #$it")
		}
	}
}

@ExperimentalCoilApi
@Composable
fun LazyList(scrollState: LazyListState, listSize: Int) {

	LazyColumn(state = scrollState) {
		items(listSize) {
			PhotographerCard()
		}
	}
}

@ExperimentalCoilApi
@Composable // for better reusability
fun PhotographerCard(modifier: Modifier = Modifier) {
	Row(
		modifier = modifier
			.clickable(onClick = { /* Ignoring onClick */ })
			.padding(16.dp)
	) {
		Surface(
			modifier = Modifier.size(56.dp),
			shape = CircleShape,
			color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
		) {
			Image(
				painter = rememberImagePainter(
					data = "https://developer.android.com/images/brand/Android_Robot.png"
				),
				contentDescription = "Android Logo",
				modifier = Modifier.size(50.dp)
			)
		}
		Column(
			modifier = Modifier
				.padding(start = 16.dp)
				.align(Alignment.CenterVertically)
		) {
			Text("Alfred Sisley", fontWeight = FontWeight.Bold)
			// LocalContentAlpha is defining opacity level of its children
			CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
				Text("3 minutes ago", style = MaterialTheme.typography.body2)
			}
		}
	}
}

@Preview
@Composable
fun LayoutsCodelabPreview() {
	ComposeTestTheme {
		LayoutsCodelab()
	}
}