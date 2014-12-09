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
import com.familybiz.greg.taqueue.model.Student;
import com.familybiz.greg.taqueue.model.StudentQueue;
import com.familybiz.greg.taqueue.model.TA;
import com.familybiz.greg.taqueue.model.User;
import com.familybiz.greg.taqueue.network.NetworkRequest;
import com.familybiz.greg.taqueue.view.InstructorListFragment;
import com.familybiz.greg.taqueue.view.QueueListFragment;
import com.familybiz.greg.taqueue.view.SchoolListFragment;
import com.familybiz.greg.taqueue.view.StudentLoginFragment;
import com.familybiz.greg.taqueue.view.TALoginFragment;
import com.familybiz.greg.taqueue.view.QueueFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


/**
 * Created by Greg Anderson
 */
public class MainActivity extends Activity implements
		SchoolListFragment.OnSchoolSelectedListener,
		InstructorListFragment.OnInstructorSelectedListener,
		QueueListFragment.OnQueueSelectedListener,
		StudentLoginFragment.OnStudentLoginSuccessListener {

	// Global access to the networking class, TODO: Which might be a bad idea
	public static NetworkRequest NETWORK_REQUEST;

	private String SAVED_DATA_FILE_NAME = "data.txt";

	// Used in saving and reading from file
	private String USER_TYPE = "user_type";
	private String USERNAME = "username";
	private String ID = "id";
	private String TOKEN = "token";
	private String LOCATION = "location";

	// Fragments
	private SchoolListFragment mSchoolListFragment;
	private InstructorListFragment mInstructorListFragment;
	private QueueListFragment mQueueListFragment;
	private StudentLoginFragment mStudentLoginFragment;
	private TALoginFragment mTALoginFragment;
	private QueueFragment mQueueFragment;

	// ActionBar
	private ActionBar mActionBar;
	private ActionBar.Tab mStudentTab;
	private ActionBar.Tab mTATab;

	// Currently selected options
	private static School mSelectedSchool;          // Current selected school
	private static Instructor mSelectedInstructor;  // Current selected instructor
	private static StudentQueue mSelectedQueue;     // Current selected queue

	// Current user data
	private User mUser;

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

		// Queue list

		mQueueListFragment = new QueueListFragment();
		mQueueListFragment.setOnQueueSelectedListener(this);

		// Login screen

		mStudentLoginFragment = new StudentLoginFragment();
		mStudentLoginFragment.setOnStudentLoginSuccessListener(this);
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

		// Actual queue

		mQueueFragment = new QueueFragment();

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
		mSelectedSchool = school;

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
		mSelectedInstructor = instructor;

		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_layout, mQueueListFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	@Override
	public void onQueueSelected(StudentQueue queue) {
		mSelectedQueue = queue;

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
		clearActionBarAndLoadingCircle();
		super.onBackPressed();
	}

	private void clearActionBarAndLoadingCircle() {
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		ProgressBar loadingCircle = (ProgressBar)findViewById(R.id.loading_circle);
		loadingCircle.setVisibility(View.GONE);
	}

	public static School getSelectedSchool() {
		return mSelectedSchool;
	}

	public static Instructor getSelectedInstructor() {
		return mSelectedInstructor;
	}

	public static StudentQueue getSelectedQueue() {
		return mSelectedQueue;
	}

	@Override
	public void onStudentLoginSuccess() {
		clearActionBarAndLoadingCircle();

		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_layout, mQueueFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	private void saveToFile() {
		try {
			JSONObject data = new JSONObject();

			data.put(USER_TYPE, mUser.getUserType());
			data.put(ID, mUser.getId());
			data.put(TOKEN, mUser.getToken());

			if (mUser.getUserType().equals(User.STUDENT))
				data.put(LOCATION, ((Student)mUser).getLocation());

			File file = new File(getFilesDir().getPath() + SAVED_DATA_FILE_NAME);
			FileWriter fileWriter = new FileWriter(file);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(data.toString());
			bufferedWriter.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void readFromFile() {
		try {
			File file = new File(getFilesDir().getPath() + SAVED_DATA_FILE_NAME);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String data = bufferedReader.readLine();
			bufferedReader.close();

			JSONObject dataJson = new JSONObject(data);
			String user_type = dataJson.getString(USER_TYPE);
			String username = dataJson.getString(USERNAME);
			String id = dataJson.getString(ID);
			String token = dataJson.getString(TOKEN);

			if (user_type.equals(User.TA)) {
				mUser = new TA(username, id, token);
			}
			else {
				String location = dataJson.getString(LOCATION);
				mUser = new Student(username, id, token, location);
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Listener for the ActionBar tabs.
	 */
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