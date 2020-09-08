/*
 * ©2009-2020 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v8

import spock.lang.Specification

class MaxTensionGroupChangesetTest extends Specification {

	def "revertProject"() {
		setup:
			MaxTensionGroupChangeset changeset = new MaxTensionGroupChangeset()
			Map projectJSON = [clientData:[wires:[[tensionGroups:[[groups:[
				[distance:[value:1, unit:"METRE"], tension:[value:200, unit:"NEWTON"]]
			]]]]]]]
			assert projectJSON.clientData.wires[0].tensionGroups[0].groups.size() == 1

		when:
			changeset.revertProject(projectJSON)
		then:
			projectJSON.clientData.wires[0].tensionGroups[0].groups.size() == 2
			projectJSON.clientData.wires[0].tensionGroups[0].groups[0].distance.value == 1
			projectJSON.clientData.wires[0].tensionGroups[0].groups[0].tension.value == 200
			projectJSON.clientData.wires[0].tensionGroups[0].groups[1].distance.value == MaxTensionGroupChangeset.MAX_DIST_METERS
			projectJSON.clientData.wires[0].tensionGroups[0].groups[1].tension.value == 200
	}

}
