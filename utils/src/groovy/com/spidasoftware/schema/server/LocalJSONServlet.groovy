package com.spidasoftware.schema.server

import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import nu.xom.ParsingException;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

/**
 * Created with IntelliJ IDEA.
 * User: mford
 * Date: 7/31/12
 * Time: 10:21 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class LocalJSONServlet extends HttpServlet {
	protected static Logger log = Logger.getLogger(LocalJSONServlet.class);

	public static int NO_ERROR = 0;
	public static int INVALID_PARAMETERS = 1;

	public Object get(HttpServletRequest request) throws JSONServletException {
		return null;
	}

	public Object update(HttpServletRequest request) throws JSONServletException {
		return null;
	}

	public Object create(HttpServletRequest request) throws JSONServletException {
		return null;
	}

	public Object delete(HttpServletRequest request) throws JSONServletException {
		return null;
	}

	public JSONObject makeResponseBody(Object result, JSONServletException exception) {
		JSONObject response = new JSONObject();
		if (result != null) {
			response.put("result", result);
		}
		if (exception != null) {
			response.put("error", exception.toJSON());
		}

		return response;
	}

	protected void sendJSONResponse(JSON body, HttpServletResponse response) throws IOException {
		response.setContentType("application/json");
		if (body != null) {
			PrintWriter out = response.getWriter();
			out.print(body.toString());
			out.close();
		}
	}

	protected JSON getJSONFromRequestBody(HttpServletRequest request) throws IOException, ParsingException {
		return JSONSerializer.toJSON(request.getInputStream());
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject json = null;
		Enumeration paramNames = request.getParameterNames();
		while(paramNames.hasMoreElements()) {
			String paramName = (String)paramNames.nextElement();
			String[] paramValues = request.getParameterValues(paramName);
			if (paramValues.length == 1) {
				String paramValue = paramValues[0];
			}
		}
		try {
			Object result = update(request);
			json = makeResponseBody(result, null);
		} catch (JSONServletException e) {
			log.warn(e, e);
			json = makeResponseBody(null, e);
		}
		sendJSONResponse(json, response);


	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject json = null;
		try {
			Object result = get(request);
			json = makeResponseBody(result, null);
		} catch (JSONServletException e) {
			log.warn(e, e);
			json = makeResponseBody(null, e);
		}
		sendJSONResponse(json, response);

	}


	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject json = null;
		try {
			Object result = create(request);
			json = makeResponseBody(result, null);
		} catch (JSONServletException e) {
			log.warn(e, e);
			json = makeResponseBody(null, e);
		}
		sendJSONResponse(json, response);
	}


	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject json = null;
		try {
			Object result = delete(request);
			json = makeResponseBody(result, null);
		} catch (JSONServletException e) {
			log.warn(e, e);
			json = makeResponseBody(null, e);
		}
		sendJSONResponse(json, response);


	}
}
