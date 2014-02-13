package com.spidasoftware.schema.client.rest

import com.spidasoftware.schema.client.GenericHttpClient
import spock.lang.*

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

	void "getting defaults properties should return a copy of the configobject"() {
		setup: "get the defaults using both mehtods"
		def d1 = api.getDefaults()
		def d2 = api.defaults

		expect: "the two objects should be equal, but not the same object in memory"
		d1 == d2
		!d1.is(d2)

	}

	void "uri's should be correctly generated"() {
		when: "generate the url from the current baseUrl, path and id"
		api.baseUrl = "http://"+ currentBaseUrl
		URI uri = api.createURI(currentPath, currentId)

		then: "the generated uri should match the expected"
		uri.toString() == "http://"+ expectedUri

		where:
		currentBaseUrl      || currentPath          || currentId    || expectedUri
		"www.website.com"   || "api/resource"       || "1234567"    || "www.website.com/api/resource/1234567"
		"spida.min.com"     || "/calcdb/projects"   ||  ""          || "spida.min.com/calcdb/projects"
		"spida.min.com/"    || "/calcdb/projects"   || "123"        || "spida.min.com/calcdb/projects/123"
		"www.google.com/"   || "search"             || null         || "www.google.com/search"
	}

	void "config overrides should be loaded properly when specified"() {
		setup: "set the config directory for the api"
		api.setConfigDirectory(new File(getClass().getResource("/rest/client/config").toURI()))

		when: "reload the defaults for the api"
		api.loadDefaults()

		then: "the defaults should be overridden"
		def heds = api.headers.Accept == "customOverrideValue"

		api.doWithResponse.call(null) == "overridden doWithResponse Closure"

	}


	void "the rest methods should call the correct client methods"() {
		setup: "create dummy config values"
		def config = new ConfigObject()
		config.doWithResponse = Mock(Closure)
		config.doWithFindResult = Mock(Closure)
		config.doWithListResult = Mock(Closure)
		config.doWithSaveResult = Mock(Closure)
		config.doWithUpdateResult = Mock(Closure)
		config.doWithDeleteResult = Mock(Closure)

		config.additionalParams = [apiToken: "7777777"]
		config.path = "/tests"
		config.name = "tests"
		config.headers = ["Accept": "application/json"]

		def saveParams = ["name":"value"]
		Map combinedParams = saveParams.plus(config.additionalParams as Map) as Map


		when: "call the find method"
		def result = api.find(config, "123")

		then: "the correct methods should be called"
		1*client.executeRequest("GET", new URI(baseUrl + "/tests/123"), _ as Map, _ as Map, config.doWithResponse) >> "response"
		1*config.doWithFindResult.call("response")

		when: "call the list method"
		def listResult = api.list(config, saveParams)

		then:
		1*client.executeRequest("GET", new URI(baseUrl + "/tests"), _ as Map, _ as Map, config.doWithResponse) >> "response"
		1*config.doWithListResult.call("response")

		when: "call the update method"
		api.update(config, saveParams, "123")

		then:
		1*client.executeRequest("PUT", new URI(baseUrl + "/tests/123"), _ as Map, _ as Map, config.doWithResponse) >> "response"
		1*config.doWithUpdateResult.call("response")

		when: "call the delete method"
		api.delete(config, "123")

		then:
		1*client.executeRequest("DELETE", new URI(baseUrl + "/tests/123"), _ as Map, _ as Map, config.doWithResponse) >> "response"
		1*config.doWithDeleteResult.call("response")

		when: "call save"
		api.save(config, saveParams)

		then:
		1*client.executeRequest("POST", new URI(baseUrl + "/tests"), _ as Map, _ as Map, config.doWithResponse) >> "response"
		1*config.doWithSaveResult.call("response")

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
		api.project.someProperty = "value"

		// also set one of the default properties
		def heds = ["Accept": "application/xml"]
		api.headers = heds

		when: "merge the settings from the resource"
		def settings = api.project.settings
		def result = api.mergeConfig(settings)

		then: "the result should contain the properties from both"
		result.doWithResponse() == "success"
		result.path == "/project"
		result.name == "project"
		result.someProperty == "value"
		result.headers == heds

	}

	void "rest API should create api resources fluently"() {
		when: "use dot notation to create a new resource and set one of it's properties inline"
		api.project.path = "/projects"

		then:
		def resource = api.project
		resource.path == "/projects"
		resource instanceof RestAPIResource

		// make sure it's added to the resources list
		api.resources.find{ it.name == "project" }.path == "/projects"

		when: "create a new resource and call find on it"
		api.testResource.find("23455")

		then:
		1*client.executeRequest("GET", new URI(baseUrl + "/testResource/23455"), _, _, _ as Closure)

	}

	void "parameters should be merged properly"() {
		setup: "create two maps of params"
		def params1 = [json: '{"name":"value"}']
		api.project.additionalParams = [apiToken:"7777777", json: '{"name":"anotherValue"}']

		when: "merge the two"
		def result = RestAPI.mergeParams(params1, api.project.additionalParams)

		then: "the result should contain both sets of data, with the supplied params taking precedence ofer config.additionalParams"
		result.json == '{"name":"value"}'
		result.apiToken == "7777777"
		api.project.additionalParams.containsKey("json")
		!params1.containsKey("apiToken")
	}


}
