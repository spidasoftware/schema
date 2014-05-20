package com.spidasoftware.schema.client

import groovy.util.logging.Log4j
import org.apache.http.HttpEntity
import org.apache.http.HttpEntityEnclosingRequest
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.protocol.HttpContext
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: pfried
 * Date: 1/16/14
 * Time: 3:48 PM
 */
@Log4j
class GenericHttpClientSpec extends Specification {
    def client = new GenericHttpClient()
	URI uri = new URI("http://www.website.com/api")

    def setup(){
		client.client = Mock(CloseableHttpClient)
    }

	def cleanup(){
		client.cleanupClient()
	}

	void "headers should get added to the request"() {
		setup: ""
		def headers = ["Content-Type":"text/html", "Accept":"application/json", "Content-Length": "768"]

		when: "create the request"
		def request = client.createHttpUriRequest(new Request(httpMethod:"DELETE", uri:uri, parameters:null, headers:headers))

		then: "the request headers should all be present"
		headers.every{k, v->
			request.getFirstHeader(k).value == v
		}
	}


	void "making a request should call the correct setup/cleanup closures"() {
		setup: "mock client and response and use an https uri"
		def httpsUri = new URI("https://www.website.com/api")
		def mockClient = Mock(CloseableHttpClient)
		def mockResponse = Mock(CloseableHttpResponse)
		mockClient.execute(_ as HttpUriRequest, _ as HttpContext) >> mockResponse
		client.cleanupResponse = Mock(Closure)
		client.client = mockClient

		when: "execute a request"
		def result = client.executeRequest("GET", uri, null, null){ "success" }

		then: "the cleanupResponse Closure called"
		1*client.cleanupResponse.call(mockResponse)


	}

	void "POST and PUT requests with files should have the files added to a multipart entity"() {
		setup: "a dummy file and uri"
		File f = File.createTempFile("httpClientTest", ".txt")
		f.deleteOnExit()
		def testText = "lskdjfoafiewflksdfjsadvoadivjalksajds"
		f << testText

		when: "create a request with the file as a parameter"
		def request = client.createHttpUriRequest(new Request(httpMethod:"POST", uri:uri, parameters:["testFile": f], headers:null))
		GenericHttpClient.logRequest(request)

		then: "the request should be of the correct type and contian the file"
		request instanceof HttpEntityEnclosingRequest
		def baos = new ByteArrayOutputStream()
		request.getEntity().writeTo(baos)
		def text = baos.toString()
		text.contains(testText)
		text.contains("Content-Disposition: form-data; name=\"testFile\"")
		println "Entity text= " + text

	}

	void "requests parameters should be added to the uri for GET, HEAD, and DELETE requests"() {

		when: "create the request"
		def request = client.createHttpUriRequest(new Request(httpMethod:currentMethod, uri:uri, parameters:currentParams, headers:null))

		then: "the uri should match the expected"
		def uriString = request.getURI().toString()
		uriString == expectedURI

		where:
		currentParams << [ ["testKey":"testValue", "limit":5], [apiToken:"57f"], ["w":1, "g":5, "t":true] ]
		expectedURI << [ "http://www.website.com/api?testKey=testValue&limit=5", "http://www.website.com/api?apiToken=57f", "http://www.website.com/api?w=1&g=5&t=true" ]
		currentMethod << ["GET", "DELETE", "HEAD"]
	}

	void "created requests should be of the correct type"() {
		when: "create the request"
		def request = client.createHttpUriRequest(new Request(httpMethod:currentMethod, uri:uri, parameters:null, headers:null))

		then: "the created request should have the correct method"
		request.getMethod() == currentMethod

		where:
		currentMethod << ["GET", "PUT", "POST", "DELETE", "HEAD", "TRACE"]
	}

	void "multipart requests should have files added with the correct content dispositions"() {
		setup: "Create a parameter map that includes both string and File values"
		File file = new File(getClass().getResource("/rest/testAPI.json").toURI())
		String fileName = "testFileName"
		def params = ["stringParam":"stringValue", (fileName): file]
		boolean foundCorrectContentDisposion = false

		when: "create the multipart request entity"
		HttpEntity entity = client.createMultipartEntity(params)

		then: "The file's Content-Disposition must include the filename"
		def os = new ByteArrayOutputStream()
		entity.writeTo(os)
		String content = os.toString()
		content.eachLine {
			if (it.startsWith("Content-Disposition: form-data; name=\"testFileName\"; filename=\"testFileName\"")) {
				foundCorrectContentDisposion = true
			}
			return true
		}
		foundCorrectContentDisposion
	}

	void "multipart requests should have the correct mime type detected"() {
		setup: "Create a parameter map that includes both string and File values"

			def params = [:]
			["rtf", "json", "pdf"].each{
				File file = new File(getClass().getResource("/rest/mime/test.${it}").toURI())
				String fileName = "test.${it}"
				params.put(fileName, file)
			}

		when: "create the multipart request entity"
		HttpEntity entity = client.createMultipartEntity(params)

		then: "The file's Content-Disposition must include the filename"
		def os = new ByteArrayOutputStream()
		entity.writeTo(os)
		String content = os.toString()
		assert content.contains("application/pdf")
		assert content.contains("application/rtf")
		assert content.contains("text/plain")
	}
}
