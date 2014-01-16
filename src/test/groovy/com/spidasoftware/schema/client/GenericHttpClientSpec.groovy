package com.spidasoftware.schema.client

import groovy.util.logging.Log4j
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.protocol.HttpContext
import spock.lang.*
import static spock.lang.MockingApi.*


/**
 * Created with IntelliJ IDEA.
 * User: pfried
 * Date: 1/16/14
 * Time: 3:48 PM
 * To change this template use File | Settings | File Templates.
 */
@Log4j
class GenericHttpClientSpec extends Specification {
    def client = new GenericHttpClient()

    def setup(){
        client.createClient = {builder ->
            def mockResponse = Mock(CloseableHttpResponse)
            def apacheClient = Mock(CloseableHttpClient)
            apacheClient.execute(_ as HttpUriRequest, _ as HttpContext) >> mockResponse
            apacheClient
        }

        // we're not dealing with real responses, so no need to close streams
        client.cleanupResponse = {response ->
            true
        }
    }

    void "get should call the correct methods and closures"() {
        setup: "mock the closures"
        URI uri = new URI("https://www.website.com/api")
        client.cleanupResponse = {response->
            assert response
        }
        client.setupSSL = {
           assert it.getMethod() == "GET"
        }

        when: "Execute the request"
        def response = client.get(uri, [:]){
            "success"
        }

        then: "the response indicates success"
        response == "success"


    }
}
