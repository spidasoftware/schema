package com.spidasoftware.schema.client.rest

import groovy.util.logging.Log4j
import spock.lang.*
import static spock.lang.MockingApi.*

/**
 * Created with IntelliJ IDEA.
 * User: pfried
 * Date: 1/17/14
 * Time: 5:48 PM
 * To change this template use File | Settings | File Templates.
 */
@Log4j
class RestAPIResourceSpec extends Specification {
	def api = Mock(RestAPI)
	def resource = new RestAPIResource("resource", api)


	void "setting resource properties should work fluently"() {
		when: "create a new api resource and set it's path"
		resource.path = "/newPath"

		then: "the new path should be the correct value"
		resource.settings.path == "/newPath"

		when: "add a closure"
		resource.doWithResponse = {response->
			"success"
		}

		then: "the closure should be added to the settings"
		resource.settings.doWithResponse(null) == "success"
	}

	void "Rest crud methods should call the correct api methods"() {
		setup: "get a reference to the resource's settings"
		def config = resource.settings
		def saveParams = [name:"value"]
		def id = "12345"

		when: "call save"
		def saveResult = resource.save(saveParams)

		then: "the api's save method should be called"
		1*api.save(config, saveParams)

		when: "call find"
		def findResult = resource.find(id)

		then: "the api's find method should be called"
		1*api.find(config, id)

	}

}
