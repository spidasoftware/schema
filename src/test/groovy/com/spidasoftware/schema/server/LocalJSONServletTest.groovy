package com.spidasoftware.schema.server

import groovy.json.*
import org.apache.http.HttpResponse
import org.apache.http.NameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import groovy.mock.interceptor.*


class LocalJSONServletTest extends GroovyTestCase {
	

	public void testAddServlet() throws Exception {
		LocalServer localServer = new LocalServer();
		LocalServiceServlet localServiceServlet = new LocalServiceServlet("/clientData", "/v1/schema/spidacalc/client/interfaces/client_data.json", [:]);
		localServer.addServlet(localServiceServlet);
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
		
