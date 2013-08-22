package com.google.plus.dougnlamb.firecast;

import org.json.JSONObject;

import com.google.cast.MessageStream;

public class FireCastMessageStream 
	extends MessageStream {

		protected FireCastMessageStream(String namespace)
				throws IllegalArgumentException {
			super(namespace);
		}

		@Override
		public void onMessageReceived(JSONObject arg0) {
			// TODO Handle messages from Receiver (playing, paused, currentTime, etc.)

		}

		public void sendMedia(String mimetype, String url, int orientation) throws Exception {

			JSONObject payload = new JSONObject();
			payload.put("command", "newmedia");
			payload.put("mimetype", mimetype);
			payload.put("url", url);
			payload.put("orientation", orientation);

			sendMessage(payload);
		}

		public void pause() throws Exception {
			sendCommand("pause");
		}
		
		public void play() throws Exception {
			sendCommand("play");
		}
		
		public void seek(int seconds) throws Exception {
			JSONObject payload = new JSONObject();
			payload.put("command", "seek");
			payload.put("time", seconds);

			sendMessage(payload);
			
		}
		
		private void sendCommand(String cmdName) throws Exception {

			JSONObject payload = new JSONObject();
			payload.put("command", cmdName);

			sendMessage(payload);
		}
}
