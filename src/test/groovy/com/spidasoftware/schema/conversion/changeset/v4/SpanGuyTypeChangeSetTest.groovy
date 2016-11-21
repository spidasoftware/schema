/*
 * ©2009-2015 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v4

import groovy.util.logging.Log4j
import net.sf.json.groovy.JsonSlurper
import spock.lang.Specification

@Log4j
class SpanGuyTypeChangeSetTest extends Specification {

	SpanGuyTypeChangeSet changeSet

  def "apply and revert"() {

    def leanStream = SpanGuyTypeChangeSetTest.getResourceAsStream("/conversions/v4/span-guy-type.json")
		def json = new JsonSlurper().parse(leanStream)
		changeSet = new SpanGuyTypeChangeSet()

	  when:
	  	changeSet.apply(json)
	  	def spanGuy = 	  json.leads.first().locations.first().designs.first().structure.spanGuys.first()

	  then:
		spanGuy.type == "SUPPORT"

	  when:
	  	changeSet.revert(json)
	  	spanGuy =	json.leads.first().locations.first().designs.first().structure.spanGuys.first()
	  then:
	  	spanGuy.type == null
	}

}
