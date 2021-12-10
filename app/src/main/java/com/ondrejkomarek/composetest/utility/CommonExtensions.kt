package com.ondrejkomarek.composetest.utility

import android.app.Activity
import android.content.res.Resources
import android.util.TypedValue
import androidx.compose.ui.unit.Dp

val Dp.toPx get() = TypedValue.applyDimension(
	TypedValue.COMPLEX_UNIT_DIP,
	this.value,
	Resources.getSystem().displayMetrics)

val Float.toDp get() = this / Resources.getSystem().displayMetrics.density


fun Activity.getStatusBarHeight(): Int {
	var result = 0
	val resourceId: Int = getResources().getIdentifier("status_bar_height", "dimen", "android")
	if(resourceId > 0) {
		result = getResources().getDimensionPixelSize(resourceId)
	}
	return result
}