/*
 * ©2009-2018 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v5

import spock.lang.Specification

class LocationRemedyChangesetTest extends Specification {

	LocationRemedyChangeset changeSet = new LocationRemedyChangeset()

	def "applyToLocation"() {
		def location = [remedies:[[description:"test 123"], [description:"test 456"]]]
		when:
			changeSet.applyToLocation(location)
		then:
			location.remedy.statements[0].description == "test 123"
			location.remedies == null

	}

	def "revertLocation"() {
		def location = [remedy:[statements:[[description:"test 123"], [description:"test 456"]]]]
		when:
			changeSet.revertLocation(location)
		then:
			location.remedies[0].description == "test 123"
			location.remedy == null
	}
}
