package com.spidasoftware.schema.server

import groovy.json.*
import org.apache.http.HttpResponse
import org.apache.http.NameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair

class LocalServerTest extends GroovyTestCase { 

  void testServer(){
      def server = new LocalServer(7890);
      def service = ["analyzeWire": {one, two, three -> return "{'analysis':true}"}]
      server.addServlet(new LocalServiceServlet("/local/path", "/calc/analysis/wire_analysis_service.json", service))
      new Thread(server).start();
      def response = "http://localhost:7890/local/path/analyzeWire?client_wire={'wire':'client'}".toURL().text
      assert response.contains("MISSING_REQUIRED_PARAM")
      response = "http://localhost:7890/local/path/noMethod?client_wire={'wire':'client'}&wire_length={'wire':'length'}&load_case={'wire':'case'}".toURL().text
      assert response.contains('MISSING_METHOD')
      response = "http://localhost:7890/local/path/analyzeWire?client_wire={'wire':'client'}&wire_length={'wire':'length'}&load_case={'wire':'case'}".toURL().text
      assert response.contains('{"analysis":true}')
      //TEST POST
      DefaultHttpClient client = new DefaultHttpClient();
      HttpPost post = new HttpPost("http://localhost:7890/local/path/analyzeWire");
      post.addHeader("Content-Type", "application/x-www-form-urlencoded");
      ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
      parameters.add(new BasicNameValuePair("client_wire", "value"));
      parameters.add(new BasicNameValuePair("wire_length", "value"));
      parameters.add(new BasicNameValuePair("load_case", "value"));
      post.setEntity(new UrlEncodedFormEntity(parameters));
      response = client.execute(post)
      println response
  }

}