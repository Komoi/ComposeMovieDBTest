package com.ondrejkomarek.composetest.utility

import android.app.Activity
import android.os.Parcel
import android.os.Parcelable
import android.view.View


/**
 * Class responsible for changing the view from full screen to non-full screen and vice versa.
 *
 * @author Pierfrancesco Soffritti
 */
class FullScreenHelper() : Parcelable {

	constructor(parcel: Parcel) : this() {
	}

	/**
	 * call this method to enter full screen
	 */
	fun enterFullScreen(context: Activity) {
		val decorView = context.window.decorView
		hideSystemUi(decorView)
	}

	/**
	 * call this method to exit full screen
	 */
	fun exitFullScreen(context: Activity) {
		val decorView = context.window.decorView
		showSystemUi(decorView)
	}

	private fun hideSystemUi(mDecorView: View) {
		mDecorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
				or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
				or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				or View.SYSTEM_UI_FLAG_FULLSCREEN
				or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
	}

	private fun showSystemUi(mDecorView: View) {
		mDecorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
	}

	override fun writeToParcel(parcel: Parcel, flags: Int) {

	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<FullScreenHelper> {
		override fun createFromParcel(parcel: Parcel): FullScreenHelper {
			return FullScreenHelper(parcel)
		}

		override fun newArray(size: Int): Array<FullScreenHelper?> {
			return arrayOfNulls(size)
		}
	}

}