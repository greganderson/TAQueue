package com.familybiz.greg.taqueue;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MoreInformationActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more_information_activity);

		Button androidGithub = (Button)findViewById(R.id.android_github);
		Button webServiceGithub = (Button)findViewById(R.id.web_service_github);
		Button emailGreg = (Button)findViewById(R.id.email_greg);
		Button emailParker = (Button)findViewById(R.id.email_parker);
		Button webClient = (Button)findViewById(R.id.go_to_web_client);


		// Android Github
		androidGithub.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.addCategory(Intent.CATEGORY_BROWSABLE);
				intent.setData(Uri.parse(getString(R.string.android_github_url)));
				startActivity(intent);
			}
		});

		// Web Service Github
		webServiceGithub.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.addCategory(Intent.CATEGORY_BROWSABLE);
				intent.setData(Uri.parse(getString(R.string.web_service_github_url)));
				startActivity(intent);
			}
		});

		// Email Greg
		emailGreg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("mailto:greg.anderson.cs@gmail.com"));
				startActivity(intent);
			}
		});

		// Email Parker
		emailParker.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("mailto:uofu.ta.queue@gmail.com"));
				startActivity(intent);
			}
		});

		// Go to web client
		webClient.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.addCategory(Intent.CATEGORY_BROWSABLE);
				intent.setData(Uri.parse(getString(R.string.web_client_url)));
				startActivity(intent);
			}
		});
	}
}
