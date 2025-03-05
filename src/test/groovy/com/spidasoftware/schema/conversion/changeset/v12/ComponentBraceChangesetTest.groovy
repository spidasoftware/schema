/*
 * Copyright (c) 2025 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v12

import groovy.json.JsonSlurper
import spock.lang.Specification

class ComponentBraceChangesetTest extends Specification {

	static ComponentBraceChangeset changeset

	def setupSpec() {
		changeset = new ComponentBraceChangeset()
	}

	def "revert structure"() {
		setup:
			InputStream stream = ComponentBraceChangesetTest.getResourceAsStream("/conversions/v12/structureWithCrossarmBrace-v12.json")
			Map json = new JsonSlurper().parse(stream) as Map
			stream.close()
			boolean changed
		expect: "there is a crossarm with a brace"
			json.componentBraces.size() == 1
			json.crossArms[0].braces.size() == 1

		when: "reverted"
			changed = changeset.revertStructure(json)
		then:
			changed
			!json.containsKey("componentBraces")
			!json.crossArms[0].containsKey("braces")

		when: "reverted again"
			changed = changeset.revertStructure(json)
		then:
			!changed
	}
}
