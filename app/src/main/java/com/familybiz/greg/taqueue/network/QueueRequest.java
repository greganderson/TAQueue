package com.familybiz.greg.taqueue.network;

import android.util.Log;

import com.familybiz.greg.taqueue.MainActivity;
import com.familybiz.greg.taqueue.model.User;
import com.familybiz.greg.taqueue.model.queue.QueueData;
import com.familybiz.greg.taqueue.model.queue.QueueStudent;
import com.familybiz.greg.taqueue.model.queue.QueueTA;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Greg Anderson
 */
public class QueueRequest implements NetworkRequest.OnJsonObjectReceivedListener {

	public QueueRequest() {
		MainActivity.NETWORK_REQUEST.addOnJsonObjectReceivedListener(this);
	}

	public void updateQueue(String id, String token) {
		String url = "/queue";
		MainActivity.NETWORK_REQUEST.executeGetRequest(url, id, token);
	}

	public void deleteUser(String userUrl) {
		User user = MainActivity.getUser();
		MainActivity.NETWORK_REQUEST.executeDeleteRequest("/" + userUrl + "/" + user.getId(), user.getId(), user.getToken());
	}

	@Override
	public void onJsonObjectReceived(String jsonObject) {
		if (mOnQueueInformationReceivedListener == null)
			return;

		try {
			List<QueueStudent> studentSet = new ArrayList<QueueStudent>();
			List<QueueTA> taSet = new ArrayList<QueueTA>();

			JSONObject queueJson = new JSONObject(jsonObject);
			boolean active = queueJson.getBoolean("active");
			boolean frozen = queueJson.getBoolean("frozen");
			String queueId = queueJson.getString("id");
			boolean questionBased = queueJson.getBoolean("is_question_based");
			String status = queueJson.getString("status");

			// Check if there are any students, then parse
			Object studentsObject = queueJson.get("students");
			if (studentsObject != JSONObject.NULL) {
				JSONArray students = queueJson.getJSONArray("students");

				studentSet = new ArrayList<QueueStudent>();
				for (int i = 0; i < students.length(); i++) {
					QueueStudent student = parseStudent(students.getJSONObject(i));
					studentSet.add(student);
				}
			}

			Object tasObject = queueJson.get("tas");
			if (tasObject != JSONObject.NULL) {
				JSONArray tas = queueJson.getJSONArray("tas");

				taSet = new ArrayList<QueueTA>();
				for (int i = 0; i < tas.length(); i++) {
					JSONObject ta = tas.getJSONObject(i);

					String id = ta.getString("id");

					QueueStudent student = null;
					Object studentObject = ta.get("student");
					if (studentObject != JSONObject.NULL) {
						JSONObject studentJson = ta.getJSONObject("student");
						student = parseStudent(studentJson);
					}
					String username = ta.getString("username");

					taSet.add(new QueueTA(id, student, username));
				}
			}
			QueueData queue = new QueueData(active, frozen, queueId, questionBased, status, studentSet, taSet);

			mOnQueueInformationReceivedListener.onQueueInformationReceived(queue);
		}
		catch (JSONException e) {
			Log.e("QueueRequest", "Error parsing response json");
			e.printStackTrace();
		}
	}

	/**
	 * Helper method for parsing a student from the queue.
	 * @throws JSONException
	 */
	private QueueStudent parseStudent(JSONObject student) throws JSONException {
		String id = student.getString("id");
		boolean inQueue = student.getBoolean("in_queue");
		String location = student.getString("location");
		String question = student.getString("question");
		String taId = student.getString("ta_id");
		String username = student.getString("username");

		return new QueueStudent(id, inQueue, location, question, taId, username);
	}


	/***************************** LISTENERS *****************************/


	public interface OnQueueInformationReceivedListener {
		public void onQueueInformationReceived(QueueData queue);
	}

	private OnQueueInformationReceivedListener mOnQueueInformationReceivedListener;

	public void setOnQueueInformationReceivedListener(OnQueueInformationReceivedListener onQueueInformationReceivedListener) {
		mOnQueueInformationReceivedListener = onQueueInformationReceivedListener;
	}
}
