package com.spidasoftware.schema.conversion.changeset.v2

import com.spidasoftware.schema.conversion.changeset.calc.CalcProjectChangeSet
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import spock.lang.Specification

@Slf4j
class PoleLeanChangeSetTest extends Specification {

	def "test revert"() {
		setup:
			def leanStream = PoleLeanChangeSetTest.getResourceAsStream("/conversions/v2/pole-lean.json")
		    Map projectJSON = new JsonSlurper().parse(leanStream)
		    Map locationJSON = CalcProjectChangeSet.duplicateAsJson(projectJSON.leads[0].locations[0])
		    Map designJSON = CalcProjectChangeSet.duplicateAsJson(projectJSON.leads[0].locations[0].designs[0])
			PoleLeanChangeSet changeSet = new PoleLeanChangeSet()
		when: "revertProject"
			changeSet.revertProject(projectJSON)
			def pole = projectJSON.leads.first().locations.first().designs.first().structure.pole
		then:
			pole.containsKey("leanAngle") == false
			pole.containsKey("leanDirection") == false
		when: "revertLocation"
			changeSet.revertLocation(locationJSON)
			pole = locationJSON.designs.first().structure.pole
		then:
			pole.containsKey("leanAngle") == false
			pole.containsKey("leanDirection") == false
		when: "revertDesign"
			changeSet.revertDesign(designJSON)
			pole = designJSON.structure.pole
		then:
			pole.containsKey("leanAngle") == false
			pole.containsKey("leanDirection") == false

	}
}
