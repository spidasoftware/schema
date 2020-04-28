/*
 * ©2009-2020 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v7

import groovy.json.JsonSlurper
import spock.lang.Specification

class RelativeElevationChangeSetTest extends Specification {

	def "RevertDesign"() {
		def stream = RelativeElevationChangeSet.getResourceAsStream("/conversions/v7/relativeElevation.json".toString())
		Map json = new JsonSlurper().parse(stream)
		stream.close()
		when:
			new RelativeElevationChangeSet().revertDesign(json)
		then:
			json.structure.wireEndPoints[0].relativeElevation == null
	}
}
