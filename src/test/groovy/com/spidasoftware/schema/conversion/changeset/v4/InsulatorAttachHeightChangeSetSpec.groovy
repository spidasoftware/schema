/*
 * ©2009-2015 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v4

import groovy.util.logging.Log4j
import net.sf.json.JSONObject
import net.sf.json.groovy.JsonSlurper
import spock.lang.Specification

@Log4j
class InsulatorAttachHeightChangeSetSpec extends Specification {

	def "apply and revert"() {
		setup:
			def stream = InsulatorAttachHeightChangeSetSpec.getResourceAsStream("/conversions/v4/insulator-attach-height.json")
			JSONObject projectJSON = new JsonSlurper().parse(stream)
			JSONObject locationJSON = JSONObject.fromObject(projectJSON.leads[0].locations[0])
			JSONObject designJSON = JSONObject.fromObject(projectJSON.leads[0].locations[0].designs[0])
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
