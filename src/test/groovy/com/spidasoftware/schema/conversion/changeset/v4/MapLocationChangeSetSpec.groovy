package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.ChangeSet
import groovy.json.JsonSlurper
import spock.lang.Specification

class MapLocationChangeSetSpec extends Specification {

    void "test revert removes map location"() {
        setup:
            def leanStream = getClass().getResourceAsStream("/conversions/v4/map-location.json")
            Map projectJSON = new JsonSlurper().parse(leanStream)
            Map locationJSON = ChangeSet.duplicateAsJson(projectJSON.leads[0].locations[0])
            Map designJSON = ChangeSet.duplicateAsJson(projectJSON.leads[0].locations[0].designs[0])
            MapLocationChangeSet mapLocationChangeSet = new MapLocationChangeSet()
        expect:
            projectJSON.leads[0].locations[0].designs[0].mapLocation != null
        when: "revertProject"
            mapLocationChangeSet.revertProject(projectJSON)
        then:
            projectJSON.leads[0].locations[0].designs[0].mapLocation == null
        when: "revertLocation"
            mapLocationChangeSet.revertLocation(locationJSON)
        then:
            locationJSON.designs[0].mapLocation == null
        when: "revertDesign"
            mapLocationChangeSet.revertDesign(designJSON)
        then:
            designJSON.mapLocation == null
    }
}
