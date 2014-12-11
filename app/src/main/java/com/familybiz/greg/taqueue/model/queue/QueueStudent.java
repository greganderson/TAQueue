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

	public boolean isInQueue() {
		return mInQueue;
	}

	public String getLocation() {
		return mLocation;
	}

	public String getQuestion() {
		return mQuestion;
	}

	public String getTAId() {
		return mTAId;
	}

	public String getUsername() {
		return mUsername;
	}
}
