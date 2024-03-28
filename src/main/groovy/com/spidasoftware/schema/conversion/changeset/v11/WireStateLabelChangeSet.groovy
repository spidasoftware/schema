/*
 * Copyright (c) 2024 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v11

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.client.AbstractClientDataChangeSet
import groovy.transform.CompileStatic

@CompileStatic
class WireStateLabelChangeSet extends AbstractClientDataChangeSet {

    @Override
    boolean applyToClientData(Map clientDataJSON) throws ConversionException {
        boolean anyChanged = false
        clientDataJSON.clearanceCases?.each { Map clearanceCase ->
            if (clearanceCase.type != "At Pole") {
                clearanceCase.thermalStateName = "Thermal"
                clearanceCase.physicalStateName = "Physical"
                anyChanged = true
            }
        }
        return anyChanged
    }

    @Override
    boolean revertClientData(Map clientDataJSON) throws ConversionException {
        boolean anyChanged = false
        clientDataJSON.clearanceCases?.each { Map clearanceCase ->
            if (clearanceCase.type != "At Pole") {
                clearanceCase.remove("thermalStateName")
                clearanceCase.remove("physicalStateName")
                anyChanged = true
            }
        }
        return anyChanged
    }

    @Override
    void applyToProject(Map projectJSON) throws ConversionException {
        super.applyToProject(projectJSON)

        projectJSON.defaultClearanceCases?.each { Map clearanceCase ->
            if (clearanceCase.type != "At Pole") {
                clearanceCase.thermalStateName = "Thermal"
                clearanceCase.physicalStateName = "Physical"
            }
        }
    }

    @Override
    void revertProject(Map projectJSON) throws ConversionException {
        super.revertProject(projectJSON)

        projectJSON.defaultClearanceCases?.each { Map clearanceCase ->
            if (clearanceCase.type != "At Pole") {
                clearanceCase.remove("thermalStateName")
                clearanceCase.remove("physicalStateName")
            }
        }
    }

    @Override
    void applyToDesign(Map designJSON) throws ConversionException {
        super.applyToDesign(designJSON)

        designJSON.clearanceCases?.each { Map clearanceCase ->
            if (clearanceCase.type != "At Pole") {
                clearanceCase.thermalStateName = "Thermal"
                clearanceCase.physicalStateName = "Physical"
            }
        }

        if (designJSON.containsKey("clearanceResults")) {
            Map clearanceResults = designJSON.clearanceResults as Map
            clearanceResults.results.each { Map clearanceCaseResult ->
                clearanceCaseResult.ruleResults.each { Map ruleResult ->
                    applyRuleResult(ruleResult)
                }
                clearanceCaseResult.exemptions.each { Map ruleResult ->
                    applyRuleResult(ruleResult)
                }
            }
            clearanceResults.violations.each { Map ruleResult ->
                applyRuleResult(ruleResult)
            }
        }
    }

    @Override
    void revertDesign(Map designJSON) throws ConversionException {
        super.revertDesign(designJSON)
        List<Map> clearanceCases = designJSON.clearanceCases as List<Map>
        clearanceCases?.each { Map clearanceCase ->
            if (clearanceCase.type != "At Pole") {
                clearanceCase.remove("thermalStateName")
                clearanceCase.remove("physicalStateName")
            }
        }

        if (designJSON.containsKey("clearanceResults")) {
            Map clearanceResults = designJSON.clearanceResults as Map
            clearanceResults.results.each { Map clearanceCaseResult ->
                clearanceCaseResult.ruleResults.each { Map ruleResult ->
                    revertRuleResult(ruleResult, clearanceCases)
                }
                clearanceCaseResult.exemptions.each { Map ruleResult ->
                    revertRuleResult(ruleResult, clearanceCases)
                }
            }
            clearanceResults.violations.each { Map ruleResult ->
                revertRuleResult(ruleResult, clearanceCases)
            }
        }
    }

    void applyRuleResult(Map ruleResult) {
        if (ruleResult.clearanceCaseType == "AT_POLE") {
            return
        }

        String ruleName = ruleResult.clearanceRuleName as String
        if (ruleName.contains(" - Thermal")) {
            ruleResult.clearanceStateName = "Thermal"
            ruleResult.clearanceRuleName = ruleName.replace(" - Thermal", "")
        } else {
            ruleResult.clearanceStateName = "Physical"
            ruleResult.clearanceRuleName = ruleName.replace(" - Physical", "")
        }

    }

    void revertRuleResult(Map ruleResult, List<Map> clearanceCases) {
        if (ruleResult.clearanceCaseType == "AT_POLE") {
            return
        }

        ruleResult.remove("clearanceStateName")

        // if the ruleResults upper matches the clearanceCases upperThermalState, then we need to append thermal
        // else append physical
        boolean isThermal = false
        clearanceCases.each { Map clearanceCase ->
            if (clearanceCase.name == ruleResult.clearanceCaseName) {
                if ((clearanceCase.upperThermalState as Map).name == (ruleResult.upperWireState as Map).name) {
                    isThermal = true
                }
            }
        }

        String name = isThermal ? "Thermal" : "Physical"
        ruleResult.clearanceRuleName = ruleResult.clearanceRuleName + " - " + name

    }

}
