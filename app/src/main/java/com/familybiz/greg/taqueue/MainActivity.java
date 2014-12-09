package com.familybiz.greg.taqueue;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.familybiz.greg.taqueue.model.Instructor;
import com.familybiz.greg.taqueue.model.School;
import com.familybiz.greg.taqueue.model.StudentQueue;
import com.familybiz.greg.taqueue.network.NetworkRequest;
import com.familybiz.greg.taqueue.view.InstructorListFragment;
import com.familybiz.greg.taqueue.view.QueueListFragment;
import com.familybiz.greg.taqueue.view.SchoolListFragment;
import com.familybiz.greg.taqueue.view.StudentLoginFragment;
import com.familybiz.greg.taqueue.view.TALoginFragment;


/**
 * Created by Greg Anderson
 */
public class MainActivity extends Activity implements SchoolListFragment.OnSchoolSelectedListener, InstructorListFragment.OnInstructorSelectedListener, QueueListFragment.OnQueueSelectedListener {

	// Global access to the networking class, TODO: Which might be a bad idea
	public static NetworkRequest NETWORK_REQUEST;

	// Fragments
	private SchoolListFragment mSchoolListFragment;
	private InstructorListFragment mInstructorListFragment;
	private QueueListFragment mQueueListFragment;
	private StudentLoginFragment mStudentLoginFragment;
	private TALoginFragment mTALoginFragment;

	// ActionBar
	private ActionBar mActionBar;
	private ActionBar.Tab mStudentTab;
	private ActionBar.Tab mTATab;

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
		mQueueListFragment.setOnQueueSelectedListener(this);

		// Login screen

		mStudentLoginFragment = new StudentLoginFragment();
		mTALoginFragment = new TALoginFragment();

		// ActionBar

		mActionBar = getActionBar();
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

		mStudentTab = mActionBar.newTab().setText(getString(R.string.student));
		mTATab = mActionBar.newTab().setText(getString(R.string.ta));

		mStudentTab.setTabListener(new LoginTabListener(mStudentLoginFragment));
		mTATab.setTabListener(new LoginTabListener(mTALoginFragment));

		mActionBar.addTab(mStudentTab);
		mActionBar.addTab(mTATab);

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

	@Override
	public void onQueueSelected(StudentQueue queue) {
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_layout, mStudentLoginFragment);
		transaction.addToBackStack(null);
		transaction.commit();


		// Bring on the actionbar
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		invalidateOptionsMenu();
	}

	@Override
	public void onBackPressed() {
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		ProgressBar loadingCircle = (ProgressBar)findViewById(R.id.loading_circle);
		loadingCircle.setVisibility(View.GONE);
		super.onBackPressed();
	}

	private class LoginTabListener implements ActionBar.TabListener {

		Fragment mFragment;

		public LoginTabListener(Fragment fragment) {
			mFragment = fragment;
		}

		@Override
		public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
			fragmentTransaction.replace(R.id.fragment_layout, mFragment);
		}

		@Override
		public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
			fragmentTransaction.remove(mFragment);
		}

		@Override
		public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) { }
	}
}
