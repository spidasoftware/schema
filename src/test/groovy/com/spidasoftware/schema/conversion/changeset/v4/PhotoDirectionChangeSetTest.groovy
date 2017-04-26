/*
 * ©2009-2015 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.ChangeSet
import groovy.json.JsonSlurper
import groovy.util.logging.Log4j
import spock.lang.Specification

@Log4j
class PhotoDirectionChangeSetTest extends Specification {

	def "apply and revert"() {
		setup:
			def leanStream = PhotoDirectionChangeSetTest.getResourceAsStream("/conversions/v4/photo-direction.json")
		    Map projectJSON = new JsonSlurper().parse(leanStream)
		    Map locationJSON = ChangeSet.duplicateAsJson(projectJSON.leads[0].locations[0])
			PhotoDirectionChangeSet changeSet = new PhotoDirectionChangeSet()
		when: "applyToProject"
			changeSet.applyToProject(projectJSON)
		then:
			projectJSON.leads.first().locations.first().images.first().direction == 'N/A'
		when: "revertProject"
			changeSet.revertProject(projectJSON)
		then:
			projectJSON.leads.first().locations.first().images.first().direction == null
		when: "applyToLocation"
			changeSet.applyToLocation(locationJSON)
		then:
			locationJSON.images.first().direction == 'N/A'
		when: "revertLocation"
			changeSet.revertLocation(locationJSON)
		then:
			locationJSON.images.first().direction == null
	}

}
