package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.ChangeSet
import groovy.json.JsonSlurper
import spock.lang.Specification

class WireConnectionIdChangeSetTest extends Specification {

    def "revert"() {
        setup:
            def leanStream = WireConnectionIdChangeSet.getResourceAsStream("/conversions/v4/wire-connection-id.json")
            Map projectJSON = new JsonSlurper().parse(leanStream)
            Map locationJSON = ChangeSet.duplicateAsJson(projectJSON.leads[0].locations[0])
            Map designJSON = ChangeSet.duplicateAsJson(projectJSON.leads[0].locations[0].designs[0])
            WireConnectionIdChangeSet changeSet = new WireConnectionIdChangeSet()
        when: "revertProject"
            changeSet.revertProject(projectJSON)
            def wire = projectJSON.leads.first().locations.first().designs.first().structure.wires.first()
        then:
            wire.connectionId == null
        when: "revertLocation"
            changeSet.revertLocation(locationJSON)
            wire = locationJSON.designs.first().structure.wires.first()
        then:
            wire.connectionId == null
        when: "revertDesign"
            changeSet.revertDesign(designJSON)
            wire = designJSON.structure.wires.first()
        then:
            wire.connectionId == null
    }
}
