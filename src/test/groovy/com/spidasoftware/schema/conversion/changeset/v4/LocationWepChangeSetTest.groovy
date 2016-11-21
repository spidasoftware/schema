package com.spidasoftware.schema.conversion.changeset.v4

import net.sf.json.groovy.JsonSlurper

class LocationWepChangeSetTest extends spock.lang.Specification {

    LocationWepChangeSet changeSet

    def "revert"() {

        def leanStream = SpanGuyTypeChangeSetTest.getResourceAsStream("/conversions/v4/location-wep.json")
        def json = new JsonSlurper().parse(leanStream)
        changeSet = new LocationWepChangeSet()

        when:
            changeSet.revert(json)
            def wireEndPoint = json.leads.first().locations.first().designs.first().structure.wireEndPoints.first()

        then:
            wireEndPoint.connectionId == null

        when:
            changeSet.revert(json)
            wireEndPoint = json.leads.first().locations.first().designs.first().structure.wireEndPoints.first()
        then:
            wireEndPoint.connectionId == null
    }

}
