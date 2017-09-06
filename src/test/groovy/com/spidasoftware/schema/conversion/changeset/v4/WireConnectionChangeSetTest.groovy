package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.ChangeSet
import groovy.json.JsonSlurper
import spock.lang.Specification

class WireConnectionChangeSetTest extends Specification {

    def "revert"() {
        setup:
            def leanStream = WireConnectionChangeSet.getResourceAsStream("/conversions/v4/wire-connection.json")
            Map projectJSON = new JsonSlurper().parse(leanStream)
            Map locationJSON = ChangeSet.duplicateAsJson(projectJSON.leads[0].locations[0])
            Map designJSON = ChangeSet.duplicateAsJson(projectJSON.leads[0].locations[0].designs[0])
            WireConnectionChangeSet changeSet = new WireConnectionChangeSet()
        when: "revertProject"
            changeSet.revertProject(projectJSON)
            def wire = projectJSON.leads.first().locations.first().designs.first().structure.wires.first()
        then:
            wire.connectionId == null
            wire.connectedWire == null
        when: "revertLocation"
            changeSet.revertLocation(locationJSON)
            wire = locationJSON.designs.first().structure.wires.first()
        then:
            wire.connectionId == null
            wire.connectedWire == null
        when: "revertDesign"
            changeSet.revertDesign(designJSON)
            wire = designJSON.structure.wires.first()
        then:
            wire.connectionId == null
            wire.connectedWire == null
    }
}
