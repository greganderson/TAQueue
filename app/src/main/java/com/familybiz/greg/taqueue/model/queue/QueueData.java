package com.familybiz.greg.taqueue.model.queue;

import java.util.Set;

/**
 * Created by Greg Anderson
 */
public class QueueData {

	private boolean mActive;
	private boolean mFrozen;
	private String mId;
	private boolean mQuestionBased;
	private String mStatus;
	private Set<QueueStudent> mStudents;
	private Set<QueueTA> mTAs;

	public QueueData(boolean active,
	                 boolean frozen,
	                 String id,
	                 boolean questionBased,
	                 String status,
	                 Set<QueueStudent> students,
	                 Set<QueueTA> TAs) {

		mActive = active;
		mFrozen = frozen;
		mId = id;
		mQuestionBased = questionBased;
		mStatus = status;
		mStudents = students;
		mTAs = TAs;
	}

	public boolean isActive() {
		return mActive;
	}

	public boolean isFrozen() {
		return mFrozen;
	}

	public String getId() {
		return mId;
	}

	public boolean isQuestionBased() {
		return mQuestionBased;
	}

	public String getStatus() {
		return mStatus;
	}

	public QueueStudent[] getStudents() {
		return mStudents.toArray(new QueueStudent[mStudents.size()]);
	}

	public QueueTA[] getTAs() {
		return mTAs.toArray(new QueueTA[mTAs.size()]);
	}
}
