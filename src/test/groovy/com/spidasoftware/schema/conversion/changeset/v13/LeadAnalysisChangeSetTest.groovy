/*
 * Copyright (c) 2026 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v13

import groovy.json.JsonSlurper
import spock.lang.Specification

class LeadAnalysisChangeSetTest extends Specification {

    static LeadAnalysisChangeSet changeSet

    def setupSpec() {
        changeSet = new LeadAnalysisChangeSet()
    }

    def "revert client data"() {
        setup:
            InputStream stream = LeadAnalysisChangeSetTest.getResourceAsStream("/conversions/v13/leadAnalysis-v13-project.json")
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
        expect:
            json.clientData.analysisCases[0].includeNeighborStructures == false
            json.clientData.analysisCases[1].includeNeighborStructures == true
        when:
            boolean reverted = changeSet.revertClientData(json.clientData as Map)
        then:
            reverted
            json.clientData.analysisCases[0].includeNeighborStructures == null
            json.clientData.analysisCases[1].includeNeighborStructures == null
    }

    def "revert project"() {
        setup:
            InputStream stream = LeadAnalysisChangeSetTest.getResourceAsStream("/conversions/v13/leadAnalysis-v13-project.json")
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
        expect:
            json.clientData.analysisCases[0].includeNeighborStructures == false
            json.clientData.analysisCases[1].includeNeighborStructures == true
            json.defaultLoadCases[0].includeNeighborStructures == false
            json.defaultLoadCases[1].includeNeighborStructures == true
            json.leads[0].locations[0].designs[0].analysis[0].analysisCaseDetails.includeNeighborStructures == false
            json.leads[0].locations[0].designs[0].analysis[1].analysisCaseDetails.includeNeighborStructures == true
        when:
            changeSet.revertProject(json)
        then:
            json.clientData.analysisCases[0].includeNeighborStructures == null
            json.clientData.analysisCases[1].includeNeighborStructures == null
            json.defaultLoadCases[0].includeNeighborStructures == null
            json.defaultLoadCases[1].includeNeighborStructures == null
            json.leads[0].locations[0].designs[0].analysis[0].analysisCaseDetails.includeNeighborStructures == null
            json.leads[0].locations[0].designs[0].analysis[1].analysisCaseDetails.includeNeighborStructures == null
    }

    def "revert results"() {
        setup:
            InputStream stream = LeadAnalysisChangeSetTest.getResourceAsStream("/conversions/v13/leadAnalysis-v13-results.json")
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
        expect:
            json.clientData.analysisCases[0].includeNeighborStructures == false
            json.clientData.analysisCases[1].includeNeighborStructures == true
            json.results[0].analysisCaseDetails.includeNeighborStructures == false
            json.results[1].analysisCaseDetails.includeNeighborStructures == true
        when:
            boolean reverted = changeSet.revertResults(json)
        then:
            reverted
            json.clientData.analysisCases[0].includeNeighborStructures == null
            json.clientData.analysisCases[1].includeNeighborStructures == null
            json.results[0].analysisCaseDetails.includeNeighborStructures == null
            json.results[1].analysisCaseDetails.includeNeighborStructures == null
    }
}
