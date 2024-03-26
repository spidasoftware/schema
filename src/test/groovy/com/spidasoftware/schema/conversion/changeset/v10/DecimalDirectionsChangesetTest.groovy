/*
 * Copyright (c) 2023 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v10

import com.spidasoftware.schema.validation.Validator
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

	def "is integer"() {
		setup:
			DecimalDirectionsChangeset changeset = new DecimalDirectionsChangeset()
			Map map = [
					"integer": 1i,
					"int-valued double": 1.0d,
					"real": 1.0001d,
					"bigdecimal": new BigDecimal(1.0001),
					"int-valued bigdecimal": new BigDecimal(1.0),
					"null": null]
		expect:
			changeset.isInteger(map.integer)
			changeset.isInteger(map."int-valued double")
			!changeset.isInteger(map.real)
			!changeset.isInteger(map.bigdecimal)
			changeset.isInteger(map."int-valued bigdecimal")
			changeset.isInteger(map.null)
	}

	def "revert direction"() {
		setup:
			DecimalDirectionsChangeset changeset = new DecimalDirectionsChangeset()
			Map map = ["integer": 1i, "int-valued double": 1.0d, "real": 1.0001d, "bigdecimal zero": BigDecimal.ZERO, "bigdecimal real": new BigDecimal(0.6d)]
			boolean result

		when:
			result = changeset.revertDirection(map, "integer")
		then:
			!result
			map.integer == 1i
			(map.integer instanceof Integer) || (map.integer instanceof Long)

		when:
			result = changeset.revertDirection(map, "int-valued double")
		then:
			!result
			map."int-valued double" == 1i
			(map."int-valued double" instanceof Integer) || (map."int-valued double" instanceof Long)

		when:
			result = changeset.revertDirection(map, "real")
		then:
			result
			map.real == 1i
			(map.real instanceof Integer) || (map.real instanceof Long)

		when:
			result = changeset.revertDirection(map, "bigdecimal zero")
		then:
			!result
			map."bigdecimal zero" == 0i
			(map."bigdecimal zero" instanceof Integer) || (map."bigdecimal zero" instanceof Long)

		when:
			result = changeset.revertDirection(map, "bigdecimal real")
		then:
			result
			map."bigdecimal real" == 1i
			(map."bigdecimal real" instanceof Integer) || (map."bigdecimal real" instanceof Long)
	}

	def "revert design"() {
		setup:
			DecimalDirectionsChangeset changeset = new DecimalDirectionsChangeset()

			def stream = LoadCaseChangeSetTest.getResourceAsStream("/conversions/v10/DecimalDirections-project.json".toString())
			Map json = new JsonSlurper().parse(stream) as Map
			stream.close()

		when: "reverted"
			changeset.revertDesign(json.leads[0].locations[0].designs[0])
		then: "valid"
			new Validator().validateAndReport("/schema/spidacalc/calc/design.schema", json.leads[0].locations[0].designs[0]).isSuccess()
	}

	def "revert client data"() {
		setup:
			DecimalDirectionsChangeset changeset = new DecimalDirectionsChangeset()

			def stream = LoadCaseChangeSetTest.getResourceAsStream("/conversions/v10/DecimalDirections-project.json".toString())
			Map json = new JsonSlurper().parse(stream) as Map
			stream.close()
			boolean result

		when: "reverted"
			result = changeset.revertClientData(json.clientData)
		then: "modified"
			result
		and: "valid"
			new Validator().validateAndReport("/schema/spidacalc/client/data.schema", json.clientData).isSuccess()

		when: "reverted again"
			result = changeset.revertClientData(json.clientData)
		then: "not modified, since all the directions were rounded already"
			!result
	}

	def "revert results"() {
		setup:
			DecimalDirectionsChangeset changeset = new DecimalDirectionsChangeset()

			def stream = LoadCaseChangeSetTest.getResourceAsStream("/conversions/v10/DecimalDirections-results.json".toString())
			Map json = new JsonSlurper().parse(stream) as Map
			stream.close()
			boolean result

		when: "reverted"
			result = changeset.revertResults(json)
		then: "modified"
			result
		and: "valid"
			new Validator().validateAndReport("/schema/spidacalc/results/results.schema", json).isSuccess()

		when: "reverted again"
			result = changeset.revertResults(json)
		then: "not modified, since all the directions were rounded already"
			!result
	}
}
