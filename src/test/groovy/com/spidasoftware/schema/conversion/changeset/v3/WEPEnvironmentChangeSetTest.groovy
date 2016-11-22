/*
 * ©2009-2015 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v3

import com.spidasoftware.schema.conversion.changeset.v2.PoleLeanChangeSetTest
import groovy.util.logging.Log4j
import net.sf.json.groovy.JsonSlurper
import spock.lang.Specification

@Log4j
class WEPEnvironmentChangeSetTest extends Specification {
	WEPEnvironmentChangeSet changeSet
	def "Revert"() {
		def leanStream = PoleLeanChangeSetTest.getResourceAsStream("/conversions/v3/wep-environment.json")
		def json = new JsonSlurper().parse(leanStream)
		changeSet = new WEPEnvironmentChangeSet()
		changeSet.revert(json)

		expect:
		def wep = json.leads.first().locations.first().designs.first().structure.wireEndPoints
		wep.every {
			it.containsKey("environment") == false
		}

	}
}
