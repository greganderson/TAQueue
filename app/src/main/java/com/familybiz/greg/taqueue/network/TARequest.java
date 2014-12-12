package com.familybiz.greg.taqueue.network;

import com.familybiz.greg.taqueue.MainActivity;
import com.familybiz.greg.taqueue.model.TA;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Used to make the network call to the server, parse the response, and populate the necessary data
 * entries.
 *
 * Created by Greg Anderson
 */
public class TARequest implements NetworkRequest.OnJsonObjectReceivedListener {

	public TARequest() {
		MainActivity.NETWORK_REQUEST.addOnJsonObjectReceivedListener(this);
	}

	/**
	 * Clears out the extra listener when needing to be deleted.
	 */
	public void removeListener() {
		MainActivity.NETWORK_REQUEST.removeOnJsonObjectReceivedListener(this);
	}

	public void createTA(String name, String password) {
		try {
			JSONObject params = new JSONObject();
			JSONObject namePassword = new JSONObject();

			namePassword.put("username", name);
			namePassword.put("password", password);

			params.put("ta", namePassword);

			String url = "/schools/" +
					MainActivity.getSelectedSchool().getAbbreviation() + "/" +
					MainActivity.getSelectedInstructor().getUsername() + "/" +
					MainActivity.getSelectedQueue().getClassNumber()   + "/tas";

			MainActivity.NETWORK_REQUEST.executePostRequest(url, params);
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onJsonObjectReceived(String response) {
		try {
			JSONObject jsonObject = new JSONObject(response);

			String username = jsonObject.getString("username");
			String id = jsonObject.getString("id");
			String token = jsonObject.getString("token");

			TA ta = new TA(username, id, token);

			if (mOnTACreatedListener != null)
				mOnTACreatedListener.onTACreated(ta);
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
	}


	/***************************** LISTENERS *****************************/


	public interface OnTACreatedListener {
		public void onTACreated(TA ta);
	}

	private OnTACreatedListener mOnTACreatedListener;

	public void setOnTACreatedListener(OnTACreatedListener onTACreatedListener) {
		mOnTACreatedListener = onTACreatedListener;
	}
}