package com.google.plus.dougnlamb.firecast;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.TextView;

public class VideoControlsActivity extends FragmentActivity {

	TextView mTextView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_controls);

		mTextView = (TextView) findViewById(R.id.name);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.video_controls, menu);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		MultimediaControlsFragment f = (MultimediaControlsFragment) getSupportFragmentManager().findFragmentById(R.id.multimedia_controls);
		
		if( f!= null ) {
			if( f.handleKeyDown(keyCode, event)) 
				return true;
		}
	    return super.onKeyDown(keyCode, event);
	}
	

}
