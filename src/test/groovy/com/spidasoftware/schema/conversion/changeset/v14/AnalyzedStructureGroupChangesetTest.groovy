/*
 * Copyright (c) 2026 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v14

import spock.lang.Specification

class AnalyzedStructureGroupChangesetTest extends Specification {

    static AnalyzedStructureGroupChangeset changeset

    def setupSpec() {
        changeset = new AnalyzedStructureGroupChangeset()
    }

    def "applyToResults wraps analyzedStructure in a structure group"() {
        setup:
            Map structure = [pole: [glc: [value: 1.0, unit: "METRE"]]]
            Map json = [id: "results-1", results: [], analyzedStructure: structure]
        when:
            boolean changed = changeset.applyToResults(json)
        then:
            !changed
            !json.containsKey("analyzedStructure")
            json.analyzedStructureGroup.primaryStructureId == "results-1"
            json.analyzedStructureGroup.primaryStructure.is(structure)
            json.analyzedStructureGroup.neighbors == []
            json.analyzedStructureGroup.wireConnections == []
            json.analyzedStructureGroup.spanGuyConnections == []
    }

    def "applyToResults does nothing without analyzedStructure"() {
        setup:
            Map json = [id: "results-1", results: []]
        when:
            boolean changed = changeset.applyToResults(json)
        then:
            !changed
            !json.containsKey("analyzedStructureGroup")
            !json.containsKey("analyzedStructure")
    }

    def "revertResults unwraps primary structure from structure group"() {
        setup:
            Map structure = [pole: [glc: [value: 1.0, unit: "METRE"]]]
            Map json = [
                id: "results-1",
                results: [],
                analyzedStructureGroup: [
                    primaryStructureId: "results-1",
                    primaryStructure: structure,
                    neighbors: [[structureId: "neighbor-1", structure: [:]]],
                    wireConnections: [],
                    spanGuyConnections: []
                ]
            ]
        when:
            boolean changed = changeset.revertResults(json)
        then:
            !changed
            !json.containsKey("analyzedStructureGroup")
            json.analyzedStructure.is(structure)
    }

    def "revertResults does nothing without analyzedStructureGroup"() {
        setup:
            Map json = [id: "results-1", results: []]
        when:
            boolean changed = changeset.revertResults(json)
        then:
            !changed
            !json.containsKey("analyzedStructure")
            !json.containsKey("analyzedStructureGroup")
    }

    def "apply then revert round-trips"() {
        setup:
            Map structure = [pole: [glc: [value: 1.0, unit: "METRE"]]]
            Map json = [id: "results-1", results: [], analyzedStructure: structure]
        when:
            changeset.applyToResults(json)
            changeset.revertResults(json)
        then:
            json == [id: "results-1", results: [], analyzedStructure: structure]
    }
}
