package com.familybiz.greg.taqueue.model;

/**
 * Represents all the information for a student as well as all the actions a student can perform.
 *
 * Created by Greg Anderson
 */
public class Student extends User {

	private String mLocation;

	public Student(String username, String id, String token, String location) {
		super(username, id, token);
		mLocation = location;
	}

	@Override
	public String getUserType() {
		return STUDENT;
	}

	public String getLocation() {
		return mLocation;
	}
}
