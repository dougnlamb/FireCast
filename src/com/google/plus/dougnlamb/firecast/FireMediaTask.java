package com.google.plus.dougnlamb.firecast;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video;
import android.util.Log;
import android.widget.Toast;

public class FireMediaTask extends AsyncTask<Intent, String, String> {

	private Context mContext;
	private FireCastService mService;

	public FireMediaTask(FireCastService service, Context context) {
		mService = service;
		mContext = context;
	}

	@Override
	protected String doInBackground(Intent... params) {
		try {
			if (mService == null) {
				throw new NullPointerException("service not started.");
			}
			Intent intent = params[0];

			String filePath = null;

			if(intent.hasExtra("LoadDeviceContent")) {
				queueAllPhotos();
				queueAllMusic();
			}
			else if (Intent.ACTION_SEND_MULTIPLE.equals(intent.getAction())
					&& intent.hasExtra(Intent.EXTRA_STREAM)) {
				ArrayList<Parcelable> list = intent
						.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
				for (Parcelable p : list) {
					Uri uri = (Uri) p;
					filePath = this.getFilePath(uri);
					queueMediaRequest(filePath);
				}

				return "Success";
			} else if (intent.getType().indexOf("image/") != -1
					|| intent.getType().indexOf("video/") != -1
					|| intent.getType().indexOf("audio/") != -1) {

				Bundle extras = intent.getExtras();
				if (extras.containsKey(Intent.EXTRA_STREAM)) {
					Uri uri = (Uri) extras.getParcelable(Intent.EXTRA_STREAM);
					filePath = getFilePath(uri);
				} else {
					intent.getData().getPath();
					filePath = intent.getData().toString();
				}

				if (filePath != null) {
					sendMediaRequest(filePath);
					return "Success";
				}
			}

			return "Unsupported media.";

		} catch (Exception ex) {
			return "Failed..." + ex.getMessage();
		}
	}

	private String getFilePath(Uri uri) throws Exception {
		String scheme = uri.getScheme();
		this.publishProgress(uri.getPath());

		if (scheme.equals("content")) {
			return getFilePathFromContent(uri);
		} else if (scheme.equals("file")) {
			return uri.getPath();
		}

		return null;
	}

	private void sendMediaRequest(String filePath) throws Exception {
		filePath = fixLocalURL(filePath);
		mService.sendMediaRequest(filePath);
	}

	private void queueMediaRequest(String filePath) throws Exception {
		filePath = fixLocalURL(filePath);
		mService.queueMediaRequest(filePath);
	}

	private String fixLocalURL(String filePath) {
		if (filePath.indexOf("127.0.0.1") > 0) {
			return filePath.replace("127.0.0.1", mService.getIpAddr());
		} else {
			return filePath;
		}
	}

	private String getFilePathFromContent(Uri uri) throws Exception {
		ContentResolver contentResolver = mContext.getContentResolver();
		Cursor cursor = contentResolver.query(uri, null, null, null, null);
		cursor.moveToFirst();

		int index = getColumnIndex(cursor);

		if (index < 0) {
			throw new Exception("Uri does not contain media data.");
		} else {
			return cursor.getString(index);
		}
	}

	private void queueAllPhotos() {
		Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		String selection = MediaStore.Images.Media.MIME_TYPE
				+ " = 'image/jpeg' or " + MediaStore.Images.Media.MIME_TYPE
				+ " = 'image/png' or " + MediaStore.Images.Media.MIME_TYPE
				+ " = 'image/gif'";
		ContentResolver contentResolver = mContext.getContentResolver();
		Cursor cursor = contentResolver.query(uri, null, selection, null, null);
		int idx = 0;
		while (cursor.moveToNext()) {
			int index = getColumnIndex(cursor);
			String path = cursor.getString(index);
			try {
			mService.queueMediaRequest(path);
			}
			catch(Exception ex) {
				Log.e("", ex.getMessage(), ex);
			}
			// System.err.println(path);
			idx += 1;
		}
		// System.err.println(idx);
		return;
	}
	
	private void queueAllMusic() {
		Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		String selection = MediaStore.Audio.Media.MIME_TYPE
				+ " = 'audio/mpeg'";
		ContentResolver contentResolver = mContext.getContentResolver();
		Cursor cursor = contentResolver.query(uri, null, selection, null, null);
		int idx = 0;
		while (cursor.moveToNext()) {
			int index = getColumnIndex(cursor);
			String path = cursor.getString(index);
			try {
			mService.queueMediaRequest(path);
			}
			catch(Exception ex) {
				Log.e("", ex.getMessage(), ex);
			}
			// System.err.println(path);
			idx += 1;
		}
		// System.err.println(idx);
		return;
	}

	private int getColumnIndex(Cursor cursor) {
		int index = cursor.getColumnIndex(Images.Media.DATA);
		if (index < 0) {
			index = cursor.getColumnIndex(Audio.Media.DATA);
		}
		if (index < 0) {
			index = cursor.getColumnIndex(Video.Media.DATA);
		}

		return index;
	}

	@Override
	protected void onPostExecute(String result) {
		if (!"Success".equals(result)) {
			Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
		}
	}
	
}
