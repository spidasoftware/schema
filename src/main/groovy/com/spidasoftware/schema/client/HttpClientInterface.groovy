package com.spidasoftware.schema.client

/**
 * Created with IntelliJ IDEA.
 * User: pfried
 * Date: 1/17/14
 * Time: 12:38 PM
 */
public interface HttpClientInterface {

	def executeRequest(Request request)

	void shutdown()

}