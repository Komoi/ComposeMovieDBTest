package com.ondrejkomarek.composetest.utility

import android.app.UiModeManager
import android.content.Context

import android.os.Build.VERSION


fun setNightMode(target: Context, state: Boolean) {
	val uiManager = target.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager

	if(state) {
		uiManager.nightMode = UiModeManager.MODE_NIGHT_YES
	} else {
		uiManager.nightMode = UiModeManager.MODE_NIGHT_NO
	}
}