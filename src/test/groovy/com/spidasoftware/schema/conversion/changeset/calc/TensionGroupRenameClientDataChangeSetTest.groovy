/*
 * ©2009-2020 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.calc

import groovy.json.JsonSlurper
import spock.lang.Specification

class TensionGroupRenameClientDataChangeSetTest extends Specification {

	def "apply and revert"() {
		def stream = TensionGroupRenameClientDataChangeSet.getResourceAsStream("/conversions/v8/TensionRenameClientDataApply.json".toString())
		Map json = new JsonSlurper().parse(stream)
		stream.close()
		when:
			new TensionGroupRenameClientDataChangeSet().applyToClientData(json)
		then:
			json.wires[0].tensionGroups[2].name == "Nonlinear Stress-Strain"
			json.wires[0].tensionGroups[3].name == "Elastic (Dynamic)"
		when:
			new TensionGroupRenameClientDataChangeSet().revertClientData(json)
		then:
			json.wires[0].tensionGroups[2].name == "Advanced"
			json.wires[0].tensionGroups[3].name == "Dynamic"
	}
}
