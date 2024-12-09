/*
 * Copyright (c) 2024 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v12

import groovy.json.JsonSlurper
import spock.lang.Specification

class PushBraceHeightChangeSetTest extends Specification {

	static PushBraceHeightChangeSet changeSet

	def setupSpec() {
		changeSet = new PushBraceHeightChangeSet()
	}

	def "project"() {
		setup:
			InputStream stream = PushBraceHeightChangeSetTest.getResourceAsStream("/conversions/v12/pushBrace-project-v11.json")
			Map json = new JsonSlurper().parse(stream) as Map
			stream.close()
		expect:
			!json.leads[0].locations[0].designs[0].structure.pushBraces[0].height
		when:
			changeSet.applyToProject(json)
		then:
			json.leads[0].locations[0].designs[0].structure.pushBraces[0].height
			json.leads[0].locations[0].designs[0].structure.pushBraces[0].height.unit
			json.leads[0].locations[0].designs[0].structure.pushBraces[0].height.value == 0
		when:
			changeSet.revertProject(json)
		then:
			!json.leads[0].locations[0].designs[0].structure.pushBraces[0].height
	}

	def "results"() {
		setup:
			InputStream stream = PushBraceHeightChangeSetTest.getResourceAsStream("/conversions/v12/pushBrace-results-v11.json")
			Map json = new JsonSlurper().parse(stream) as Map
			stream.close()
		expect:
			!json.analyzedStructure.pushBraces[0].height
		when:
			changeSet.applyToResults(json)
		then:
			json.analyzedStructure.pushBraces[0].height
			json.analyzedStructure.pushBraces[0].height.unit
			json.analyzedStructure.pushBraces[0].height.value == 0
		when:
			changeSet.revertResults(json)
		then:
			!json.analyzedStructure.pushBraces[0].height
	}
}
