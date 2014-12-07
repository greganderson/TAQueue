package com.familybiz.greg.taqueue.network;

import com.familybiz.greg.taqueue.MainActivity;
import com.familybiz.greg.taqueue.model.School;

import org.json.JSONArray;

/**
 * Created by Greg Anderson
 *
 */
public class SchoolRequest implements NetworkRequest.OnJsonArrayReceivedListener {

	public SchoolRequest() {
		MainActivity.NETWORK_REQUEST.addOnJsonArrayReceivedListener(this);

		// TODO: Figure out when to remove this from the NETWORK_REQUEST listeners.
	}

	public void populateSchoolData() {
		MainActivity.NETWORK_REQUEST.executeGetRequest("");
	}

	@Override
	public void onJsonArrayReceived(JSONArray jsonArray) {
		if (mOnSchoolsReceivedListener == null)
			return;

		// TODO: Parse the json data into school array
		School[] schools = new School[0];

		// Trigger listener
		mOnSchoolsReceivedListener.onSchoolsReceived(schools);
	}


	/***************************** LISTENERS *****************************/


	// Received school information

	public interface OnSchoolsReceivedListener {
		public void onSchoolsReceived(School[] schools);
	}

	private OnSchoolsReceivedListener mOnSchoolsReceivedListener;

	public void setOnSchoolsReceivedListener(OnSchoolsReceivedListener onSchoolsReceivedListener) {
		mOnSchoolsReceivedListener = onSchoolsReceivedListener;
	}
}
