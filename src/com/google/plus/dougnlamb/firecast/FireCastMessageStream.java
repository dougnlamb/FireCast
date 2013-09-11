package com.google.plus.dougnlamb.firecast;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.cast.MessageStream;

public class FireCastMessageStream extends MessageStream {

	protected FireCastMessageStream(String namespace)
			throws IllegalArgumentException {
		super(namespace);
	}

	@Override
	public void onMessageReceived(JSONObject arg0) {
		// TODO Handle messages from Receiver (playing, paused, currentTime,
		// etc.)

	}

	public void sendMedia(String mimetype, String url, int orientation)
			throws Exception {
		sendMessage(getPayload("newmedia", mimetype, url, orientation));
	}

	public void queueMedia(String mimetype, String url, int orientation)
			throws Exception {
		sendMessage(getPayload("queue-media", mimetype, url, orientation));
	}

	private JSONObject getPayload(String command, String mimetype, String url,
			int orientation) throws JSONException {
		JSONObject payload = new JSONObject();
		payload.put("command", command);

		MediaItem mi = new MediaItem(url, mimetype, orientation);
		payload.put("mediaItem", new JSONObject(mi.getMap()));

		return payload;
	}

	public void pause() throws Exception {
		sendCommand("pause");
	}

	public void play() throws Exception {
		sendCommand("play");
	}

	public void nextPhoto() throws Exception {
		sendCommand("next-photo");
	}

	public void previousPhoto() throws Exception {
		sendCommand("previous-photo");
	}
	
	public void startSlideshow() throws Exception {
		sendCommand("start-slideshow");
	}

	public void seek(int seconds) throws Exception {
		JSONObject payload = new JSONObject();
		payload.put("command", "seek");
		payload.put("seekTime", seconds);

		sendMessage(payload);

	}

	private void sendCommand(String cmdName) throws Exception {

		JSONObject payload = new JSONObject();
		payload.put("command", cmdName);

		sendMessage(payload);
	}

	public void restart() throws Exception {
		sendCommand("restart");
	}

	public void volume_down() throws Exception {
		sendCommand("volume-down");

	}

	public void volume_up() throws Exception {
		sendCommand("volume-up");

	}

	public void mute() throws Exception {
		sendCommand("mute");

	}

}
