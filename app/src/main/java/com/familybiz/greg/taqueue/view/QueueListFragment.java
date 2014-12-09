package com.familybiz.greg.taqueue.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.familybiz.greg.taqueue.MainActivity;
import com.familybiz.greg.taqueue.model.StudentQueue;

/**
 * Created by Greg Anderson
 */
public class QueueListFragment extends ListFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Populate the list of instructors.
		mArrayAdapter.clear();
		StudentQueue[] queues = MainActivity.getSelectedInstructor().getQueues();
		String[] classNumbers = new String[queues.length];
		for (int i = 0; i < classNumbers.length; i++)
			classNumbers[i] = queues[i].getClassNumber() + " - " + queues[i].getTitle();

		mArrayAdapter.addAll(classNumbers);

		return super.onCreateView(inflater, container, savedInstanceState);
	}


	/**
	 * Find the queue with the given name.  Returns null if it doesn't exist.
	 */
	@Override
	Object getSelectedItem(String name) {
		// TODO: Figure out a better way to do this, it just seems a little fragile.
		// Extract the class number
		String classNumber = name.substring(0, name.indexOf('-') - 1);

		for (StudentQueue queue : MainActivity.getSelectedInstructor().getQueues())
			if (queue.getClassNumber().equals(classNumber))
				return queue;
		return null;
	}

	@Override
	void itemSelectedListener(Object queue) {
		if (mOnQueueSelectedListener != null)
			mOnQueueSelectedListener.onQueueSelected((StudentQueue)queue);
	}


	/***************************** LISTENERS *****************************/


	// Queue selected

	public interface OnQueueSelectedListener {
		public void onQueueSelected(StudentQueue queue);
	}

	private OnQueueSelectedListener mOnQueueSelectedListener;

	public void setOnQueueSelectedListener(OnQueueSelectedListener onQueueSelectedListener) {
		mOnQueueSelectedListener = onQueueSelectedListener;
	}
}
