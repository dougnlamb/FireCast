package com.google.plus.dougnlamb.firecast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.cast.ApplicationSession;
import com.google.cast.CastContext;
import com.google.cast.CastDevice;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

public class FireCastService extends Service {

	// private static final String PATH = "/1";
	private static final int PORT = 5002;
	private final IBinder mBinder = new LocalBinder();
	private AsyncHttpServer mServer;

	private CastContext mCastContext;

	private FireCastSession mSession;

	private static FireCastService mInstance;
	private long mStartupTime;
	private Date mServiceStartDate;

	private String mMessages = "";
	private ArrayList<String> mFiles;

	public FireCastService() {
		mFiles = new ArrayList<String>();
	}

	public static FireCastService getInstance() {
		return mInstance;
	}

	public String getMessages() {
		return mMessages;
	}

	public FireCastSession getSession() {
		return mSession;
	}

	@Override
	public void onCreate() {
		mCastContext = new CastContext(getApplicationContext());
		mInstance = this;

		showNotification();
	}// met

	@Override
	public void onDestroy() {
		mInstance = null;
		try {
			if (mSession != null) {
				mSession.endSession();
			}

			if (mCastContext != null) {
				mCastContext.dispose();
			}
		} catch (Exception ex) {
			Log.e("", ex.getMessage(), ex);
		}
		mNotificationManager.cancel(R.string.service_started);

		if (mServer != null) {
			mServer.stop();
		}

	}// met

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mServiceStartDate = new Date();
		startServer();

		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public long getStartupTime() {
		return mStartupTime;
	}

	private void startServer() {
		if (mServer == null) {
			mServer = new AsyncHttpServer();

			mServer.get("/\\d+", new ServerCallback());
			mServer.listen(PORT);

			mMessages = "Started Server.";
		} else {
			mMessages = "Server already started.";
		}
		mStartupTime = new Date().getTime() - mServiceStartDate.getTime();
	}

	public class LocalBinder extends Binder {
		FireCastService getService() {
			return FireCastService.this;
		}
	}

	private class ServerCallback implements HttpServerRequestCallback {

		public ServerCallback() {
		}

		@Override
		public void onRequest(AsyncHttpServerRequest request,
				AsyncHttpServerResponse response) {

			try {
				int index = Integer
						.parseInt(request.getPath().replace("/", ""));
				String filePath = mFiles.get(index);
				// Only serve the file set during in sendMediaRequest().
				File f = new File(filePath);

				String mimeType = getMimeType(filePath);
				response.setContentType(mimeType);

				response.sendFile(f);
			} catch (Exception ex) {
				response.send(ex.getMessage());
			}

		}
	}

	private String mContentType;

	public void sendMediaRequest(String filePath) throws Exception {

		// File path is already http, so just send the path as the url.
		if (filePath.startsWith("http://") || filePath.startsWith("https://")) {
			mSession.sendMedia(getMimeType(filePath), filePath, 0);
		} else {
			this.mFiles.add(filePath);
			String url = getLocalMediaURL(this.mFiles.size() - 1);
			mContentType = getMimeType(filePath);
			int orientation = getOrientation(filePath);

			mSession.sendMedia(mContentType, url, orientation);
		}
	}

	public void queueMediaRequest(String filePath) throws Exception {
		if (filePath.startsWith("http://") || filePath.startsWith("https://")) {
			mSession.queueMedia(getMimeType(filePath), filePath, 0);
		} else {
			this.mFiles.add(filePath);
			String url = getLocalMediaURL(this.mFiles.size() - 1);
			mContentType = getMimeType(filePath);
			int orientation = getOrientation(filePath);

			mSession.queueMedia(mContentType, url, orientation);
		}

	}

	private String getMimeType(String filePath) {
		String mimeType = AsyncHttpServer.getContentType(filePath);
		if ("text/plain".equals(mimeType)) {
			if (filePath.endsWith(".mp3")) {
				mimeType = "audio/mp3";
			} else if (filePath.contains(".mp4?")) {
				mimeType = "video/mp4";
			}
		}

		return mimeType;
	}

	private String getLocalMediaURL(int id) {
		return "http://" + getIpAddr() + ":" + PORT + "/" + id;
	}

	private int getOrientation(String path) throws IOException {
		ExifInterface exif = new ExifInterface(path);
		int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
				ExifInterface.ORIENTATION_NORMAL);

		switch (orientation) {
		case ExifInterface.ORIENTATION_ROTATE_270:
			return 270;
		case ExifInterface.ORIENTATION_ROTATE_180:
			return 180;
		case ExifInterface.ORIENTATION_ROTATE_90:
			return 90;
		}
		return 0;
	}

	public String getIpAddr() {
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ip = wifiInfo.getIpAddress();

		String ipString = String.format("%d.%d.%d.%d", (ip & 0xff),
				(ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff));

		return ipString;
	}

	public boolean isCastSessionReady() {
		if (mSession == null) {
			return false;
		} else {
			return true;
		}
	}

	public CastContext getCastContext() {
		if (mCastContext == null) {
			mCastContext = new CastContext(getApplication()
					.getApplicationContext());
		}

		return mCastContext;
	}

	public void startCasting(CastDevice device) {
		mSession = new FireCastSession();
		mSession.setDevice(device);
		mSession.startSession(getCastContext());
	}

	private NotificationManager mNotificationManager;

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@SuppressLint("NewApi")
	private void showNotification() {
		// The PendingIntent to launch our activity if the user selects this
		// notification
		Intent stopIntent = new Intent(this, MainActivity.class);
		stopIntent.putExtra("command", "stopService");
		PendingIntent pStopIntent = PendingIntent.getActivity(this, 0,
				stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		PendingIntent pControlsIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, SlideshowControlsActivity.class),
				PendingIntent.FLAG_UPDATE_CURRENT);

		Notification notification = new Notification.Builder(this)
				.setContentTitle("Firecast")
				.setContentText("Firecast service running")
				.setSmallIcon(R.drawable.play)
				.addAction(R.drawable.stop, "Stop", pStopIntent)
				.addAction(R.drawable.play, "Video Controls", pControlsIntent)
				.setOngoing(true).build();

		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		// Send the notification.
		// We use a string id because it is a unique number. We use it later to
		// cancel.
		mNotificationManager.notify(R.string.service_started, notification);
	}

}
