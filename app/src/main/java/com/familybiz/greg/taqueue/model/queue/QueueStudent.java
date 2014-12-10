package com.familybiz.greg.taqueue.model.queue;

/**
 * Represents a student from the queue.
 *
 * Created by Greg Anderson
 */
public class QueueStudent {
	private String mId;
	private boolean mInQueue;
	private String mLocation;
	private String mQuestion;
	private String mTAId;
	private String mUsername;

	public QueueStudent(String id, boolean inQueue, String location, String question, String TAId, String username) {
		mId = id;
		mInQueue = inQueue;
		mLocation = location;
		mQuestion = question;
		mTAId = TAId;
		mUsername = username;
	}

	public String getId() {
		return mId;
	}

	public void setId(String id) {
		mId = id;
	}

	public boolean isInQueue() {
		return mInQueue;
	}

	public void setInQueue(boolean inQueue) {
		mInQueue = inQueue;
	}

	public String getLocation() {
		return mLocation;
	}

	public void setLocation(String location) {
		mLocation = location;
	}

	public String getQuestion() {
		return mQuestion;
	}

	public void setQuestion(String question) {
		mQuestion = question;
	}

	public String getTAId() {
		return mTAId;
	}

	public void setTAId(String TAId) {
		mTAId = TAId;
	}

	public String getUsername() {
		return mUsername;
	}

	public void setUsername(String username) {
		mUsername = username;
	}
}
