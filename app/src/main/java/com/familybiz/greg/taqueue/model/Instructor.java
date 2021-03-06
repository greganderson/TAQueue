package com.familybiz.greg.taqueue.model;

import java.util.ArrayList;

/**
 * Represents an instructor from a school.  An instructor has a name, a username, and a list of
 * queues associated with them.
 *
 * Created by Greg Anderson
 */
public class Instructor {

	private String mName;
	private String mUsername;
	private ArrayList<StudentQueue> mQueue;

	public Instructor(String name, String username) {
		mName = name;
		mUsername = username;
		mQueue = new ArrayList<StudentQueue>();
	}

	public String getName() {
		return mName;
	}

	public String getUsername() {
		return mUsername;
	}

	public void addQueue(StudentQueue queue) {
		mQueue.add(queue);
	}

	/**
	 * Returns the list of queues as an array.
	 */
	public StudentQueue[] getQueues() {
		return mQueue.toArray(new StudentQueue[mQueue.size()]);
	}

	@Override
	public String toString() {
		return getName();
	}
}
