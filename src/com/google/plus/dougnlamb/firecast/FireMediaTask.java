package com.google.plus.dougnlamb.firecast;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video;
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

			// Figure out what to do based on the intent type
			if (intent.getType().indexOf("image/") != -1
					|| intent.getType().indexOf("video/") != -1
					|| intent.getType().indexOf("audio/") != -1) {

				Bundle extras = intent.getExtras();
				if (extras.containsKey(Intent.EXTRA_STREAM)) {
					Uri uri = (Uri) extras.getParcelable(Intent.EXTRA_STREAM);
					String scheme = uri.getScheme();
					this.publishProgress(uri.getPath());

					if (scheme.equals("content")) {
						filePath = getFilePathFromContent(intent, filePath, uri);
					} else if (scheme.equals("file")) {
						filePath = uri.getPath();
					}
				}
			}
			
			if (filePath != null) {
				mService.sendMediaRequest(filePath);
				return "Success";
			} else {
				return "Unsupported media.";
			}
		} catch (Exception ex) {
			return "Failed..." + ex.getMessage();
		}
	}

	private String getFilePathFromContent(Intent intent, String filePath,
			Uri uri) {
		ContentResolver contentResolver = mContext.getContentResolver();
		Cursor cursor = contentResolver.query(uri, null, null, null, null);
		cursor.moveToFirst();

		String mimeType = intent.getType();

		if (mimeType.indexOf("image/") != -1) {
			filePath = cursor.getString(cursor
					.getColumnIndexOrThrow(Images.Media.DATA));
		} else if (mimeType.indexOf("audio/") != -1) {
			filePath = cursor.getString(cursor
					.getColumnIndexOrThrow(Audio.Media.DATA));
		} else if (mimeType.indexOf("video/") != -1) {
			filePath = cursor.getString(cursor
					.getColumnIndexOrThrow(Video.Media.DATA));
		}
		return filePath;
	}

	@Override
	protected void onPostExecute(String result) {
		if (!"Success".equals(result)) {
			Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
		}
	}

}
