package com.familybiz.greg.taqueue.model;

/**
 * Represents all the information for a student as well as all the actions a student can perform.
 *
 * Created by Greg Anderson
 */
public class Student extends User {

	public Student(String id, String token) {
		super(id, token);
	}

	@Override
	public String getUserType() {
		return STUDENT;
	}

	/**
	 * Attempts to enter the queue.  Returns true on success, false on failure.
	 */
	public boolean enterQueue() {
		// TODO: Implement
		return false;
	}

	/**
	 * Attempts to exit the queue.  Returns true on success, false on failure.
	 */
	public boolean exitQueue() {
		// TODO: Implement
		return false;
	}
}
