package com.spidasoftware.schema.server

import nu.xom.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created with IntelliJ IDEA.
 * User: mford
 * Date: 7/25/12
 * Time: 3:19 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class LocalXMLServlet extends HttpServlet {

	public  Element get(HttpServletRequest request) {
		return null;
	}
	public  Element update(HttpServletRequest request) {
		return null;
	}
	public  Element create(HttpServletRequest request) {
		return null;
	}
	public  Element delete(HttpServletRequest request) {
		return null;
	}


	protected void sendXmlResponse(Element body, HttpServletResponse response) throws IOException{
		response.setContentType("text/xml");
		if (body != null) {
			PrintWriter out = response.getWriter();
			out.print(body.toXML());
			out.close();
		}
	}

	protected Element getElementFromRequestBody(HttpServletRequest request) throws IOException, ParsingException {
		Builder builder = new Builder();
		Document document = builder.build(request.getInputStream());
		return document.getRootElement();
	}
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Element element = update(request);
		sendXmlResponse(element, response);

	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Element element = get(request);
		sendXmlResponse(element, response);

	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Element element = create(request);
		sendXmlResponse(element, response);
	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Element element = delete(request);
		sendXmlResponse(element, response);
	}
}
