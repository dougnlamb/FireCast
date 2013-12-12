package com.google.plus.dougnlamb.firecast;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.MediaRouteButton;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.support.v7.media.MediaRouter.RouteInfo;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.google.cast.CastDevice;
import com.google.cast.MediaRouteAdapter;
import com.google.cast.MediaRouteHelper;
import com.google.cast.MediaRouteStateChangeListener;
import com.google.plus.dougnlamb.firecast.FireCastService.LocalBinder;

public class SetupCastSessionActivity extends FragmentActivity implements
		MediaRouteAdapter {

	private static final String TAG = SetupCastSessionActivity.class
			.getSimpleName();

	// private AlertDialog mConnectionDialog;

	private FireCastService mService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ServiceConnection conn = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				serviceConnected(((LocalBinder) service).getService());
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
				serviceDisconnected();
			}
		};

		setContentView(R.layout.activity_setup_cast_session);

		Intent svcIntent = new Intent(this, FireCastService.class);
		startService(svcIntent);
		bindService(svcIntent, conn, Context.BIND_AUTO_CREATE);

	}

	protected void serviceDisconnected() {
		finish();
	}

	private void serviceConnected(FireCastService service) {
		mService = service;
		startDeviceScan();
	}

	@Override
	public void onBackPressed() {
		disconnect();
		return;
	}

	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		if (mMediaRouter != null && mMediaRouterCallback != null) {
			mMediaRouter.removeCallback(mMediaRouterCallback);
		}
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setup_cast_session, menu);
		return true;
	}

	private void startCasting(CastDevice device) {
		mService.startCasting(device);

		Intent data = new Intent();
		data.putExtra("isConnected", "yes");
		setResult(RESULT_OK, data);
		finish();
	}

	private void disconnect() {
		stopService(new Intent(this, FireCastService.class));
		finish();
	}

	private MediaRouter mMediaRouter;
	private MediaRouteButton mMediaRouteButton;
	private MediaRouteSelector mMediaRouteSelector;
	private MediaRouter.Callback mMediaRouterCallback;

	private boolean mDeviceScanStarted = false;

	private void startDeviceScan() {
		MediaRouteHelper.registerMinimalMediaRouteProvider(
				mService.getCastContext(), this);
		mMediaRouter = MediaRouter.getInstance(getApplicationContext());
		mMediaRouteSelector = MediaRouteHelper.buildMediaRouteSelector(
				MediaRouteHelper.CATEGORY_CAST, null, null);

		mMediaRouteButton = (MediaRouteButton) findViewById(R.id.media_route_button);
		mMediaRouteButton.setRouteSelector(mMediaRouteSelector);
		// mMediaRouteButton.setDialogFactory(mDialogFactory);
		mMediaRouterCallback = new MyMediaRouterCallback();

		mMediaRouter.addCallback(mMediaRouteSelector, mMediaRouterCallback,
				MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);

	}

	@Override
	public void onDeviceAvailable(CastDevice castDevice, String arg1,
			MediaRouteStateChangeListener arg2) {
		startCasting(castDevice);

	}

	@Override
	public void onSetVolume(double arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpdateVolume(double arg0) {
		// TODO Auto-generated method stub

	}

	private class MyMediaRouterCallback extends MediaRouter.Callback {
		@Override
		public void onRouteAdded(MediaRouter router, MediaRouter.RouteInfo info) {

//			Toast.makeText(SetupCastSessionActivity.this,
//					info.getName() + " added.", Toast.LENGTH_SHORT).show();
//			mMediaRouteButton.showDialog();
		}

		@Override
		public void onRouteSelected(MediaRouter router, RouteInfo route) {
			MediaRouteHelper.requestCastDeviceForRoute(route);
		}

		@Override
		public void onRouteUnselected(MediaRouter router, RouteInfo route) {
			try {
				disconnect();
			} catch (IllegalStateException e) {
				Log.e(TAG, "onRouteUnselected:");
				e.printStackTrace();
			}
		}
	}

}
