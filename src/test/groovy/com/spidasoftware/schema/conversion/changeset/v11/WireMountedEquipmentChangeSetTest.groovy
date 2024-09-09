/*
 * Copyright (c) 2024 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v11

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
		when:
			changeSet.revertDesign(json.leads[0].locations[0].designs[0])
		then:
			!json.leads[0].locations[0].designs[0].structure.wireMountedEquipments
	}

}


