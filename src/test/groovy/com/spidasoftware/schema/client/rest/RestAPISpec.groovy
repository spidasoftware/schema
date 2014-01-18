package com.spidasoftware.schema.client.rest

import com.spidasoftware.schema.client.GenericHttpClient
import net.sf.json.JSONObject
import spock.lang.*
import static spock.lang.MockingApi.*

/**
 * Created with IntelliJ IDEA.
 * User: pfried
 * Date: 1/17/14
 * Time: 4:15 PM
 * To change this template use File | Settings | File Templates.
 */
class RestAPISpec extends Specification {
	def baseUrl = "http://www.spidamin.com/calcdb"
	def client = Mock(GenericHttpClient)
	def api = new RestAPI(baseUrl, client)

	void "the find method should call the correct client method"() {
		setup: "create dummy config values"
		def respClosure = Mock(Closure)
		def findClosure = Mock(Closure)
		def config = new ConfigObject()
		config.doWithResponse = respClosure
		config.doWithFindResult = findClosure
		config.additionalParams = [apiToken: "7777777"]
		config.path = "/tests"
		config.name = "tests"
		config.headers = ["Accept": "application/json"]

		when: "call the find method"
		def result = api.find(config, "123")

		then: "the correct methods should be called"
		1*client.executeRequest("GET", new URI(baseUrl + "/tests/123"), config.additionalParams, config.headers, respClosure) >> "response"
		1*findClosure.call("response")
	}

	void "the save method should call the correct client method"() {
		setup: "create dummy config values and params"
		def respClosure = Mock(Closure)
		def saveClosure = Mock(Closure)
		def config = new ConfigObject()
		config.additionalParams = [apiToken: "7777777"]
		config.doWithResponse = respClosure
		config.doWithSaveResult = saveClosure
		config.path = "/tests"
		config.name = "tests"
		config.headers = ["Accept": "application/json"]
		def params = ["name": "value"]

		when: "call the save method"
		def result = api.save(config, params)

		then: "the correct methods should be called"
		1*client.executeRequest("POST", new URI(baseUrl + "/tests"), _ as Map, _ as Map, respClosure) >> "result"
		1*saveClosure.call("result")
	}


	void "RestAPI should merge configObjects properly"() {
		setup: "new resource and set it's configObject properties"
		api.project.doWithResponse = {response ->  "success" }

		when: "call a rest method"
		def settings = api.project.settings
		def result = api.mergeConfig(settings)

		then:
		result.doWithResponse() == "success"
		result.path == "/project"
		result.name == "project"

	}

	void "rest API should create api resources fluently"() {
		when: "use dot notation to create a new resource and set one of it's properties inline"
		api.project.path = "/projects"

		then:
		def resource = api.project
		resource.settings.path == "/projects"
		resource instanceof RestAPIResource
		resource.path == "/projects"

		// make sure it's added to the resources list
		api.resources.find{ it.name == "project" }.path == "/projects"

		when: "create a new resource and call get on it"
		api.testResource.find("23455")

		then:
		1*client.executeRequest("GET", new URI(baseUrl + "/testResource/23455"), _, _, _ as Closure)

	}

	void "parameters should be merged properly"() {
		setup: "create two maps of params"
		def params1 = [json: '{"name":"value"}']
		api.project.additionalParams = [apiToken:"7777777"]

		when: "merge the two"
		def result = RestAPI.mergeParams(params1, api.project.additionalParams)

		then: "the result should contain both sets of data"
		result.json == '{"name":"value"}'
		result.apiToken == "7777777"
		!api.project.additionalParams.containsKey("json")
		!params1.containsKey("apiToken")
	}


}
