package com.familybiz.greg.taqueue.network;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Template by fdoyle: https://gist.github.com/fdoyle/6424721
 */
public class CustomStringRequest extends StringRequest {

    public static final String PROTOCOL_CHARSET = "utf-8";
    private Map<String, String> headers;
    private String payload;

    public CustomStringRequest(int method, String url, Map<String, String> headers, String payload, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        this.headers = headers;
        this.payload = payload;
    }

    public static CustomStringRequest get(String url, Map<String, String> headers, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        CustomStringRequest request = new CustomStringRequest(Method.GET, url, headers, null, listener, errorListener);
        request.setTag(url);
        return request;
    }

    public static CustomStringRequest post(String url, Map<String, String> headers,  String payload, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        CustomStringRequest request = new CustomStringRequest(Method.POST, url, headers,  payload, listener, errorListener);
        request.setTag(url);
        return request;
    }

    public static CustomStringRequest put(String url, Map<String, String> headers, String payload, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        CustomStringRequest request = new CustomStringRequest(Method.PUT, url, headers, payload, listener, errorListener);
        request.setTag(url);
        return request;
    }

	public static CustomStringRequest delete(String url, Map<String, String> headers, Response.Listener<String> listener, Response.ErrorListener errorListener) {
		CustomStringRequest request = new CustomStringRequest(Method.DELETE, url, headers, null, listener, errorListener);
		request.setTag(url);
		return request;
	}


    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    public byte[] getBody() {
        try {
            if(payload == null) {
                payload = "";
            }
            return payload.getBytes(PROTOCOL_CHARSET);
        }
        catch (UnsupportedEncodingException e) {
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", payload, PROTOCOL_CHARSET);
            return null;
        }
    }

	@Override
    public String getBodyContentType() {
        return "application/json";
    }
}
