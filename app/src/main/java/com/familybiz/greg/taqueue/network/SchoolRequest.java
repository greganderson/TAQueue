package com.familybiz.greg.taqueue.network;

import com.familybiz.greg.taqueue.MainActivity;
import com.familybiz.greg.taqueue.model.Instructor;
import com.familybiz.greg.taqueue.model.School;
import com.familybiz.greg.taqueue.model.StudentQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Used to make the network call to the server, parse the response, and populate the necessary data
 * entries.
 *
 * Created by Greg Anderson
 */
public class SchoolRequest implements NetworkRequest.OnJsonArrayReceivedListener {

	public SchoolRequest() {
		MainActivity.NETWORK_REQUEST.addOnJsonArrayReceivedListener(this);

		// TODO: Figure out when to remove this from the NETWORK_REQUEST listeners.
	}

	public void populateSchoolData() {
		MainActivity.NETWORK_REQUEST.executeGetRequest("");
	}

	/**
	 * Parses the json response into an array of Schools then triggers the listener if there is one.
	 */
	@Override
	public void onJsonArrayReceived(String response) {
		if (mOnSchoolsReceivedListener == null)
			return;

		ArrayList<School> schools = new ArrayList<School>();

		try {
			JSONArray jsonArray = new JSONArray(response);

			// Get Schools
			for (int schoolIndex = 0; schoolIndex < jsonArray.length(); schoolIndex++) {
				JSONObject schoolJson = jsonArray.getJSONObject(schoolIndex);

				// Parse school json
				String schoolAbbreviation = schoolJson.getString("abbreviation");
				String schoolName = schoolJson.getString("name");

				School school = new School(schoolName, schoolAbbreviation);

				// Get instructors
				JSONArray instructors = schoolJson.getJSONArray("instructors");
				for (int instructorIndex = 0; instructorIndex < instructors.length(); instructorIndex++) {
					JSONObject instructorJson = instructors.getJSONObject(instructorIndex);

					// Parse instructor json
					String instructorName = instructorJson.getString("name");
					String instructorUsername = instructorJson.getString("username");

					Instructor instructor = new Instructor(instructorName, instructorUsername);

					// Get queues
					JSONArray queues = instructorJson.getJSONArray("queues");
					for (int queueIndex = 0; queueIndex < queues.length(); queueIndex++) {
						JSONObject queueJson = queues.getJSONObject(queueIndex);

						// Parse queue json
						boolean active = queueJson.getBoolean("active");
						String classNumber = queueJson.getString("class_number");
						boolean frozen = queueJson.getBoolean("frozen");
						String id = queueJson.getString("id");
						String title = queueJson.getString("title");

						StudentQueue queue = new StudentQueue(active, frozen, classNumber, title, id);

						instructor.addQueue(queue);
					}

					school.addInstructor(instructor);
				}

				schools.add(school);
			}
		}
		catch (JSONException e) {
			// Error in json, may have been an array for something else
			return;
		}

		// Trigger listener
		mOnSchoolsReceivedListener.onSchoolsReceived(schools.toArray(new School[schools.size()]));
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
