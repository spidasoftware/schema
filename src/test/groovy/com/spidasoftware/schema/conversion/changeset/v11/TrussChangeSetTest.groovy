/*
 * Copyright (c) 2024 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v11

import groovy.json.JsonSlurper
import spock.lang.Specification

class TrussChangeSetTest extends Specification {

    static TrussChangeSet trussChangeSet

    def setupSpec() {
        trussChangeSet = new TrussChangeSet()
    }

    def "revert client data"() {
        setup:
            InputStream stream = TrussChangeSetTest.getResourceAsStream("/conversions/v11/Truss-project-v11.json")
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
        expect:
            json.clientData.trusses
            json.clientData.assemblies[0].assemblyStructure.trusses
        when:
            boolean changed = trussChangeSet.revertClientData(json.clientData as Map)
        then:
            changed
            !json.clientData.trusses
            !json.clientData.assemblies[0].assemblyStructure.trusses
    }

    def "revert project json"() {
        setup:
            InputStream stream = TrussChangeSetTest.getResourceAsStream("/conversions/v11/Truss-project-v11.json")
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
        expect:
            json.clientData.trusses
            json.clientData.assemblies[0].assemblyStructure.trusses
            json.leads[0].locations[0].designs[0].structure.trusses
        when:
            trussChangeSet.revertProject(json)
        then:
            !json.clientData.trusses
            !json.clientData.assemblies[0].assemblyStructure.trusses
            !json.leads[0].locations[0].designs[0].structure.trusses
    }

    def "revert results"() {
        setup:
            InputStream stream = TrussChangeSetTest.getResourceAsStream("/conversions/v11/Truss-results-v11.json")
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
        expect:
            json.analyzedStructure.trusses
        when:
            trussChangeSet.revertResults(json)
        then:
            !json.analyzedStructure.trusses
    }
}
