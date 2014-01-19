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

	void "resource setting should be automatically loaded from an external config file if the parent api has one"() {
		setup: "set the external config directory for the parent api to a directory containing projects.config"
		api.configDirectory >> new File(getClass().getResource("/rest/client/config").toURI())

		when: "create a new resource called projects"
		def projects = new RestAPIResource("projects", api)

		then: "the overridden settings should be applied from the projects.config file"
		projects.settings.doWithFindResult.call(null) == "projects override"
		projects.settings.headers == ["Accept":"X-projects"]

	}

	void "defining properties in resource settings should work fluently"() {
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
		setup: "dummy values for params"
		def config = resource.settings
		def params = [name:"value"]
		def id = "12345"

		when: "call save"
		def saveResult = resource.save(params)

		then: "the api's save method should be called"
		1*api.save(config, params)

		when: "call find"
		def findResult = resource.find(id)

		then: "the api's find method should be called"
		1*api.find(config, id)

		when: "call list"
		def listResult = resource.list(params)

		then: "the api's list method should be called"
		1*api.list(config, params)

		when: "call update"
		def updateResult = resource.update(id, params)

		then: "the api's update method is called"
		1*api.update(config, params, id)

		when: "call delete"
		def deleteResult = resource.delete(id)

		then: "the api's delete method is called"
		1*api.delete(config, id)
	}

}
