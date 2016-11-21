package com.spidasoftware.schema.server

import net.sf.json.JSONObject

/**
 * Exception for servlets to throw when they have an error. Will be communicated back to the calling client.
 */
public class JSONServletException extends Exception {

	public static final String INTERNAL_ERROR = "INTERNAL_ERROR";
	public static final String MISSING_METHOD = "MISSING_METHOD";
	public static final String MISSING_RESOURCE = "MISSING_RESOURCE";
	public static final String METHOD_NOT_ALLOWED = "METHOD_NOT_ALLOWED";
	public static final String BAD_REQUEST = "BAD_REQUEST";
	public static final String FORBIDDEN = "FORBIDDEN";

	protected String code;


	public JSONServletException(String code, String message) {
		super(message);
		this.code = code;
	}
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public JSONObject toJSON() {
		JSONObject error = new JSONObject();
		error.put("code", code);
		error.put("message", this.getMessage());
		return error;
	}

	public static JSONServletException fromJSON(JSONObject json) {
		String code = ((String)json.get("code"))
		String message =(String)json.get("message")

		JSONServletException ex = new JSONServletException(code , message);
		return ex;
	}
}
