package com.spidasoftware.schema.conversion.changeset.v2

import groovy.util.logging.Log4j
import net.sf.json.JSONObject
import net.sf.json.groovy.JsonSlurper
import spock.lang.Specification

@Log4j
class PoleLeanChangeSetTest extends Specification {

	def "test revert"() {
		setup:
			def leanStream = PoleLeanChangeSetTest.getResourceAsStream("/conversions/v2/pole-lean.json")
			JSONObject projectJSON = new JsonSlurper().parse(leanStream)
			JSONObject locationJSON = JSONObject.fromObject(projectJSON.leads[0].locations[0])
			JSONObject designJSON = JSONObject.fromObject(projectJSON.leads[0].locations[0].designs[0])
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
