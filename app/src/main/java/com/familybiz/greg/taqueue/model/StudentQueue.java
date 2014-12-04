package com.familybiz.greg.taqueue.model;

/**
 * Created by Greg Anderson
 *
 * Represents a queue for an instructor.  Would have called it a Queue, but java swiped that name
 * before I could get to it.  A queue keeps track of whether it is active or frozen.  It also has
 * a class number, title, and an ID.
 */
public class StudentQueue {

	private boolean mIsActive;
	private boolean mIsFrozen;
	private String mClassNumber;
	private String mTitle;
	private String mId;

	public StudentQueue(boolean isActive, boolean isFrozen, String classNumber, String title, String id) {
		mIsActive = isActive;
		mIsFrozen = isFrozen;
		mClassNumber = classNumber;
		mTitle = title;
		mId = id;
	}

	public boolean isActive() {
		return mIsActive;
	}

	public boolean isFrozen() {
		return mIsFrozen;
	}

	public String getClassNumber() {
		return mClassNumber;
	}

	public String getTitle() {
		return mTitle;
	}

	public String getId() {
		return mId;
	}
}
