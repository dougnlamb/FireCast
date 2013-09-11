package com.google.plus.dougnlamb.firecast;

import java.util.Hashtable;
import java.util.Map;

public class MediaItem {

	private String url;
	private String mimeType;
	private int orientation;

	public MediaItem(String url, String mimetype, int orientation) {
		this.setUrl(url);
		this.setMimeType(mimetype);
		this.setOrientation(orientation);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	private int getOrientation() {
		return orientation;
	}

	private void setOrientation(int orientation) {
		this.orientation = orientation;
	}

	public Map getMap() {
		Map<String, String> map = new Hashtable<String, String>();
		map.put("url", getUrl());
		map.put("mimeType", getMimeType());
		map.put("orientation", Integer.toString(getOrientation()));
		
		return map;
	}

}
