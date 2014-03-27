package com.spidasoftware.schema.server

import groovy.mock.interceptor.MockFor

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest

class LocalJSONServletTest extends GroovyTestCase {
	

	public void testAddServlet() throws Exception {
		LocalServer localServer = new LocalServer();
		LocalServiceServlet localServiceServlet = new LocalServiceServlet("/clientData", "/v1/schema/spidacalc/client/interfaces/client_data.json", [:]);
		localServer.addServlet(localServiceServlet);
		LocalServiceServlet otherLocalServiceServlet = new LocalServiceServlet("/calc", "/v1/schema/spidacalc/calc/interfaces/calc.json", [:])
		localServer.addServlet(otherLocalServiceServlet);
		Thread serverThread = new Thread(localServer);
		serverThread.start();

		int timeout = 0;
		Thread.currentThread().sleep(1000);

		boolean responded = false;
		while (timeout < 15 && !responded){
			timeout++;
			try {
				"http://localhost:3491/clientData/poles".toURL().text
				responded = true
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
		assertTrue "Make sure that the clientData servlet responded", responded
		responded = false
		timeout = 0
		while (timeout < 15 && !responded){
			timeout++;
			try {
				"http://localhost:3491/calc/openProject".toURL().text
				responded = true
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
		assertTrue "Make sure that the calc servlet responded", responded
	}

	public void testLocality() throws Exception {	
		def mockRequest = new MockFor(HttpServletRequest)
		mockRequest.demand.getRemoteAddr(1..100) { "10.0.2.4" } 
		
		
		LocalJSONServlet servlet = new LocalJSONServlet()
		
		try {
			servlet.doPut(mockRequest.proxyInstance(), null)
			assertFalse("This request should have failed", true)
		} catch (ServletException se) {
			assertTrue(se.toString().contains("Local Services are restricted to localhost"))
		}
		
		try {
			servlet.doGet(mockRequest.proxyInstance(), null)
			assertFalse("This request should have failed", true)
		} catch (ServletException se) {
			assertTrue(se.toString().contains("Local Services are restricted to localhost"))
		}
		
		try {
			servlet.doDelete(mockRequest.proxyInstance(), null)
			assertFalse("This request should have failed", true)
		} catch (ServletException se) {
			assertTrue(se.toString().contains("Local Services are restricted to localhost"))
		}
		
		try {
			servlet.doPost(mockRequest.proxyInstance(), null)
			assertFalse("This request should have failed", true)
		} catch (ServletException se) {
			assertTrue(se.toString().contains("Local Services are restricted to localhost"))
		}
	}
	
}
		
