/*
 * ©2009-2015 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.calc.CalcProjectChangeSet
import groovy.json.JsonSlurper
import groovy.util.logging.Log4j
import spock.lang.Specification

@Log4j
class SupportTypeChangeSetTest extends Specification {

	def "apply and revert"() {
		setup:
			def leanStream = SupportTypeChangeSetTest.getResourceAsStream("/conversions/v4/support-type.json")
		    Map projectJSON = new JsonSlurper().parse(leanStream)
		    Map locationJSON = CalcProjectChangeSet.duplicateAsJson(projectJSON.leads[0].locations[0])
		    Map designJSON = CalcProjectChangeSet.duplicateAsJson(projectJSON.leads[0].locations[0].designs[0])
			SupportTypeChangeSet changeSet = new SupportTypeChangeSet()
		when: "applyToProject"
			changeSet.applyToProject(projectJSON)
			def structure = projectJSON.leads.first().locations.first().designs.first().structure
		then:
			structure.anchors.first().supportedWEPs != null
			structure.anchors.first().supportedWEPs.size() == 0
			structure.crossArms.first().supportedWEPs.size() ==1
			structure.crossArms.first().supportedWEPs.first() == 'WEP#1'
		when: "revertProject"
			changeSet.revertProject(projectJSON)
			structure = projectJSON.leads.first().locations.first().designs.first().structure
		then:
			structure.anchors.first().supportType == 'Other'
			structure.crossArms.first().associatedBacking == 'WEP#1'
		when: "applyToLocation"
			changeSet.applyToLocation(locationJSON)
			structure = locationJSON.designs.first().structure
		then:
			structure.anchors.first().supportedWEPs != null
			structure.anchors.first().supportedWEPs.size() == 0
			structure.crossArms.first().supportedWEPs.size() ==1
			structure.crossArms.first().supportedWEPs.first() == 'WEP#1'
		when: "revertLocation"
			changeSet.revertLocation(locationJSON)
			structure = locationJSON.designs.first().structure
		then:
			structure.anchors.first().supportType == 'Other'
			structure.crossArms.first().associatedBacking == 'WEP#1'
		when: "applyToDesign"
			changeSet.applyToDesign(designJSON)
			structure = designJSON.structure
		then:
			structure.anchors.first().supportedWEPs != null
			structure.anchors.first().supportedWEPs.size() == 0
			structure.crossArms.first().supportedWEPs.size() ==1
			structure.crossArms.first().supportedWEPs.first() == 'WEP#1'
		when: "revertDesign"
			changeSet.revertDesign(designJSON)
			structure = designJSON.structure
		then:
			structure.anchors.first().supportType == 'Other'
			structure.crossArms.first().associatedBacking == 'WEP#1'
	}

	def "bisector"() {
		setup:
			def leanStream = SupportTypeChangeSetTest.getResourceAsStream("/conversions/v4/support-type-2.json")
		    Map projectJSON = new JsonSlurper().parse(leanStream)
		    Map locationJSON = CalcProjectChangeSet.duplicateAsJson(projectJSON.leads[0].locations[0])
		    Map designJSON = CalcProjectChangeSet.duplicateAsJson(projectJSON.leads[0].locations[0].designs[0])
			SupportTypeChangeSet changeSet = new SupportTypeChangeSet()
		when: "applyToProject"
			changeSet.applyToProject(projectJSON)
			def structure = projectJSON.leads.first().locations.first().designs.first().structure
		then:
			structure.anchors.first().supportedWEPs.size() == 2
			structure.anchors.first().supportedWEPs.containsAll(["WEP#1","WEP#2"])
		when: "revertProject"
			changeSet.revertProject(projectJSON)
			structure = projectJSON.leads.first().locations.first().designs.first().structure
		then:
			structure.anchors.first().supportType == 'Bisector'
		when: "applyToLocation"
			changeSet.applyToLocation(locationJSON)
			structure = locationJSON.designs.first().structure
		then:
			structure.anchors.first().supportedWEPs.size() == 2
			structure.anchors.first().supportedWEPs.containsAll(["WEP#1","WEP#2"])
		when: "revertLocation"
			changeSet.revertLocation(locationJSON)
			structure = locationJSON.designs.first().structure
		then:
			structure.anchors.first().supportType == 'Bisector'
		when: "applyToDesign"
			changeSet.applyToDesign(designJSON)
			structure = designJSON.structure
		then:
			structure.anchors.first().supportedWEPs.size() == 2
			structure.anchors.first().supportedWEPs.containsAll(["WEP#1","WEP#2"])
		when: "revertDesign"
			changeSet.revertDesign(designJSON)
			structure = designJSON.structure
		then:
			structure.anchors.first().supportType == 'Bisector'
	}

	def "test downconvert when bisecting other weps"() {
		def leanStream = SupportTypeChangeSetTest.getResourceAsStream("/conversions/v4/supportedWeps-for-downconvert.json")
		Map projectJSON = new JsonSlurper().parse(leanStream)
		def changeset = new SupportTypeChangeSet()
		when: "revert project"
			changeset.revertProject(projectJSON)
			def anchor = projectJSON.leads[0].locations[0].designs[0].structure.anchors[0] as Map
		then:
			anchor.supportType == "Other"
	}
}
