package com.spidasoftware.schema.server

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: mford
 * Date: 9/19/12
 * Time: 9:36 PM
 */
public abstract class LocalJSONProcedure extends LocalJSONServlet {
	@Override
	public final Object get(HttpServletRequest request) throws JSONServletException {
//		throw new JSONServletException(403, "Get method not supported on RPC connection. Use POST.");
		return doMethod(request);
	}

	@Override
	public final Object update(HttpServletRequest request) throws JSONServletException {
		 return doMethod(request);
	}

	@Override
	public final Object create(HttpServletRequest request) throws JSONServletException {
		throw new JSONServletException(403, "Get method not supported on RPC connection. Use POST.");
	}

	@Override
	public final Object delete(HttpServletRequest request) throws JSONServletException {
		throw new JSONServletException(403, "Get method not supported on RPC connection. Use POST.");
	}

	protected abstract Object doMethod(HttpServletRequest request) throws JSONServletException;


}
