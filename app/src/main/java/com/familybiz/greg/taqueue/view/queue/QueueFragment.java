package com.familybiz.greg.taqueue.view.queue;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
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
import java.util.Arrays;
import java.util.Comparator;
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
	private LinearLayout.LayoutParams mLayoutParams;
	private FrameLayout mTASection;
	private ListView mList;
	protected ArrayAdapter<String> mAdapter;
	private LayoutInflater mInflater;

	// Network
	private QueueRequest mQueueRequest;
	protected Timer mTimer;

	// Data
	private QueueData mQueue;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Used to get an xml view file and use it multiple times
		mInflater = inflater;
		mTimer = new Timer();

		STUDENTS_BEING_HELPED = new HashMap<String, Set<String>>();

		mNotYetHelpedActionOptions = new CharSequence[] {
				getString(R.string.accept_student_action),
				getString(R.string.remove_student_action)};

		mAlreadyHelpedActionOptions = new CharSequence[] {
				getString(R.string.remove_student_action),
				getString(R.string.put_student_back_action)};

		rootLayout = new LinearLayout(getActivity());
		rootLayout.setOrientation(LinearLayout.VERTICAL);

		mLayoutParams = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);

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

		mTASection = new FrameLayout(getActivity());
		rootLayout.addView(mTASection, mLayoutParams);

		// Queue label

		View queueLabelViewXml = mInflater.inflate(R.layout.label_layout, null);
		TextView queueLabelView = (TextView)queueLabelViewXml.findViewById(R.id.label_layout);
		queueLabelView.setText(getString(R.string.queue_label));
		rootLayout.addView(queueLabelView, new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				getResources().getDimensionPixelSize(R.dimen.label_height)));

		// Queue list

		mAdapter = new ColorableArrayAdapter(getActivity(), R.layout.list_item);
		mList = new ListView(getActivity());
		mList.setAdapter(mAdapter);
		mList.setBackgroundColor(getResources().getColor(R.color.background_color));
		rootLayout.addView(mList, new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				0,
				1
		));

		mList.setOnItemClickListener(this);

		return rootLayout;
	}

	@Override
	public void onQueueInformationReceived(QueueData queue) {
		mQueue = queue;
		populateQueue(queue);
		mTimer.schedule(new RefreshQueue(), 1000);
	}

	private void populateQueue(QueueData queue) {

		// Populate the list of TA's

		mTASection.removeAllViews();
		STUDENTS_BEING_HELPED.clear();

		QueueTA[] taArray = queue.getTAs();
		Arrays.sort(taArray, new Comparator<QueueTA>() {
			@Override
			public int compare(QueueTA ta1, QueueTA ta2) {
				return ta1.getUsername().compareTo(ta2.getUsername());
			}
		});

		for (int i = 0; i < taArray.length; i++) {
			View listItemXml = mInflater.inflate(R.layout.list_item, null);
			TextView taView = (TextView)listItemXml.findViewById(R.id.basic_list_item);
			taView.setBackgroundColor(getResources().getColor(R.color.ta_highlight_color));
			taView.setHeight(getResources().getDimensionPixelSize(R.dimen.list_item_height));
			taView.setPadding(getResources().getDimensionPixelSize(R.dimen.list_item_left_padding), 0, 0, 0);

			QueueTA ta = taArray[i];
			if (ta.getStudent() == null)
				taView.setText(ta.getUsername());
			else {
				taView.setText(ta.getUsername() + " helping " + ta.getStudent().getUsername());

				// Store the student name and location
				String username = ta.getStudent().getUsername();
				String location = ta.getStudent().getLocation();
				if (!STUDENTS_BEING_HELPED.containsKey(username))
					STUDENTS_BEING_HELPED.put(username, new HashSet<String>());
				STUDENTS_BEING_HELPED.get(username).add(location);
			}

			mTASection.addView(taView, mLayoutParams);
		}
		mTASection.invalidate();
		rootLayout.invalidate();

		// Populate the list of students in the queue

		mAdapter.clear();

		List<String> students = new ArrayList<String>();
		QueueStudent[] studentArray = queue.getStudents();
		for (int i = 0; i < studentArray.length; i++) {
			String studentNameLocation = studentArray[i].getUsername() + " @ " + studentArray[i].getLocation();
			if (studentArray[i].isInQueue())
				students.add(studentNameLocation);
		}

		mAdapter.addAll(students);
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
			User user = MainActivity.getUser();
			mQueueRequest.updateQueue(user.getId(), user.getToken());
		}
	}

	public abstract void signOut();

	@Override
	abstract public void onItemClick(AdapterView<?> adapterView, View view, int i, long l);
}
