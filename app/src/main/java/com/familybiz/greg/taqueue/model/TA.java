package com.familybiz.greg.taqueue.model;

/**
 * Represents all the information for a TA as well as all the actions a TA can perform.
 *
 * Created by Greg Anderson
 */
public class TA extends User {

	public TA(String username, String id, String token) {
		super(username, id, token);
	}

	@Override
	public String getUserType() {
		return TA;
	}
}
