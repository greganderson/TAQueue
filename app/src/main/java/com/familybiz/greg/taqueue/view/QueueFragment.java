package com.familybiz.greg.taqueue.view;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.familybiz.greg.taqueue.R;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Represents the queue.  ListView from http://jsharkey.org/blog/2008/08/18/separating-lists-with-headers-in-android-09/.
 *
 * Created by Greg Anderson
 */
public class QueueFragment extends Fragment {

	public final static String ITEM_TITLE = "title";
	public final static String ITEM_CAPTION = "caption";

	public Map<String,?> createItem(String title, String caption) {
		Map<String,String> item = new HashMap<String,String>();
		item.put(ITEM_TITLE, title);
		item.put(ITEM_CAPTION, caption);
		return item;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// create our list and custom adapter
		QueueListAdapter adapter = new QueueListAdapter(getActivity());

		List<Map<String,?>> tas = new LinkedList<Map<String,?>>();
		tas.add(createItem("Aaron", "Helping Bob"));
		tas.add(createItem("Batman", ""));

		adapter.addSection("TA's", new SimpleAdapter(getActivity(), tas, R.layout.list_complex,
				new String[] { ITEM_TITLE, ITEM_CAPTION }, new int[] { R.id.list_complex_title, R.id.list_complex_caption }));

		adapter.addSection("Queue", new ArrayAdapter<String>(getActivity(),
				R.layout.list_item_example, new String[] { "Bob @ lab1-13", "John @ lab2-7" }));

		ListView list = new ListView(getActivity());
		list.setAdapter(adapter);

		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				// TODO: Implement
			}
		});

		return list;
	}
}

