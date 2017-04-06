package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.ChangeSet
import groovy.json.JsonSlurper
import spock.lang.Specification

class LocationWepChangeSetTest extends Specification {

    def "revert"() {
        setup:
            def leanStream = SpanGuyTypeChangeSetTest.getResourceAsStream("/conversions/v4/location-wep.json")
            Map projectJSON = new JsonSlurper().parse(leanStream)
            Map locationJSON = ChangeSet.duplicateAsJson(projectJSON.leads[0].locations[0])
            Map designJSON = ChangeSet.duplicateAsJson(projectJSON.leads[0].locations[0].designs[0])
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
