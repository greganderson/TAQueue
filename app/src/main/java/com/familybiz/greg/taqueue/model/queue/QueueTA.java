package com.familybiz.greg.taqueue.model.queue;

/**
 * This represents the information of a TA from the queue.
 *
 * Created by Greg Anderson
 */
public class QueueTA {
	private String mId;
	private QueueStudent mStudent;
	private String mUsername;

	public QueueTA(String id, QueueStudent student, String username) {
		mId = id;
		mStudent = student;
		mUsername = username;
	}

	public String getId() {
		return mId;
	}

	public void setId(String id) {
		mId = id;
	}

	public QueueStudent getStudent() {
		return mStudent;
	}

	public void setStudent(QueueStudent student) {
		mStudent = student;
	}

	public String getUsername() {
		return mUsername;
	}

	public void setUsername(String username) {
		mUsername = username;
	}
}
