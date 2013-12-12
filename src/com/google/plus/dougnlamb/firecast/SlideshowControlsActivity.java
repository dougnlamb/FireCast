package com.google.plus.dougnlamb.firecast;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class SlideshowControlsActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_slideshow_controls);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.slideshow_controls, menu);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		MultimediaControlsFragment f = (MultimediaControlsFragment) getSupportFragmentManager()
				.findFragmentById(R.id.multimedia_controls);

		if (f != null) {
			if (f.handleKeyDown(keyCode, event))
				return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void startSlideshow(View v) {
		startSlideshow();
	}

	private boolean mSlideshowStarted = false;

	public void startSlideshow() {
		if (!mSlideshowStarted) {
			mSlideshowStarted = true;
			FireMediaTask task = new FireMediaTask(
					FireCastService.getInstance(), this);

			Intent intent = new Intent();
			intent.putExtra("LoadDeviceContent", true);
			task.execute(intent);
			try {
				FireCastService.getInstance().getSession().getMessageStream()
						.startSlideshow();
			} catch (Exception ex) {
				Log.e("", ex.getMessage(), ex);
				makeToast("Failed to start show");
			}
		}
	}

	private void makeToast(String txt) {
		Toast.makeText(this, txt, Toast.LENGTH_SHORT).show();
	}
}
