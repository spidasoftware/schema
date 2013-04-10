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

  def baseURL = "/"
  def service
  def serviceDescriptor

  private static Logger log = Logger.getLogger(LocalServiceServlet.class);

  public LocalServiceServlet(){
  }

  public LocalServiceServlet(String baseURL, URL serviceDescriptorURL, def service){
    this.baseURL = baseURL
    this.serviceDescriptor = serviceDescriptorURL.text
    this.service = service
  }  

  public LocalServiceServlet(String baseURL, String serviceDescriptorResource, def service){
    this.baseURL = baseURL
    def resource = LocalServiceServlet.class.getResourceAsStream(serviceDescriptorResource)
    this.serviceDescriptor = resource.text
    this.service = service
  }

  protected Object doMethod(HttpServletRequest httpServletRequest) throws JSONServletException {
    
    def action = httpServletRequest.requestURI.replace(baseURL.endsWith("/") ? baseURL : baseURL+"/", "")
    def parameterMap = [:]
    for(param in httpServletRequest.getParameterMap().keySet()){
      parameterMap.put param, httpServletRequest.getParameter(param);
    }
    if(!serviceDescriptor){
      throw new JSONServletException("MISSING_RESOURCE", "The specied service descriptor isn't available in the jar.")
      return
    }
    
    if (action == "") {
      return serviceDescriptor 
    }
    def jsonAction = JSONSerializer.toJSON(serviceDescriptor).get(action)
    if(!jsonAction){
      throw new JSONServletException("MISSING_METHOD", "The method ${action} is not defined in this interface.")
      return
    }
    def jsonParams = jsonAction.get("params")
    def methodParamsList = []
    for(param in jsonParams){
      def value = parameterMap.get(param.get("name"))
      if(!value && param.get("required")){
        throw new JSONServletException("MISSING_REQUIRED_PARAM", "The value ${param.get('name')} was not provided, but is required on the $action method.")
        return
      } 
      methodParamsList.add "$value".toString()
    }
    try {
      def response = methodParamsList.size()>0  ? service.invokeMethod(action, methodParamsList.toArray()) : service."$action"() 
      return response
    } catch (IOException e) {
      throw new JSONServletException("INTERNAL_ERROR", "Error in do doMethod. $e")
    }
  }
}
