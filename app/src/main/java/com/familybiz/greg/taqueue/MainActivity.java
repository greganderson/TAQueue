package com.familybiz.greg.taqueue;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.familybiz.greg.taqueue.model.Instructor;
import com.familybiz.greg.taqueue.model.School;
import com.familybiz.greg.taqueue.network.NetworkRequest;
import com.familybiz.greg.taqueue.view.InstructorListFragment;
import com.familybiz.greg.taqueue.view.QueueListFragment;
import com.familybiz.greg.taqueue.view.SchoolListFragment;


/**
 * Created by Greg Anderson
 */
public class MainActivity extends Activity implements SchoolListFragment.OnSchoolSelectedListener, InstructorListFragment.OnInstructorSelectedListener {

	// Global access to the networking class, TODO: Which might be a bad idea
	public static NetworkRequest NETWORK_REQUEST;

	// Fragments
	private SchoolListFragment mSchoolListFragment;
	private InstructorListFragment mInstructorListFragment;
	private QueueListFragment mQueueListFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		NETWORK_REQUEST = new NetworkRequest(this);

		setContentView(R.layout.activity_main);

		// Schools

		mSchoolListFragment = new SchoolListFragment();
		mSchoolListFragment.setOnSchoolSelectedListener(this);

		// Instructors

		mInstructorListFragment = new InstructorListFragment();
		mInstructorListFragment.setOnInstructorSelectedListener(this);

		// Queues

		mQueueListFragment = new QueueListFragment();

		FragmentTransaction addTransaction = getFragmentManager().beginTransaction();
		addTransaction.add(R.id.fragment_layout, mSchoolListFragment);
		addTransaction.commit();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Switches the fragment to the list of instructors for the selected school.
	 */
	@Override
	public void onSchoolSelected(School school) {
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_layout, mInstructorListFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	/**
	 * Switches the fragment to the list of classes for the selected instructor.
	 * @param instructor
	 */
	@Override
	public void onInstructorSelected(Instructor instructor) {
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_layout, mQueueListFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}
}
