package com.spidasoftware.schema.conversion.changeset.v4

import net.sf.json.groovy.JsonSlurper
import spock.lang.Specification

class MapLocationChangeSetSpec extends Specification {

    void "test revert removes map location"() {
        setup:
            def json = new JsonSlurper().parse(getClass().getResourceAsStream("/conversions/v4/map-location.json"))
            MapLocationChangeSet mapLocationChangeSet = new MapLocationChangeSet()
            assert json.leads[0].locations[0].designs[0].mapLocation != null
        when:
            mapLocationChangeSet.revert(json)
        then:
            json.leads[0].locations[0].designs[0].mapLocation == null
    }
}
