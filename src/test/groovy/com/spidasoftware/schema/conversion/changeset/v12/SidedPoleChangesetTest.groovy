/*
 * Copyright (c) 2025 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v12

import com.spidasoftware.schema.conversion.changeset.ConversionException
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import spock.lang.Specification

class SidedPoleChangesetTest extends Specification {

    static SidedPoleChangeset changeset

    def setupSpec() {
        changeset = new SidedPoleChangeset()
    }

    def "project"() {
        setup:
            String text = SidedPoleChangesetTest.getResourceAsStream("/conversions/v13/sided-pole-shapes-v13-project.json").getText("UTF-8")
            Map json = new JsonSlurper().parseText(text) as Map
        expect:
            text.count("RECTANGLE") == 6
            text.count("POLYGON") == 6
        when: "down-convert"
            changeset.revertProject(json)
            String reverted = JsonOutput.toJson(json)
        then:
            !reverted.contains("RECTANGLE")
            !reverted.contains("POLYGON")
    }

    def "revertClientData throws for non-round pole"() {
        when:
            changeset.revertClientData([poles: [[shape: "SQUARE"]]])
        then:
            thrown(ConversionException)
    }

    def "revertResults tolerates results without analysisCaseDetails"() {
        setup:
            Map results = [results: [[:], [analysisCaseDetails: [overrides: [areaMultipliers: [RECTANGLE: 1.625]]]]]]
        when:
            boolean changed = changeset.revertResults(results)
        then:
            changed
            !results.results[1].analysisCaseDetails.overrides.areaMultipliers.containsKey("RECTANGLE")
    }

    def "revertLoadCase"() {
        setup:
            Map loadCase
            boolean changed
        when: "load case contains sided overrides"
            loadCase = [overrides: [areaMultipliers: [RECTANGLE: 1.625, POLYGON: 1.0, ROUND: 1.0]]]
            changed = changeset.revertLoadCase(loadCase)
        then:
            changed
            loadCase.overrides.areaMultipliers == [ROUND: 1.0]
        when: "load case doesn't contain sided overrides"
            loadCase = [overrides: [areaMultipliers: [ROUND: 1.0]]]
            changed = changeset.revertLoadCase(loadCase)
        then:
            !changed
    }
}
