package com.google.plus.dougnlamb.firecast;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.plus.dougnlamb.firecast.R;

public class VideoControlsActivity extends Activity {

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

	public void play(View view) {
		try {
		FireCastService.getInstance().getSession().getMessageStream().play();
		mTextView.setText("Playing");
		mTextView.invalidate();
		}
		catch(Exception ex) {
			makeToast(ex.getMessage());
		}
	}

	public void pause(View view) {
		try {
		FireCastService.getInstance().getSession().getMessageStream().pause();
		mTextView.setText("Pausing");
		mTextView.invalidate();
		}
		catch(Exception ex) {
			makeToast(ex.getMessage());
		}
	}

	public void forward(View view) {
		try {
	
		FireCastService.getInstance().getSession().getMessageStream().seek(10);
		mTextView.setText("Moving FW");
		mTextView.invalidate();
	}
	catch(Exception ex) {
		makeToast(ex.getMessage());
	}
	}

	public void rewind(View view) {
		try {
	
		FireCastService.getInstance().getSession().getMessageStream().seek(-10);
		
		mTextView.setText("Rewinding");
		mTextView.invalidate();
	}
	catch(Exception ex) {
		makeToast(ex.getMessage());
	}
	}

	private void makeToast(String txt) {
		Toast.makeText(this, txt, Toast.LENGTH_SHORT).show();
	}
}
