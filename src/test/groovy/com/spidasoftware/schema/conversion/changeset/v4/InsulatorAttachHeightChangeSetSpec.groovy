/*
 * ©2009-2015 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v4

import groovy.util.logging.Log4j
import net.sf.json.groovy.JsonSlurper
import spock.lang.Specification

@Log4j
class InsulatorAttachHeightChangeSetSpec extends Specification {

	def "apply and revert"() {

			def stream = InsulatorAttachHeightChangeSetSpec.getResourceAsStream("/conversions/v4/insulator-attach-height.json")
			def json = new JsonSlurper().parse(stream)
			def changeSet = new InsulatorAttachHeightChangeSet()

			def structure = json.leads[0].locations[0].designs[0].structure
			assert structure.insulators[0].attachmentHeight.unit == "FOOT"
			assert structure.insulators[0].attachmentHeight.value == 10.4

		when:
			changeSet.apply(json)
			structure = json.leads[0].locations[0].designs[0].structure
		then:
			structure.insulators[0].attachmentHeight == null
			structure.insulators[0].offset.unit == "INCH"
			structure.insulators[0].offset.value == 125

		when:
			changeSet.revert(json)
			structure = json.leads[0].locations[0].designs[0].structure
		then:
			structure.insulators[0].attachmentHeight.unit == "INCH"
			structure.insulators[0].attachmentHeight.value == 125
			structure.insulators[0].offset.unit == "INCH"
			structure.insulators[0].offset.value == 125
	}

}
