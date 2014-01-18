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
		when:
		resource.path = "/newPath"

		then:
		resource.path == "/newPath"

		when:
		resource.doWithResponse = {response->
			"success"
		}

		then:
		resource.settings.doWithResponse(null) == "success"
	}

}
