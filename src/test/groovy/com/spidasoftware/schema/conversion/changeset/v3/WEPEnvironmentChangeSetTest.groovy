/*
 * ©2009-2015 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v3

import com.spidasoftware.schema.conversion.changeset.v2.PoleLeanChangeSetTest
import groovy.util.logging.Log4j
import net.sf.json.JSONObject
import net.sf.json.groovy.JsonSlurper
import spock.lang.Specification

@Log4j
class WEPEnvironmentChangeSetTest extends Specification {

	def "test revert"() {
		setup:
			def leanStream = PoleLeanChangeSetTest.getResourceAsStream("/conversions/v3/wep-environment.json")
			JSONObject projectJSON = new JsonSlurper().parse(leanStream)
			JSONObject locationJSON = JSONObject.fromObject(projectJSON.leads[0].locations[0])
			JSONObject designJSON = JSONObject.fromObject(projectJSON.leads[0].locations[0].designs[0])
			WEPEnvironmentChangeSet changeSet = new WEPEnvironmentChangeSet()
		when: "revertProject"
			changeSet.revertProject(projectJSON)
			def wep = projectJSON.leads.first().locations.first().designs.first().structure.wireEndPoints
		then:
			wep.every { it.containsKey("environment") == false }
		when: "revertLocation"
			changeSet.revertLocation(locationJSON)
			wep = locationJSON.designs.first().structure.wireEndPoints
		then:
			wep.every { it.containsKey("environment") == false }
		when: "revertDesign"
			changeSet.revertDesign(designJSON)
			wep = designJSON.structure.wireEndPoints
		then:
			wep.every { it.containsKey("environment") == false }
	}
}
