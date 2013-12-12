package com.google.plus.dougnlamb.firecast;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that
 * contain this fragment must implement the
 * {@link MultimediaControlsFragment.OnFragmentInteractionListener} interface to
 * handle interaction events. Use the
 * {@link MultimediaControlsFragment#newInstance} factory method to create an
 * instance of this fragment.
 * 
 */
public class MultimediaControlsFragment extends Fragment {

	public MultimediaControlsFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_multimedia_controls,
				container, false);

		ButtonListener buttonListener = new ButtonListener();

		view.findViewById(R.id.button_forward).setOnClickListener(
				buttonListener);
		view.findViewById(R.id.button_mute).setOnClickListener(buttonListener);
		view.findViewById(R.id.button_next).setOnClickListener(buttonListener);
		view.findViewById(R.id.button_pause).setOnClickListener(buttonListener);
		view.findViewById(R.id.button_play).setOnClickListener(buttonListener);
		view.findViewById(R.id.button_previous).setOnClickListener(
				buttonListener);
		view.findViewById(R.id.button_restart).setOnClickListener(
				buttonListener);
		view.findViewById(R.id.button_rewind)
				.setOnClickListener(buttonListener);
		view.findViewById(R.id.button_voldown).setOnClickListener(
				buttonListener);
		view.findViewById(R.id.button_volup).setOnClickListener(buttonListener);

		return view;
	}

	private class ButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button_forward:
				forward(v);
				break;
			case R.id.button_mute:
				mute(v);
				break;
			case R.id.button_next:
				nextPhoto(v);
				break;
			case R.id.button_pause:
				pause(v);
				break;
			case R.id.button_play:
				play(v);
				break;
			case R.id.button_previous:
				previousPhoto(v);
				break;
			case R.id.button_restart:
				restart(v);
				break;
			case R.id.button_rewind:
				rewind(v);
				break;
			case R.id.button_voldown:
				volume_down(v);
				break;
			case R.id.button_volup:
				volume_up(v);
				break;
			}

		}

	}

	public void play(View view) {
		try {
			FireCastService.getInstance().getSession().getMessageStream()
					.play();
			makeToast("play");
		} catch (Exception ex) {
			makeToast(ex.getMessage());
		}
	}

	public void pause(View view) {
		try {
			FireCastService.getInstance().getSession().getMessageStream()
					.pause();
			makeToast("Pausing");
		} catch (Exception ex) {
			makeToast(ex.getMessage());
		}
	}

	public void forward(View view) {
		try {

			FireCastService.getInstance().getSession().getMessageStream()
					.seek(30);
			makeToast("Moving FW");
		} catch (Exception ex) {
			makeToast(ex.getMessage());
		}
	}

	public void rewind(View view) {
		try {

			FireCastService.getInstance().getSession().getMessageStream()
					.seek(-30);

			makeToast("Rewinding");
		} catch (Exception ex) {
			makeToast(ex.getMessage());
		}
	}

	public void nextPhoto(View view) {
		try {

			FireCastService.getInstance().getSession().getMessageStream()
					.nextPhoto();
			makeToast("Next");
		} catch (Exception ex) {
			makeToast(ex.getMessage());
		}
	}

	public void previousPhoto(View view) {
		try {
			FireCastService.getInstance().getSession().getMessageStream()
					.previousPhoto();

			makeToast("Previous");
		} catch (Exception ex) {
			makeToast(ex.getMessage());
		}
	}

	public void restart(View view) {
		try {

			FireCastService.getInstance().getSession().getMessageStream()
					.restart();

			makeToast("Restarting");
		} catch (Exception ex) {
			makeToast(ex.getMessage());
		}
	}

	public boolean handleKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			volume_down(null);
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			volume_up(null);
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_MUTE) {
			mute(null);
			return true;
		}

		return false;
	}

	public void mute(View view) {
		try {

			FireCastService.getInstance().getSession().getMessageStream()
					.mute();

			makeToast("Mute");
		} catch (Exception ex) {
			makeToast(ex.getMessage());
		}
	}

	public void volume_down(View view) {
		try {

			FireCastService.getInstance().getSession().getMessageStream()
					.volume_down();

			makeToast("Volume Down");
		} catch (Exception ex) {
			makeToast(ex.getMessage());
		}
	}

	public void volume_up(View view) {
		try {

			FireCastService.getInstance().getSession().getMessageStream()
					.volume_up();

			makeToast("Volume Up");
		} catch (Exception ex) {
			makeToast(ex.getMessage());
		}
	}



	private void makeToast(String txt) {
		Toast.makeText(getActivity(), txt, Toast.LENGTH_SHORT).show();
	}
}
