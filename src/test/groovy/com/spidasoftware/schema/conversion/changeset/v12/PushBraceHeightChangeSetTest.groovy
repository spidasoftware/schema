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

	def "project does nothing on up conversion"() {
		setup:
			InputStream stream = PushBraceHeightChangeSetTest.getResourceAsStream("/conversions/v12/pushBrace-project-v11.json")
			Map json = new JsonSlurper().parse(stream) as Map
			stream.close()
		expect:
			json.leads[0].locations[0].designs[0].structure.pushBraces[0].height
		when:
			changeSet.revertProject(json)
		then:
			!json.leads[0].locations[0].designs[0].structure.pushBraces[0].height
		when:
			changeSet.applyToProject(json)
		then:
			!json.leads[0].locations[0].designs[0].structure.pushBraces[0].height
	}

	def "results does nothing on up conversion"() {
		setup:
			InputStream stream = PushBraceHeightChangeSetTest.getResourceAsStream("/conversions/v12/pushBrace-results-v11.json")
			Map json = new JsonSlurper().parse(stream) as Map
			stream.close()
		expect:
			json.analyzedStructure.pushBraces[0].height
		when:
			changeSet.revertResults(json)
		then:
			!json.analyzedStructure.pushBraces[0].height
		when:
			changeSet.applyToResults(json)
		then:
			!json.analyzedStructure.pushBraces[0].height
	}
}
