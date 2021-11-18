package com.ondrejkomarek.composetest.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import coil.annotation.ExperimentalCoilApi
import com.ondrejkomarek.composetest.ui.theme.ComposeTestTheme

val topics = listOf(
	"Arts & Crafts", "Beauty", "Books", "Business", "Comics", "Culinary",
	"Design", "Fashion", "Film", "History", "Maths", "Music", "People", "Philosophy",
	"Religion", "Social sciences", "Technology", "TV", "Writing"
)

@ExperimentalCoilApi
class ThirdActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			ComposeTestTheme {
				// A surface container using the 'background' color from the theme
				Surface(color = MaterialTheme.colors.background) {
					BodyContent()
				}
			}
		}
	}
}

@Composable
fun BodyContent(modifier: Modifier = Modifier) {
	Column(modifier = modifier.horizontalScroll(rememberScrollState()).verticalScroll(rememberScrollState())) {
		StaggeredGrid {
			for (topic in topics) {
				Chip(modifier = Modifier.padding(8.dp), text = topic)
			}
		}
		ConstraintLayoutContent()
		ConstraintLayoutContent2()
		LargeConstraintLayout()
		DecoupledConstraintLayout()
		TwoTexts("asd1", "asd2")
	}
}

@Composable
fun TwoTexts(text1: String, text2: String, modifier: Modifier = Modifier) {
	Row(modifier = modifier.height(IntrinsicSize.Min)) {
		Text(
			modifier = Modifier
				//.weight(1f) TODO does not work as it should
				.padding(start = 4.dp)
				.wrapContentWidth(Alignment.Start),
			text = text1
		)

		Divider(color = Color.Black, modifier = Modifier.fillMaxHeight().width(1.dp))
		Text(
			modifier = Modifier
				//.weight(1f)
				.padding(end = 4.dp)
				.wrapContentWidth(Alignment.End),

			text = text2
		)
	}
}

@Composable
fun ConstraintLayoutContent() {
	ConstraintLayout {

		// Create references for the composables to constrain
		val (button, text) = createRefs()

		Button(
			onClick = { /* Do something */ },
			// Assign reference "button" to the Button composable
			// and constrain it to the top of the ConstraintLayout
			modifier = Modifier.constrainAs(button) {
				top.linkTo(parent.top, margin = 16.dp)
			}
		) {
			Text("Button")
		}

		// Assign reference "text" to the Text composable
		// and constrain it to the bottom of the Button composable
		Text("Text", Modifier.constrainAs(text) {
			top.linkTo(button.bottom, margin = 16.dp)
			// Centers Text horizontally in the ConstraintLayout
			centerHorizontallyTo(parent)
		})
	}
}

@Composable
fun ConstraintLayoutContent2() {
	ConstraintLayout {
		// Creates references for the three composables
		// in the ConstraintLayout's body
		val (button1, button2, text) = createRefs()

		Button(
			onClick = { /* Do something */ },
			modifier = Modifier.constrainAs(button1) {
				top.linkTo(parent.top, margin = 16.dp)
			}
		) {
			Text("Button 1")
		}

		Text("Text", Modifier.constrainAs(text) {
			top.linkTo(button1.bottom, margin = 16.dp)
			centerAround(button1.end)
		})

		val barrier = createEndBarrier(button1, text)
		Button(
			onClick = { /* Do something */ },
			modifier = Modifier.constrainAs(button2) {
				top.linkTo(parent.top, margin = 16.dp)
				start.linkTo(barrier)
			}
		) {
			Text("Button 2")
		}
	}
}

@Composable
fun LargeConstraintLayout() {
	ConstraintLayout {
		val text = createRef()

		val guideline = createGuidelineFromStart(0.5f)
		Text(
			"This is a very very very very very very very long text",
			Modifier.constrainAs(text) {
				linkTo(guideline, parent.end)
				width = Dimension.preferredWrapContent
			}
		)
	}
}

@Composable
fun DecoupledConstraintLayout() {
	BoxWithConstraints {
		val constraints = if (maxWidth < maxHeight) {
			decoupledConstraints(margin = 16.dp) // Portrait constraints
		} else {
			decoupledConstraints(margin = 32.dp) // Landscape constraints
		}

		ConstraintLayout(constraints) {
			Button(
				onClick = { /* Do something */ },
				modifier = Modifier.layoutId("button")
			) {
				Text("Button")
			}

			Text("Text", Modifier.layoutId("text"))
		}
	}
}

private fun decoupledConstraints(margin: Dp): ConstraintSet {
	return ConstraintSet {
		val button = createRefFor("button")
		val text = createRefFor("text")

		constrain(button) {
			top.linkTo(parent.top, margin= margin)
		}
		constrain(text) {
			top.linkTo(button.bottom, margin)
		}
	}
}

//***************

@Composable
fun Chip(modifier: Modifier = Modifier, text: String) {
	Card(
		modifier = modifier,
		border = BorderStroke(color = Color.Black, width = Dp.Hairline),
		shape = RoundedCornerShape(8.dp)
	) {
		Row(
			modifier = Modifier.padding(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 4.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			Box(
				modifier = Modifier.size(16.dp, 16.dp)
					.background(color = MaterialTheme.colors.secondary)
			)
			Spacer(Modifier.width(4.dp))
			Text(text = text)
		}
	}
}

@Preview
@Composable
fun ChipPreview() {
	ComposeTestTheme {
		Chip(text = "Hi there")
	}
}

@Composable
fun StaggeredGrid(
	modifier: Modifier = Modifier,
	rows: Int = 3,
	content: @Composable () -> Unit
) {
	Layout(
		modifier = modifier,
		content = content
	) { measurables, constraints ->

		// Keep track of the width of each row
		val rowWidths = IntArray(rows) { 0 }

		// Keep track of the max height of each row
		val rowHeights = IntArray(rows) { 0 }

		// Don't constrain child views further, measure them with given constraints
		// List of measured children
		val placeables = measurables.mapIndexed { index, measurable ->

			// Measure each child
			val placeable = measurable.measure(constraints)

			// Track the width and max height of each row
			val row = index % rows
			rowWidths[row] += placeable.width
			rowHeights[row] = Math.max(rowHeights[row], placeable.height)

			placeable
		}

		// Grid's width is the widest row
		val width = rowWidths.maxOrNull()
			?.coerceIn(constraints.minWidth.rangeTo(constraints.maxWidth)) ?: constraints.minWidth

		// Grid's height is the sum of the tallest element of each row
		// coerced to the height constraints
		val height = rowHeights.sumOf { it }
			.coerceIn(constraints.minHeight.rangeTo(constraints.maxHeight))

		// Y of each row, based on the height accumulation of previous rows
		val rowY = IntArray(rows) { 0 }
		for (i in 1 until rows) {
			rowY[i] = rowY[i-1] + rowHeights[i-1]
		}

		// Set the size of the parent layout
		layout(width, height) {
			// x cord we have placed up to, per row
			val rowX = IntArray(rows) { 0 }

			placeables.forEachIndexed { index, placeable ->
				val row = index % rows
				placeable.placeRelative(
					x = rowX[row],
					y = rowY[row]
				)
				rowX[row] += placeable.width
			}
		}
	}
}