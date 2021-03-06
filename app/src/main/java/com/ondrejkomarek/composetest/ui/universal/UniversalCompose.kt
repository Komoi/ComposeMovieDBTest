package com.ondrejkomarek.composetest.ui.universal

import androidx.annotation.FloatRange
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import coil.annotation.ExperimentalCoilApi
import com.ondrejkomarek.composetest.model.Movie
import com.ondrejkomarek.composetest.ui.popular_movies.MovieListCard
import com.ondrejkomarek.composetest.ui.popular_movies.PopularMoviesListState
import android.graphics.Path
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.unit.dp
import kotlin.math.hypot


@Composable
fun MyCircularProgressIndicator(modifier: Modifier = Modifier) {
	Column(modifier.fillMaxSize(),
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally) {
		CircularProgressIndicator()
	}
}

@Composable
fun EmptyState(modifier: Modifier = Modifier) {
	Column(modifier.fillMaxSize(),
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally) {
		Text(text = "There is no content here :/", style = MaterialTheme.typography.h5, modifier = Modifier.padding(24.dp))
	}
}

// https://dev.to/bmonjoie/jetpack-compose-reveal-effect-1fao
fun Modifier.circularReveal(@FloatRange(from = 0.0, to = 1.0) progress: Float, offset: Offset? = null) = clip(CircularRevealShape(progress, offset))

private class CircularRevealShape(
	@FloatRange(from = 0.0, to = 1.0) private val progress: Float,
	private val offset: Offset? = null
) : Shape {
	override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
		return Outline.Generic(Path().apply {
			addCircle(
				offset?.x ?: (size.width / 2f),
				offset?.y ?: (size.height / 2f),
				size.width.coerceAtLeast(size.height) * 2 * progress,
				Path.Direction.CW
			)
		}.asComposePath())
	}
}

private fun longestDistanceToACorner(size: Size, offset: Offset?): Float {
	if (offset == null) {
		return hypot(size.width /2f, size.height / 2f)
	}

	val topLeft = hypot(offset.x, offset.y)
	val topRight = hypot(size.width - offset.x, offset.y)
	val bottomLeft = hypot(offset.x, size.height - offset.y)
	val bottomRight = hypot(size.width - offset.x, size.height - offset.y)

	return topLeft.coerceAtLeast(topRight).coerceAtLeast(bottomLeft).coerceAtLeast(bottomRight)
}

@Composable
fun <T> CircularReveal(
	targetState: T,
	modifier: Modifier = Modifier,
	animationSpec: FiniteAnimationSpec<Float> = tween(),
	content: @Composable (T) -> Unit
) {
	val items = remember { mutableStateListOf<CircularRevealAnimationItem<T>>() }
	val transitionState = remember { MutableTransitionState(targetState) }
	val targetChanged = (targetState != transitionState.targetState)
	transitionState.targetState = targetState
	val transition = updateTransition(transitionState, label = "transition")
	if (targetChanged || items.isEmpty()) {
		// Only manipulate the list when the state is changed, or in the first run.
		val keys = items.map { it.key }.run {
			if (!contains(targetState)) {
				toMutableList().also { it.add(targetState) }
			} else {
				this
			}
		}
		items.clear()
		keys.mapIndexedTo(items) { index, key ->
			CircularRevealAnimationItem(key) {
				val progress by transition.animateFloat(
					transitionSpec = { animationSpec }, label = ""
				) {
					if (index == keys.size - 1) {
						if (it == key) 1f else 0f
					} else 1f
				}
				Box(Modifier.circularReveal(progress = progress)) {
					content(key)
				}
			}
		}
	} else if (transitionState.currentState == transitionState.targetState) {
		// Remove all the intermediate items from the list once the animation is finished.
		items.removeAll { it.key != transitionState.targetState }
	}

	Box {
		items.forEach {
			key(it.key) {
				it.content()
			}
		}
	}
}

private data class CircularRevealAnimationItem<T>(
	val key: T,
	val content: @Composable () -> Unit
)

val LocalThemeToggle: ProvidableCompositionLocal<() -> Unit> = staticCompositionLocalOf { {} }
