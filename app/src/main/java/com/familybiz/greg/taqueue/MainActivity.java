package com.familybiz.greg.taqueue;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.familybiz.greg.taqueue.network.NetworkRequest;
import com.familybiz.greg.taqueue.view.SchoolListFragment;


public class MainActivity extends Activity {

	public static NetworkRequest NETWORK_REQUEST;

	private FrameLayout mSchoolListView;
	private SchoolListFragment mSchoolListFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		NETWORK_REQUEST = new NetworkRequest(this);

		LinearLayout rootLayout = new LinearLayout(this);
		setContentView(rootLayout);

		mSchoolListView = new FrameLayout(this);
		// TODO: Figure out better way to do this ID business
		mSchoolListView.setId(10);

		mSchoolListFragment = new SchoolListFragment();

		rootLayout.addView(mSchoolListView, new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));

		FragmentTransaction addTransaction = getFragmentManager().beginTransaction();
		addTransaction.add(10, mSchoolListFragment);
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
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
		                         Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			return rootView;
		}
	}
}
