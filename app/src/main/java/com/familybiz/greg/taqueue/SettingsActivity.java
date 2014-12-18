package com.familybiz.greg.taqueue;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.familybiz.greg.taqueue.network.NetworkRequest;

/**
 * Created by Greg Anderson
 */
public class SettingsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_activity);

		TextView baseUrlTextView = (TextView)findViewById(R.id.basic_list_item);
		baseUrlTextView.setText(getString(R.string.base_url_settings_label));

		final EditText baseUrlEditText = new EditText(this);

		// Set the maximum number of characters allowed
		int maxLength = 125;
		baseUrlEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
		baseUrlEditText.setHint("e.g. http://nine.eng.utah.edu");
		baseUrlEditText.setText(NetworkRequest.BASE_URL);
		baseUrlEditText.setInputType(InputType.TYPE_TEXT_VARIATION_URI);

		baseUrlTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				new AlertDialog.Builder(SettingsActivity.this)
						.setTitle(getString(R.string.base_url_popup_title))
						.setView(baseUrlEditText)
						.setCancelable(true)
						.setPositiveButton(getString(R.string.save_label), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								String url = baseUrlEditText.getText().toString();

								// Strip trailing slash if it is there
								if (url.charAt(url.length()-1) == '/')
									url = url.substring(0, url.length()-1);

								NetworkRequest.BASE_URL = url;
							}
						})
						.setNegativeButton(getString(R.string.cancel_label), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								// TODO: Implement
							}
						})
						.show();
			}
		});
	}
}
