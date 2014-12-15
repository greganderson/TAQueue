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
import com.familybiz.greg.taqueue.model.queue.QueueTA;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Greg Anderson
 */
public class TAQueueFragment extends QueueFragment {

	/**
	 * Returns the QueueTA object of the user.
	 */
	private QueueTA getSelfData() {
		String id = MainActivity.getUser().getId();
		for (QueueTA ta : mQueue.getTAs())
			if (ta.getId().equals(id))
				return ta;
		return null;
	}

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

	public void changeStatus(String status) {
		JSONObject params = new JSONObject();
		JSONObject options = new JSONObject();
		try {
			options.put("status", status);
			params.put("queue", options);
		}
		catch (JSONException e) {
			e.printStackTrace();
		}

		User user = MainActivity.getUser();
		MainActivity.NETWORK_REQUEST.executePutRequest("/queue", params, user.getId(), user.getToken());
	}

	@Override
	public void signOut() {
		mQueueRequest.deleteUser("tas");
		mReadyToRefresh = false;
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		String[] items = MainActivity.getNameAndLocation(((TextView) view).getText().toString());
		final String name = items[0];
		final String location = items[1];


		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(getString(R.string.ta_selecting_student_popup_label));

		// Student hasn't been helped
		if (QueueFragment.indexOfHelpingTA(name, location) == -1) {
			builder.setItems(mNotYetHelpedActionOptions, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					String clicked = mNotYetHelpedActionOptions[i].toString();
					if (clicked.equals(getString(R.string.accept_student_action))) {

						// If already helping a student, remove that student and accept the new one.
						QueueTA ta = getSelfData();
						QueueStudent student = ta.getStudent();
						if (student != null)
							removeStudent(student.getUsername(), student.getLocation());

						acceptStudent(name, location);
					}
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
