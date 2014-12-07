package com.familybiz.greg.taqueue.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Greg Anderson
 */
public class NetworkRequest {

	private String BASE_URL = "http://nine.eng.utah.edu";

	private RequestQueue mQueue;

	public NetworkRequest(Context context) {
		mQueue = Volley.newRequestQueue(context);
	}

	public void executeGetRequest(String url) {
		// TODO: Make the change to using Uri.Builder

		StringRequest stringRequest = new StringRequest(
				Request.Method.GET,
				BASE_URL + url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						parseResponse(response);
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e("Json Parsing", "Error in parsing the json response.");
					}
				}){

			// Set the correct header to prevent getting html back
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String, String> headers = new HashMap<String, String>();
				headers.put("Accept", "application/json");
				return headers;
			}
		};

		mQueue.add(stringRequest);
	}

	public void executePostRequest(String url, JSONObject params) {
		// TODO: Make the change to using Uri.Builder

		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
				BASE_URL + url,
				params,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						parseResponse(response.toString());
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e("Json Parsing", "Error in parsing the json response.");
					}
				}){

			// Set the correct header to prevent getting html back
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String, String> headers = new HashMap<String, String>();
				headers.put("Content-Type", "application/json");
				headers.put("Accept", "application/json");
				return headers;
			}
		};

		mQueue.add(jsonObjectRequest);
	}


	/**
	 * Parses the HTTP response into either a json object or a json array, logs the error if it
	 * fails.  Once parsed, it will trigger all appropriate listeners.
	 */
	private void parseResponse(String response) {
		// Json array
		try {
			new JSONArray(response);
			for (OnJsonArrayReceivedListener listener : mOnJsonArrayReceivedListeners)
				listener.onJsonArrayReceived(response);
		}
		catch (JSONException e) {

			// Json object
			try {
				new JSONObject(response);
				for (OnJsonObjectReceivedListener listener : mOnJsonObjectReceivedListeners)
					listener.onJsonObjectReceived(response);
			}
			catch (JSONException e1) {
				Log.e("Json Parsing", "Error in parsing the json response.");
				e1.printStackTrace();
			}
		}
	}


	/***************************** LISTENERS *****************************/


	// Json object listener

	public interface OnJsonObjectReceivedListener {
		public void onJsonObjectReceived(String jsonObject);
	}
	private Set<OnJsonObjectReceivedListener> mOnJsonObjectReceivedListeners = new HashSet<OnJsonObjectReceivedListener>();
	public void addOnJsonObjectReceivedListener(OnJsonObjectReceivedListener onJsonObjectReceivedListener) {
		mOnJsonObjectReceivedListeners.add(onJsonObjectReceivedListener);
	}
	public void removeOnJsonObjectReceivedListener(OnJsonObjectReceivedListener onJsonObjectReceivedListener) {
		mOnJsonObjectReceivedListeners.remove(onJsonObjectReceivedListener);
	}

	// Json array listener

	public interface OnJsonArrayReceivedListener {
		public void onJsonArrayReceived(String jsonArray);
	}
	private Set<OnJsonArrayReceivedListener> mOnJsonArrayReceivedListeners = new HashSet<OnJsonArrayReceivedListener>();
	public void addOnJsonArrayReceivedListener(OnJsonArrayReceivedListener onJsonArrayReceivedListener) {
		mOnJsonArrayReceivedListeners.add(onJsonArrayReceivedListener);
	}
	public void removeOnJsonArrayReceivedListener(OnJsonArrayReceivedListener onJsonArrayReceivedListener) {
		mOnJsonArrayReceivedListeners.remove(onJsonArrayReceivedListener);
	}
}
