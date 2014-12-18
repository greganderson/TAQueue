package com.familybiz.greg.taqueue.network;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Greg Anderson
 */
public class NetworkRequest {

	// TODO: Consider changing all StringRequests to NetworkResponseRequest.

	private int TIMEOUT_TIME = 2000;

	private String BASE_URL = "http://nine.eng.utah.edu";

	private RequestQueue mQueue;

	public NetworkRequest(Context context) {
		mQueue = Volley.newRequestQueue(context);
	}

	/**
	 * Creates a new url using the BASE_URL and the given url: BASE_URL + url.
	 */
	public void executeGetRequest(String url) {
		executeGetRequest(url, "", "");
	}

	/**
	 * Creates a new url using the BASE_URL and the given url: BASE_URL + url.  Adds the username (id)
	 * and password (token) as basic authorization to the header.
	 */
	public void executeGetRequest(String url, final String id, String token) {
		// TODO: Make the change to using Uri.Builder

		Map<String, String> headers = new HashMap<String, String>();
		encodeHeader(headers, id, token);

		CustomStringRequest getRequest = CustomStringRequest.get(
				BASE_URL + url,
				headers,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						parseResponse(response);
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						parseError(error.networkResponse);
					}
				});

		getRequest.setRetryPolicy(new DefaultRetryPolicy(
				TIMEOUT_TIME,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

		mQueue.add(getRequest);
	}

	public void executePostRequest(String url, JSONObject params) {
		executePostRequest(url, params, "", "");
	}

	/**
	 * Wrapper for the POST request.
	 */
	public void executePostRequest(String url, JSONObject params, String id, String token) {
		// TODO: Make the change to using Uri.Builder

		Map<String, String> headers = new HashMap<String, String>();
		encodeHeader(headers, id, token);

		CustomStringRequest postRequest = CustomStringRequest.post(
				BASE_URL + url,
				headers,
				params.toString(),
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						parseResponse(response);
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						parseError(error.networkResponse);
					}
				});

		postRequest.setRetryPolicy(new DefaultRetryPolicy(
				TIMEOUT_TIME,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

		mQueue.add(postRequest);
	}

	/**
	 * Wrapper for the PUT request.
	 */
	public void executePutRequest(String url, JSONObject params, String id, String token) {
		// TODO: Make the change to using Uri.Builder

		Map<String, String> headers = new HashMap<String, String>();
		encodeHeader(headers, id, token);

		CustomStringRequest putRequest = CustomStringRequest.put(
				BASE_URL + url,
				headers,
				params.toString(),
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						parseResponse(response);
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						parseError(error.networkResponse);
					}
				});

		putRequest.setRetryPolicy(new DefaultRetryPolicy(
				TIMEOUT_TIME,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

		mQueue.add(putRequest);
	}

	/**
	 * Wrapper for the DELETE request.
	 */
	public void executeDeleteRequest(String url, String id, String token) {
		// TODO: Make the change to using Uri.Builder

		Map<String, String> headers = new HashMap<String, String>();
		encodeHeader(headers, id, token);

		CustomStringRequest deleteRequest = CustomStringRequest.delete(
				BASE_URL + url,
				headers,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						if (mOnDeleteRequestSuccessListener != null)
							mOnDeleteRequestSuccessListener.onDeleteRequestSuccess();
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						parseError(error.networkResponse);
					}
				});

		deleteRequest.setRetryPolicy(new DefaultRetryPolicy(
				TIMEOUT_TIME,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

		mQueue.add(deleteRequest);
	}

	private void encodeHeader(Map<String, String> headers, String username, String password) {
		headers.put("Accept", "application/json");

		if (!username.isEmpty()) {
			String code = Base64.encodeToString((username + ":" + password).getBytes(), Base64.DEFAULT);
			code = code.replaceAll("\n", "");
			code = "Basic " + code;
			headers.put("Authorization", code);
		}
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

	private void parseError(NetworkResponse response) {

		// Null if no internet connection
		if (response == null) {
			if (mOnNetworkTimeoutListener != null)
				mOnNetworkTimeoutListener.onNetworkTimeout();
			return;
		}

		String message = "";
		JSONArray errors = null;
		try {
			message = new String(response.data, CustomStringRequest.PROTOCOL_CHARSET);
			JSONObject jsonMessage = new JSONObject(message);
			errors = jsonMessage.getJSONArray("errors");
		}
		catch (UnsupportedEncodingException e) {
			Log.e("Parse Data", "Error parsing byte array from response.");
			e.printStackTrace();
		}
		catch (JSONException e) {
			e.printStackTrace();
		}

		if (response.statusCode == 401) {
			// (Unauthorized) meaning you probably did not send the right user_id/token (or none at all)
		}
		else if (response.statusCode == 422) {
			// (Unprocessable Entity) meaning you sent invalid data associated with the resource,
			// like forgetting a username when logging in.
		}
		else if (response.statusCode == 403) {
			// (Forbidden) You sent correct user_id/token but attempted to do something you can't
			// (like enter the queue when it's deactivated)
		}
		else if (response.statusCode == 500) {
			// (Server Error) meaning the server balked. Please report this if it happens
		}
		else {
			// Something else, need to figure out what this status code means.
			Log.e("REQUEST", "Unknown status code: " + response.statusCode);
		}

		if (mOnErrorCodeReceivedListeners != null)
			mOnErrorCodeReceivedListeners.onErrorCodeReceived(response.statusCode, errors);
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

	// Delete request

	public interface OnDeleteRequestSuccessListener {
		public void onDeleteRequestSuccess();
	}

	private OnDeleteRequestSuccessListener mOnDeleteRequestSuccessListener;

	public void setOnDeleteRequestSuccessListener(OnDeleteRequestSuccessListener onDeleteRequestSuccessListener) {
		mOnDeleteRequestSuccessListener = onDeleteRequestSuccessListener;
	}


	/***************************** ERROR LISTENERS *****************************/


	// General error

	public interface OnErrorCodeReceivedListener {
		public void onErrorCodeReceived(int code, JSONArray errors);
	}

	private OnErrorCodeReceivedListener mOnErrorCodeReceivedListeners;

	public void setOnErrorCodeReceivedListeners(OnErrorCodeReceivedListener onErrorCodeReceivedListeners) {
		mOnErrorCodeReceivedListeners = onErrorCodeReceivedListeners;
	}

	// Network timeout

	public interface OnNetworkTimeoutListener {
		public void onNetworkTimeout();
	}

	private OnNetworkTimeoutListener mOnNetworkTimeoutListener;

	public void setOnNetworkTimeoutListener(OnNetworkTimeoutListener onNetworkTimeoutListener) {
		mOnNetworkTimeoutListener = onNetworkTimeoutListener;
	}
}
