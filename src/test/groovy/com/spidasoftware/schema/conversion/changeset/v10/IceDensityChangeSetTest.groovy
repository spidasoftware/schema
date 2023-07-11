/*
 * ©2009-2023 SPIDAWEB LLC
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
                    println analysisCase.toString()
                    analysisCase.overrides.iceDensity.value == 917 &&
                            analysisCase.overrides.iceDensity.unit == "KILOGRAM_PER_CUBIC_METRE" &&
                            analysisCase.valuesApplied.iceDensity.value == 917 &&
                            analysisCase.valuesApplied.iceDensity.unit == "KILOGRAM_PER_CUBIC_METRE"
                } else {
                    !analysisCase.containsKey("overrides") || analysisCase.overrides.isEmpty()
                }
            }
    }
}
