package com.familybiz.greg.taqueue.view.queue;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the queue.  ListView from http://jsharkey.org/blog/2008/08/18/separating-lists-with-headers-in-android-09/.
 *
 * Created by Greg Anderson
 */
public class QueueFragment extends Fragment implements QueueRequest.OnQueueInformationReceivedListener {

	public final static String ITEM_TITLE = "title";
	public final static String ITEM_CAPTION = "caption";

	private LinearLayout.LayoutParams mLayoutParams;
	private FrameLayout mTASection;
	private ListView mList;
	private ArrayAdapter<String> mAdapter;

	private LayoutInflater mInflater;

	private QueueData mQueue;
	private QueueRequest mQueueRequest;

	public Map<String,?> createItem(String title, String caption) {
		Map<String,String> item = new HashMap<String,String>();
		item.put(ITEM_TITLE, title);
		item.put(ITEM_CAPTION, caption);
		return item;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Used to get an xml view file and use it multiple times
		//inflater = (LayoutInflater)getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
		mInflater = inflater;

		LinearLayout rootLayout = new LinearLayout(getActivity());
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

		return rootLayout;
	}

	@Override
	public void onQueueInformationReceived(QueueData queue) {
		populateQueue(queue);
	}

	private void populateQueue(QueueData queue) {

		// Populate the list of TA's


		mTASection.removeAllViews();

		QueueTA[] taArray = queue.getTAs();
		for (int i = 0; i < taArray.length; i++) {
			View listItemXml = mInflater.inflate(R.layout.list_item, null);
			TextView taView = (TextView)listItemXml.findViewById(R.id.basic_list_item);
			taView.setBackgroundColor(getResources().getColor(R.color.ta_highlight_color));
			taView.setHeight(getResources().getDimensionPixelSize(R.dimen.list_item_height));
			taView.setPadding(getResources().getDimensionPixelSize(R.dimen.list_item_left_padding), 0, 0, 0);

			QueueTA ta = taArray[i];
			if (ta.getStudent() == null)
				taView.setText(ta.getUsername());
			else
				taView.setText(ta.getUsername() + " helping " + ta.getStudent().getUsername());

			mTASection.addView(taView, mLayoutParams);
		}
		mTASection.invalidate();

		// Populate the list of students in the queue

		mAdapter.clear();

		List<String> students = new ArrayList<String>();
		QueueStudent[] studentArray = queue.getStudents();
		for (int i = 0; i < studentArray.length; i++)
			//if (studentArray[i].isInQueue())
			students.add(studentArray[i].getUsername() + " @ " + studentArray[i].getLocation());

		mAdapter.addAll(students);
	}

	/*
	private void populateQueue(QueueData queue) {

		// Get list of TA's
		List<Map<String,?>> tas = new LinkedList<Map<String,?>>();
		QueueTA[] taArray = queue.getTAs();
		for (int i = 0; i < taArray.length; i++) {
			QueueTA ta = taArray[i];
			if (ta.getStudent() == null)
				tas.add(createItem(ta.getUsername(), ""));
			else
				tas.add(createItem(ta.getUsername(), ta.getStudent().getUsername()));
		}

		mAdapter.addSection("TA's", new SimpleAdapter(getActivity(), tas, R.layout.list_complex,
				new String[]{ITEM_TITLE, ITEM_CAPTION}, new int[]{R.id.list_complex_title, R.id.list_complex_caption}));

		// Get students that are in the queue

		List<String> students = new ArrayList<String>();
		QueueStudent[] studentArray = queue.getStudents();
		for (int i = 0; i < studentArray.length; i++)
			//if (studentArray[i].isInQueue())
				students.add(studentArray[i].getUsername() + " @ " + studentArray[i].getLocation());

		mAdapter.addSection("Queue", new ArrayAdapter<String>(getActivity(),
				R.layout.list_item_example, students.toArray(new String[students.size()])));

		mList = new ListView(getActivity());
		mList.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
		mList.invalidateViews();

		mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				// TODO: Implement
			}
		});
	}
	*/
}
