package com.familybiz.greg.taqueue.model;

import java.util.ArrayList;

/**
 * Created by Greg Anderson
 *
 * Represents a school with all of its information.  A school has a name, an abbreviation, and a
 * list of instructors.
 */
public class School {

	private String mName;
	private String mAbbreviation;
	private ArrayList<Instructor> mInstructors;

	public School(String name, String abbreviation, ArrayList<Instructor> instructors) {
		mName = name;
		mAbbreviation = abbreviation;
		mInstructors = instructors;
	}

	public String getName() {
		return mName;
	}

	public String getAbbreviation() {
		return mAbbreviation;
	}

	/**
	 * Get the list of instructors as an array.
	 */
	public Instructor[] getInstructors() {
		return mInstructors.toArray(new Instructor[mInstructors.size()]);
	}
}