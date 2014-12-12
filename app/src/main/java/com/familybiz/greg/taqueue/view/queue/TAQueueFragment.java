package com.familybiz.greg.taqueue.view.queue;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.familybiz.greg.taqueue.MainActivity;
import com.familybiz.greg.taqueue.R;
import com.familybiz.greg.taqueue.model.User;
import com.familybiz.greg.taqueue.model.queue.QueueStudent;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Greg Anderson
 */
public class TAQueueFragment extends QueueFragment {

	public void activateQueue()  {
		setActiveQueue(true);
	}

	public void deactivateQueue() {
		setActiveQueue(false);
	}

	/**
	 * Helper method for activating and deactivating the queue.
	 */
	private void setActiveQueue(boolean active) {
		JSONObject params = new JSONObject();
		JSONObject options = new JSONObject();
		try {
			options.put("active", active);
			params.put("queue", options);
		}
		catch (JSONException e) {
			e.printStackTrace();
		}

		User user = MainActivity.getUser();
		MainActivity.NETWORK_REQUEST.executePutRequest("/queue", params, user.getId(), user.getToken());
	}

	public void freezeQueue() {
		setFrozenQueue(true);
	}

	public void unfreezeQueue() {
		setFrozenQueue(false);
	}

	/**
	 * Helper method for freezing and unfreezing the queue.
	 */
	private void setFrozenQueue(boolean frozen) {
		JSONObject params = new JSONObject();
		JSONObject options = new JSONObject();
		try {
			options.put("frozen", frozen);
			params.put("queue", options);
		}
		catch (JSONException e) {
			e.printStackTrace();
		}

		User user = MainActivity.getUser();
		MainActivity.NETWORK_REQUEST.executePutRequest("/queue", params, user.getId(), user.getToken());
	}

	public void acceptStudent(String name, String location) {
		actionOnStudent(name, location, "ta_accept");
	}

	public void removeStudent(String name, String location) {
		actionOnStudent(name, location, "ta_remove");
	}

	public void putBackStudent(String name, String location) {
		actionOnStudent(name, location, "ta_putback");
	}

	/**
	 * Helper method that accepts, removes, or puts back a student.
	 */
	private void actionOnStudent(String name, String location, String action) {
		QueueStudent student = getStudent(name, location);
		User user = MainActivity.getUser();
		MainActivity.NETWORK_REQUEST.executeGetRequest("/students/" + student.getId() + "/" + action, user.getId(), user.getToken());
	}

	@Override
	public void signOut() {
		User user = MainActivity.getUser();
		MainActivity.NETWORK_REQUEST.executeDeleteRequest("/tas/" + user.getId(), user.getId(), user.getToken());
		mTimer.cancel();
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		String[] items = MainActivity.getNameAndLocation(((TextView) view).getText().toString());
		final String name = items[0];
		final String location = items[1];


		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(getString(R.string.ta_selecting_student_popup_label));

		// Student hasn't been helped
		if (!QueueFragment.beingHelped(name, location)) {
			builder.setItems(mNotYetHelpedActionOptions, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					String clicked = mNotYetHelpedActionOptions[i].toString();
					if (clicked.equals(getString(R.string.accept_student_action)))
						acceptStudent(name, location);
					else
						removeStudent(name, location);
				}
			});
		}

		// Student being helped already
		else {
			builder.setItems(mAlreadyHelpedActionOptions, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					String clicked = mAlreadyHelpedActionOptions[i].toString();
					if (clicked.equals(getString(R.string.remove_student_action)))
						removeStudent(name, location);
					else
						putBackStudent(name, location);
				}
			});
		}

		builder.show();
	}
}
