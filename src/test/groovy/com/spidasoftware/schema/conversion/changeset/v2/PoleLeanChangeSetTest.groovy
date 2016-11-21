package com.spidasoftware.schema.conversion.changeset.v2

import groovy.util.logging.Log4j
import net.sf.json.groovy.JsonSlurper
import spock.lang.Specification
/**
 * Created: 11/26/14
 * Copyright SPIDAWeb
 */
@Log4j
class PoleLeanChangeSetTest extends Specification {
	PoleLeanChangeSet changeSet
	def "Revert"() {
		def leanStream = PoleLeanChangeSetTest.getResourceAsStream("/conversions/v2/pole-lean.json")
		def json = new JsonSlurper().parse(leanStream)
		changeSet = new PoleLeanChangeSet()
		changeSet.revert(json)

			def pole = json.leads.first().locations.first().designs.first().structure.pole
		expect:
			pole.containsKey("leanAngle") == false
			pole.containsKey("leanDirection") == false

	}
}
