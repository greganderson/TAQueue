package com.familybiz.greg.taqueue.view.queue;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.familybiz.greg.taqueue.MainActivity;
import com.familybiz.greg.taqueue.R;
import com.familybiz.greg.taqueue.model.User;
import com.familybiz.greg.taqueue.model.queue.QueueData;
import com.familybiz.greg.taqueue.model.queue.QueueStudent;
import com.familybiz.greg.taqueue.model.queue.QueueTA;
import com.familybiz.greg.taqueue.network.NetworkRequest;
import com.familybiz.greg.taqueue.network.QueueRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Represents the queue.  ListView from http://jsharkey.org/blog/2008/08/18/separating-lists-with-headers-in-android-09/.
 *
 * Created by Greg Anderson
 */
public abstract class QueueFragment extends Fragment implements
		QueueRequest.OnQueueInformationReceivedListener,
		ListView.OnItemClickListener,
		NetworkRequest.OnDeleteRequestSuccessListener {

	private static List<StudentNameLocationTA> mStudentsBeingHelped;

	protected CharSequence[] mNotYetHelpedActionOptions;

	protected CharSequence[] mAlreadyHelpedActionOptions;

	/**
	 * Takes the name and location of a student and gets the index of the TA helping them if they
	 * are being helped, returns -1 otherwise.
	 */
	public static int indexOfHelpingTA(String name, String location) {
		for (StudentNameLocationTA student : mStudentsBeingHelped)
			if (student.mName.equals(name) && student.mLocation.equals(location))
				return student.mTALocation;

		// Student not being helped
		return -1;
	}

	// View
	private LinearLayout rootLayout;
	private ListView mStudentList;
	private ListView mTAList;
	protected ColorableStudentArrayAdapter mStudentListAdapter;
	protected ColorableTAArrayAdapter mTAListAdapter;
	private LayoutInflater mInflater;
	private TextView mTALabelView;
	private TextView mQueueLabelView;

	// Network
	protected QueueRequest mQueueRequest;
	protected Timer mTimer;
	protected boolean mReadyToRefresh;
	private boolean mInitialQueueRefresh;

	// Data
	protected QueueData mQueue;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Used to get an xml view file and use it multiple times
		mInflater = inflater;
		mTimer = new Timer();
		mReadyToRefresh = true;
		mInitialQueueRefresh = true;

		mStudentsBeingHelped = new ArrayList<StudentNameLocationTA>();

		MainActivity.NETWORK_REQUEST.setOnDeleteRequestSuccessListener(this);

		mNotYetHelpedActionOptions = new CharSequence[] {
				getString(R.string.accept_student_action),
				getString(R.string.remove_student_action)};

		mAlreadyHelpedActionOptions = new CharSequence[] {
				getString(R.string.remove_student_action),
				getString(R.string.put_student_back_action)};

		rootLayout = new LinearLayout(getActivity());
		rootLayout.setOrientation(LinearLayout.VERTICAL);

		mQueueRequest = new QueueRequest();
		mQueueRequest.setOnQueueInformationReceivedListener(this);

		// Load the queue data

		User user = MainActivity.getUser();
		mQueueRequest.updateQueue(user.getId(), user.getToken());

		// TA label

		View labelLayoutTA = mInflater.inflate(R.layout.label_layout, null);
		mTALabelView = (TextView)labelLayoutTA.findViewById(R.id.label_layout);
		mTALabelView.setText(getString(R.string.ta_list_label));
		mTALabelView.setMinHeight(getResources().getDimensionPixelSize(R.dimen.label_height));
		rootLayout.addView(mTALabelView, new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));

		// TA list

		mTAListAdapter = new ColorableTAArrayAdapter(getActivity(), R.layout.list_item);
		mTAList = new ListView(getActivity());
		mTAList.setAdapter(mTAListAdapter);
		mTAList.setBackgroundColor(getResources().getColor(R.color.background_color));
		rootLayout.addView(mTAList, new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));

		// Queue label

		View queueLabelViewXml = mInflater.inflate(R.layout.label_layout, null);
		mQueueLabelView = (TextView)queueLabelViewXml.findViewById(R.id.label_layout);
		mQueueLabelView.setText(getString(R.string.queue_label));
		rootLayout.addView(mQueueLabelView, new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				getResources().getDimensionPixelSize(R.dimen.label_height)));

		// Queue list

		mStudentListAdapter = new ColorableStudentArrayAdapter(getActivity(), R.layout.list_item);
		mStudentList = new ListView(getActivity());
		mStudentList.setAdapter(mStudentListAdapter);
		mStudentList.setBackgroundColor(getResources().getColor(R.color.background_color));
		rootLayout.addView(mStudentList, new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));

		mStudentList.setOnItemClickListener(this);

		return rootLayout;
	}

	@Override
	public void onStop() {
		mTimer.cancel();
		mReadyToRefresh = false;
		mInitialQueueRefresh = true;
		MainActivity.NETWORK_REQUEST.setOnDeleteRequestSuccessListener(null);
		super.onStop();
	}

	@Override
	public void onQueueInformationReceived(QueueData queue) {
		// Check to make sure the fragment has been added to the activity
		if (!isAdded())
			return;

		mQueue = queue;
		populateQueue(queue);
		checkQueueSettings();

		if (mInitialQueueRefresh) {
			updateTabs();
			mInitialQueueRefresh = false;
		}

		if (!mReadyToRefresh)
			return;
		mTimer.schedule(new RefreshQueue(), 1000);
	}

	public QueueData getQueue() {
		return mQueue;
	}

	protected void checkQueueSettings() {
		// Update status
		mTALabelView.setText(getString(R.string.ta_list_label) + ": " + mQueue.getStatus());

		// Active/frozen

		if (!mQueue.isActive()) {
			// Not active, disable entering queue and set background
			mQueueLabelView.setBackgroundColor(getResources().getColor(R.color.queue_deactivated_background));
			mQueueLabelView.setText(getString(R.string.queue_deactivated_label));
		}
		else if (mQueue.isFrozen()) {
			// Frozen, disable entering queue and set background
			mQueueLabelView.setBackgroundColor(getResources().getColor(R.color.queue_frozen_background));
			mQueueLabelView.setText(getString(R.string.queue_frozen_label));
		}
		else {
			// Must be good to go!
			mQueueLabelView.setBackgroundColor(getResources().getColor(R.color.background_color));
			mQueueLabelView.setText(getString(R.string.queue_label));
		}


	}

	private void populateQueue(QueueData queue) {

		// Populate the list of TA's

		mStudentsBeingHelped.clear();
		mTAListAdapter.clear();

		QueueTA[] taArray = queue.getTAs();

		List<String> tas = new ArrayList<String>();

		// Add them to the list
		for (int i = 0; i < taArray.length; i++) {
			QueueTA ta = taArray[i];

			if (ta.getStudent() == null)
				tas.add(ta.getUsername());
			else {
				QueueStudent student = ta.getStudent();
				tas.add(ta.getUsername() + " helping " + student.getUsername());

				mStudentsBeingHelped.add(new StudentNameLocationTA(student.getUsername(), student.getLocation(), i, ta.getId()));
			}
		}

		// Check if there are no TA's on duty
		if (tas.isEmpty())
			mTAListAdapter.add(getString(R.string.no_tas_on_duty));
		else
			mTAListAdapter.addAll(tas);


		// Populate the list of students in the queue

		mStudentListAdapter.clear();

		QueueStudent[] studentArray = queue.getStudents();

		// Check if there are no students in the queue

		List<String> studentsInQueue = new ArrayList<String>();

		for (int i = 0; i < studentArray.length; i++) {
			if (studentArray[i].isInQueue()) {
				String studentNameLocation = studentArray[i].getUsername() + " @ " + studentArray[i].getLocation();
				studentsInQueue.add(studentNameLocation);
			}
		}

		if (studentsInQueue.isEmpty())
			mStudentListAdapter.add(getString(R.string.no_students_in_the_queue));
		else
			mStudentListAdapter.addAll(studentsInQueue);
	}

	protected QueueStudent getStudent(String name, String location) {
		QueueStudent[] students = mQueue.getStudents();
		for (int i = 0; i < students.length; i++) {
			boolean sameName = students[i].getUsername().equals(name);
			boolean sameLocation = students[i].getLocation().equals(location);
			if (sameName && sameLocation)
				return students[i];
		}
		return null;
	}

	private class RefreshQueue extends TimerTask {
		@Override
		public void run() {
			if (!mReadyToRefresh)
				return;
			User user = MainActivity.getUser();
			mQueueRequest.updateQueue(user.getId(), user.getToken());
		}
	}

	@Override
	public void onDeleteRequestSuccess() {
		// This should be redundant, but I was having some issues with it not working before
		mReadyToRefresh = false;
		mTimer.cancel();
		if (mOnSignOutListener != null)
			mOnSignOutListener.onSignOut();
	}

	/**
	 * Helper class for finding if a student is being helped, and which TA is helping.  Used for
	 * getting the right background color.
	 */
	private class StudentNameLocationTA {

		public String mName;
		public String mLocation;
		public int mTALocation;
		public String mTAId;

		public StudentNameLocationTA(String name, String location, int taLocation, String taId) {
			mName = name;
			mLocation = location;
			mTALocation = taLocation;
			mTAId = taId;
		}
	}

	public abstract void signOut();

	@Override
	abstract public void onItemClick(AdapterView<?> adapterView, View view, int i, long l);

	abstract void updateTabs();


	/***************************** LISTENERS *****************************/


	// Sign out

	public interface OnSignOutListener {
		public void onSignOut();
	}

	private OnSignOutListener mOnSignOutListener;

	public void setOnSignOutListener(OnSignOutListener onSignOutListener) {
		mOnSignOutListener = onSignOutListener;
	}
}
