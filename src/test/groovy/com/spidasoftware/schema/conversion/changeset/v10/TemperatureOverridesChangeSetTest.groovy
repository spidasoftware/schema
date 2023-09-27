/*
 * Copyright (c) 2023 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v10

import groovy.json.JsonSlurper
import spock.lang.Specification

class TemperatureOverridesChangeSetTest extends Specification {

    static TemperatureOverridesChangeSet changeSet

    def setupSpec() {
        changeSet = new TemperatureOverridesChangeSet()
    }

    def "apply client data"() {
        setup:
            def stream = TemperatureOverridesChangeSetTest.getResourceAsStream("/conversions/v10/TempOverride-client.json")
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
        expect:
            json.clearanceCases[0].upper[0].thermalTemperatureOverride == [value: 25.0, unit: "FAHRENHEIT"]
            json.clearanceCases[0].clearances[0].upper.thermalTemperatureOverride == [value: 0, unit: "FAHRENHEIT"]
            json.clearanceCases[0].upper[1].thermalTemperatureOverride == [value: 50.0, unit: "FAHRENHEIT"]
            json.clearanceCases[0].clearances[1].upper.thermalTemperatureOverride == null
            json.clearanceCases[0].upper[2].thermalTemperatureOverride == null
            json.clearanceCases[0].clearances[2].upper.thermalTemperatureOverride == [value: 75.0, unit: "FAHRENHEIT"]
        when:
            changeSet.applyToClientData(json)
        then:
            json.clearanceCases[0].upper[0].thermalTemperatureOverride == [value: 25.0, unit: "FAHRENHEIT"]
            json.clearanceCases[0].clearances[0].upper.thermalTemperatureOverride == [value: 25.0, unit: "FAHRENHEIT"]
            json.clearanceCases[0].upper[1].thermalTemperatureOverride == [value: 50.0, unit: "FAHRENHEIT"]
            json.clearanceCases[0].clearances[1].upper.thermalTemperatureOverride == [value: 50.0, unit: "FAHRENHEIT"]
            json.clearanceCases[0].upper[2].thermalTemperatureOverride == null
            json.clearanceCases[0].clearances[2].upper.thermalTemperatureOverride == null
    }

    def "apply to project"() {
        setup:
            def stream = TemperatureOverridesChangeSetTest.getResourceAsStream("/conversions/v10/TempOverride-project.json")
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
        expect:
            json.defaultClearanceCases[0].upper[0].thermalTemperatureOverride == [value: 25.0, unit: "FAHRENHEIT"]
            json.defaultClearanceCases[0].clearances[0].upper.thermalTemperatureOverride == [value: 0, unit: "FAHRENHEIT"]
            json.defaultClearanceCases[0].upper[1].thermalTemperatureOverride == [value: 50.0, unit: "FAHRENHEIT"]
            json.defaultClearanceCases[0].clearances[1].upper.thermalTemperatureOverride == null
            json.defaultClearanceCases[0].upper[2].thermalTemperatureOverride == null
            json.defaultClearanceCases[0].clearances[2].upper.thermalTemperatureOverride == [value: 75.0, unit: "FAHRENHEIT"]
        when:
            changeSet.applyToProject(json)
        then:
            json.defaultClearanceCases[0].upper[0].thermalTemperatureOverride == [value: 25.0, unit: "FAHRENHEIT"]
            json.defaultClearanceCases[0].clearances[0].upper.thermalTemperatureOverride == [value: 25.0, unit: "FAHRENHEIT"]
            json.defaultClearanceCases[0].upper[1].thermalTemperatureOverride == [value: 50.0, unit: "FAHRENHEIT"]
            json.defaultClearanceCases[0].clearances[1].upper.thermalTemperatureOverride == [value: 50.0, unit: "FAHRENHEIT"]
            json.defaultClearanceCases[0].upper[2].thermalTemperatureOverride == null
            json.defaultClearanceCases[0].clearances[2].upper.thermalTemperatureOverride == null

    }
}
