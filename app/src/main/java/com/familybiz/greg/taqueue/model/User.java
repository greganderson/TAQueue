package com.familybiz.greg.taqueue.model;

/**
 * Represents all the information for a user.  A user can be a TA or a student.
 *
 * Created by Greg Anderson
 */
public abstract class User {

	public static final String TA = "ta";
	public static final String STUDENT = "student";

	private String mUsername;

	// Username and password used for authentication
	private String mId;     // username
	private String mToken;  // password

	public User(String username, String id, String token) {
		mId = id;
		mToken = token;
	}

	public String getUsername() {
		return mUsername;
	}

	public String getId() {
		return mId;
	}

	public String getToken() {
		return mToken;
	}

	public abstract String getUserType();
}
