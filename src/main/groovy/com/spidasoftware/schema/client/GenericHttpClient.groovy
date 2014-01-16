package com.spidasoftware.schema.client

import com.spidasoftware.schema.utils.SSLUtils
import groovy.util.logging.Log4j
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.NameValuePair
import org.apache.http.client.HttpClient
import org.apache.http.client.HttpResponseException
import org.apache.http.client.methods.HttpDelete
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpHead
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpPut
import org.apache.http.client.methods.HttpRequestBase
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
 * Generic http client class for making most of the basic http calls. If you need to
 * do anything fancy, you may set a custom HttpClient, though it likely won't be necessary..
 * <br>
 * By default, the client will follow redirects.
 *
 * Created with IntelliJ IDEA.
 * User: pfried
 * Date: 1/15/14
 * Time: 3:12 PM
 * To change this template use File | Settings | File Templates.
 */
@Log4j
class GenericHttpClient {

    /*
     *The supported http methods are here for convenience
     */
    public static String POST = "post"
    public static String GET = "get"
    public static String DELETE = "delete"
    public static String PUT = "put"
    public static String HEAD = "head"


    protected CloseableHttpClient client

    /**
     * Closure for creating the HttpClient to be used for executing the requests.
     * Default is a pretty standard implementation that will follow redirects on GET and HEAD requests,
     * but not on anything else.
     * If you create you own client, you may also need to customize the cleanupClient closure.
     */
    def createClient = {clientBuilder ->
        clientBuilder.build()
    }

    /**
     *
     */
    def cleanupClient = {client ->
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
    def getBodyAsString = {response ->
        if (response.getStatusLine().getStatusCode() >= 300) {
            log.error("Received error response from server. Code: ${response.getStatusLine().getStatusCode()}, Reason: ${response.getStatusLine().getReasonPhrase()}")
            throw new HttpResponseException(response.getStatusLine().getStatusCode(), response.getStatusLine().reasonPhrase)
        }
        def body = EntityUtils.toString(response.getEntity())
        return body
    }

    /**
     * Closure for cleaning up an Http connection after the request has completed.
     * Default will release the connection every time.
     * If you're using a custom ConnectionManager, you may want to set your own cleanupRequest closure
     * to allow connections to remain open and be reused
     */
    def cleanupRequest = {request ->
        request.releaseConnection()

    }

    /**
     *  Closure for performing cleanup operations on an HttpRepsonse.
     *  Default will just close the inputStream, if it has one.
     */
    def cleanupResponse = {response ->
        response?.getEntity()?.getContent()?.close()
    }

    /**
     * Called when the protocol is https.
     */
    def setupSSL = {request ->
        SSLUtils.setupSSL(request.getURI().toURL())
    }

    def get(URI uri, Map<String, Object> parameters, Closure responseHandler = getBodyAsString){
        URIBuilder builder = new URIBuilder(uri)
        if (parameters && !parameters.isEmpty()) {
            builder.setParameters(createBasicNameValuePairs(parameters))
        }
        HttpGet httpGet = new HttpGet(builder.build())

        return executeHttpRequest(httpGet, responseHandler)
    }

    def put(URI uri, Map<String, Object> parameters, Closure responseHandler = getBodyAsString){
        HttpPut put = new HttpPut(uri)
        if (parameters && !parameters.isEmpty()) {
            put.setEntity(createMultipartEntity(parameters))
        }
        return executeHttpRequest(put, responseHandler)
    }

    def post(URI uri, Map<String, Object> parameters, Closure responseHandler = getBodyAsString) {
        HttpPost post = new HttpPost(uri)
        if (parameters && !parameters.isEmpty()) {
            post.setEntity(createMultipartEntity(parameters))
        }
        return executeHttpRequest(post, responseHandler)
    }

    def delete(URI uri, Map<String, Object> parameters, Closure responseHandler = getBodyAsString){
        URIBuilder builder = new URIBuilder(uri)
        if (parameters && !parameters.isEmpty()) {
            builder.addParameters(createBasicNameValuePairs(parameters))
        }
        HttpDelete delete = new HttpDelete(builder.build())
        return executeHttpRequest(delete, responseHandler)
    }

    def head(URI uri, Map<String, Object> parameters, Closure responseHandler = getBodyAsString){
        URIBuilder builder = new URIBuilder(uri)
        if (parameters && !parameters.isEmpty()) {
            builder.addParameters(createBasicNameValuePairs(parameters))
        }
        HttpHead head = new HttpHead(builder.build())
        return executeHttpRequest(head, responseHandler)
    }

    protected def executeHttpRequest(HttpRequestBase request, Closure responseHandler) throws Exception{
        URI uri = request.getURI()
        log.debug("Executing request: ${request.getMethod()} to URI: ${uri.toString()}")

        if (!client) {
            this.client = createClient(HttpClientBuilder.create())
        }

        if(uri.toURL().getProtocol() == "https") {
            setupSSL(request)
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

    protected static List<NameValuePair> createBasicNameValuePairs(Map params) {
        def list = []
        params?.each{k, v ->
            list.add(new BasicNameValuePair(k.toString(), v.toString()))
        }
        return list
    }

}
