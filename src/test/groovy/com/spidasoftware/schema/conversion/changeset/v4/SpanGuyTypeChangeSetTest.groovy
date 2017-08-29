/*
 * ©2009-2015 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.ChangeSet
import groovy.json.JsonSlurper
import groovy.util.logging.Log4j
import spock.lang.Specification

@Log4j
class SpanGuyTypeChangeSetTest extends Specification {

    def "apply and revert"() {
	  	setup:
    		def leanStream = SpanGuyTypeChangeSetTest.getResourceAsStream("/conversions/v4/span-guy-type.json")
	  	    Map projectJSON = new JsonSlurper().parse(leanStream)
	  	    Map locationJSON = ChangeSet.duplicateAsJson(projectJSON.leads[0].locations[0])
	  	    Map designJSON = ChangeSet.duplicateAsJson(projectJSON.leads[0].locations[0].designs[0])
			SpanGuyTypeChangeSet changeSet = new SpanGuyTypeChangeSet()
	  	when: "applyToProject"
	  		changeSet.applyToProject(projectJSON)
	  		def spanGuy = projectJSON.leads.first().locations.first().designs.first().structure.spanGuys.first()
	  	then:
			spanGuy.type == "SUPPORT"
		when: " add loads and revertProject"
			spanGuy.loads = [["test": "test"]]
	  		changeSet.revertProject(projectJSON)
	  		spanGuy = projectJSON.leads.first().locations.first().designs.first().structure.spanGuys.first()
	  	then:
	  		spanGuy.type == null
			spanGuy.loads == null
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
