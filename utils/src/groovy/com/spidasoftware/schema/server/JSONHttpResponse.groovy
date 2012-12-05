package com.spidasoftware.schema.server

import net.sf.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: mford
 * Date: 9/20/12
 * Time: 10:35 AM
 * To change this template use File | Settings | File Templates.
 */
public class JSONHttpResponse {

	private Object result;
	private JSONServletException error;

	public static JSONHttpResponse fromJSON(JSONObject json) {
		Object result = json.get("result");
		JSONServletException error = null;
		if (json.containsKey("error")) {
			error = JSONServletException.fromJSON((JSONObject)json.get("error"));
		}
		return new JSONHttpResponse(result, error);
	}

	public JSONObject toJSON() {
		JSONObject response = new JSONObject();

		 if (result != null) {
			 response.put("result", result);
		 }
		if (error != null) {
			response.put("error", error.toJSON());
		}
		return response;
	}

	public JSONHttpResponse(Object result, JSONServletException error) {
		this.result = result;
		this.error = error;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public JSONServletException getError() {
		return error;
	}

	public void setError(JSONServletException error) {
		this.error = error;
	}

	public String toString() {
		return toJSON().toString(2);
	}
}
