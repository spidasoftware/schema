package com.spidasoftware.schema.conversion.changeset.v4

import net.sf.json.JSONObject
import net.sf.json.groovy.JsonSlurper
import spock.lang.Specification

class LocationWepChangeSetTest extends Specification {

    def "revert"() {
        setup:
            def leanStream = SpanGuyTypeChangeSetTest.getResourceAsStream("/conversions/v4/location-wep.json")
            JSONObject projectJSON = new JsonSlurper().parse(leanStream)
            JSONObject locationJSON = JSONObject.fromObject(projectJSON.leads[0].locations[0])
            JSONObject designJSON = JSONObject.fromObject(projectJSON.leads[0].locations[0].designs[0])
            LocationWepChangeSet changeSet = new LocationWepChangeSet()
        when: "revertProject"
            changeSet.revertProject(projectJSON)
            def wireEndPoint = projectJSON.leads.first().locations.first().designs.first().structure.wireEndPoints.first()
        then:
            wireEndPoint.connectionId == null
        when: "revertLocation"
            changeSet.revertLocation(locationJSON)
            wireEndPoint = locationJSON.designs.first().structure.wireEndPoints.first()
        then:
            wireEndPoint.connectionId == null
        when: "revertDesign"
            changeSet.revertDesign(designJSON)
            wireEndPoint = designJSON.structure.wireEndPoints.first()
        then:
            wireEndPoint.connectionId == null
    }

}
