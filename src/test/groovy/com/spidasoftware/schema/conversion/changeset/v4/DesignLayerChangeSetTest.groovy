/*
 * ©2009-2017 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v4

import spock.lang.Specification

class DesignLayerChangeSetTest extends Specification {

	DesignLayerChangeSet designLayerChangeSet = new DesignLayerChangeSet()
	def "ApplyToDesign"() {
		def design = [:]
		when:
			designLayerChangeSet.applyToDesign(design)
		then:
			design.isEmpty()

	}

	def "RevertDesign"() {
		def design = [layerType: "Measured"]
		when:
			designLayerChangeSet.revertDesign(design)
		then:
			design.isEmpty()
	}
}
