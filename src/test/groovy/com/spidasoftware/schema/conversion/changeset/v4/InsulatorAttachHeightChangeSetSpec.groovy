/*
 * ©2009-2015 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.calc.CalcProjectChangeSet
import groovy.json.JsonSlurper
import groovy.util.logging.Log4j
import spock.lang.Specification

@Log4j
class InsulatorAttachHeightChangeSetSpec extends Specification {

	def "apply and revert"() {
		setup:
			def stream = InsulatorAttachHeightChangeSetSpec.getResourceAsStream("/conversions/v4/insulator-attach-height.json")
		    Map projectJSON = new JsonSlurper().parse(stream)
		    Map locationJSON = CalcProjectChangeSet.duplicateAsJson(projectJSON.leads[0].locations[0])
		    Map designJSON = CalcProjectChangeSet.duplicateAsJson(projectJSON.leads[0].locations[0].designs[0])
			InsulatorAttachHeightChangeSet changeSet = new InsulatorAttachHeightChangeSet()

			def structure = projectJSON.leads[0].locations[0].designs[0].structure
			assert structure.insulators[0].attachmentHeight.unit == "FOOT"
			assert structure.insulators[0].attachmentHeight.value == 10.4

		when: "applyToProject"
			changeSet.applyToProject(projectJSON)
			structure = projectJSON.leads[0].locations[0].designs[0].structure
		then:
			structure.insulators[0].attachmentHeight == null
			structure.insulators[0].offset.unit == "INCH"
			structure.insulators[0].offset.value == 125

		when: "revertProject"
			changeSet.revertProject(projectJSON)
			structure = projectJSON.leads[0].locations[0].designs[0].structure
		then:
			structure.insulators[0].attachmentHeight.unit == "INCH"
			structure.insulators[0].attachmentHeight.value == 125
			structure.insulators[0].offset.unit == "INCH"
			structure.insulators[0].offset.value == 125
		when: "applyToLocation"
			changeSet.applyToLocation(locationJSON)
			structure = locationJSON.designs[0].structure
		then:
			structure.insulators[0].attachmentHeight == null
			structure.insulators[0].offset.unit == "INCH"
			structure.insulators[0].offset.value == 125

		when: "revertLocation"
			changeSet.revertLocation(locationJSON)
			structure = locationJSON.designs[0].structure
		then:
			structure.insulators[0].attachmentHeight.unit == "INCH"
			structure.insulators[0].attachmentHeight.value == 125
			structure.insulators[0].offset.unit == "INCH"
			structure.insulators[0].offset.value == 125
		when: "applyToDesign"
			changeSet.applyToDesign(designJSON)
			structure = designJSON.structure
		then:
			structure.insulators[0].attachmentHeight == null
			structure.insulators[0].offset.unit == "INCH"
			structure.insulators[0].offset.value == 125

		when: "revertDesign"
			changeSet.revertDesign(designJSON)
			structure = designJSON.structure
		then:
			structure.insulators[0].attachmentHeight.unit == "INCH"
			structure.insulators[0].attachmentHeight.value == 125
			structure.insulators[0].offset.unit == "INCH"
			structure.insulators[0].offset.value == 125
	}

}
