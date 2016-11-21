package com.spidasoftware.schema.conversion.changeset.v2

import net.sf.json.groovy.JsonSlurper
import spock.lang.Specification

class FoundationChangeSetTest extends Specification {

	def "test revert"() {
		setup:
			def leanStream = PoleLeanChangeSetTest.getResourceAsStream("/conversions/v2/foundation.json")
			def json = new JsonSlurper().parse(leanStream)
			def changeset = new FoundationChangeSet()
		expect: "start with foundation"
			json.leads[0].locations[0].designs[0].structure.pole.has("foundation") == true
			json.leads[0].locations[0].designs[0].structure.has("foundations") == true

		when: "we revert json that contains foundation"
			changeset.revert(json)
		then: "the json no longer contains foundation"
			json.leads[0].locations[0].designs[0].structure.pole != null
			json.leads[0].locations[0].designs[0].structure.pole.has("foundation") == false
			json.leads[0].locations[0].designs[0].structure.has("foundations") == false
	}
}
