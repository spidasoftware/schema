package com.spidasoftware.schema.conversion.changeset.v4

import groovy.json.JsonSlurper
import spock.lang.Specification

class WireEndPointPlacementChangeSetTest extends Specification {

    def "revert"() {
        when:
            def leanStream = WireEndPointPlacementChangeSet.getResourceAsStream("/conversions/v4/wire-endpoint-placement.json")
            Map projectJSON = new JsonSlurper().parse(leanStream)
            WireEndPointPlacementChangeSet changeSet = new WireEndPointPlacementChangeSet()
        then:
            def wire = projectJSON.leads.first().locations.first().designs.first().structure.wires.first()
            wire.wireEndPointPlacement != null
        when: "revertProject"
            changeSet.revertProject(projectJSON)
        then:
            wire.wireEndPointPlacement == null

    }
}
