/*
 * ©2009-2015 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v4

import groovy.util.logging.Log4j
import net.sf.json.JSONObject
import net.sf.json.groovy.JsonSlurper
import spock.lang.Specification

@Log4j
class SpanGuyTypeChangeSetTest extends Specification {

    def "apply and revert"() {
	  	setup:
    		def leanStream = SpanGuyTypeChangeSetTest.getResourceAsStream("/conversions/v4/span-guy-type.json")
	  		JSONObject projectJSON = new JsonSlurper().parse(leanStream)
	  		JSONObject locationJSON = JSONObject.fromObject(projectJSON.leads[0].locations[0])
	  		JSONObject designJSON = JSONObject.fromObject(projectJSON.leads[0].locations[0].designs[0])
			SpanGuyTypeChangeSet changeSet = new SpanGuyTypeChangeSet()
	  	when: "applyToProject"
	  		changeSet.applyToProject(projectJSON)
	  		def spanGuy = projectJSON.leads.first().locations.first().designs.first().structure.spanGuys.first()
	  	then:
			spanGuy.type == "SUPPORT"
		when: "revertProject"
	  		changeSet.revertProject(projectJSON)
	  		spanGuy = projectJSON.leads.first().locations.first().designs.first().structure.spanGuys.first()
	  	then:
	  		spanGuy.type == null
		when: "applyToLocation"
			changeSet.applyToLocation(locationJSON)
			spanGuy = locationJSON.designs.first().structure.spanGuys.first()
		then:
			spanGuy.type == "SUPPORT"
		when: "revertLocation"
			changeSet.revertLocation(locationJSON)
			spanGuy =locationJSON.designs.first().structure.spanGuys.first()
		then:
			spanGuy.type == null
		when: "applyToDesign"
			changeSet.applyToDesign(designJSON)
			spanGuy = designJSON.structure.spanGuys.first()
		then:
			spanGuy.type == "SUPPORT"
		when: "revertDesign"
			changeSet.revertDesign(designJSON)
			spanGuy = designJSON.structure.spanGuys.first()
		then:
			spanGuy.type == null
	}
}
