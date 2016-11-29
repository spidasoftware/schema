package com.spidasoftware.schema.conversion.changeset.v4

import net.sf.json.groovy.JsonSlurper

class WireConnectionIdChangeSetTest extends spock.lang.Specification {

    WireConnectionIdChangeSet changeSet

    def "revert"() {

        def leanStream = WireConnectionIdChangeSet.getResourceAsStream("/conversions/v4/wire-connection-id.json")
        def json = new JsonSlurper().parse(leanStream)
        changeSet = new WireConnectionIdChangeSet()

        when:
            changeSet.revert(json)
            def wire = json.leads.first().locations.first().designs.first().structure.wires.first()

        then:
            wire.connectionId == null

        when:
            changeSet.revert(json)
            wire = json.leads.first().locations.first().designs.first().structure.wires.first()
        then:
            wire.connectionId == null
    }

}
