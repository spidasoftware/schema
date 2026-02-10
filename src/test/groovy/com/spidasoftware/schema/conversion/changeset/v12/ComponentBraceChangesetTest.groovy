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

	def "revert loadcases in project json"() {

	}

	def "revert components in results"() {
		setup:
			InputStream stream = ComponentBraceChangesetTest.getResourceAsStream("/conversions/v12/detailedResult-crossarmbrace-analyzedcomponent.json")
			Map json = new JsonSlurper().parse(stream) as Map
			stream.close()
			boolean changed
		expect: "there is a load case with COMPONENT_BRACE as a component"
			json.results.every { it.analysisCaseDetails.components.contains("COMPONENT_BRACE") }
		and: "every result has a componentBraces component list"
			json.results.every { it.components.every { it.containsKey("componentBraces") } }
		when: "reverted"
			changed = changeset.revertResults(json)
		then:
			changed
		and: "no load case has COMPONENT_BRACE as a component"
			!json.results.any { it.analysisCaseDetails.components.contains("COMPONENT_BRACE") }
		and: "no result has any component braces component"
			!json.results.any { it.components.any { it.containsKey("componentBraces") } }
		when: "reverted again"
			changed = changeset.revertResults(json)
		then:
			!changed
	}
}
