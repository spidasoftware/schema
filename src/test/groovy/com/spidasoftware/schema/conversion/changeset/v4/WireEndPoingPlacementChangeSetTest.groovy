package com.spidasoftware.schema.conversion.changeset.v4

import net.sf.json.JSONObject
import net.sf.json.groovy.JsonSlurper
import spock.lang.Specification

class WireEndPointPlacementChangeSetTest extends Specification {

    def "revert"() {
        when:
            def leanStream = WireEndPointPlacementChangeSet.getResourceAsStream("/conversions/v4/wire-endpoint-placement.json")
            JSONObject projectJSON = new JsonSlurper().parse(leanStream)
            JSONObject locationJSON = JSONObject.fromObject(projectJSON.leads[0].locations[0])
            JSONObject designJSON = JSONObject.fromObject(projectJSON.leads[0].locations[0].designs[0])
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
