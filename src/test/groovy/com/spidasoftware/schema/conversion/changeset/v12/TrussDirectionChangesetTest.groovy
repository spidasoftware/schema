/*
 * Copyright (c) 2025 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v12

import groovy.json.JsonSlurper
import spock.lang.Specification

class TrussDirectionChangesetTest extends Specification {

    static TrussDirectionChangeset changeset

    void setupSpec() {
        changeset = new TrussDirectionChangeset()
    }

    def "clientData"() {
        setup:
            InputStream stream = TrussDirectionChangesetTest.getResourceAsStream("/conversions/v11/Truss-project-v11.json")
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
        expect:
            json.clientData.assemblies[0].assemblyStructure.trusses[0].direction == 358.5d
            json.clientData.assemblies[0].assemblyStructure.trusses[1].direction == 1.5d
        when: "up-convert"
            changeset.applyToClientData(json.clientData as Map)
        then: "directions get reversed"
            json.clientData.assemblies[0].assemblyStructure.trusses[0].direction == 1.5d
            json.clientData.assemblies[0].assemblyStructure.trusses[1].direction == 358.5d
        when: "down-convert"
            changeset.revertClientData(json.clientData as Map)
        then: "directions get reversed"
            json.clientData.assemblies[0].assemblyStructure.trusses[0].direction == 358.5d
            json.clientData.assemblies[0].assemblyStructure.trusses[1].direction == 1.5d
    }

    def "project"() {
        setup:
            InputStream stream = TrussDirectionChangesetTest.getResourceAsStream("/conversions/v11/Truss-project-v11.json")
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
        expect:
            json.leads[0].locations[0].designs[0].structure.trusses[0].direction == 358.5d
            json.leads[0].locations[0].designs[0].structure.trusses[1].direction == 1.5d
        when: "up-convert"
            changeset.applyToProject(json)
        then: "directions get reversed"
            json.leads[0].locations[0].designs[0].structure.trusses[0].direction == 1.5d
            json.leads[0].locations[0].designs[0].structure.trusses[1].direction == 358.5d
        when: "down-convert"
            changeset.revertProject(json)
        then: "directions get reversed"
            json.leads[0].locations[0].designs[0].structure.trusses[0].direction == 358.5d
            json.leads[0].locations[0].designs[0].structure.trusses[1].direction == 1.5d
    }

    def "results"() {
        setup:
            InputStream stream = TrussDirectionChangesetTest.getResourceAsStream("/conversions/v11/Truss-results-v11.json")
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
        expect:
            json.analyzedStructure.trusses[0].direction == 358.5d
            json.analyzedStructure.trusses[1].direction == 1.5d
        when: "up-convert"
            changeset.applyToResults(json)
        then: "directions get reversed"
            json.analyzedStructure.trusses[0].direction == 1.5d
            json.analyzedStructure.trusses[1].direction == 358.5d
        when: "down-convert"
            changeset.revertResults(json)
        then: "directions get reversed"
            json.analyzedStructure.trusses[0].direction == 358.5d
            json.analyzedStructure.trusses[1].direction == 1.5d
    }

}
