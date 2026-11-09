/*
 * Copyright (c) 2026 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v14

import com.spidasoftware.schema.conversion.changeset.Converter
import com.spidasoftware.schema.conversion.changeset.ConverterUtils
import com.spidasoftware.schema.validation.Validator
import groovy.json.JsonSlurper
import spock.lang.Specification

class AnalyzedStructureGroupChangesetTest extends Specification {

    static final String RESULTS_SCHEMA = "/schema/spidacalc/results/results.schema"

    static AnalyzedStructureGroupChangeset changeset

    def setupSpec() {
        changeset = new AnalyzedStructureGroupChangeset()
    }

    private static Map loadV13Results() {
        InputStream stream = AnalyzedStructureGroupChangesetTest.getResourceAsStream("/conversions/v14/minimalResults-v13-results.json")
        Map json = new JsonSlurper().parse(stream) as Map
        stream.close()
        return json
    }

    def "converter up-converts v13 results to a schema-valid v14 structure group"() {
        setup:
            Map json = loadV13Results()
            Converter converter = ConverterUtils.getConverterInstance(RESULTS_SCHEMA)
            Validator validator = new Validator()
        expect: "v13 results are not valid against the current schema"
            json.version == 13
            !validator.validateAndReport(RESULTS_SCHEMA, json).isSuccess()
        when:
            converter.convert(json, 14)
        then:
            json.version == 14
            !json.containsKey("analyzedStructure")
            json.analyzedStructureGroup.primaryStructureId == "66fd9b782cec05af05a684a1"
            json.analyzedStructureGroup.primaryStructure.wires == []
            json.analyzedStructureGroup.neighbors == []
        and: "converted results validate against the current schema"
            validator.validateAndReport(RESULTS_SCHEMA, json).isSuccess()
    }

    def "converter down-converts v14 results back to v13 analyzedStructure"() {
        setup:
            Map json = loadV13Results()
            Converter converter = ConverterUtils.getConverterInstance(RESULTS_SCHEMA)
            converter.convert(json, 14)
        expect:
            json.containsKey("analyzedStructureGroup")
        when:
            converter.convert(json, 13)
        then:
            json.version == 13
            !json.containsKey("analyzedStructureGroup")
            json.analyzedStructure.wires == []
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

    def "applyToDesign converts inline detailed results"() {
        setup:
            Map structure = [pole: [glc: [value: 1.0, unit: "METRE"]]]
            Map design = [analysisDetails: [detailedResults: [id: "results-1", results: [], analyzedStructure: structure]]]
        when:
            changeset.applyToDesign(design)
        then:
            Map detailedResults = design.analysisDetails.detailedResults
            !detailedResults.containsKey("analyzedStructure")
            detailedResults.analyzedStructureGroup.primaryStructure.is(structure)
    }

    def "revertDesign converts inline detailed results"() {
        setup:
            Map structure = [pole: [glc: [value: 1.0, unit: "METRE"]]]
            Map design = [analysisDetails: [detailedResults: [id: "results-1", results: [],
                    analyzedStructureGroup: [primaryStructureId: "results-1", primaryStructure: structure, neighbors: [], wireConnections: [], spanGuyConnections: []]]]]
        when:
            changeset.revertDesign(design)
        then:
            Map detailedResults = design.analysisDetails.detailedResults
            !detailedResults.containsKey("analyzedStructureGroup")
            detailedResults.analyzedStructure.is(structure)
    }

    def "applyToDesign and revertDesign ignore designs without inline detailed results"() {
        setup:
            Map noAnalysisDetails = [structure: [:]]
            Map resultIdOnly = [analysisDetails: [resultId: "results-1"]]
        when:
            changeset.applyToDesign(noAnalysisDetails)
            changeset.revertDesign(noAnalysisDetails)
            changeset.applyToDesign(resultIdOnly)
            changeset.revertDesign(resultIdOnly)
        then:
            noAnalysisDetails == [structure: [:]]
            resultIdOnly == [analysisDetails: [resultId: "results-1"]]
    }

    def "design converter down-converts inline detailed results to v13 analyzedStructure"() {
        setup:
            Map structure = [pole: [glc: [value: 1.0, unit: "METRE"]]]
            Map design = [schema: "/schema/spidacalc/calc/design.schema", version: 14, structure: [:],
                    analysisDetails: [detailedResults: [id: "results-1", results: [],
                            analyzedStructureGroup: [primaryStructureId: "results-1", primaryStructure: structure, neighbors: [], wireConnections: [], spanGuyConnections: []]]]]
            Converter converter = ConverterUtils.getConverterInstance("/schema/spidacalc/calc/design.schema")
        when:
            converter.convert(design, 13)
        then:
            design.version == 13
            !design.analysisDetails.detailedResults.containsKey("analyzedStructureGroup")
            design.analysisDetails.detailedResults.analyzedStructure.is(structure)
        when:
            converter.convert(design, 14)
        then:
            design.version == 14
            !design.analysisDetails.detailedResults.containsKey("analyzedStructure")
            design.analysisDetails.detailedResults.analyzedStructureGroup.primaryStructure.is(structure)
    }
}
