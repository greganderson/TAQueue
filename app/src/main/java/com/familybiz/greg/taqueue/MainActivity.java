package com.familybiz.greg.taqueue;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.familybiz.greg.taqueue.model.Instructor;
import com.familybiz.greg.taqueue.model.School;
import com.familybiz.greg.taqueue.network.NetworkRequest;
import com.familybiz.greg.taqueue.view.InstructorListFragment;
import com.familybiz.greg.taqueue.view.SchoolListFragment;


public class MainActivity extends Activity implements SchoolListFragment.OnSchoolSelectedListener, InstructorListFragment.OnInstructorSelectedListener {

	public static NetworkRequest NETWORK_REQUEST;

	private SchoolListFragment mSchoolListFragment;
	private InstructorListFragment mInstructorListFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		NETWORK_REQUEST = new NetworkRequest(this);

		setContentView(R.layout.activity_main);

		// Lists

		mSchoolListFragment = new SchoolListFragment();
		mSchoolListFragment.setOnSchoolSelectedListener(this);

		mInstructorListFragment = new InstructorListFragment();
		mInstructorListFragment.setOnInstructorSelectedListener(this);

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

	@Override
	public void onSchoolSelected(School school) {
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_layout, mInstructorListFragment);
		transaction.commit();
	}

	@Override
	public void onInstructorSelected(Instructor instructor) {
		// TODO: Implement
	}
}
