/*
 * Copyright (c) 2024 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v12

import groovy.json.JsonSlurper
import spock.lang.Specification

class TensionOverrideChangesetTest extends Specification {

    static TensionOverrideChangeset changeset

    def setupSpec() {
        changeset = new TensionOverrideChangeset()
    }

    def "clientData"() {
        setup:
            InputStream stream = TensionOverrideChangesetTest.getResourceAsStream("/conversions/v12/tensionOverride-v12-project.json")
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
        expect:
            json.clientData.assemblies[0].assemblyStructure.wires[0].tensionOverride
        when: "down-convert"
            boolean changed = changeset.revertClientData(json.clientData as Map)
        then:
            changed
            !json.clientData.assemblies[0].assemblyStructure.wires[0].tensionOverride
    }

    def "project"() {
        setup:
            InputStream stream = TensionOverrideChangesetTest.getResourceAsStream("/conversions/v12/tensionOverride-v12-project.json")
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
        expect:
            json.clientData.assemblies[0].assemblyStructure.wires[0].tensionOverride
            json.leads[0].locations[0].designs[0].structure.wires[0].tensionOverride
        when: "down-convert"
            changeset.revertProject(json)
        then:
            !json.clientData.assemblies[0].assemblyStructure.wires[0].tensionOverride
            !json.leads[0].locations[0].designs[0].structure.wires[0].tensionOverride
    }

    def "results"() {
        setup:
            InputStream stream = TensionOverrideChangesetTest.getResourceAsStream("/conversions/v12/tensionOverride-v12-results.json")
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
        expect:
            json.clientData.assemblies[0].assemblyStructure.wires[0].tensionOverride
            json.analyzedStructure.wires[0].tensionOverride
        when:
            boolean changed = changeset.revertResults(json)
        then:
            changed
            !json.clientData.assemblies[0].assemblyStructure.wires[0].tensionOverride
            !json.analyzedStructure.wires[0].tensionOverride
    }

    def "revertStructure"() {
        setup:
            Map structure
            boolean changed
        when: "structure contains tensionOverride"
            structure = [wires: [[tensionOverride: "INITIAL"]]]
            changed = changeset.revertStructure(structure)
        then:
            changed
            !structure.wires[0].tensionOverride
        when: "structure doesn't contain tensionOverride"
            structure = [wires: [[:]]]
            changed = changeset.revertStructure(structure)
        then:
            !changed
    }
}
