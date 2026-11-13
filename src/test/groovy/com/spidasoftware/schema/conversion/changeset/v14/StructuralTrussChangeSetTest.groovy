/*
 * Copyright (c) 2026 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v14

import com.spidasoftware.schema.validation.Validator
import groovy.json.JsonSlurper
import spock.lang.Specification

class StructuralTrussChangeSetTest extends Specification {

    static final List<String> V13_TRUSS_PROPERTIES = ["id", "version", "size", "lateralArea", "poleSideArea", "shape", "defaultHeightAboveGround", "momentCapacity", "aliases"]

    static StructuralTrussChangeSet changeSet = new StructuralTrussChangeSet()

    private static Map load(String resource) {
        InputStream stream = StructuralTrussChangeSetTest.getResourceAsStream(resource)
        try {
            return new JsonSlurper().parse(stream) as Map
        } finally {
            stream.close()
        }
    }

    def "v14 property list covers every optional truss property"() {
        setup:
            Map trussSchema = new JsonSlurper().parse(new File("resources/schema/spidacalc/client/truss.schema")) as Map
            // id, version and aliases are v13 properties shared by every client item
            Set<String> optionalProperties = (trussSchema.properties as Map).keySet() - (trussSchema.required as List) - ["id", "version", "aliases"]
        expect:
            StructuralTrussChangeSet.V14_TRUSS_PROPERTIES as Set == optionalProperties
    }

    def "apply client data is a no-op"() {
        setup:
            Map json = load("/conversions/v14/structuralTruss-v14-clientfile.json")
            Map before = load("/conversions/v14/structuralTruss-v14-clientfile.json")
        when:
            boolean applied = changeSet.applyToClientData(json)
        then:
            !applied
            json == before
    }

    def "revert client data"() {
        setup:
            Map json = load("/conversions/v14/structuralTruss-v14-clientfile.json")
            List<Map> trusses = json.trusses as List<Map>
        expect:
            trusses.size() == 3
            trusses[0].modelingType == "MOMENT_CAPACITY"
            trusses[1].modelingType == "STRUCTURAL"
            trusses[1].keySet().containsAll(StructuralTrussChangeSet.V14_TRUSS_PROPERTIES)
            !trusses[2].containsKey("modelingType")
        when:
            boolean reverted = changeSet.revertClientData(json)
        then:
            reverted
        and: "no trusses deleted"
            trusses.size() == 3
        and: "every truss is a v13 moment capacity truss"
            trusses.every { Map truss -> V13_TRUSS_PROPERTIES.containsAll(truss.keySet()) }
            trusses.every { Map truss -> truss.momentCapacity != null }
            trusses[0].aliases == [[id: "LEGACY"]]
            trusses[1].aliases == [[id: "STRUCT"]]
            trusses[1].size == "structural truss"
            trusses[1].momentCapacity == [unit: "NEWTON_METRE", value: 286200.0]
        and: "valid"
            new Validator().validateAndReport("/schema/spidacalc/client/data.schema", json).isSuccess()
        when: "reverting again"
            boolean revertedAgain = changeSet.revertClientData(json)
        then: "nothing left to do"
            !revertedAgain
    }

    def "revert client data without trusses"() {
        setup:
            Map json = [schema: "/schema/spidacalc/client/data.schema", version: 14, name: "Empty.client"]
        when:
            boolean reverted = changeSet.revertClientData(json)
        then:
            !reverted
            json.size() == 3
    }

    def "revert client data with only v13 trusses"() {
        setup:
            Map json = load("/conversions/v14/structuralTruss-v14-clientfile.json")
            json.trusses = [(json.trusses as List<Map>)[2]]
        when:
            boolean reverted = changeSet.revertClientData(json)
        then:
            !reverted
            (json.trusses as List<Map>)[0].size() == 6
    }

    def "revert project"() {
        setup:
            Map json = load("/conversions/v14/structuralTruss-v14-project.json")
            Map clientData = json.clientData as Map
            List<Map> designs = json.leads[0].locations[0].designs as List<Map>
        expect:
            json.clientFileVersion != null
            clientData.hash != null
            (clientData.trusses as List<Map>)[0].modelingType == "STRUCTURAL"
            designs.every { it.analysisCurrent == true }
            designs[0].analysisDetails.detailedResults.clientData.hash != null
            designs[0].analysisDetails.detailedResults.clientData.trusses[0].modelingType == "STRUCTURAL"
            designs[1].analysisDetails.detailedResults.clientData.hash != null
            !designs[1].analysisDetails.detailedResults.clientData.trusses[0].containsKey("modelingType")
        when:
            changeSet.revertProject(json)
        then: "project client data reverted and its hash dropped"
            !json.containsKey("clientFileVersion")
            !clientData.containsKey("hash")
            (clientData.trusses as List<Map>)[0].keySet() == ["size", "lateralArea", "poleSideArea", "shape", "defaultHeightAboveGround", "momentCapacity"] as Set
        and: "design results holding a structural truss are reverted and marked stale"
            designs[0].analysisCurrent == false
            !designs[0].analysisDetails.detailedResults.clientData.containsKey("hash")
            (designs[0].analysisDetails.detailedResults.clientData.trusses[0] as Map).keySet() == ["size", "lateralArea", "poleSideArea", "shape", "defaultHeightAboveGround", "momentCapacity"] as Set
        and: "design results holding only v13 trusses are untouched"
            designs[1].analysisCurrent == true
            designs[1].analysisDetails.detailedResults.clientData.hash != null
    }

    def "revert results"() {
        setup:
            Map json = load("/conversions/v14/structuralTruss-v14-results.json")
            Map clientData = json.clientData as Map
        expect:
            clientData.hash != null
            (clientData.trusses as List<Map>)[1].modelingType == "STRUCTURAL"
        when:
            boolean reverted = changeSet.revertResults(json)
        then:
            reverted
            !clientData.containsKey("hash")
            (clientData.trusses as List<Map>).every { Map truss -> V13_TRUSS_PROPERTIES.containsAll(truss.keySet()) }
            (clientData.trusses as List<Map>)[1].momentCapacity == [unit: "NEWTON_METRE", value: 286200.0]
    }
}
