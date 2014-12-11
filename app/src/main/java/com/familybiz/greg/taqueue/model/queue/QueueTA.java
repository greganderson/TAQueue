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

	public QueueStudent getStudent() {
		return mStudent;
	}

	public String getUsername() {
		return mUsername;
	}
}
