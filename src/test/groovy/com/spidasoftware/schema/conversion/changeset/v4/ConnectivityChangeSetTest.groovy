package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.calc.CalcProjectChangeSet
import groovy.json.JsonSlurper
import spock.lang.Specification

class ConnectivityChangeSetTest extends Specification {

    def "revert"() {
        setup:
            def leanStream = ConnectivityChangeSet.getResourceAsStream("/conversions/v4/wire-connection.json")
            Map projectJSON = new JsonSlurper().parse(leanStream)
            Map locationJSON = CalcProjectChangeSet.duplicateAsJson(projectJSON.leads[0].locations[0])
            Map designJSON = CalcProjectChangeSet.duplicateAsJson(projectJSON.leads[0].locations[0].designs[0])
            ConnectivityChangeSet changeSet = new ConnectivityChangeSet()
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

    def "don't crash on empty structure"() {
        def design
        when:
            design = [:]
            new ConnectivityChangeSet().revertDesign(design)
        then: "don't crash"
            true
        when:
            design = [structure:[:]]
            new ConnectivityChangeSet().revertDesign(design)
        then: "also don't crash"
            true
    }

    def "revertWeps"() {
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

    def "revertSpanGuys"() {
        def design = [structure: [
                spanGuys: [
                        [
                                connectionId:"1234"
                        ]
                ]
        ]]
        when:
            new ConnectivityChangeSet().revertDesign(design)
        then:
            design.structure.spanGuys.first().connectionId == null
    }

    def "revert spanPoints"() {
        def design
        when:
            design = [structure: [
                    spanPoints: [
                            [heights:
                                     [
                                             [
                                              wire        : "Wire#1",
                                              height      : [value: 1,
                                                             unit : "FOOT"]
                                             ]
                                     ],
                             connectionId: "1234"
                            ]
                    ]
            ]
            ]
            new ConnectivityChangeSet().revertDesign(design)
        then: "should have removed connectionId"
            design.structure.spanPoints.first().connectionId == null
            design.structure.spanPoints.first().heights.first().wire == "Wire#1"
            design.structure.spanPoints.first().heights.first().height.value  == 1
    }
}
