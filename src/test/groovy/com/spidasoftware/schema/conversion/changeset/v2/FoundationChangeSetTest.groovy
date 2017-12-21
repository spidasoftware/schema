package com.spidasoftware.schema.conversion.changeset.v2

import com.spidasoftware.schema.conversion.changeset.calc.CalcProjectChangeSet
import groovy.json.JsonSlurper
import spock.lang.Specification

class FoundationChangeSetTest extends Specification {

	def "test revert"() {
		setup:
			def leanStream = PoleLeanChangeSetTest.getResourceAsStream("/conversions/v2/foundation.json")
		    Map projectJSON = new JsonSlurper().parse(leanStream)
		    Map locationJSON = CalcProjectChangeSet.duplicateAsJson(projectJSON.leads[0].locations[0])
		    Map designJSON = CalcProjectChangeSet.duplicateAsJson(projectJSON.leads[0].locations[0].designs[0])
			FoundationChangeSet changeset = new FoundationChangeSet()
		expect: "start with foundation"
			projectJSON.leads[0].locations[0].designs[0].structure.pole.containsKey("foundation") == true
			projectJSON.leads[0].locations[0].designs[0].structure.containsKey("foundations") == true

		when: "we revertProject json that contains foundation"
			changeset.revertProject(projectJSON)
		then: "the json no longer contains foundation"
			projectJSON.leads[0].locations[0].designs[0].structure.pole != null
			projectJSON.leads[0].locations[0].designs[0].structure.pole.containsKey("foundation") == false
			projectJSON.leads[0].locations[0].designs[0].structure.containsKey("foundations") == false

		expect: "start with foundation"
			locationJSON.designs[0].structure.pole.containsKey("foundation") == true
			locationJSON.designs[0].structure.containsKey("foundations") == true
		when: "we revertLocation json that contains foundation"
			changeset.revertLocation(locationJSON)
		then: "the json no longer contains foundation"
			locationJSON.designs[0].structure.pole != null
			locationJSON.designs[0].structure.pole.containsKey("foundation") == false
			locationJSON.designs[0].structure.containsKey("foundations") == false

		expect: "start with foundation"
			designJSON.structure.pole.containsKey("foundation") == true
			designJSON.structure.containsKey("foundations") == true
		when: "we revertDesign json that contains foundation"
			changeset.revertDesign(designJSON)
		then: "the json no longer contains foundation"
			designJSON.structure.pole != null
			designJSON.structure.pole.containsKey("foundation") == false
			designJSON.structure.containsKey("foundations") == false
	}
}
