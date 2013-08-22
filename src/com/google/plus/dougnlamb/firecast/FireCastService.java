package com.google.plus.dougnlamb.firecast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Date;

import com.google.cast.ApplicationSession;
import com.google.cast.CastContext;
import com.google.cast.CastDevice;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class FireCastService extends Service {

	private static final String PATH = "/1";
	private static final int PORT = 5002;
	private final IBinder mBinder = new LocalBinder();
	private AsyncHttpServer mServer;

	private CastContext mCastContext;

	private FireCastSession mSession;

	private static FireCastService mInstance;
	private long mStartupTime;
	private Date mServiceStartDate;

	private String mMessages = "";

	public FireCastService() {
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
		}

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

			mServer.get(PATH, new ServerCallback());
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
				if (mFilePath != null) {

					// Only serve the file set during in sendMediaRequest().
					File f = new File(mFilePath);
					
					String mimeType = getMimeType(mFilePath);
					response.setContentType(mimeType);
					
					response.sendFile(f);
				} else {
					response.send("No File");
				}
			} catch (Exception ex) {
				response.send(ex.getMessage());
			}

		}
	}

	private String mFilePath;
	private String mContentType;

	public void sendMediaRequest(String filePath) throws Exception {
		mFilePath = filePath;

		// File path is already http, so just send the path as the url.
		if (mFilePath.startsWith("http://") || mFilePath.startsWith("https://")) {
			mSession.sendMedia(getMimeType(mFilePath),
					mFilePath);
		} else {
			String url = getLocalMediaURL();
			mContentType = getMimeType(mFilePath);
			int orientation = getOrientation(mFilePath);

			
			mSession.sendMedia(mContentType, url, orientation);
		}
	}
	
	private String getMimeType(String filePath) {
		String mimeType = AsyncHttpServer.getContentType(mFilePath);
		if("text/plain".equals(mimeType) && filePath.endsWith(".mp3")) {
			mimeType = "audio/mp3";
		}
		
		return mimeType;
	}

	private String getLocalMediaURL() {
		return "http://" + getIpAddr() + ":" + PORT + PATH;
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

}
