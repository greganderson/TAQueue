package com.familybiz.greg.taqueue.view.queue;

import com.familybiz.greg.taqueue.MainActivity;
import com.familybiz.greg.taqueue.model.User;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Greg Anderson
 */
public class TAQueueFragment extends QueueFragment {

	public void activateQueue()  {
		setActiveQueue(false);
	}

	public void deactivateQueue() {
		setActiveQueue(true);
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

	@Override
	public void signOut() {
		User user = MainActivity.getUser();
		MainActivity.NETWORK_REQUEST.executeDeleteRequest("/tas/" + user.getId(), user.getId(), user.getToken());
	}
}
