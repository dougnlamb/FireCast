The Google Cast SDK has changed greatly since this was written.  Someday I hope to get back to this project and update it to the latest.  Until then, I strongly suggest developers research other solutions.

FireCast
========

A demo Android App for casting device videos, mp3s and images.  View YouTube demo: http://www.youtube.com/watch?v=OHJpr1Qg5sc

TODO: 
-----
* Add audio controls
* Improve video controls functionality and UI
* Debug issues when switching back and forth between audio and video
* Improve receiver UI
* Improve message stream API (need audio commands and more video controls)
* Provide ability display media info on chromecast screen
* Add notifications to disconnect and re-open video controls

Dependencies
------------
* AndroidAsync for serving the media by HTTP.
* Chromecast SDK

Main classes
------------

* FireCastActivity - Handles "android.intent.action.SEND" action for image, audio & video mime types.
* SetupCastSessionActivity - Provides dialog for user to connect to desired chromecast device. 
* FireMediaTask - An AsyncTask which extracts the file path or url from the shared intent and sends it to the FireCastService
* FireCastService - Manages the HTTP service and chromecast service, stays running until user disconnects 
* FireCastSession - Manages the chromecast session.
* VideoControlsActivity - Provides controls for play/pause/skip forward/restart of videos.
* FireCastMessageStream - Handles messages to and from the Chromecast receiver
* MainActivity - This main application entry point is used to disconnect from the service.  It can also be used to connect to the service, but that isn't necessary since connection will occur in FireCastActivity if it isn't already to the service.


Normal Application Flow
-----------------------
* User shares media file from another Android Activity
* FireCastActivity handles the shared Intent and starts/binds to FireCastService
* If the cast session isn't connected yet, FireCastActivity starts the SetupCastSessionActivity
* FireCastActivity uses the FireMediaTask to send the media request to the receiver asynchronously
* FireMediaTask extracts the file path or url from the shared Intent and forwards it to the FireCastService
* FireCastService sends the request to FireCastSession
* FireCastSession sends the request to FireCastMessageStream
* FireCastMessageStream sends a JSON message containing the media url to the receiver
* The chromecast receiver requests the 
* FireCastService handles the HTTP request and returns the media file.
 
