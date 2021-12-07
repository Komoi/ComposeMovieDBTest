package com.ondrejkomarek.composetest.utility

import android.content.res.Resources
import android.util.TypedValue
import androidx.compose.ui.unit.Dp

val Dp.toPx get() = TypedValue.applyDimension(
	TypedValue.COMPLEX_UNIT_DIP,
	this.value,
	Resources.getSystem().displayMetrics)