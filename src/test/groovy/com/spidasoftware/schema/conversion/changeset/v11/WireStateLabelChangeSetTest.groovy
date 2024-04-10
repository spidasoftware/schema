/*
 * Copyright (c) 2024 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v11

import groovy.json.JsonSlurper
import spock.lang.Specification

class WireStateLabelChangeSetTest extends Specification {

    static WireStateLabelChangeSet changeSet

    def setupSpec() {
        changeSet = new WireStateLabelChangeSet()
    }

    def "apply/revert client data"() {
        setup:
            def stream = WireStateLabelChangeSetTest.getResourceAsStream("/conversions/v11/WireStateLabel-project-v10.json")
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
        expect:
            json.clientData.clearanceCases[0].type == "Vertical to Ground"
            !json.clientData.clearanceCases[0].thermalStateName
            !json.clientData.clearanceCases[0].physicalStateName
            json.clientData.clearanceCases[1].type == "At Pole"
            !json.clientData.clearanceCases[1].thermalStateName
            !json.clientData.clearanceCases[1].physicalStateName
            json.clientData.clearanceCases[3].type == "Wire To Wire"
            !json.clientData.clearanceCases[3].thermalStateName
            !json.clientData.clearanceCases[3].physicalStateName
        when: "apply client data"
            boolean applied = changeSet.applyToClientData(json.clientData as Map)
        then:
            applied
            json.clientData.clearanceCases[0].type == "Vertical to Ground"
            json.clientData.clearanceCases[0].thermalStateName == "Thermal"
            json.clientData.clearanceCases[0].physicalStateName == "Physical"
            json.clientData.clearanceCases[1].type == "At Pole"
            !json.clientData.clearanceCases[1].thermalStateName
            !json.clientData.clearanceCases[1].physicalStateName
            json.clientData.clearanceCases[3].type == "Wire To Wire"
            json.clientData.clearanceCases[3].thermalStateName == "Thermal"
            json.clientData.clearanceCases[3].physicalStateName == "Physical"
        when: "revert client data"
            boolean reverted = changeSet.revertClientData(json.clientData as Map)
        then:
            reverted
            json.clientData.clearanceCases[0].type == "Vertical to Ground"
            !json.clientData.clearanceCases[0].thermalStateName
            !json.clientData.clearanceCases[0].physicalStateName
            json.clientData.clearanceCases[1].type == "At Pole"
            !json.clientData.clearanceCases[1].thermalStateName
            !json.clientData.clearanceCases[1].physicalStateName
            json.clientData.clearanceCases[3].type == "Wire To Wire"
            !json.clientData.clearanceCases[3].thermalStateName
            !json.clientData.clearanceCases[3].physicalStateName
    }

    def "apply/revert project json"() {
        setup:
            def stream = WireStateLabelChangeSetTest.getResourceAsStream("/conversions/v11/WireStateLabel-project-v10.json")
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
        expect:
            json
            // default clearance cases
            json.defaultClearanceCases[0].type == "Vertical to Ground"
            !json.defaultClearanceCases[0].thermalStateName
            !json.defaultClearanceCases[0].physicalStateName
            json.defaultClearanceCases[1].type == "At Pole"
            !json.defaultClearanceCases[1].thermalStateName
            !json.defaultClearanceCases[1].physicalStateName
            json.defaultClearanceCases[3].type == "Wire To Wire"
            !json.defaultClearanceCases[3].thermalStateName
            !json.defaultClearanceCases[3].physicalStateName

            // design clearance cases
            json.leads[0].locations[0].designs[0].clearanceCases[0].type == "Vertical to Ground"
            !json.leads[0].locations[0].designs[0].clearanceCases[0].thermalStateName
            !json.leads[0].locations[0].designs[0].clearanceCases[0].physicalStateName
            json.leads[0].locations[0].designs[0].clearanceCases[1].type == "At Pole"
            !json.leads[0].locations[0].designs[0].clearanceCases[1].thermalStateName
            !json.leads[0].locations[0].designs[0].clearanceCases[1].physicalStateName
            json.leads[0].locations[0].designs[0].clearanceCases[3].type == "Wire To Wire"
            !json.leads[0].locations[0].designs[0].clearanceCases[3].thermalStateName
            !json.leads[0].locations[0].designs[0].clearanceCases[3].physicalStateName

            // clearance results
            json.leads[0].locations[0].designs[0].clearanceResults.results[4].ruleResults[0].clearanceRuleName == "Primary to 0-750V - Thermal"
            !json.leads[0].locations[0].designs[0].clearanceResults.results[4].ruleResults[0].clearanceStateName
            json.leads[0].locations[0].designs[0].clearanceResults.violations[0].clearanceRuleName == "Primary to 0-750V - Thermal"
            !json.leads[0].locations[0].designs[0].clearanceResults.violations[0].clearanceStateName
            json.leads[0].locations[0].designs[0].clearanceResults.violations[2].clearanceRuleName == "Primary to 0-750V - Physical"
            !json.leads[0].locations[0].designs[0].clearanceResults.violations[2].clearanceStateName
        when: "apply project json"
            changeSet.applyToProject(json)
        then:
            // default clearance cases
            json.defaultClearanceCases[0].type == "Vertical to Ground"
            json.defaultClearanceCases[0].thermalStateName == "Thermal"
            json.defaultClearanceCases[0].physicalStateName == "Physical"
            json.defaultClearanceCases[1].type == "At Pole"
            !json.defaultClearanceCases[1].thermalStateName
            !json.defaultClearanceCases[1].physicalStateName
            json.defaultClearanceCases[3].type == "Wire To Wire"
            json.defaultClearanceCases[3].thermalStateName == "Thermal"
            json.defaultClearanceCases[3].physicalStateName == "Physical"

            // design clearance cases
            json.leads[0].locations[0].designs[0].clearanceCases[0].type == "Vertical to Ground"
            json.leads[0].locations[0].designs[0].clearanceCases[0].thermalStateName == "Thermal"
            json.leads[0].locations[0].designs[0].clearanceCases[0].physicalStateName == "Physical"
            json.leads[0].locations[0].designs[0].clearanceCases[1].type == "At Pole"
            !json.leads[0].locations[0].designs[0].clearanceCases[1].thermalStateName
            !json.leads[0].locations[0].designs[0].clearanceCases[1].physicalStateName
            json.leads[0].locations[0].designs[0].clearanceCases[3].type == "Wire To Wire"
            json.leads[0].locations[0].designs[0].clearanceCases[3].thermalStateName == "Thermal"
            json.leads[0].locations[0].designs[0].clearanceCases[3].physicalStateName == "Physical"

            // clearance results
            json.leads[0].locations[0].designs[0].clearanceResults.results[4].ruleResults[0].clearanceRuleName == "Primary to 0-750V"
            json.leads[0].locations[0].designs[0].clearanceResults.results[4].ruleResults[0].clearanceStateName == "Thermal"
            json.leads[0].locations[0].designs[0].clearanceResults.violations[0].clearanceRuleName == "Primary to 0-750V"
            json.leads[0].locations[0].designs[0].clearanceResults.violations[0].clearanceStateName == "Thermal"
            json.leads[0].locations[0].designs[0].clearanceResults.violations[2].clearanceRuleName == "Primary to 0-750V"
            json.leads[0].locations[0].designs[0].clearanceResults.violations[2].clearanceStateName == "Physical"
        when: "revert project json"
            changeSet.revertProject(json)
        then:
            // default clearance cases
            json.defaultClearanceCases[0].type == "Vertical to Ground"
            !json.defaultClearanceCases[0].thermalStateName
            !json.defaultClearanceCases[0].physicalStateName
            json.defaultClearanceCases[1].type == "At Pole"
            !json.defaultClearanceCases[1].thermalStateName
            !json.defaultClearanceCases[1].physicalStateName
            json.defaultClearanceCases[3].type == "Wire To Wire"
            !json.defaultClearanceCases[3].thermalStateName
            !json.defaultClearanceCases[3].physicalStateName

            // design clearance cases
            json.leads[0].locations[0].designs[0].clearanceCases[0].type == "Vertical to Ground"
            !json.leads[0].locations[0].designs[0].clearanceCases[0].thermalStateName
            !json.leads[0].locations[0].designs[0].clearanceCases[0].physicalStateName
            json.leads[0].locations[0].designs[0].clearanceCases[1].type == "At Pole"
            !json.leads[0].locations[0].designs[0].clearanceCases[1].thermalStateName
            !json.leads[0].locations[0].designs[0].clearanceCases[1].physicalStateName
            json.leads[0].locations[0].designs[0].clearanceCases[3].type == "Wire To Wire"
            !json.leads[0].locations[0].designs[0].clearanceCases[3].thermalStateName
            !json.leads[0].locations[0].designs[0].clearanceCases[3].physicalStateName

            // clearance results
            json.leads[0].locations[0].designs[0].clearanceResults.results[4].ruleResults[0].clearanceRuleName == "Primary to 0-750V - Thermal"
            !json.leads[0].locations[0].designs[0].clearanceResults.results[4].ruleResults[0].clearanceStateName
            json.leads[0].locations[0].designs[0].clearanceResults.violations[0].clearanceRuleName == "Primary to 0-750V - Thermal"
            !json.leads[0].locations[0].designs[0].clearanceResults.violations[0].clearanceStateName
            json.leads[0].locations[0].designs[0].clearanceResults.violations[2].clearanceRuleName == "Primary to 0-750V - Physical"
            !json.leads[0].locations[0].designs[0].clearanceResults.violations[2].clearanceStateName
    }
}
