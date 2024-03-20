/*
 * Copyright (c) 2024 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v11

import groovy.json.JsonSlurper
import spock.lang.Specification

class PoleCutTopChangeSetTest extends Specification {

    static PoleCutTopChangeSet changeSet

    def setupSpec() {
        changeSet = new PoleCutTopChangeSet()
    }

    def "apply/revert project json"() {
        setup:
            InputStream stream = PoleCutTopChangeSetTest.getResourceAsStream("/conversions/v11/PoleCutTop-project-v11.json")
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
        expect: "cut top is present"
            json.leads[0].locations[0].designs[0].structure.pole.cutTop == [value: 0, unit: "METRE"]
        when: "down convert"
            changeSet.revertProject(json)
        then: "cut top is removed"
            !json.leads[0].locations[0].designs[0].structure.pole.cutTop
        when: "up convert"
            changeSet.applyToProject(json)
        then: "cut top is added"
            json.leads[0].locations[0].designs[0].structure.pole.cutTop == [value: 0, unit: "METRE"]
    }

    def "apply/revert results json"() {
        setup:
            InputStream stream = PoleCutTopChangeSetTest.getResourceAsStream("/conversions/v10/DecimalDirections-results.json")
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
            boolean changed
        expect: "cut top is not present"
            !json.analyzedStructure.pole.cutTop
        when: "up convert"
            changed = changeSet.applyToResults(json)
        then: "cut top is added"
            changed
            json.analyzedStructure.pole.cutTop == [value: 0, unit: "METRE"]
        when: "down convert"
            changed = changeSet.revertResults(json)
        then: "cut top is removed"
            changed
            !json.analyzedStructure.pole.cutTop
    }

}
