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
        expect:
            !json.clientData.analysisCases[0].overrides.temperature
            json.clientData.analysisCases[1].overrides.temperature.value == 15.0
        when: "up convert"
            changeset.applyToClientData(json.clientData as Map)
        then:
            json.clientData.analysisCases[0].overrides.temperature.value == -20.0
            !json.clientData.analysisCases[1].overrides.temperature
        when: "down convert"
            changeset.revertClientData(json.clientData as Map)
        then:
            !json.clientData.analysisCases[0].overrides.temperature
            json.clientData.analysisCases[1].overrides.temperature.value == 15.0
    }

    def "project"() {
        setup:
            InputStream stream = CSAMaxWindTemperatureChangesetTest.getResourceAsStream("/conversions/v12/csaMaxWindTemperature-v11-project.json")
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
        expect:
            !json.clientData.analysisCases[0].overrides.temperature
            json.clientData.analysisCases[1].overrides.temperature.value == 15.0
            !json.defaultLoadCases[0].overrides.temperature
            json.defaultLoadCases[1].overrides.temperature.value == 15.0
            !json.leads[0].locations[0].designs[0].analysis[0].analysisCaseDetails.overrides.temperature
            json.leads[0].locations[0].designs[0].analysis[1].analysisCaseDetails.overrides.temperature.value == 15.0
        when: "up convert"
            changeset.applyToProject(json)
        then:
            json.clientData.analysisCases[0].overrides.temperature.value == -20.0
            !json.clientData.analysisCases[1].overrides.temperature
            json.defaultLoadCases[0].overrides.temperature.value == -20.0
            !json.defaultLoadCases[1].overrides.temperature
            json.leads[0].locations[0].designs[0].analysis[0].analysisCaseDetails.overrides.temperature.value == -20.0
            !json.leads[0].locations[0].designs[0].analysis[1].analysisCaseDetails.overrides.temperature
        when: "down convert"
            changeset.revertProject(json)
        then:
            !json.clientData.analysisCases[0].overrides.temperature
            json.clientData.analysisCases[1].overrides.temperature.value == 15.0
            !json.defaultLoadCases[0].overrides.temperature
            json.defaultLoadCases[1].overrides.temperature.value == 15.0
            !json.leads[0].locations[0].designs[0].analysis[0].analysisCaseDetails.overrides.temperature
            json.leads[0].locations[0].designs[0].analysis[1].analysisCaseDetails.overrides.temperature.value == 15.0
    }

    def "results"() {
        setup:
            InputStream stream = CSAMaxWindTemperatureChangesetTest.getResourceAsStream("/conversions/v12/csaMaxWindTemperature-v11-results.json")
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
        expect:
            !json.clientData.analysisCases[0].overrides.temperature
            json.clientData.analysisCases[1].overrides.temperature.value == 15.0
            !json.results[0].analysisCaseDetails.overrides.temperature
            json.results[1].analysisCaseDetails.overrides.temperature.value == 15.0
        when: "up convert"
            changeset.applyToResults(json)
        then:
            json.clientData.analysisCases[0].overrides.temperature.value == -20.0
            !json.clientData.analysisCases[1].overrides.temperature
            json.results[0].analysisCaseDetails.overrides.temperature.value == -20.0
            !json.results[1].analysisCaseDetails.overrides.temperature
        when: "down convert"
            changeset.revertResults(json)
        then:
            !json.clientData.analysisCases[0].overrides.temperature
            json.clientData.analysisCases[1].overrides.temperature.value == 15.0
            !json.results[0].analysisCaseDetails.overrides.temperature
            json.results[1].analysisCaseDetails.overrides.temperature.value == 15.0
    }

    def "applyTemperatureToLoadCase"() {
        setup:
            Map loadCase
        when: "old csa max wind load case"
            loadCase = [type: "CSA 2020 Maximum Wind", overrides: [:]]
            changeset.applyTemperatureToLoadCase(loadCase)
        then: "create override with old default value"
            (loadCase.overrides as Map).temperature == [unit: "CELSIUS", value: -20.0]
        when: "new csa max wind load case"
            loadCase = [type: "CSA 2020 Maximum Wind", overrides: [temperature: [unit: "CELSIUS", value: 15.0]]]
            changeset.applyTemperatureToLoadCase(loadCase)
        then: "remove un-needed override"
            (loadCase.overrides as Map).temperature == null
        when: "csa max wind load case with custom override"
            loadCase = [type: "CSA 2020 Maximum Wind", overrides: [temperature: [unit: "CELSIUS", value: 100.0]]]
            changeset.applyTemperatureToLoadCase(loadCase)
        then: "leave as is"
            (loadCase.overrides as Map).temperature == [unit: "CELSIUS", value: 100.0]
    }

    def "revertTemperatureFromLoadCase"() {
        setup:
            Map loadCase
        when: "old csa max wind load case"
            loadCase = [type: "CSA 2020 Maximum Wind", overrides: [temperature: [unit: "CELSIUS", value: -20.0]]]
            changeset.revertTemperatureFromLoadCase(loadCase)
        then: "remove un-needed override"
            (loadCase.overrides as Map).temperature == null
        when: "new csa max wind load case"
            loadCase = [type: "CSA 2020 Maximum Wind", overrides: [:]]
            changeset.revertTemperatureFromLoadCase(loadCase)
        then: "create override with new default value"
            (loadCase.overrides as Map).temperature == [unit: "CELSIUS", value: 15.0]
        when: "csa max wind load case with custom override"
            loadCase = [type: "CSA 2020 Maximum Wind", overrides: [temperature: [unit: "CELSIUS", value: 100.0]]]
            changeset.revertTemperatureFromLoadCase(loadCase)
        then:
            (loadCase.overrides as Map).temperature == [unit: "CELSIUS", value: 100.0]
    }
}
