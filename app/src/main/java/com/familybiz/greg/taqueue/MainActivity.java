package com.familybiz.greg.taqueue;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.familybiz.greg.taqueue.model.Instructor;
import com.familybiz.greg.taqueue.model.School;
import com.familybiz.greg.taqueue.model.Student;
import com.familybiz.greg.taqueue.model.StudentQueue;
import com.familybiz.greg.taqueue.model.TA;
import com.familybiz.greg.taqueue.model.User;
import com.familybiz.greg.taqueue.network.NetworkRequest;
import com.familybiz.greg.taqueue.view.lists.InstructorListFragment;
import com.familybiz.greg.taqueue.view.lists.QueueListFragment;
import com.familybiz.greg.taqueue.view.lists.SchoolListFragment;
import com.familybiz.greg.taqueue.view.login.LoginFragment;
import com.familybiz.greg.taqueue.view.login.StudentLoginFragment;
import com.familybiz.greg.taqueue.view.login.TALoginFragment;
import com.familybiz.greg.taqueue.view.queue.QueueFragment;
import com.familybiz.greg.taqueue.view.queue.StudentQueueFragment;
import com.familybiz.greg.taqueue.view.queue.TAQueueFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


// TODO: Make it so the user can refresh the schools list

/**
 * Created by Greg Anderson
 */
public class MainActivity extends Activity implements
		SchoolListFragment.OnSchoolSelectedListener,
		InstructorListFragment.OnInstructorSelectedListener,
		QueueListFragment.OnQueueSelectedListener,
		StudentLoginFragment.OnStudentLoginSuccessListener,
		TALoginFragment.OnTALoginSuccessListener,
		QueueFragment.OnSignOutListener,
		NetworkRequest.OnErrorCodeReceivedListener,
		SchoolListFragment.OnSchoolsLoadedListener,
		NetworkRequest.OnNetworkTimeoutListener,
		NetworkTestFragment.OnNetworkReconnectedListener {

	// Global access to the networking class, TODO: Which might be a bad idea
	public static NetworkRequest NETWORK_REQUEST;

	public static int QUERY_INTERVAL = 1000;

	private String SAVED_DATA_FILE_NAME = "data.txt";

	// Used in saving and reading from file
	private String USER_TYPE = "user_type";
	private String USERNAME = "username";
	private String ID = "id";
	private String TOKEN = "token";
	private String LOCATION = "location";
	private String SELECTED_SCHOOL = "selected_school";
	private String SELECTED_INSTRUCTOR = "selected_instructor";
	private String SELECTED_QUEUE = "selected_queue";
	private String DO_NOT_SHOW_OPENING_DIALOG = "do_not_show_opening_dialog";

	// Fragments
	private SchoolListFragment mSchoolListFragment;
	private InstructorListFragment mInstructorListFragment;
	private QueueListFragment mQueueListFragment;
	private StudentLoginFragment mStudentLoginFragment;
	private TALoginFragment mTALoginFragment;
	private StudentQueueFragment mStudentQueueFragment;
	private TAQueueFragment mTAQueueFragment;
	private boolean mOnQueueScreen; // Used for overriding the back button
	private NetworkTestFragment mNetworkTestFragment;
	// Fragment tags
	private String school_list_fragment_tag = "SCHOOL_LIST";
	private String instructor_list_fragment_tag = "INSTRUCTOR_LIST";
	private String queue_list_fragment_tag = "QUEUE_LIST";
	private String login_screen_fragment_tag = "LOGIN_SCREEN";
	private String student_queue_fragment_tag = "STUDENT_QUEUE";
	private String ta_queue_fragment_tag = "TA_QUEUE";

	private boolean mLoginFragmentAdded;

	// Fragment was the fragment replaced by the connection error
	private boolean mLastFragmentWasLogin;
	private boolean mLastFragmentWasStudentQueue;
	private boolean mLastFragmentWasTAQueue;

	// ActionBar
	private int mSettingsMenuItem = Menu.FIRST;
	private int mRefreshMenuItem = Menu.FIRST;
	private int mMoreInformationMenuItem = Menu.FIRST;
	public static ActionBar mActionBar;
	private boolean mInitialSelect;

	// Currently selected options
	private static School mSelectedSchool;          // Current selected school
	private static Instructor mSelectedInstructor;  // Current selected instructor
	private static StudentQueue mSelectedQueue;     // Current selected queue

	// Current user data
	private static User mUser;
	private boolean mDoNotShowOpeningDialog;

	// Options menu for changing what is shown depending on which screen the user is on
	private int mTAOptionsMenuQueueStatus = Menu.FIRST;
	private int mTAOptionsMenuIsQuestionBased = Menu.FIRST;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load what the user had to say about the opening dialog.
		checkShowOpeningDialog();

		if (!mDoNotShowOpeningDialog) {
			final LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.opening_dialog_text, null);
			Button okayButton = new Button(this);
			okayButton.setText(getString(R.string.okay_button));
			new AlertDialog.Builder(this)
					.setTitle(getString(R.string.opening_dialog_box_title))
					.setView(layout)
					.setCancelable(true)
					.setPositiveButton(getString(R.string.okay_button), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							CheckBox checker = (CheckBox) layout.findViewById(R.id.do_not_show_message_checkbox);
							mDoNotShowOpeningDialog = checker.isChecked();
						}
					})
					.show();
		}

		mOnQueueScreen = false;
		mLoginFragmentAdded = false;

		NETWORK_REQUEST = new NetworkRequest(this);
		NETWORK_REQUEST.setOnErrorCodeReceivedListeners(this);
		NETWORK_REQUEST.setOnNetworkTimeoutListener(this);

		mNetworkTestFragment = new NetworkTestFragment();
		mNetworkTestFragment.setOnNetworkReconnectedListener(this);

		setContentView(R.layout.activity_main);

		// Schools

		mSchoolListFragment = new SchoolListFragment();
		mSchoolListFragment.setOnSchoolSelectedListener(this);
		mSchoolListFragment.setOnSchoolsLoadedListener(this);

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
		mTALoginFragment.setOnTALoginSuccessListener(this);

		// ActionBar

		mActionBar = getActionBar();
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

		// Actual queue

		mStudentQueueFragment = new StudentQueueFragment();
		mStudentQueueFragment.setOnSignOutListener(this);

		mTAQueueFragment = new TAQueueFragment();
		mTAQueueFragment.setOnSignOutListener(this);

		FragmentTransaction addTransaction = getFragmentManager().beginTransaction();
		addTransaction.add(R.id.fragment_layout, mSchoolListFragment, school_list_fragment_tag);
		addTransaction.commit();
	}

	@Override
	protected void onStop() {
		saveToFile();
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String title = item.getTitle().toString();

		// Settings
		if (title.equals(getString(R.string.settings_label))) {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
		}

		// Only available if on the school list fragment
		// Refresh
		else if (title.equals(getString(R.string.refresh_label))) {
			mSchoolListFragment.refreshData();
		}

		// More info
		if (title.equals(getString(R.string.more_information_options_menu_label))) {
			Intent intent = new Intent(this, MoreInformationActivity.class);
			startActivity(intent);
		}

		// Only available if user is a student
		// Chat
		else if (title.equals(getString(R.string.cancel_label))) {
			final EditText statusView = new EditText(this);

			// Set the maximum number of characters allowed
			// 51 characters fits the full line of the input box on the browser version (at least on
			// my screen in chrome)
			int maxLength = 51;
			statusView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
			statusView.setHint(getString(R.string.change_queue_status_edittext_hint));

			// Build the alert dialog
			new AlertDialog.Builder(this)
					.setTitle(getString(R.string.change_queue_status_title))
					.setCancelable(true)
					.setView(statusView)
					.setPositiveButton(getString(R.string.save_label), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							mTAQueueFragment.changeStatus(statusView.getText().toString());
						}
					})
					.setNegativeButton(getString(R.string.cancel_label), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							// Do nothing
						}
					})
					.show();
			return true;
		}

		// TODO: Check why making the queue question based isn't working (on the server end)
		// TODO: NOTE!  THIS MENU ITEM WILL NOT DO ANYTHING UNTIL THE SERVER IS FIXED!
		// Question based
		else if (title.equals(getString(R.string.ta_options_menu_is_question_based_label))) {
			new AlertDialog.Builder(this)
					.setTitle(getString(R.string.queue_is_question_based_dialog_title))
					.setCancelable(true)
					.setPositiveButton(getString(R.string.is_queue_question_based_yes_label), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							mTAQueueFragment.changeIsQuestionBased(true);
						}
					})
					.setNegativeButton(getString(R.string.is_queue_question_based_no_label), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							mTAQueueFragment.changeIsQuestionBased(false);
						}
					})
					.show();
			return true;
		}

		// Only available if user is a TA
		// Queue status
		else if (title.equals(getString(R.string.queue_status_options_menu_label))) {

		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Set the options menu for when the user is a TA.
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		menu.add(0, mSettingsMenuItem, 0, getString(R.string.settings_label));

		// Make it so the user can refresh if something goes wrong on the school list fragment
		SchoolListFragment schoolListFragment = (SchoolListFragment)getFragmentManager().findFragmentByTag(school_list_fragment_tag);
		if (schoolListFragment != null && schoolListFragment.isVisible())
			menu.add(0, mRefreshMenuItem, 0, getString(R.string.refresh_label));

		menu.add(0, mMoreInformationMenuItem, 0, getString(R.string.more_information_options_menu_label));

		// Add the TA actions
		if (mUser != null && mUser.getUserType().equals(User.TA)) {
			menu.add(0, mTAOptionsMenuQueueStatus, 0, getString(R.string.queue_status_options_menu_label));
			menu.add(0, mTAOptionsMenuIsQuestionBased, 0, getString(R.string.ta_options_menu_is_question_based_label));
		}
		// Add the Student actions
		if (mUser != null && mUser.getUserType().equals(User.STUDENT)) {
			menu.add(0, mTAOptionsMenuQueueStatus, 0, getString(R.string.chat_label));
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onNetworkTimeout() {
		Toast.makeText(this, getString(R.string.network_timeout_toast), Toast.LENGTH_SHORT).show();

		clearActionBarAndLoadingCircle();

		// Check for login screen
		LoginFragment loginFragment = (LoginFragment)getFragmentManager()
				.findFragmentByTag(login_screen_fragment_tag);

		// Check for student queue screen
		StudentQueueFragment studentFragment = (StudentQueueFragment)getFragmentManager()
				.findFragmentByTag(student_queue_fragment_tag);

		// Check for TA queue screen
		TAQueueFragment taFragment = (TAQueueFragment)getFragmentManager()
				.findFragmentByTag(ta_queue_fragment_tag);

		if (loginFragment != null && loginFragment.isVisible())
			mLastFragmentWasLogin = true;

		else if (studentFragment != null && studentFragment.isVisible())
			mLastFragmentWasStudentQueue = true;

		else if (taFragment != null && taFragment.isVisible())
			mLastFragmentWasTAQueue = true;

		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_layout, mNetworkTestFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	@Override
	public void onNetworkReconnected() {
		Toast.makeText(this, getString(R.string.reconnected_toast), Toast.LENGTH_SHORT).show();

		// Press back if fragment was the login fragment
		if (mLastFragmentWasLogin) {
			onQueueSelected(mSelectedQueue);
			onBackPressed();
		}
		else if (mLastFragmentWasStudentQueue)
			onStudentLoginSuccess((Student)mUser);
		else if (mLastFragmentWasTAQueue)
			onTALoginSuccess((TA)mUser);

		mLastFragmentWasLogin = false;
		mLastFragmentWasStudentQueue = false;
		mLastFragmentWasTAQueue = false;
	}

	/**
	 * Switches the fragment to the list of instructors for the selected school.
	 */
	@Override
	public void onSchoolSelected(School school) {
		mSelectedSchool = school;

		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_layout, mInstructorListFragment, instructor_list_fragment_tag);
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
		transaction.replace(R.id.fragment_layout, mQueueListFragment, queue_list_fragment_tag);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	@Override
	public void onQueueSelected(StudentQueue queue) {
		mSelectedQueue = queue;

		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_layout, mStudentLoginFragment, login_screen_fragment_tag);
		if (!mLoginFragmentAdded)
			transaction.addToBackStack(null);
		transaction.commit();

		mLoginFragmentAdded = true;

		// Load the action bar

		mActionBar.removeAllTabs();

		ActionBar.Tab studentTab = mActionBar.newTab().setText(getString(R.string.student));
		ActionBar.Tab taTab = mActionBar.newTab().setText(getString(R.string.ta));

		studentTab.setTabListener(new LoginTabListener(mStudentLoginFragment));
		taTab.setTabListener(new LoginTabListener(mTALoginFragment));

		mActionBar.addTab(studentTab);
		mActionBar.addTab(taTab);

		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		invalidateOptionsMenu();
	}

	@Override
	public void onBackPressed() {
		// Check to see if the user was on the queue screen, ignoring it if they are
		if (mOnQueueScreen)
			return;

		// TODO: This is probably a bad idea (which means it really is), but it fixes the bug of the
		// TODO: fragments not being added to the back stack correctly.
		mLoginFragmentAdded = false;

		clearActionBarAndLoadingCircle();

		deleteSavedFile();

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
	public void onSchoolsLoaded() {
		readFromFile();
	}

	@Override
	public void onStudentLoginSuccess(Student student) {
		mUser = student;
		mOnQueueScreen = true;

		clearActionBarAndLoadingCircle();

		// Turn on Student ActionBar
		mInitialSelect = true;
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		mActionBar.removeAllTabs();
		ActionBar.Tab enterQueue = mActionBar.newTab().setText(getString(R.string.enter_queue_button_text));
		ActionBar.Tab signOut = mActionBar.newTab().setText(getString(R.string.sign_out_button_text));

		enterQueue.setTabListener(new QueueStudentActionTabListener());
		signOut.setTabListener(new QueueStudentActionTabListener());

		mActionBar.addTab(enterQueue);
		mActionBar.addTab(signOut);
		invalidateOptionsMenu();

		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_layout, mStudentQueueFragment, student_queue_fragment_tag);
		transaction.addToBackStack(null);
		transaction.commit();

	}

	@Override
	public void onTALoginSuccess(TA ta) {
		mUser = ta;
		mOnQueueScreen = true;

		clearActionBarAndLoadingCircle();

		// Turn on TA ActionBar
		mInitialSelect = true;
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		mActionBar.removeAllTabs();
		ActionBar.Tab deactivateQueue = mActionBar.newTab().setText(getString(R.string.deactivate_tab_label));
		ActionBar.Tab freezeQueue = mActionBar.newTab().setText(getString(R.string.freeze_tab_label));
		ActionBar.Tab signOut = mActionBar.newTab().setText(getString(R.string.sign_out_button_text));

		deactivateQueue.setTabListener(new QueueTAActionTabListener());
		freezeQueue.setTabListener(new QueueTAActionTabListener());
		signOut.setTabListener(new QueueTAActionTabListener());

		mActionBar.addTab(deactivateQueue, 0);
		mActionBar.addTab(freezeQueue, 1);
		mActionBar.addTab(signOut, 2);
		invalidateOptionsMenu();

		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_layout, mTAQueueFragment, ta_queue_fragment_tag);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	public static User getUser() {
		return mUser;
	}

	/**
	 * Takes a name/location string and splits it properly to get the name and location out.
	 */
	public static String[] getNameAndLocation(String nameLocation) {
		String[] items = nameLocation.split(" @ ");
		if (items.length == 1)
			items = new String[] {"", ""};
		return items;
	}

	@Override
	public void onSignOut() {
		mUser = null;
		mOnQueueScreen = false;
		//onBackPressed();
		getFragmentManager().popBackStack();
		onQueueSelected(mSelectedQueue);
	}

	@Override
	public void onErrorCodeReceived(int code, JSONArray errors) {
		// Using a set because sometimes the server sends duplicate error messages
		Set<String> messages = new HashSet<String>();

		try {
            if(errors != null) {
                for (int i = 0; i < errors.length(); i++)
                    messages.add(errors.getString(i));
            }
		}
		catch (JSONException e) {
			e.printStackTrace();
		}

		// Check if no error messages came back from the server
		if (messages.isEmpty())
			Toast.makeText(this, getString(R.string.error_from_server_with_no_message), Toast.LENGTH_SHORT).show();
		else
			for (String message : messages)
				Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

		// Clear the loading circle if it was going
		ProgressBar loadingCircle = (ProgressBar)findViewById(R.id.loading_circle);
		loadingCircle.setVisibility(View.GONE);
	}

	private void saveToFile() {
		try {
			JSONObject data = new JSONObject();

			// Save what the user had to say about the opening dialog
			data.put(DO_NOT_SHOW_OPENING_DIALOG, mDoNotShowOpeningDialog);

			// Get the fragments

			// School list screen
			SchoolListFragment schoolListFragment = (SchoolListFragment)getFragmentManager()
					.findFragmentByTag(school_list_fragment_tag);

			// Instructor list screen
			InstructorListFragment instructorListFragment = (InstructorListFragment)getFragmentManager()
					.findFragmentByTag(instructor_list_fragment_tag);

			// Queue list screen
			QueueListFragment queueListFragment = (QueueListFragment)getFragmentManager()
					.findFragmentByTag(queue_list_fragment_tag);

			if (schoolListFragment.isVisible()) {
				// Don't save anything
			}
			else if (instructorListFragment.isVisible()) {
				if (mSelectedSchool != null)
					data.put(SELECTED_SCHOOL, mSelectedSchool.getName());
			}
			else if (queueListFragment.isVisible()) {
				if (mSelectedSchool != null)
					data.put(SELECTED_SCHOOL, mSelectedSchool.getName());
				if (mSelectedInstructor != null)
					data.put(SELECTED_INSTRUCTOR, mSelectedInstructor.getName());
			}
			// Anything else, save everything
			else {
				if (mSelectedSchool != null)
					data.put(SELECTED_SCHOOL, mSelectedSchool.getName());
				if (mSelectedInstructor != null)
					data.put(SELECTED_INSTRUCTOR, mSelectedInstructor.getName());
				if  (mSelectedQueue != null)
					data.put(SELECTED_QUEUE, mSelectedQueue.getClassNumber());
			}

			if (mUser != null) {
				data.put(USER_TYPE, mUser.getUserType());
				data.put(USERNAME, mUser.getUsername());
				data.put(ID, mUser.getId());
				data.put(TOKEN, mUser.getToken());

				if (mUser.getUserType().equals(User.STUDENT))
					data.put(LOCATION, ((Student)mUser).getLocation());
			}

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
		catch (NullPointerException e) {
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

			if (dataJson.has(SELECTED_SCHOOL)) {
				String selectedSchoolName = dataJson.getString(SELECTED_SCHOOL);
				Object school = mSchoolListFragment.getSelectedItem(selectedSchoolName);
				mSchoolListFragment.itemSelectedListener(school);
			}
			if (dataJson.has(SELECTED_INSTRUCTOR)) {
				String selectedInstructorName = dataJson.getString(SELECTED_INSTRUCTOR);
				Object instructor = mInstructorListFragment.getSelectedItem(selectedInstructorName);
				mInstructorListFragment.itemSelectedListener(instructor);
			}
			if (dataJson.has(SELECTED_QUEUE)) {
				String selectedQueueClassNumber = dataJson.getString(SELECTED_QUEUE);
				Object queue = mQueueListFragment.getQueue(selectedQueueClassNumber);
				mQueueListFragment.itemSelectedListener(queue);
			}

			if (dataJson.has(USER_TYPE)) {
				String user_type = dataJson.getString(USER_TYPE);

				String username = dataJson.getString(USERNAME);
				String id = dataJson.getString(ID);
				String token = dataJson.getString(TOKEN);

				if (user_type.equals(User.TA)) {
					mUser = new TA(username, id, token);
					onTALoginSuccess((TA)mUser);
				}
				else {
					String location = dataJson.getString(LOCATION);
					mUser = new Student(username, id, token, location);
					onStudentLoginSuccess((Student)mUser);
				}
			}

			// Clear out file so it doesn't get loaded again unless it gets saved again
			deleteSavedFile();
		}
		catch (FileNotFoundException e) {
			Log.i("LOAD", "Error loading saved file, maybe there was nothing saved.");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void checkShowOpeningDialog() {
		try {
			File file = new File(getFilesDir().getPath() + SAVED_DATA_FILE_NAME);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String data = bufferedReader.readLine();
			bufferedReader.close();

			JSONObject dataJson = new JSONObject(data);

			if (dataJson.has(DO_NOT_SHOW_OPENING_DIALOG))
				mDoNotShowOpeningDialog = dataJson.getBoolean(DO_NOT_SHOW_OPENING_DIALOG);
		}
		catch (FileNotFoundException e) {
			Log.i("LOAD", "Error loading saved file, maybe there was nothing saved.");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void deleteSavedFile() {
		new File(getFilesDir().getPath() + SAVED_DATA_FILE_NAME).delete();
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

	/**
	 * Listener for ActionBar tabs when logged in as a student.
	 */
	private class QueueStudentActionTabListener implements ActionBar.TabListener {

		@Override
		public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
			// Stop bug where the first tab would get selected on loading
			if (mInitialSelect) {
				mInitialSelect = false;
				return;
			}

			tabSelected(tab);
		}

		@Override
		public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) { }

		@Override
		public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
			// Stop bug where the first tab would get selected on loading
			if (mInitialSelect) {
				mInitialSelect = false;
				return;
			}
			tabSelected(tab);
		}

		private void tabSelected(ActionBar.Tab tab) {
			// Enter/exit

			if (tab.getText().equals(getString(R.string.enter_queue_button_text))) {
				mStudentQueueFragment.enterQueue();
				tab.setText(getString(R.string.exit_queue_button_text));
			}
			else if (tab.getText().equals(getString(R.string.exit_queue_button_text))) {
				mStudentQueueFragment.exitQueue();
				tab.setText(getString(R.string.enter_queue_button_text));
			}

			// Sign out

			else {
				mStudentQueueFragment.signOut();
			}
		}
	}

	/**
	 * Listener for ActionBar tabs when logged in as a TA.
	 */
	private class QueueTAActionTabListener implements ActionBar.TabListener {

		@Override
		public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
			// Stop bug where the first tab would get selected on loading
			if (mInitialSelect) {
				mInitialSelect = false;
				return;
			}

			tabSelected(tab);
		}

		@Override
		public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) { }

		@Override
		public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
			// Stop bug where the first tab would get selected on loading
			if (mInitialSelect) {
				mInitialSelect = false;
				return;
			}

			tabSelected(tab);
		}

		private void tabSelected(ActionBar.Tab tab) {
			// Activate/deactivate

			if (tab.getText().equals(getString(R.string.deactivate_tab_label))) {
				mTAQueueFragment.deactivateQueue();
				tab.setText(getString(R.string.activate_tab_label));
			}
			else if (tab.getText().equals(getString(R.string.activate_tab_label))) {
				mTAQueueFragment.activateQueue();
				tab.setText(getString(R.string.deactivate_tab_label));
			}

			// Freeze/unfreeze

			else if (tab.getText().equals(getString(R.string.freeze_tab_label))) {
				mTAQueueFragment.freezeQueue();
				tab.setText(getString(R.string.unfreeze_tab_label));
			}
			else if (tab.getText().equals(getString(R.string.unfreeze_tab_label))) {
				mTAQueueFragment.unfreezeQueue();
				tab.setText(getString(R.string.freeze_tab_label));
			}

			// Sign out

			else {
				mTAQueueFragment.signOut();
			}
		}
	}
}