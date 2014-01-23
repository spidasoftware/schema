package com.spidasoftware.schema.client

/**
 * Created with IntelliJ IDEA.
 * User: pfried
 * Date: 1/17/14
 * Time: 12:38 PM
 * To change this template use File | Settings | File Templates.
 */
public interface HttpClientInterface {

	def executeRequest(String httpMethod, URI uri, Map<String, Object> parameters, Map<String, String> headers, Closure responseHandler)

	void shutdown()

}