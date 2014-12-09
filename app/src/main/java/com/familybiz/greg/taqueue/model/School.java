package com.familybiz.greg.taqueue.model;

import java.util.ArrayList;

/**
 * Represents a school with all of its information.  A school has a name, an abbreviation, and a
 * list of instructors.
 *
 * Created by Greg Anderson
 */
public class School {

	private String mName;
	private String mAbbreviation;
	private ArrayList<Instructor> mInstructors;

	public School(String name, String abbreviation) {
		mName = name;
		mAbbreviation = abbreviation;
		mInstructors = new ArrayList<Instructor>();
	}

	public String getName() {
		return mName;
	}

	public String getAbbreviation() {
		return mAbbreviation;
	}

	public void addInstructor(Instructor instructor) {
		mInstructors.add(instructor);
	}

	/**
	 * Get the list of instructors as an array.
	 */
	public Instructor[] getInstructors() {
		return mInstructors.toArray(new Instructor[mInstructors.size()]);
	}

	@Override
	public String toString() {
		return getName();
	}
}
