package com.familybiz.greg.taqueue.network;

import com.familybiz.greg.taqueue.MainActivity;
import com.familybiz.greg.taqueue.model.Student;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Used to make the network call to the server, parse the response, and populate the necessary data
 * entries.
 *
 * Created by Greg Anderson
 */
public class StudentRequest implements NetworkRequest.OnJsonObjectReceivedListener {

	public StudentRequest() {
		MainActivity.NETWORK_REQUEST.addOnJsonObjectReceivedListener(this);
	}

	public void createStudent(String name, String location) {
		try {
			JSONObject params = new JSONObject();
			JSONObject nameLocation = new JSONObject();

			nameLocation.put("name", name);
			nameLocation.put("location", location);

			params.put("student", nameLocation);

			String url = "/schools/" +
					MainActivity.getSelectedSchool().getAbbreviation() + "/" +
					MainActivity.getSelectedInstructor().getUsername() + "/" +
					MainActivity.getSelectedQueue().getClassNumber()   + "/students";

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

			String location = jsonObject.getString("location");
			String token = jsonObject.getString("token");
			String id = jsonObject.getString("id");
			String username = jsonObject.getString("username");

			Student student = new Student(username, id, token, location);

			if (mOnStudentCreatedListener != null)
				mOnStudentCreatedListener.onStudentCreated(student);
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
	}


	/***************************** LISTENERS *****************************/


	public interface OnStudentCreatedListener {
		public void onStudentCreated(Student student);
	}

	private OnStudentCreatedListener mOnStudentCreatedListener;

	public void setOnStudentCreatedListener(OnStudentCreatedListener onStudentCreatedListener) {
		mOnStudentCreatedListener = onStudentCreatedListener;
	}
}
