package com.google.plus.dougnlamb.firecast;

import com.google.cast.CastDevice;
import com.google.cast.CastDeviceAdapter;
import com.google.cast.DeviceManager;
import com.google.plus.dougnlamb.firecast.R;
import com.google.plus.dougnlamb.firecast.FireCastService.LocalBinder;

import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class SetupCastSessionActivity extends Activity {

	private DeviceManager mDeviceManager;
	private CastDeviceAdapter mDeviceAdapter;

	private AlertDialog mConnectionDialog;

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

		mDeviceAdapter = new CastDeviceAdapter(this);

		Intent svcIntent = new Intent(this, FireCastService.class);
		startService(svcIntent);
		bindService(svcIntent, conn, Context.BIND_AUTO_CREATE);

		// ((TextView) this.findViewById(R.id.setup_status))
		// .setText("Waiting for service");

		buildDialog();

	}

	private void buildDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		// Get the layout inflater
		LayoutInflater inflater = this.getLayoutInflater();

		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		final View v = inflater.inflate(R.layout.dialog_setup_cast_session,
				null);
		Spinner spinner = (Spinner) v.findViewById(R.id.spinner_device);
		spinner.setAdapter(mDeviceAdapter);

		builder.setView(v)
				.setPositiveButton(R.string.connect_button,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								connect(v);
							}
						})
				.setNegativeButton(R.string.cancel_button,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								disconnect();
							}
						}).setTitle("Chromecast Devices")
				.setMessage("Scanning...");

		mDeviceAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		mConnectionDialog = builder.create();

		mConnectionDialog
				.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						disconnect();
					}
				});
		
	
		mConnectionDialog.show();

		checkOKEnabled();
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setup_cast_session, menu);
		return true;
	}

	private void connect(View v) {
		mDeviceManager.stopScan();
		CastDevice device = (CastDevice) ((Spinner) v
				.findViewById(R.id.spinner_device)).getSelectedItem();
		startCasting(device);
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
		mDeviceManager.stopScan();
		finish();
	}

	private void startDeviceScan() {
		mDeviceManager = new DeviceManager(mService.getCastContext());
		mDeviceManager.addListener(new DeviceManager.Listener() {
			@Override
			public void onScanStateChanged(int state) {
				if (state == DeviceManager.SCAN_SUSPENDED_NETWORK_ERROR) {
					new AlertDialog.Builder(SetupCastSessionActivity.this)
							.setMessage("Network Error")
							.setPositiveButton(R.string.connect_button, null)
							.create().show();
				}
			}

			@Override
			public void onDeviceOnline(CastDevice device) {
				mConnectionDialog.setMessage("Select Device");
				mConnectionDialog.getButton(RESULT_OK).setEnabled(true);
				mDeviceAdapter.add(device);

				checkOKEnabled();
			}

			@Override
			public void onDeviceOffline(CastDevice device) {
				mDeviceAdapter.remove(device);
				checkOKEnabled();
			}
		});
		mDeviceManager.startScan();

	}

	private void checkOKEnabled() {
		Button b = mConnectionDialog.getButton(RESULT_OK);
		if (b != null) {
			b.setEnabled(mDeviceAdapter.getCount() > 0);
		}
	}
}
