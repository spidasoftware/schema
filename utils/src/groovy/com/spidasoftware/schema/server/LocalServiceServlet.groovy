package com.spidasoftware.schema.server

import Acme.Serve.Serve
import Acme.Serve.Serve.PathTreeDictionary
import java.io.File
import org.apache.log4j.Logger

import javax.servlet.Servlet
import java.util.Properties
import net.sf.json.*
import javax.servlet.http.HttpServletRequest

//import org.eclipse.jetty.server.Server;
//import org.eclipse.jetty.servlet.ServletContextHandler;
//import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Wraps a simple embedded web server
 * To add one to your project, you should usually put it in its own thread:
 * LocalServer localServer = new LocalServer();
 * // add servlets
 * // add file paths
 * new Thread( localServer).start();
 * @author mford
 */
public class LocalServiceServlet extends LocalJSONProcedure {

  def url = "/"
  def service
  def serviceDescriptor

  private static Logger log = Logger.getLogger(LocalServiceServlet.class);

  public LocalServiceServlet(){
  }

  public LocalServiceServlet(url, serviceDescriptor, service){
    this.url = url
    this.serviceDescriptor = serviceDescriptor
    this.service = service
  }

  protected Object doMethod(HttpServletRequest httpServletRequest) throws JSONServletException {
    
    def action = httpServletRequest.requestURI.replace(url.endsWith("/") ? url : url+"/", "")
    def parameterMap = [:]
    for(param in httpServletRequest.getParameterMap().keySet()){
      parameterMap.put param, httpServletRequest.getParameter(param);
    }
    def urlToDescriptor = getClass().getClassLoader().getResource(serviceDescriptor)
    def serviceDescriptorJSON = JSONSerializer.toJSON(urlToDescriptor.text)
    def jsonParams = serviceDescriptorJSON.get(action).get("params")
    def methodParamsList = []
    for(param in jsonParams){
      def value = parameterMap.get(param.get("name"))
      if(!value && param.get("required")){
        throw new JSONServletException("MISSING_REQUIRED_PARAM", "The value ${param.get('name')} was not provided, but is required on the $action method.")
        return
      } 
      methodParamsList.add value
    }
    try {
      def response = methodParamsList.size()>0  ? service."$action"( methodParamsList.toArray().flatten() ) : service."$action"() 
      return response
    } catch (IOException e) {
      throw new JSONServletException("INTERNAL_ERROR", "Error in do doMethod. $e")
    }
  }
}
