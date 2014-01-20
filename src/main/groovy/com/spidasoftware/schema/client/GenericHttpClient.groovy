package com.spidasoftware.schema.client

import com.spidasoftware.schema.utils.SSLUtils
import groovy.util.logging.Log4j
import org.apache.http.HttpEntity
import org.apache.http.HttpEntityEnclosingRequest
import org.apache.http.HttpResponse
import org.apache.http.NameValuePair
import org.apache.http.client.HttpClient
import org.apache.http.client.HttpResponseException
import org.apache.http.client.RedirectStrategy
import org.apache.http.client.methods.HttpDelete
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpHead
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpPut
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.client.methods.RequestBuilder
import org.apache.http.client.utils.URIBuilder
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.entity.mime.content.ContentBody
import org.apache.http.entity.mime.content.FileBody
import org.apache.http.entity.mime.content.StringBody
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.message.BasicNameValuePair
import org.apache.http.protocol.BasicHttpContext
import org.apache.http.protocol.HttpContext
import org.apache.http.util.EntityUtils

import java.nio.file.Files

/**
 * Generic http client class for making most of the basic http calls. It is fairly customizable by overriding the
 * Closures that it uses to create and configure the client and cleanup the responses and connections. The
 * default implementation will automatically close the response output stream and release the connections after each call.
 * It will not automatically cleanup the httpClient, however. You must call the shutdown() method when you are done in order
 * avoid leaking connections.
 *
 * <br>
 * By default, the client will not follow any redirects.
 *
 * Created with IntelliJ IDEA.
 * User: pfried
 * Date: 1/15/14
 * Time: 3:12 PM
 * To change this template use File | Settings | File Templates.
 */
@Log4j
class GenericHttpClient implements HttpClientInterface {


	CloseableHttpClient client

	/**
	 * Executes the specified request with the given headers and parameters. After executing the request,
	 * the responseHandler closure is called with the response, an then the response entity is consumed and
	 * the request connection is released. All of this default behavior is defined in closures that can be
	 * easily overridden for a particular instnace without having to define a new subclass.
	 * For a POST or PUT request, all the parameters will be put into a multipart entity, including files.
	 * For any other request type, the parameters will be added to the URI.
	 *
	 * @param httpMethod specify the http method to use: GET, POST, PUT, DELETE, etc.
	 * @param uri base uri to make the request to, parameters will automatically be added if appropriate
	 * @param parameters a map of params that will get added to the request Entity (for POST or PUT), or
	 *      added to the URI (any other method). These will automatically be escaped as needed. may be null
	 * @param headers a map of request headers to use for this request. May be null
	 * @param responseHandler an optional Closure that get's called after the response has been received. The default will
	 *      simply return the response body as a String, or throw an HttpResponseException if the status code is >= 300.
	 *      Note that the default behavior is for GenericHttpClient to call consumeContent() on the entity AFTER this closure
	 *      returns. It will then release the requests connection and then return the result of the responseHandler Closure.
	 * @return
	 */
	def executeRequest(String httpMethod, URI uri, Map<String, Object> parameters, Map<String, String> headers, Closure responseHandler = getBodyAsString) {
		def request = createRequest(httpMethod, uri, parameters, headers)
		logRequest(request)
		return executeHttpRequest(request, responseHandler)

	}

	def createRequest = {String httpMethod, URI uri, Map<String, Object> parameters, Map<String, String> headers ->
		def requestBuilder = RequestBuilder.create(httpMethod.toUpperCase())
		requestBuilder.setUri(uri)
		if (headers && !headers.isEmpty()) {
			headers.each { k, v ->
				requestBuilder.addHeader(k, v)
			}
		}

		if (parameters && !parameters.isEmpty()) {
			boolean containsFile = parameters.values().any { it instanceof File }
			boolean canContainFile = httpMethod.equalsIgnoreCase("POST") || httpMethod.equalsIgnoreCase("PUT")

			// If there are files in the request, we'll have to build the entity ourselves
			if (containsFile && canContainFile) {
				requestBuilder.setEntity(createMultipartEntity(parameters))
			} else {
				parameters.each { k, v ->
					requestBuilder.addParameter(k, v.toString())
				}
			}
		}
		return requestBuilder.build()
	}

	static void logRequest(HttpUriRequest request) {
		log.info("Http Request: method: ${request.getMethod()}, URI: ${request.getURI().toString()}")
		request.getAllHeaders().each {
			log.debug("Request Header: ${it.name}: ${it.value}")
		}

	}

	/**
	 * Closure for creating the HttpClient to be used for executing the requests.
	 * Default is a pretty standard implementation that will follow redirects on GET and HEAD requests,
	 * but not on anything else.
	 * If you create you own client, you may also need to customize the cleanupClient closure.
	 *
	 * NOTE for migrating HttpInterface:
	 * Part of why the HttpInterface class is so complicated is that it has to deal with proxy authentication issues.
	 * We should look into using a custom ProxyAuthenticationStrategy in order to deal with these. Then set it using
	 * clientBuilder.setProxyAuthenticationStrategy(<custom strategy instance>)
	 */
	def createClient = {HttpClientBuilder clientBuilder ->
		clientBuilder.disableRedirectHandling().build()
	}

	/**
	 * call this when you are done!
	 */
	def cleanupClient = {
		if (client.respondsTo("close")) {
			client.close()
		}
	}

	/**
	 * default Closure for getting the body of an HttpResponse as a string. Can be passed into
	 * other methods like:
	 * <br><code>
	 *     def responseString = new GenericHttpClient().get(new URI("http://www.spidamin.com/calcdb/projects"), [name: "projectName"], client.getBodyAsString)
	 * </code>
	 * This will return the response body as a string. If the response status code is >= 300, an HttpResponseException will be thrown.
	 */
	def getBodyAsString = { response ->
		if (response.getStatusLine().getStatusCode() >= 300) {
			log.error("Received error response from server. Code: ${response.getStatusLine().getStatusCode()}, Reason: ${response.getStatusLine().getReasonPhrase()}")
			throw new HttpResponseException(response.getStatusLine().getStatusCode(), response.getStatusLine().reasonPhrase)
		}
		def body = EntityUtils.toString(response.getEntity())
		return body
	}

	/**
	 * Called by client.shutdown()
	 * Closure for cleaning up an Http connection after the request has completed.
	 * Default will release the connection every time.
	 * If you're using a custom ConnectionManager, you may want to set your own cleanupRequest closure
	 * to allow connections to remain open and be reused
	 */
	def cleanupRequest = { request ->
		request.releaseConnection()

	}

	/**
	 *  Closure for performing cleanup operations on an HttpRepsonse.
	 *  Default will just close the inputStream, if it has one.
	 */
	def cleanupResponse = { response ->
		response?.getEntity()?.getContent()?.close()
	}


	def executeHttpRequest(HttpUriRequest request, Closure responseHandler) throws Exception {
		URI uri = request.getURI()
		log.debug("Executing request: ${request.getMethod()} to URI: ${uri.toString()}")

		if (!client) {
			this.client = createClient(HttpClientBuilder.create())
		}

		def response
		def returnValue
		Throwable error
		try {
			HttpContext localContext = new BasicHttpContext()
			response = this.client.execute(request, localContext)
			returnValue = responseHandler(response)

		} catch (Exception e) {
			error = e
			log.error(e, e)
		} finally {
			try {
				if (response) {
					cleanupResponse(response)
				}
				cleanupRequest(request)
			} catch (Exception ex) {
				log.debug(ex, ex)
			}
		}

		if (error) {
			throw error
		}
		return returnValue
	}

	void shutdown(){
		cleanupClient()
	}

	protected static HttpEntity createMultipartEntity(Map<String, Object> params) {
		MultipartEntityBuilder builder = MultipartEntityBuilder.create()
		params?.each { k, v ->
			ContentBody bodyPart
			if (v instanceof File) {
				bodyPart = new FileBody(v, Files.probeContentType(v.toPath()), "UTF-8")
			} else {
				bodyPart = new StringBody(v.toString())
			}
			builder.addPart(k, bodyPart)
		}
		return builder.build()

	}


}
