/*
 * ©2009-2015 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v4

import groovy.util.logging.Log4j
import net.sf.json.groovy.JsonSlurper
import spock.lang.Specification

@Log4j
class SupportTypeChangeSetTest extends Specification {

	SupportTypeChangeSet changeSet

	def "apply and revert"() {

		def leanStream = SupportTypeChangeSetTest.getResourceAsStream("/conversions/v4/support-type.json")
			def json = new JsonSlurper().parse(leanStream)
			changeSet = new SupportTypeChangeSet()

		when:
			changeSet.apply(json)
			def structure = json.leads.first().locations.first().designs.first().structure
		then:
			structure.anchors.first().supportedWEPs != null
			structure.anchors.first().supportedWEPs.size() == 0
			structure.crossArms.first().supportedWEPs.size() ==1
			structure.crossArms.first().supportedWEPs.first() == 'WEP#1'

		when:
			changeSet.revert(json)
			structure = json.leads.first().locations.first().designs.first().structure
		then:
			structure.anchors.first().supportType == 'Other'
			structure.crossArms.first().associatedBacking == 'WEP#1'
	}

	def "bisector"() {
		def leanStream = SupportTypeChangeSetTest.getResourceAsStream("/conversions/v4/support-type-2.json")
			def json = new JsonSlurper().parse(leanStream)
			changeSet = new SupportTypeChangeSet()

		when:
			changeSet.apply(json)
			def structure = json.leads.first().locations.first().designs.first().structure
		then:
			structure.anchors.first().supportedWEPs.size() == 2
			structure.anchors.first().supportedWEPs.containsAll(["WEP#1","WEP#2"])

		when:
			changeSet.revert(json)
			structure = json.leads.first().locations.first().designs.first().structure
		then:
			structure.anchors.first().supportType == 'Bisector'
	}


}
