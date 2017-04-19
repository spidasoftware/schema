/*
 * ©2009-2017 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v4

import net.sf.json.JSONObject
import spock.lang.Specification

class DesignLayerChangeSetTest extends Specification {

	DesignLayerChangeSet designLayerChangeSet = new DesignLayerChangeSet()
	def "ApplyToDesign"() {
		def design = [:] as JSONObject
		when:
			designLayerChangeSet.applyToDesign(design)
		then:
			design.isEmpty()

	}

	def "RevertDesign"() {
		def design = [layerType: "Measured"] as JSONObject
		when:
			designLayerChangeSet.revertDesign(design)
		then:
			design.isEmpty()
	}
}
