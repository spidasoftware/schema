/*
 * ©2009-2020 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v8

import groovy.json.JsonSlurper
import spock.lang.Specification

class RelativeElevationChangeSetTest extends Specification {

	def "ApplyToDesign"() {
		def stream = RelativeElevationChangeSet.getResourceAsStream("/conversions/v8/RelativeElevationApplyToDesign.json".toString())
		Map json = new JsonSlurper().parse(stream)
		stream.close()
		when:
			new RelativeElevationChangeSet().applyToDesign(json)
		then:
			//from metre and radian
			json.structure.wireEndPoints[0].relativeElevation.value == 7.9391946829320545
			json.structure.wireEndPoints[0].relativeElevation.unit == "METRE"
			//from foot and degree angle
			json.structure.wireEndPoints[1].relativeElevation.value == 10.585592910576073
			json.structure.wireEndPoints[1].relativeElevation.unit == "METRE"
	}

	def "RevertDesign"() {
		def stream = RelativeElevationChangeSet.getResourceAsStream("/conversions/v8/RelativeElevationRevertDesign.json".toString())
		Map json = new JsonSlurper().parse(stream)
		stream.close()
		when:
			new RelativeElevationChangeSet().revertDesign(json)
		then:
			//from metre
			json.structure.wireEndPoints[0].inclination.value == 0.08693354666361731
			json.structure.wireEndPoints[0].inclination.unit == "RADIAN"
			//from foot
			json.structure.wireEndPoints[1].inclination.value == 0.17453292519943295
			json.structure.wireEndPoints[1].inclination.unit == "RADIAN"
			//mix and match foot and metre
			json.structure.wireEndPoints[2].inclination.value == 0.07947558659675234
			json.structure.wireEndPoints[2].inclination.unit == "RADIAN"
			//removed relativeElevation
			json.structure.wireEndPoints[0].relativeElevation == null
			json.structure.wireEndPoints[1].relativeElevation == null
			json.structure.wireEndPoints[2].relativeElevation == null
	}
}
