package com.google.plus.dougnlamb.firecast;

import java.io.PrintWriter;
import java.io.StringWriter;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.widget.TextView;

import com.google.plus.dougnlamb.firecast.FireCastService.LocalBinder;

public class FireCastActivity extends Activity {
	private boolean mBound;
	private FireCastService mService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();

		setContentView(R.layout.activity_fire_cast);

		try {
			// Bind to LocalService
			Intent svcIntent = new Intent(this, FireCastService.class);
			startService(svcIntent);
			mBound = bindService(svcIntent, mConnection,
					Context.BIND_AUTO_CREATE);
		} catch (Exception ex) {
			displayException(ex);
		}

	}

	@Override
	protected void onStart() {
		super.onStart();
		try {
			TextView tv = (TextView) findViewById(R.id.fire_cast_status);
			tv.setText("Waiting for service.");
			tv.invalidate();

		} catch (Exception ex) {
			displayException(ex);
		}
	}

	protected void processRequest() {

		if (!mService.isCastSessionReady()) {
			launchSetupCastSessionActivity();
		} else {
			sendMedia();
		}
	}

	private void sendMedia() {
		FireMediaTask task = new FireMediaTask(mService, this);
		task.execute(getIntent());

		String typ = getIntent().getType();
		if (typ != null && (typ.startsWith("video/") || typ.startsWith("audio/") ) ) {
			Intent myIntent = new Intent(this, VideoControlsActivity.class);
			startActivity(myIntent);
		}

		finish();
	}

	private void launchSetupCastSessionActivity() {
		Intent myIntent = new Intent(this, SetupCastSessionActivity.class);
		this.startActivityForResult(myIntent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == 0) {
			if (data.hasExtra("isConnected")
					&& data.getExtras().getString("isConnected").equals("yes")) {
				sendMedia();
			} else {
				finish();
			}
		}
	}

	private void displayException(Exception ex) {
		TextView tv = (TextView) findViewById(R.id.fire_cast_status);
		displayException(tv, ex);
	}

	private void displayException(TextView tv, Exception ex) {
		tv.setTextSize(12);
		StringWriter sw = new StringWriter();
		ex.printStackTrace(new PrintWriter(sw));
		tv.setText("Exception: " + sw.toString() + "\n" + tv.getText());
		tv.invalidate();
	}

	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			try {
				mService = ((LocalBinder) service).getService();
				TextView tv = (TextView) findViewById(R.id.fire_cast_status);
				tv.setText("Attached to service.");
				tv.invalidate();
				mBound = true;
				processRequest();
			} catch (Exception ex) {
				displayException(ex);
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBound = false;
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.fire_cast, menu);
		return true;
	}

}
