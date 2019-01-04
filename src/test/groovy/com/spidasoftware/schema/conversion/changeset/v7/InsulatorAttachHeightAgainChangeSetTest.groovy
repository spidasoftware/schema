package com.spidasoftware.schema.conversion.changeset.v7

import com.spidasoftware.schema.conversion.changeset.calc.CalcProjectChangeSet
import groovy.json.JsonSlurper
import spock.lang.Specification

class InsulatorAttachHeightAgainChangeSetTest extends Specification {

	def "apply and revert"() {
		setup:
			def stream = InsulatorAttachHeightAgainChangeSetTest.getResourceAsStream("/conversions/v6/insulator-attach-height.json")
			Map projectJSON = new JsonSlurper().parse(stream)
			Map locationJSON = CalcProjectChangeSet.duplicateAsJson(projectJSON.leads[0].locations[0])
			Map designJSON = CalcProjectChangeSet.duplicateAsJson(projectJSON.leads[0].locations[0].designs[0])
			InsulatorAttachHeightAgainChangeSet changeSet = new InsulatorAttachHeightAgainChangeSet()
			def structure = projectJSON.leads[0].locations[0].designs[0].structure
			assert structure.insulators[0].offset.unit == "METRE"
			assert structure.insulators[0].offset.value == 7.62

		when: "applyToProject"
			changeSet.applyToProject(projectJSON)
			structure = projectJSON.leads[0].locations[0].designs[0].structure
		then:
			structure.insulators[0].offset == null
			structure.insulators[0].attachmentHeight.unit == "METRE"
			structure.insulators[0].attachmentHeight.value == 7.62
			structure.insulators[1].attachmentHeight == null
			structure.insulators[1].offset.unit == "METRE"
			structure.insulators[1].offset.value == 0.10160000000000001

		when: "revertProject"
			changeSet.revertProject(projectJSON)
			structure = projectJSON.leads[0].locations[0].designs[0].structure
		then:
			structure.insulators[0].attachmentHeight == null
			structure.insulators[0].offset.unit == "METRE"
			structure.insulators[0].offset.value == 7.62
			structure.insulators[1].attachmentHeight == null
			structure.insulators[1].offset.unit == "METRE"
			structure.insulators[1].offset.value == 0.10160000000000001

		when: "applyToLocation"
			changeSet.applyToLocation(locationJSON)
			structure = locationJSON.designs[0].structure
		then:
			structure.insulators[0].offset == null
			structure.insulators[0].attachmentHeight.unit == "METRE"
			structure.insulators[0].attachmentHeight.value == 7.62
			structure.insulators[1].attachmentHeight == null
			structure.insulators[1].offset.unit == "METRE"
			structure.insulators[1].offset.value == 0.10160000000000001

		when: "revertLocation"
			changeSet.revertLocation(locationJSON)
			structure = locationJSON.designs[0].structure
		then:
			structure.insulators[0].attachmentHeight == null
			structure.insulators[0].offset.unit == "METRE"
			structure.insulators[0].offset.value == 7.62
			structure.insulators[1].attachmentHeight == null
			structure.insulators[1].offset.unit == "METRE"
			structure.insulators[1].offset.value == 0.10160000000000001

		when: "applyToDesign"
			changeSet.applyToDesign(designJSON)
			structure = designJSON.structure
		then:
			structure.insulators[0].offset == null
			structure.insulators[0].attachmentHeight.unit == "METRE"
			structure.insulators[0].attachmentHeight.value == 7.62
			structure.insulators[1].attachmentHeight == null
			structure.insulators[1].offset.unit == "METRE"
			structure.insulators[1].offset.value == 0.10160000000000001

		when: "revertDesign"
			changeSet.revertDesign(designJSON)
			structure = designJSON.structure
		then:
			structure.insulators[0].attachmentHeight == null
			structure.insulators[0].offset.unit == "METRE"
			structure.insulators[0].offset.value == 7.62
			structure.insulators[1].attachmentHeight == null
			structure.insulators[1].offset.unit == "METRE"
			structure.insulators[1].offset.value == 0.10160000000000001
	}
}
