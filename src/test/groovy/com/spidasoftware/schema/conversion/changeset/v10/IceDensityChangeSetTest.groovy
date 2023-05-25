/*
 * ©2009-2023 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v10

import groovy.json.JsonSlurper
import spock.lang.Specification

class IceDensityChangeSetTest extends Specification {

    def "revert client data"() {
        setup:
            def changeSet = new IceDensityChangeSet()
            def stream = IceDensityChangeSet.getResourceAsStream("/conversions/v10/weatherCondition.v10.client.json".toString())
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
        expect:
            json.wireStates[0].designCondition.iceDensity
            json.wireStates[0].highestStressCondition.iceDensity
            json.wireStates[0].creepCondition.iceDensity
            json.clearanceCases[0].upperThermalState.wireState.designCondition.iceDensity
            json.clearanceCases[0].upperThermalState.wireState.highestStressCondition.iceDensity
            json.clearanceCases[0].upperThermalState.wireState.creepCondition.iceDensity
            json.clearanceCases[0].upperPhysicalState.wireState.designCondition.iceDensity
            json.clearanceCases[0].upperPhysicalState.wireState.highestStressCondition.iceDensity
            json.clearanceCases[0].upperPhysicalState.wireState.creepCondition.iceDensity
        when: "apply changeset"
            boolean reverted = changeSet.revertClientData(json)
        then:
            reverted
            !json.wireStates[0].designCondition.iceDensity
            !json.wireStates[0].highestStressCondition.iceDensity
            !json.wireStates[0].creepCondition.iceDensity
            !json.clearanceCases[0].upperThermalState.wireState.designCondition.iceDensity
            !json.clearanceCases[0].upperThermalState.wireState.highestStressCondition.iceDensity
            !json.clearanceCases[0].upperThermalState.wireState.creepCondition.iceDensity
            !json.clearanceCases[0].upperPhysicalState.wireState.designCondition.iceDensity
            !json.clearanceCases[0].upperPhysicalState.wireState.highestStressCondition.iceDensity
            !json.clearanceCases[0].upperPhysicalState.wireState.creepCondition.iceDensity
    }

    def "revert project"() {
        setup:
            def changeSet = new IceDensityChangeSet()
            def stream = IceDensityChangeSet.getResourceAsStream("/conversions/v10/weatherCondition.v10.project.json".toString())
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
        expect:
            json.defaultClearanceCases[1].upperThermalState.wireState.designCondition.iceDensity
            json.defaultClearanceCases[1].upperThermalState.wireState.highestStressCondition.iceDensity
            json.defaultClearanceCases[1].upperThermalState.wireState.creepCondition.iceDensity
            json.defaultClearanceCases[1].upperPhysicalState.wireState.designCondition.iceDensity
            json.defaultClearanceCases[1].upperPhysicalState.wireState.highestStressCondition.iceDensity
            json.defaultClearanceCases[1].upperPhysicalState.wireState.creepCondition.iceDensity
            json.leads[0].locations[0].designs[0].clearanceCases[1].upperThermalState.wireState.designCondition.iceDensity
            json.leads[0].locations[0].designs[0].clearanceCases[1].upperThermalState.wireState.highestStressCondition.iceDensity
            json.leads[0].locations[0].designs[0].clearanceCases[1].upperThermalState.wireState.creepCondition.iceDensity
            json.leads[0].locations[0].designs[0].clearanceCases[1].upperPhysicalState.wireState.designCondition.iceDensity
            json.leads[0].locations[0].designs[0].clearanceCases[1].upperPhysicalState.wireState.highestStressCondition.iceDensity
            json.leads[0].locations[0].designs[0].clearanceCases[1].upperPhysicalState.wireState.creepCondition.iceDensity
            json.leads[0].locations[0].designs[0].clearanceResults.results[1].ruleResults[0].upperWireState.wireState.designCondition.iceDensity
            json.leads[0].locations[0].designs[0].clearanceResults.results[1].ruleResults[1].upperWireState.wireState.designCondition.iceDensity
            json.leads[0].locations[0].designs[0].clearanceResults.results[1].ruleResults[2].upperWireState.wireState.designCondition.iceDensity
            json.leads[0].locations[0].designs[0].clearanceResults.results[2].ruleResults[0].upperWireState.wireState.designCondition.iceDensity
            json.leads[0].locations[0].designs[0].clearanceResults.results[2].ruleResults[0].lowerWireState.wireState.designCondition.iceDensity
            json.leads[0].locations[0].designs[0].clearanceResults.results[2].ruleResults[1].upperWireState.wireState.designCondition.iceDensity
            json.leads[0].locations[0].designs[0].clearanceResults.results[2].ruleResults[1].lowerWireState.wireState.designCondition.iceDensity
            json.leads[0].locations[0].designs[0].clearanceResults.results[2].ruleResults[2].upperWireState.wireState.designCondition.iceDensity
            json.leads[0].locations[0].designs[0].clearanceResults.results[2].ruleResults[2].lowerWireState.wireState.designCondition.iceDensity
        when: "apply changeset"
            changeSet.revertProject(json)
        then:
            !json.defaultClearanceCases[1].upperThermalState.wireState.designCondition.iceDensity
            !json.defaultClearanceCases[1].upperThermalState.wireState.highestStressCondition.iceDensity
            !json.defaultClearanceCases[1].upperThermalState.wireState.creepCondition.iceDensity
            !json.defaultClearanceCases[1].upperPhysicalState.wireState.designCondition.iceDensity
            !json.defaultClearanceCases[1].upperPhysicalState.wireState.highestStressCondition.iceDensity
            !json.defaultClearanceCases[1].upperPhysicalState.wireState.creepCondition.iceDensity
            !json.leads[0].locations[0].designs[0].clearanceCases[1].upperThermalState.wireState.designCondition.iceDensity
            !json.leads[0].locations[0].designs[0].clearanceCases[1].upperThermalState.wireState.highestStressCondition.iceDensity
            !json.leads[0].locations[0].designs[0].clearanceCases[1].upperThermalState.wireState.creepCondition.iceDensity
            !json.leads[0].locations[0].designs[0].clearanceCases[1].upperPhysicalState.wireState.designCondition.iceDensity
            !json.leads[0].locations[0].designs[0].clearanceCases[1].upperPhysicalState.wireState.highestStressCondition.iceDensity
            !json.leads[0].locations[0].designs[0].clearanceCases[1].upperPhysicalState.wireState.creepCondition.iceDensity
            !json.leads[0].locations[0].designs[0].clearanceResults.results[1].ruleResults[0].upperWireState.wireState.designCondition.iceDensity
            !json.leads[0].locations[0].designs[0].clearanceResults.results[1].ruleResults[1].upperWireState.wireState.designCondition.iceDensity
            !json.leads[0].locations[0].designs[0].clearanceResults.results[1].ruleResults[2].upperWireState.wireState.designCondition.iceDensity
            !json.leads[0].locations[0].designs[0].clearanceResults.results[2].ruleResults[0].upperWireState.wireState.designCondition.iceDensity
            !json.leads[0].locations[0].designs[0].clearanceResults.results[2].ruleResults[0].lowerWireState.wireState.designCondition.iceDensity
            !json.leads[0].locations[0].designs[0].clearanceResults.results[2].ruleResults[1].upperWireState.wireState.designCondition.iceDensity
            !json.leads[0].locations[0].designs[0].clearanceResults.results[2].ruleResults[1].lowerWireState.wireState.designCondition.iceDensity
            !json.leads[0].locations[0].designs[0].clearanceResults.results[2].ruleResults[2].upperWireState.wireState.designCondition.iceDensity
            !json.leads[0].locations[0].designs[0].clearanceResults.results[2].ruleResults[2].lowerWireState.wireState.designCondition.iceDensity

    }

    def "apply client data"() {
        setup:
            def changeSet = new IceDensityChangeSet()
            def stream = IceDensityChangeSet.getResourceAsStream("/conversions/v10/weatherCondition.v9.client.json".toString())
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
        expect:
            !json.wireStates[0].designCondition.iceDensity
            !json.wireStates[0].highestStressCondition.iceDensity
            !json.wireStates[0].creepCondition.iceDensity
            !json.clearanceCases[0].upperThermalState.wireState.designCondition.iceDensity
            !json.clearanceCases[0].upperThermalState.wireState.highestStressCondition.iceDensity
            !json.clearanceCases[0].upperThermalState.wireState.creepCondition.iceDensity
            !json.clearanceCases[0].upperPhysicalState.wireState.designCondition.iceDensity
            !json.clearanceCases[0].upperPhysicalState.wireState.highestStressCondition.iceDensity
            !json.clearanceCases[0].upperPhysicalState.wireState.creepCondition.iceDensity
        when: "apply changeset"
            boolean applied = changeSet.applyToClientData(json)
        then:
            applied
            json.wireStates[0].designCondition.iceDensity.value == 917
            json.wireStates[0].designCondition.iceDensity.unit == "KILOGRAM_PER_CUBIC_METRE"
            json.wireStates[0].highestStressCondition.iceDensity.value == 917
            json.wireStates[0].highestStressCondition.iceDensity.unit == "KILOGRAM_PER_CUBIC_METRE"
            json.wireStates[0].creepCondition.iceDensity.value == 917
            json.wireStates[0].creepCondition.iceDensity.unit == "KILOGRAM_PER_CUBIC_METRE"
            json.clearanceCases[0].upperThermalState.wireState.designCondition.iceDensity.value == 917
            json.clearanceCases[0].upperThermalState.wireState.designCondition.iceDensity.unit == "KILOGRAM_PER_CUBIC_METRE"
            json.clearanceCases[0].upperThermalState.wireState.highestStressCondition.iceDensity.value == 917
            json.clearanceCases[0].upperThermalState.wireState.highestStressCondition.iceDensity.unit == "KILOGRAM_PER_CUBIC_METRE"
            json.clearanceCases[0].upperThermalState.wireState.creepCondition.iceDensity.value == 917
            json.clearanceCases[0].upperThermalState.wireState.creepCondition.iceDensity.unit == "KILOGRAM_PER_CUBIC_METRE"
            json.clearanceCases[0].upperPhysicalState.wireState.designCondition.iceDensity.value == 917
            json.clearanceCases[0].upperPhysicalState.wireState.designCondition.iceDensity.unit == "KILOGRAM_PER_CUBIC_METRE"
            json.clearanceCases[0].upperPhysicalState.wireState.highestStressCondition.iceDensity.value == 917
            json.clearanceCases[0].upperPhysicalState.wireState.highestStressCondition.iceDensity.unit == "KILOGRAM_PER_CUBIC_METRE"
            json.clearanceCases[0].upperPhysicalState.wireState.creepCondition.iceDensity.value == 917
            json.clearanceCases[0].upperPhysicalState.wireState.creepCondition.iceDensity.unit == "KILOGRAM_PER_CUBIC_METRE"
    }

    def "apply project"() {
        setup:
            def changeSet = new IceDensityChangeSet()
            def stream = IceDensityChangeSet.getResourceAsStream("/conversions/v10/weatherCondition.v9.project.json".toString())
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
        expect:
            !json.defaultClearanceCases[1].upperThermalState.wireState.designCondition.iceDensity
            !json.defaultClearanceCases[1].upperThermalState.wireState.highestStressCondition.iceDensity
            !json.defaultClearanceCases[1].upperThermalState.wireState.creepCondition.iceDensity
            !json.defaultClearanceCases[1].upperPhysicalState.wireState.designCondition.iceDensity
            !json.defaultClearanceCases[1].upperPhysicalState.wireState.highestStressCondition.iceDensity
            !json.defaultClearanceCases[1].upperPhysicalState.wireState.creepCondition.iceDensity
            !json.leads[0].locations[0].designs[0].clearanceCases[1].upperThermalState.wireState.designCondition.iceDensity
            !json.leads[0].locations[0].designs[0].clearanceCases[1].upperThermalState.wireState.highestStressCondition.iceDensity
            !json.leads[0].locations[0].designs[0].clearanceCases[1].upperThermalState.wireState.creepCondition.iceDensity
            !json.leads[0].locations[0].designs[0].clearanceCases[1].upperPhysicalState.wireState.designCondition.iceDensity
            !json.leads[0].locations[0].designs[0].clearanceCases[1].upperPhysicalState.wireState.highestStressCondition.iceDensity
            !json.leads[0].locations[0].designs[0].clearanceCases[1].upperPhysicalState.wireState.creepCondition.iceDensity
            !json.leads[0].locations[0].designs[0].clearanceResults.results[1].ruleResults[0].upperWireState.wireState.designCondition.iceDensity
            !json.leads[0].locations[0].designs[0].clearanceResults.results[1].ruleResults[1].upperWireState.wireState.designCondition.iceDensity
            !json.leads[0].locations[0].designs[0].clearanceResults.results[1].ruleResults[2].upperWireState.wireState.designCondition.iceDensity
            !json.leads[0].locations[0].designs[0].clearanceResults.results[2].ruleResults[0].upperWireState.wireState.designCondition.iceDensity
            !json.leads[0].locations[0].designs[0].clearanceResults.results[2].ruleResults[0].lowerWireState.wireState.designCondition.iceDensity
            !json.leads[0].locations[0].designs[0].clearanceResults.results[2].ruleResults[1].upperWireState.wireState.designCondition.iceDensity
            !json.leads[0].locations[0].designs[0].clearanceResults.results[2].ruleResults[1].lowerWireState.wireState.designCondition.iceDensity
            !json.leads[0].locations[0].designs[0].clearanceResults.results[2].ruleResults[2].upperWireState.wireState.designCondition.iceDensity
            !json.leads[0].locations[0].designs[0].clearanceResults.results[2].ruleResults[2].lowerWireState.wireState.designCondition.iceDensity
        when: "apply changeset"
            changeSet.applyToProject(json)
        then:
            json.defaultClearanceCases[1].upperThermalState.wireState.designCondition.iceDensity.value == 917
            json.defaultClearanceCases[1].upperThermalState.wireState.designCondition.iceDensity.unit == "KILOGRAM_PER_CUBIC_METRE"
            json.defaultClearanceCases[1].upperThermalState.wireState.highestStressCondition.iceDensity.value == 917
            json.defaultClearanceCases[1].upperThermalState.wireState.highestStressCondition.iceDensity.unit == "KILOGRAM_PER_CUBIC_METRE"
            json.defaultClearanceCases[1].upperThermalState.wireState.creepCondition.iceDensity.value == 917
            json.defaultClearanceCases[1].upperThermalState.wireState.creepCondition.iceDensity.unit == "KILOGRAM_PER_CUBIC_METRE"
            json.defaultClearanceCases[1].upperPhysicalState.wireState.designCondition.iceDensity.value == 917
            json.defaultClearanceCases[1].upperPhysicalState.wireState.designCondition.iceDensity.unit == "KILOGRAM_PER_CUBIC_METRE"
            json.defaultClearanceCases[1].upperPhysicalState.wireState.highestStressCondition.iceDensity.value == 917
            json.defaultClearanceCases[1].upperPhysicalState.wireState.highestStressCondition.iceDensity.unit == "KILOGRAM_PER_CUBIC_METRE"
            json.defaultClearanceCases[1].upperPhysicalState.wireState.creepCondition.iceDensity.value == 917
            json.defaultClearanceCases[1].upperPhysicalState.wireState.creepCondition.iceDensity.unit == "KILOGRAM_PER_CUBIC_METRE"
            json.leads[0].locations[0].designs[0].clearanceCases[1].upperThermalState.wireState.designCondition.iceDensity.value == 917
            json.leads[0].locations[0].designs[0].clearanceCases[1].upperThermalState.wireState.designCondition.iceDensity.unit == "KILOGRAM_PER_CUBIC_METRE"
            json.leads[0].locations[0].designs[0].clearanceCases[1].upperThermalState.wireState.highestStressCondition.iceDensity.value == 917
            json.leads[0].locations[0].designs[0].clearanceCases[1].upperThermalState.wireState.highestStressCondition.iceDensity.unit == "KILOGRAM_PER_CUBIC_METRE"
            json.leads[0].locations[0].designs[0].clearanceCases[1].upperThermalState.wireState.creepCondition.iceDensity.value == 917
            json.leads[0].locations[0].designs[0].clearanceCases[1].upperThermalState.wireState.creepCondition.iceDensity.unit == "KILOGRAM_PER_CUBIC_METRE"
            json.leads[0].locations[0].designs[0].clearanceCases[1].upperPhysicalState.wireState.designCondition.iceDensity.value == 917
            json.leads[0].locations[0].designs[0].clearanceCases[1].upperPhysicalState.wireState.designCondition.iceDensity.unit == "KILOGRAM_PER_CUBIC_METRE"
            json.leads[0].locations[0].designs[0].clearanceCases[1].upperPhysicalState.wireState.highestStressCondition.iceDensity.value == 917
            json.leads[0].locations[0].designs[0].clearanceCases[1].upperPhysicalState.wireState.highestStressCondition.iceDensity.unit == "KILOGRAM_PER_CUBIC_METRE"
            json.leads[0].locations[0].designs[0].clearanceCases[1].upperPhysicalState.wireState.creepCondition.iceDensity.value == 917
            json.leads[0].locations[0].designs[0].clearanceCases[1].upperPhysicalState.wireState.creepCondition.iceDensity.unit == "KILOGRAM_PER_CUBIC_METRE"
            json.leads[0].locations[0].designs[0].clearanceResults.results[1].ruleResults[0].upperWireState.wireState.designCondition.iceDensity.value == 917
            json.leads[0].locations[0].designs[0].clearanceResults.results[1].ruleResults[0].upperWireState.wireState.designCondition.iceDensity.unit == "KILOGRAM_PER_CUBIC_METRE"
            json.leads[0].locations[0].designs[0].clearanceResults.results[1].ruleResults[1].upperWireState.wireState.designCondition.iceDensity.value == 917
            json.leads[0].locations[0].designs[0].clearanceResults.results[1].ruleResults[1].upperWireState.wireState.designCondition.iceDensity.unit == "KILOGRAM_PER_CUBIC_METRE"
            json.leads[0].locations[0].designs[0].clearanceResults.results[1].ruleResults[2].upperWireState.wireState.designCondition.iceDensity.value == 917
            json.leads[0].locations[0].designs[0].clearanceResults.results[1].ruleResults[2].upperWireState.wireState.designCondition.iceDensity.unit == "KILOGRAM_PER_CUBIC_METRE"
            json.leads[0].locations[0].designs[0].clearanceResults.results[2].ruleResults[0].upperWireState.wireState.designCondition.iceDensity.value == 917
            json.leads[0].locations[0].designs[0].clearanceResults.results[2].ruleResults[0].upperWireState.wireState.designCondition.iceDensity.unit == "KILOGRAM_PER_CUBIC_METRE"
            json.leads[0].locations[0].designs[0].clearanceResults.results[2].ruleResults[0].lowerWireState.wireState.designCondition.iceDensity.value == 917
            json.leads[0].locations[0].designs[0].clearanceResults.results[2].ruleResults[0].lowerWireState.wireState.designCondition.iceDensity.unit == "KILOGRAM_PER_CUBIC_METRE"
            json.leads[0].locations[0].designs[0].clearanceResults.results[2].ruleResults[1].upperWireState.wireState.designCondition.iceDensity.value == 917
            json.leads[0].locations[0].designs[0].clearanceResults.results[2].ruleResults[1].upperWireState.wireState.designCondition.iceDensity.unit == "KILOGRAM_PER_CUBIC_METRE"
            json.leads[0].locations[0].designs[0].clearanceResults.results[2].ruleResults[1].lowerWireState.wireState.designCondition.iceDensity.value == 917
            json.leads[0].locations[0].designs[0].clearanceResults.results[2].ruleResults[1].lowerWireState.wireState.designCondition.iceDensity.unit == "KILOGRAM_PER_CUBIC_METRE"
            json.leads[0].locations[0].designs[0].clearanceResults.results[2].ruleResults[2].upperWireState.wireState.designCondition.iceDensity.value == 917
            json.leads[0].locations[0].designs[0].clearanceResults.results[2].ruleResults[2].upperWireState.wireState.designCondition.iceDensity.unit == "KILOGRAM_PER_CUBIC_METRE"
            json.leads[0].locations[0].designs[0].clearanceResults.results[2].ruleResults[2].lowerWireState.wireState.designCondition.iceDensity.value == 917
            json.leads[0].locations[0].designs[0].clearanceResults.results[2].ruleResults[2].lowerWireState.wireState.designCondition.iceDensity.unit == "KILOGRAM_PER_CUBIC_METRE"

    }
}
