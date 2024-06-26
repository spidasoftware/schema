/*
 * Copyright (c) 2024 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v10

import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import spock.lang.Specification

@Slf4j
class IceDensityChangeSetTest extends Specification {

    static IceDensityChangeSet changeSet

    def setupSpec() {
        changeSet = new IceDensityChangeSet()
    }

    def "revert client data"() {
        setup:
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
            !json.leads[0].locations[0].designs[0].clearanceCases[2].lowerThermalState.wireState.designCondition.iceDensity
            !json.leads[0].locations[0].designs[0].clearanceCases[2].lowerThermalState.wireState.highestStressCondition.iceDensity
            !json.leads[0].locations[0].designs[0].clearanceCases[2].lowerThermalState.wireState.creepCondition.iceDensity
            !json.leads[0].locations[0].designs[0].clearanceCases[2].lowerPhysicalState.wireState.designCondition.iceDensity
            !json.leads[0].locations[0].designs[0].clearanceCases[2].lowerPhysicalState.wireState.highestStressCondition.iceDensity
            !json.leads[0].locations[0].designs[0].clearanceCases[2].lowerPhysicalState.wireState.creepCondition.iceDensity
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
            json.leads[0].locations[0].designs[0].clearanceCases[2].lowerThermalState.wireState.designCondition.iceDensity.value == 917
            json.leads[0].locations[0].designs[0].clearanceCases[2].lowerThermalState.wireState.designCondition.iceDensity.unit == "KILOGRAM_PER_CUBIC_METRE"
            json.leads[0].locations[0].designs[0].clearanceCases[2].lowerThermalState.wireState.highestStressCondition.iceDensity.value == 917
            json.leads[0].locations[0].designs[0].clearanceCases[2].lowerThermalState.wireState.highestStressCondition.iceDensity.unit == "KILOGRAM_PER_CUBIC_METRE"
            json.leads[0].locations[0].designs[0].clearanceCases[2].lowerThermalState.wireState.creepCondition.iceDensity.value == 917
            json.leads[0].locations[0].designs[0].clearanceCases[2].lowerThermalState.wireState.creepCondition.iceDensity.unit == "KILOGRAM_PER_CUBIC_METRE"
            json.leads[0].locations[0].designs[0].clearanceCases[2].lowerPhysicalState.wireState.designCondition.iceDensity.value == 917
            json.leads[0].locations[0].designs[0].clearanceCases[2].lowerPhysicalState.wireState.designCondition.iceDensity.unit == "KILOGRAM_PER_CUBIC_METRE"
            json.leads[0].locations[0].designs[0].clearanceCases[2].lowerPhysicalState.wireState.highestStressCondition.iceDensity.value == 917
            json.leads[0].locations[0].designs[0].clearanceCases[2].lowerPhysicalState.wireState.highestStressCondition.iceDensity.unit == "KILOGRAM_PER_CUBIC_METRE"
            json.leads[0].locations[0].designs[0].clearanceCases[2].lowerPhysicalState.wireState.creepCondition.iceDensity.value == 917
            json.leads[0].locations[0].designs[0].clearanceCases[2].lowerPhysicalState.wireState.creepCondition.iceDensity.unit == "KILOGRAM_PER_CUBIC_METRE"
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

    def "csa ice density - project"() {
        setup:
            def stream = IceDensityChangeSet.getResourceAsStream("/conversions/v10/LoadCaseIceDensity-project.json".toString())
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
        expect:
            json.defaultLoadCases.every {
                it.overrides.isEmpty() && it.valuesApplied.containsKey("iceDensity")
            }
            !(json.clientData.analysisCases[0] as Map).containsKey("valuesApplied")  // first analysis case is a strength case
            !(json.clientData.analysisCases[0] as Map).containsKey("overrides")  // first analysis case is a strength case
            (1..(json.clientData.analysisCases as List).size() - 1).every {
                json.clientData.analysisCases[it].overrides.isEmpty() &&
                        json.clientData.analysisCases[it].valuesApplied.containsKey("iceDensity")
            }
            json.leads.every { Map lead ->
                lead.locations.every { Map location ->
                    location.designs.every { Map design ->
                        design.analysis.every { Map analysis ->
                            if(analysis.analysisCaseDetails?.type?.startsWith("CSA ")) {
                                analysis.analysisCaseDetails.overrides.isEmpty() &&
                                        analysis.analysisCaseDetails.valuesApplied.containsKey("iceDensity")
                            } else {
                                true
                            }
                        }
                    }
                }
            }
        when: "reverted"
            changeSet.revertProject(json)
        then:
            json.defaultLoadCases.every {
                it.overrides.isEmpty()
            }
            (1..(json.clientData.analysisCases as List).size() - 1).every {
                json.clientData.analysisCases[it].overrides.isEmpty() &&
                        !json.clientData.analysisCases[it].containsKey("iceDensity")
            }
            json.leads.every { Map lead ->
                lead.locations.every { Map location ->
                    location.designs.every { Map design ->
                        design.analysis.every { Map analysis ->
                            if(analysis.containsKey("analysisCaseDetails")) {
                                if (analysis.analysisCaseDetails.type?.startsWith("CSA ")) {
                                    analysis.analysisCaseDetails.overrides.isEmpty() &&
                                            !analysis.analysisCaseDetails.containsKey("iceDensity")
                                } else {
                                    analysis.analysisCaseDetails.overrides == null || analysis.analysisCaseDetails.overrides.isEmpty()
                                }
                            }
                        }
                    }
                }
            }
        when: "applied"
            changeSet.applyToProject(json)
        then:
            json.defaultLoadCases.every {
                if(it.type.startsWith("CSA ")) {
                    it.overrides.iceDensity.value == 917 &&
                            it.overrides.iceDensity.unit == "KILOGRAM_PER_CUBIC_METRE" &&
                            it.valuesApplied.iceDensity.value == 917 &&
                            it.valuesApplied.iceDensity.unit == "KILOGRAM_PER_CUBIC_METRE"
                } else {
                    it.overrides.isEmpty()
                }
            }
            (1..(json.clientData.analysisCases as List).size() - 1).every {
                if(json.clientData.analysisCases[it].type.startsWith("CSA ")) {
                    json.clientData.analysisCases[it].overrides.iceDensity.value == 917 &&
                            json.clientData.analysisCases[it].overrides.iceDensity.unit == "KILOGRAM_PER_CUBIC_METRE" &&
                            json.clientData.analysisCases[it].valuesApplied.iceDensity.value == 917 &&
                            json.clientData.analysisCases[it].valuesApplied.iceDensity.unit == "KILOGRAM_PER_CUBIC_METRE"
                } else {
                    json.clientData.analysisCases[it].overrides.isEmpty()
                }
            }
            json.leads.every { Map lead ->
                lead.locations.every { Map location ->
                    location.designs.every { Map design ->
                        design.analysis.every { Map analysis ->
                            if(analysis.containsKey("analysisCaseDetails")) {
                                if (analysis.analysisCaseDetails.type?.startsWith("CSA ")) {
                                    analysis.analysisCaseDetails.overrides.iceDensity.value == 917 &&
                                            analysis.analysisCaseDetails.overrides.iceDensity.unit == "KILOGRAM_PER_CUBIC_METRE" &&
                                            analysis.analysisCaseDetails.valuesApplied.iceDensity.value == 917 &&
                                            analysis.analysisCaseDetails.valuesApplied.iceDensity.unit == "KILOGRAM_PER_CUBIC_METRE"
                                } else {
                                    analysis.analysisCaseDetails.overrides == null || analysis.analysisCaseDetails.overrides.isEmpty()
                                }
                            } else {
                                true
                            }
                        }
                    }
                }
            }
    }

    def "csa ice density - client file"() {
        setup:
            def stream = IceDensityChangeSet.getResourceAsStream("/conversions/v10/LoadCaseIceDensity-client.json".toString())
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
        expect:
            json.analysisCases.every { Map analysisCase ->
                !analysisCase.containsKey("overrides") ||
                        (analysisCase.overrides.isEmpty() && analysisCase.valuesApplied.containsKey("iceDensity"))
            }
        when: "reverted"
            changeSet.revertClientData(json)
        then:
            json.analysisCases.every { Map analysisCase ->
                !analysisCase.containsKey("overrides") ||
                        (analysisCase.overrides.isEmpty() && !analysisCase.containsKey("iceDensity"))
            }
        when: "applied"
            changeSet.applyToClientData(json)
        then:
            json.analysisCases.every { Map analysisCase ->
                if(analysisCase.type.startsWith("CSA ")) {
                    analysisCase.overrides.iceDensity.value == 917 &&
                            analysisCase.overrides.iceDensity.unit == "KILOGRAM_PER_CUBIC_METRE" &&
                            analysisCase.valuesApplied.iceDensity.value == 917 &&
                            analysisCase.valuesApplied.iceDensity.unit == "KILOGRAM_PER_CUBIC_METRE"
                } else {
                    !analysisCase.containsKey("overrides") || analysisCase.overrides.isEmpty()
                }
            }
    }

    def "csa ice density - results"() {
        setup:
            def stream = IceDensityChangeSet.getResourceAsStream("/conversions/v10/LoadCaseIceDensity-results.json".toString())
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
        expect:
            json.results.every { Map result ->
                if(result.analysisCaseDetails?.type?.startsWith("CSA ")) {
                    result.analysisCaseDetails.overrides.isEmpty() && result.analysisCaseDetails.valuesApplied.containsKey("iceDensity")
                } else {
                    true
                }
            }
        when: "reverted"
            changeSet.revertResults(json)
        then:
            json.results.every { Map result ->
                if(result.analysisCaseDetails?.type?.startsWith("CSA ")) {
                    result.analysisCaseDetails.overrides.isEmpty()
                } else {
                    true
                }
            }
        when: "applied"
            changeSet.applyToResults(json)
        then:
            json.results.every { Map result ->
                if(result.analysisCaseDetails?.type?.startsWith("CSA ")) {
                    result.analysisCaseDetails.overrides.iceDensity.value == 917 &&
                            result.analysisCaseDetails.overrides.iceDensity.unit == "KILOGRAM_PER_CUBIC_METRE" &&
                            result.analysisCaseDetails.valuesApplied.iceDensity.value == 917 &&
                            result.analysisCaseDetails.valuesApplied.iceDensity.unit == "KILOGRAM_PER_CUBIC_METRE"
                } else {
                    true
                }
            }
    }

    def "revert design json when design.analysis have no analysisCaseDetails"() {
        setup:
            def stream = IceDensityChangeSet.getResourceAsStream("/conversions/v10/StudioDesignWithoutAnalysisCaseDetails.json".toString())
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
        expect:
            json.calcDesign.analysis.size() == 2
            json.calcDesign.analysis.every { !it.containsKey("analysisCaseDetails") }
        when: "apply changeset"
            boolean anyChanged = changeSet.revertDesign(json.calcDesign)
        then: "no exception thrown"
            !anyChanged
    }

    def "remove ice density override from load case"() {
        setup:
            Map loadCase
            boolean changed

        when: "load case does not contain creepWireTensionIceDensity or highestWireTensionIceDensity"
            loadCase = [overrides: [iceDensity: 6.0]]
            changed = changeSet.removeIceDensityFromLoadCase(loadCase)
        then: "changed"
            changed
        and: "ice density override removed"
            !loadCase.overrides.containsKey("iceDensity")

        when: "load case contains creepWireTensionIceDensity or highestWireTensionIceDensity"
            loadCase = [creepWireTensionIceDensity: 5.0, overrides: [iceDensity: 6.0]]
            changed = changeSet.removeIceDensityFromLoadCase(loadCase)
        then: "changed"
            changed
        and: "ice density override removed"
            !loadCase.overrides.containsKey("iceDensity")

        when: "overrides does not contain ice density"
            loadCase = [overrides: [:]]
            changed = changeSet.removeIceDensityFromLoadCase(loadCase)
        then: "not changed"
            !changed

        when: "load case does not contain overrides"
            loadCase = [:]
            changed = changeSet.removeIceDensityFromLoadCase(loadCase)
        then: "not changed"
            !changed
    }
}
