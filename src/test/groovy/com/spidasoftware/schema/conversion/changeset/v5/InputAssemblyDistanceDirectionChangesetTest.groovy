/*
 * ©2009-2017 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v5

import spock.lang.Specification

class InputAssemblyDistanceDirectionChangesetTest extends Specification {
	def "RevertDesign"() {
		Map json = [structure:
							[assemblies: [
									[direction: 10,
									 support  : [
											 [:],
											 [distance: [value: 10, unit: "FOOT"]]
									 ]
									]
							]
							]
		]

		when:
			new InputAssemblyDistanceDirectionChangeset().revertDesign(json)
		then:
			json.structure.assemblies[0].direction == null
			json.structure.assemblies[0].support[1].distance == null
	}
}
