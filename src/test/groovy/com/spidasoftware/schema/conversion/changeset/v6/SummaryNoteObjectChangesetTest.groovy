/*
 * ©2009-2018 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v6

import spock.lang.Specification

class SummaryNoteObjectChangesetTest extends Specification {

	SummaryNoteObjectChangeset changeSet = new SummaryNoteObjectChangeset()

	def "applyToLocation"() {
		setup:
			def location = [summaryNotes:["note 123", "note abc"]]
		when:
			changeSet.applyToLocation(location)
		then:
			location.summaryNotes.size() == 2
			location.summaryNotes[0] instanceof Map
			location.summaryNotes[1] instanceof Map
			location.summaryNotes[0].description == "note 123"
			location.summaryNotes[1].description == "note abc"
	}

	def "revertLocation"() {
		setup:
			def location = [summaryNotes:[["description":"note 123"], ["description":"note abc"]]]
		when:
			changeSet.revertLocation(location)
		then:
			location.summaryNotes.size() == 2
			location.summaryNotes[0] == "note 123"
			location.summaryNotes[1] == "note abc"
	}
}
