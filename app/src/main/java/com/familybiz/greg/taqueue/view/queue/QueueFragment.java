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
import com.familybiz.greg.taqueue.network.QueueRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Represents the queue.  ListView from http://jsharkey.org/blog/2008/08/18/separating-lists-with-headers-in-android-09/.
 *
 * Created by Greg Anderson
 */
public abstract class QueueFragment extends Fragment implements QueueRequest.OnQueueInformationReceivedListener, ListView.OnItemClickListener {

	private static Map<String, Set<String>> STUDENTS_BEING_HELPED;

	protected CharSequence[] mNotYetHelpedActionOptions;

	protected CharSequence[] mAlreadyHelpedActionOptions;

	public static boolean beingHelped(String name, String location) {
		if (QueueFragment.STUDENTS_BEING_HELPED.containsKey(name))
			if (QueueFragment.STUDENTS_BEING_HELPED.get(name).contains(location))
				return true;

		// Student not being helped
		return false;
	}

	// View
	private LinearLayout rootLayout;
	private ListView mStudentList;
	private ListView mTAList;
	protected ColorableStudentArrayAdapter mStudentListAdapter;
	protected ColorableTAArrayAdapter mTAListAdapter;
	private LayoutInflater mInflater;

	// Network
	private QueueRequest mQueueRequest;
	protected Timer mTimer;
	protected boolean mReadyToRefresh;

	// Data
	private QueueData mQueue;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Used to get an xml view file and use it multiple times
		mInflater = inflater;
		mTimer = new Timer();
		mReadyToRefresh = true;

		STUDENTS_BEING_HELPED = new HashMap<String, Set<String>>();

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
		TextView taLabelView = (TextView)labelLayoutTA.findViewById(R.id.label_layout);
		taLabelView.setText(getString(R.string.ta_list_label));
		rootLayout.addView(taLabelView, new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				getResources().getDimensionPixelSize(R.dimen.label_height)));

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
		TextView queueLabelView = (TextView)queueLabelViewXml.findViewById(R.id.label_layout);
		queueLabelView.setText(getString(R.string.queue_label));
		rootLayout.addView(queueLabelView, new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				getResources().getDimensionPixelSize(R.dimen.label_height)));

		// Queue list

		mStudentListAdapter = new ColorableStudentArrayAdapter(getActivity(), R.layout.list_item);
		mStudentList = new ListView(getActivity());
		mStudentList.setAdapter(mStudentListAdapter);
		mStudentList.setBackgroundColor(getResources().getColor(R.color.background_color));
		rootLayout.addView(mStudentList, new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				getResources().getDimensionPixelSize(R.dimen.label_height) * 2));

		mStudentList.setOnItemClickListener(this);

		return rootLayout;
	}

	@Override
	public void onQueueInformationReceived(QueueData queue) {
		mQueue = queue;
		populateQueue(queue);
		if (!mReadyToRefresh)
			return;
		mTimer.schedule(new RefreshQueue(), 1000);
	}

	private void populateQueue(QueueData queue) {

		// Populate the list of TA's

		STUDENTS_BEING_HELPED.clear();
		mTAListAdapter.clear();

		QueueTA[] taArray = queue.getTAs();

		List<String> tas = new ArrayList<String>();

		// Add them to the list
		for (int i = 0; i < taArray.length; i++) {
			QueueTA ta = taArray[i];

			if (ta.getStudent() == null)
				tas.add(ta.getUsername());
			else {
				tas.add(ta.getUsername() + " helping " + ta.getStudent().getUsername());

				// Store the student name and location
				String username = ta.getStudent().getUsername();
				String location = ta.getStudent().getLocation();
				if (!STUDENTS_BEING_HELPED.containsKey(username))
					STUDENTS_BEING_HELPED.put(username, new HashSet<String>());
				STUDENTS_BEING_HELPED.get(username).add(location);
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

	public abstract void signOut();

	@Override
	abstract public void onItemClick(AdapterView<?> adapterView, View view, int i, long l);
}
