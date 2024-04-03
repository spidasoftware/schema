/*
 * Copyright (c) 2024 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v11

import groovy.json.JsonSlurper
import spock.lang.Specification

class UpdateTempOverridesChangeSetTest extends Specification {

    static UpdateTempOverridesChangeSet changeSet

    def setupSpec() {
        changeSet = new UpdateTempOverridesChangeSet()
    }

    def "apply/revert client data"() {
        setup:
            def stream = UpdateTempOverridesChangeSetTest.getResourceAsStream("/conversions/v10/TempOverride-client.json")
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
        expect:
            !json.clearanceCases[0].checkThermalTemperatureOverride
            !json.clearanceCases[0].checkPhysicalTemperatureOverride
            json.clearanceCases[0].upper[0].thermalTemperatureOverride
            json.clearanceCases[0].upper[1].thermalTemperatureOverride
            !json.clearanceCases[0].upper[2].thermalTemperatureOverride
            !json.clearanceCases[0].upper[0].temperatureOverride
            !json.clearanceCases[0].upper[1].temperatureOverride
            !json.clearanceCases[0].upper[2].temperatureOverride
        when:
            boolean converted = changeSet.applyToClientData(json)
        then:
            converted
            json.clearanceCases[0].checkThermalTemperatureOverride == true
            json.clearanceCases[0].checkPhysicalTemperatureOverride == false
            !json.clearanceCases[0].upper[0].thermalTemperatureOverride
            !json.clearanceCases[0].upper[1].thermalTemperatureOverride
            !json.clearanceCases[0].upper[2].thermalTemperatureOverride
            json.clearanceCases[0].upper[0].temperatureOverride
            json.clearanceCases[0].upper[1].temperatureOverride
            !json.clearanceCases[0].upper[2].temperatureOverride

        when:
            boolean reverted = changeSet.revertClientData(json)
        then:
            reverted
            !json.clearanceCases[0].checkThermalTemperatureOverride
            !json.clearanceCases[0].checkPhysicalTemperatureOverride
            json.clearanceCases[0].upper[0].thermalTemperatureOverride
            json.clearanceCases[0].upper[1].thermalTemperatureOverride
            !json.clearanceCases[0].upper[2].thermalTemperatureOverride
            !json.clearanceCases[0].upper[0].temperatureOverride
            !json.clearanceCases[0].upper[1].temperatureOverride
            !json.clearanceCases[0].upper[2].temperatureOverride
    }

    def "apply/revert project json"() {
        setup:
            def stream = UpdateTempOverridesChangeSetTest.getResourceAsStream("/conversions/v10/TempOverride-project.json")
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
        expect:
            !json.defaultClearanceCases[0].checkThermalTemperatureOverride
            !json.defaultClearanceCases[0].checkPhysicalTemperatureOverride
            json.defaultClearanceCases[0].upper[0].thermalTemperatureOverride
            json.defaultClearanceCases[0].upper[1].thermalTemperatureOverride
            !json.defaultClearanceCases[0].upper[2].thermalTemperatureOverride
            !json.defaultClearanceCases[0].upper[0].temperatureOverride
            !json.defaultClearanceCases[0].upper[1].temperatureOverride
            !json.defaultClearanceCases[0].upper[2].temperatureOverride
        when:
            changeSet.applyToProject(json)
        then:
            json.defaultClearanceCases[0].checkThermalTemperatureOverride == true
            json.defaultClearanceCases[0].checkPhysicalTemperatureOverride == false
            !json.defaultClearanceCases[0].upper[0].thermalTemperatureOverride
            !json.defaultClearanceCases[0].upper[1].thermalTemperatureOverride
            !json.defaultClearanceCases[0].upper[2].thermalTemperatureOverride
            json.defaultClearanceCases[0].upper[0].temperatureOverride
            json.defaultClearanceCases[0].upper[1].temperatureOverride
            !json.defaultClearanceCases[0].upper[2].temperatureOverride
        when:
            changeSet.revertProject(json)
        then:
            !json.defaultClearanceCases[0].checkThermalTemperatureOverride
            !json.defaultClearanceCases[0].checkPhysicalTemperatureOverride
            json.defaultClearanceCases[0].upper[0].thermalTemperatureOverride
            json.defaultClearanceCases[0].upper[1].thermalTemperatureOverride
            !json.defaultClearanceCases[0].upper[2].thermalTemperatureOverride
            !json.defaultClearanceCases[0].upper[0].temperatureOverride
            !json.defaultClearanceCases[0].upper[1].temperatureOverride
            !json.defaultClearanceCases[0].upper[2].temperatureOverride
    }
}
