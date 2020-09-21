/*
 * ©2009-2020 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v8

import groovy.json.JsonSlurper
import spock.lang.Specification

class TensionGroupRenameDesignChangeSetTest extends Specification {

	def "apply and revert"() {
		def stream = TensionGroupRenameDesignChangeSet.getResourceAsStream("/conversions/v8/TensionRenameDesignApply.json".toString())
		Map json = new JsonSlurper().parse(stream)
		stream.close()
		when:
			new TensionGroupRenameDesignChangeSet().applyToDesign(json)
		then:
			json.structure.wires[0].tensionGroup == "Nonlinear Stress-Strain"
			json.structure.wires[1].tensionGroup == "Nonlinear Stress-Strain"
			json.structure.wires[2].tensionGroup == "Elastic (Dynamic)"
			json.structure.wires[3].tensionGroup == "Elastic (Dynamic)"
		when:
			new TensionGroupRenameDesignChangeSet().revertDesign(json)
		then:
			json.structure.wires[0].tensionGroup == "Advanced"
			json.structure.wires[1].tensionGroup == "Advanced"
			json.structure.wires[2].tensionGroup == "Dynamic"
			json.structure.wires[3].tensionGroup == "Dynamic"
		}
}
