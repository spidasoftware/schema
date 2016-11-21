/*
 * ©2009-2015 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v4

import groovy.util.logging.Log4j
import net.sf.json.groovy.JsonSlurper
import spock.lang.Specification

@Log4j
class PhotoDirectionChangeSetTest extends Specification {

	PhotoDirectionChangeSet changeSet

	def "apply and revert"() {

		def leanStream = PhotoDirectionChangeSetTest.getResourceAsStream("/conversions/v4/photo-direction.json")
			def json = new JsonSlurper().parse(leanStream)
			changeSet = new PhotoDirectionChangeSet()

		when:
			changeSet.apply(json)
		then:
			json.leads.first().locations.first().images.first().direction == 'N/A'

		when:
			changeSet.revert(json)
		then:
			json.leads.first().locations.first().images.first().direction == null
	}

}
