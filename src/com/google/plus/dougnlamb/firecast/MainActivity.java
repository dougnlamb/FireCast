package com.google.plus.dougnlamb.firecast;

import java.util.Date;

import com.google.plus.dougnlamb.firecast.R;
import com.google.plus.dougnlamb.firecast.FireCastService.LocalBinder;

import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private FireCastService mService;
	private boolean mBound = false;
	private Date startTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		
		if(getIntent().hasExtra("command")) {
			if(getIntent().getStringExtra("command").equals("stopService")) {
				stopService(new Intent(this, FireCastService.class));
				finish();
				return;
			}
		}
		// Bind to LocalService
		Intent svcIntent = new Intent(this, FireCastService.class);

		startTime = new Date();
		startService(svcIntent);
		mBound = bindService(svcIntent, mConnection, Context.BIND_AUTO_CREATE);
		System.err.println(mBound);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void launch() {
		Intent myIntent = new Intent(this, SetupCastSessionActivity.class);
		this.startActivity(myIntent);
		finish();
	}
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			mService = ((LocalBinder) service).getService();
			Date connTime = new Date();
			long millis = connTime.getTime() - startTime.getTime();
			TextView tv = (TextView) findViewById(R.id.info);
			tv.setText("Bind Time: " + millis + "\nStartup Time: " + mService.getStartupTime()
					+ "\n" + mService.getMessages());
			tv.invalidate();
			mBound = true;
			
			launch();
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBound = false;
		}
	};
}
