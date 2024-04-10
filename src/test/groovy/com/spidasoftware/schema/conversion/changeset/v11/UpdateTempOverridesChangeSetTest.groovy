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

    def "test clear clearance results"() {
        setup:
            def stream = UpdateTempOverridesChangeSetTest.getResourceAsStream("/conversions/v11/UpdateTempOverrides-project-v11.json")
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
            Map clearanceResults = json.leads[0].locations[0].designs[0].clearanceResults
        expect:
            json.leads[0].locations[0].designs[0].clearanceResults
        when: "check thermal and not physical"
            json.leads[0].locations[0].designs[0].clearanceCases[0].checkThermalTemperatureOverride = true
            json.leads[0].locations[0].designs[0].clearanceCases[0].checkPhysicalTemperatureOverride = false
            changeSet.revertProject(json)
        then: "clearance results are not cleared"
            json.leads[0].locations[0].designs[0].clearanceResults
        when: "check physical but not thermal"
            changeSet.applyToProject(json)
            json.leads[0].locations[0].designs[0].clearanceCases[0].checkThermalTemperatureOverride = false
            json.leads[0].locations[0].designs[0].clearanceCases[0].checkPhysicalTemperatureOverride = true
            changeSet.revertProject(json)
        then: "clearance results are cleared"
            !json.leads[0].locations[0].designs[0].clearanceResults
        when: "both thermal and physical are checked"
            changeSet.applyToProject(json)
            json.leads[0].locations[0].designs[0].clearanceResults = clearanceResults
            json.leads[0].locations[0].designs[0].clearanceCases[0].checkThermalTemperatureOverride = true
            json.leads[0].locations[0].designs[0].clearanceCases[0].checkPhysicalTemperatureOverride = true
            changeSet.revertProject(json)
        then: "clearance results are cleared"
            !json.leads[0].locations[0].designs[0].clearanceResults
        when: "both thermal and physical are not checked"
            changeSet.applyToProject(json)
            json.leads[0].locations[0].designs[0].clearanceResults = clearanceResults
            json.leads[0].locations[0].designs[0].clearanceCases[0].checkThermalTemperatureOverride = false
            json.leads[0].locations[0].designs[0].clearanceCases[0].checkPhysicalTemperatureOverride = false
            changeSet.revertProject(json)
        then: "clearance results are cleared"
            !json.leads[0].locations[0].designs[0].clearanceResults
    }
}
