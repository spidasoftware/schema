package com.spidasoftware.schema.conversion.changeset.v2

import net.sf.json.JSONObject
import net.sf.json.groovy.JsonSlurper
import spock.lang.Specification

class FoundationChangeSetTest extends Specification {

	def "test revert"() {
		setup:
			def leanStream = PoleLeanChangeSetTest.getResourceAsStream("/conversions/v2/foundation.json")
			JSONObject projectJSON = new JsonSlurper().parse(leanStream)
			JSONObject locationJSON = JSONObject.fromObject(projectJSON.leads[0].locations[0])
			JSONObject designJSON = JSONObject.fromObject(projectJSON.leads[0].locations[0].designs[0])
			FoundationChangeSet changeset = new FoundationChangeSet()
		expect: "start with foundation"
			projectJSON.leads[0].locations[0].designs[0].structure.pole.has("foundation") == true
			projectJSON.leads[0].locations[0].designs[0].structure.has("foundations") == true

		when: "we revertProject json that contains foundation"
			changeset.revertProject(projectJSON)
		then: "the json no longer contains foundation"
			projectJSON.leads[0].locations[0].designs[0].structure.pole != null
			projectJSON.leads[0].locations[0].designs[0].structure.pole.has("foundation") == false
			projectJSON.leads[0].locations[0].designs[0].structure.has("foundations") == false

		expect: "start with foundation"
			locationJSON.designs[0].structure.pole.has("foundation") == true
			locationJSON.designs[0].structure.has("foundations") == true
		when: "we revertLocation json that contains foundation"
			changeset.revertLocation(locationJSON)
		then: "the json no longer contains foundation"
			locationJSON.designs[0].structure.pole != null
			locationJSON.designs[0].structure.pole.has("foundation") == false
			locationJSON.designs[0].structure.has("foundations") == false

		expect: "start with foundation"
			designJSON.structure.pole.has("foundation") == true
			designJSON.structure.has("foundations") == true
		when: "we revertDesign json that contains foundation"
			changeset.revertDesign(designJSON)
		then: "the json no longer contains foundation"
			designJSON.structure.pole != null
			designJSON.structure.pole.has("foundation") == false
			designJSON.structure.has("foundations") == false
	}
}
