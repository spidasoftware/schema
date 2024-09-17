/*
 * Copyright (c) 2024 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v11

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import spock.lang.Specification

class WireMountedEquipmentChangeSetTest extends Specification {

	def "revert project"() {
		setup:
			WireMountedEquipmentChangeSet changeSet = new WireMountedEquipmentChangeSet()
			def stream = WireMountedEquipmentChangeSetTest.getResourceAsStream("/conversions/v11/WireMountedEquipment-project-v11.json")
			Map json = new JsonSlurper().parse(stream) as Map
			stream.close()
		expect:
			json.leads[0].locations[0].designs[0].structure.wireMountedEquipments
			!json.leads[0].locations[1].designs[0].structure
		when:
			changeSet.revertDesign(json.leads[0].locations[0].designs[0])
		then:
			!json.leads[0].locations[0].designs[0].structure.wireMountedEquipments
		when: "try to revert design with no structure"
			changeSet.revertDesign(json.leads[0].locations[1].designs[0])
		then: "changeset does not throw NPE"
			!json.leads[0].locations[1].designs[0].structure
	}

	def "revert a project containing an assembly"() {
		setup:
			WireMountedEquipmentChangeSet changeSet = new WireMountedEquipmentChangeSet()
			def stream = WireMountedEquipmentChangeSetTest.getResourceAsStream("/conversions/v11/project-with-assembly-v11.json")
			String jsonStr = stream.text
			stream.close()
			Map json = (new JsonSlurper()).parseText(jsonStr)
		expect:
			jsonStr.contains("wireMountedEquipments")
		when:
			changeSet.revertProject(json)
		then:
			!JsonOutput.toJson(json).contains("wireMountedEquipments")
	}

}


