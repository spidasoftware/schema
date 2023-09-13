package com.spidasoftware.schema.conversion.changeset.v10

import groovy.json.JsonSlurper
import spock.lang.Specification

class DecimalDirectionsChangesetTest extends Specification {

	def "apply sets analysis current to false"() {
		setup:
			DecimalDirectionsChangeset changeset = new DecimalDirectionsChangeset()

			def stream = LoadCaseChangeSetTest.getResourceAsStream("/conversions/v10/RoundedDirections-project.json".toString())
			Map json = new JsonSlurper().parse(stream) as Map
			stream.close()
		expect: "all designs have analysis current"
			json.leads.every { Map lead ->
				lead.locations.every { Map location ->
					location.designs.every { Map design ->
						design.analysisCurrent == true
					}
				}
			}

		when: "applied"
			changeset.applyToProject(json)
		then: "the first location still has analysis current"
			json.leads[0].locations[0].designs[0].analysisCurrent
		and: "the locations that have directions that will change are no longer analysis current"
			!json.leads[0].locations[1].designs[0].analysisCurrent
			!json.leads[0].locations[2].designs[0].analysisCurrent
			!json.leads[0].locations[3].designs[0].analysisCurrent
	}
}
