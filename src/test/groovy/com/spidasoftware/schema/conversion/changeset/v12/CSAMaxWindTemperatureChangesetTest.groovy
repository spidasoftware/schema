/*
 * Copyright (c) 2024 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v12

import groovy.json.JsonSlurper
import spock.lang.Specification

class CSAMaxWindTemperatureChangesetTest extends Specification {

    static CSAMaxWindTemperatureChangeset changeset

    def setupSpec() {
        changeset = new CSAMaxWindTemperatureChangeset()
    }

    def "clientData"() {
        setup:
            InputStream stream = CSAMaxWindTemperatureChangesetTest.getResourceAsStream("/conversions/v12/csaMaxWindTemperature-v11-project.json")
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
            boolean changed
        expect:
            json.clientData.analysisCases[0].type == "CSA 2020 Maximum Wind"
            !json.clientData.analysisCases[0].overrides.temperature
            json.clientData.analysisCases[0].valuesApplied.temperature == [unit: "CELSIUS", value: -20.0]
        when: "up convert"
            changed = changeset.applyToClientData(json.clientData as Map)
        then:
            changed
            json.clientData.analysisCases[0].overrides.temperature == [unit: "CELSIUS", value: 15.0]
            json.clientData.analysisCases[0].valuesApplied.temperature == [unit: "CELSIUS", value: 15.0]
        when: "down convert"
            changed = changeset.revertClientData(json.clientData as Map)
        then:
            changed
            !json.clientData.analysisCases[0].overrides.temperature
    }

    def "project"() {
        setup:
            InputStream stream = CSAMaxWindTemperatureChangesetTest.getResourceAsStream("/conversions/v12/csaMaxWindTemperature-v11-project.json")
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
        expect:
            json.clientData.analysisCases[0].type == "CSA 2020 Maximum Wind"
            !json.clientData.analysisCases[0].overrides.temperature
            json.clientData.analysisCases[0].valuesApplied.temperature == [unit: "CELSIUS", value: -20.0]
            json.defaultLoadCases[0].type == "CSA 2020 Maximum Wind"
            !json.defaultLoadCases[0].overrides.temperature
            json.defaultLoadCases[0].valuesApplied.temperature == [unit: "CELSIUS", value: -20.0]
            json.leads[0].locations[0].designs[0].analysis[0].analysisCaseDetails.type == "CSA 2020 Maximum Wind"
            !json.leads[0].locations[0].designs[0].analysis[0].analysisCaseDetails.overrides.temperature
            json.leads[0].locations[0].designs[0].analysis[0].analysisCaseDetails.valuesApplied.temperature == [unit: "CELSIUS", value: -20.0]
        when: "up convert"
            changeset.applyToProject(json)
        then:
            json.clientData.analysisCases[0].overrides.temperature == [unit: "CELSIUS", value: 15.0]
            json.clientData.analysisCases[0].valuesApplied.temperature == [unit: "CELSIUS", value: 15.0]
            json.defaultLoadCases[0].overrides.temperature == [unit: "CELSIUS", value: 15.0]
            json.defaultLoadCases[0].valuesApplied.temperature == [unit: "CELSIUS", value: 15.0]
            json.leads[0].locations[0].designs[0].analysis[0].analysisCaseDetails.overrides.temperature == [unit: "CELSIUS", value: 15.0]
            json.leads[0].locations[0].designs[0].analysis[0].analysisCaseDetails.valuesApplied.temperature == [unit: "CELSIUS", value: 15.0]
        when: "down convert"
            changeset.revertProject(json)
        then:
            !json.clientData.analysisCases[0].overrides.temperature
            !json.defaultLoadCases[0].overrides.temperature
            !json.leads[0].locations[0].designs[0].analysis[0].analysisCaseDetails.overrides.temperature
    }

    def "results"() {
        setup:
            InputStream stream = CSAMaxWindTemperatureChangesetTest.getResourceAsStream("/conversions/v12/csaMaxWindTemperature-v11-results.json")
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
            boolean changed
        expect:
            json.clientData.analysisCases[0].type == "CSA 2020 Maximum Wind"
            !json.clientData.analysisCases[0].overrides.temperature
            json.clientData.analysisCases[0].valuesApplied.temperature == [unit: "CELSIUS", value: -20.0]
            json.results[0].analysisCaseDetails.type == "CSA 2020 Maximum Wind"
            !json.results[0].analysisCaseDetails.overrides.temperature
            json.results[0].analysisCaseDetails.valuesApplied.temperature == [unit: "CELSIUS", value: -20.0]
        when: "up convert"
            changed = changeset.applyToResults(json)
        then:
            changed
            json.clientData.analysisCases[0].overrides.temperature == [unit: "CELSIUS", value: 15.0]
            json.clientData.analysisCases[0].valuesApplied.temperature == [unit: "CELSIUS", value: 15.0]
            json.results[0].analysisCaseDetails.overrides.temperature == [unit: "CELSIUS", value: 15.0]
            json.results[0].analysisCaseDetails.valuesApplied.temperature == [unit: "CELSIUS", value: 15.0]
        when: "down convert"
            changed = changeset.revertResults(json)
        then:
            changed
            !json.clientData.analysisCases[0].overrides.temperature
            !json.results[0].analysisCaseDetails.overrides.temperature
    }

    def "applyTemperatureToLoadCase"() {
        setup:
            Map loadCase
            boolean changed
        when: "load case is not csa max wind"
            loadCase = [type: "CSA 2015", overrides: [:], valuesApplied: [:]]
            changed = changeset.applyTemperatureToLoadCase(loadCase)
        then:
            !changed
            !loadCase.overrides
            !loadCase.valuesApplied
        when: "load case is csa max wind without temp override"
            loadCase = [type: "CSA 2020 Maximum Wind", overrides: [:], valuesApplied: [:]]
            changed = changeset.applyTemperatureToLoadCase(loadCase)
        then:
            changed
            (loadCase.overrides as Map) == [temperature: [unit: "CELSIUS", value: 15]]
            (loadCase.valuesApplied as Map) == [temperature: [unit: "CELSIUS", value: 15]]
        when: "load case is csa max wind with temp override"
            loadCase = [type: "CSA 2020 Maximum Wind", overrides: [temperature: [unit: "CELSIUS", value: 40.0]], valuesApplied: [:]]
            changed = changeset.applyTemperatureToLoadCase(loadCase)
        then:
            !changed
            (loadCase.overrides as Map).temperature == [unit: "CELSIUS", value: 40.0]
    }

    def "revertTemperatureFromLoadCase"() {
        setup:
            Map loadCase
            boolean changed
        when: "overrides doesn't have temperature"
            loadCase = [type: "CSA 2020 Maximum Wind", overrides: [:]]
            changed = changeset.revertTemperatureFromLoadCase(loadCase)
        then:
            !changed
        when: "overrides has temperature equal to 15"
            loadCase = [type: "CSA 2020 Maximum Wind", overrides: [temperature: [unit: "CELSIUS", value: 15]]]
            changed = changeset.revertTemperatureFromLoadCase(loadCase)
        then:
            changed
            !(loadCase.overrides as Map).temperature
        when: "overrides has temperature not equal to 15"
            loadCase = [type: "CSA 2020 Maximum Wind", overrides: [temperature: [unit: "CELSIUS", value: 40.0]]]
            changed = changeset.revertTemperatureFromLoadCase(loadCase)
        then:
            !changed
            (loadCase.overrides as Map).temperature == [unit: "CELSIUS", value: 40.0]
    }
}
