/*
 * Copyright (c) 2024 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v10

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.client.AbstractClientDataChangeSet
import groovy.transform.CompileStatic

/**
 * Ice density was added to Calc v24.0
 *
 * When down converting, remove ice density
 *
 * When up converting, add ice density to Weather Conditions with a default value of 917.0 kg/m^3
 */
@CompileStatic
class IceDensityChangeSet extends AbstractClientDataChangeSet {

    @Override
    boolean applyToClientData(Map clientDataJSON) throws ConversionException {
        boolean anyChanged = false

        clientDataJSON.wireStates?.each { Map wireState ->
            anyChanged |= addIceDensityToWireState(wireState)
        }

        clientDataJSON.clearanceCases?.each { Map clearanceCase ->
            anyChanged |= addIceDensityToClearanceCases(clearanceCase)
        }

        clientDataJSON.analysisCases?.each { Map analysisCase ->
            anyChanged |= addIceDensityToLoadCase(analysisCase)
        }

        return anyChanged
    }

    @Override
    void applyToProject(Map projectJSON) throws ConversionException {
        super.applyToProject(projectJSON)

        projectJSON.defaultClearanceCases?.each { Map defaultClearanceCase ->
            addIceDensityToClearanceCases(defaultClearanceCase)
        }

        projectJSON.defaultLoadCases?.each { Map loadCase ->
            addIceDensityToLoadCase(loadCase)
        }
    }

    @Override
    void applyToDesign(Map designJSON) throws ConversionException {
        super.applyToDesign(designJSON)

        designJSON.clearanceCases?.each { Map clearanceCase ->
            addIceDensityToClearanceCases(clearanceCase)
        }

        if (designJSON.containsKey("clearanceResults")) {
            Map clearanceResults = designJSON.clearanceResults as Map
            clearanceResults.results?.each { Map clearanceCaseResult ->
                clearanceCaseResult.ruleResults?.each { Map clearanceRuleResult ->
                    addIceDensityToRuleResults(clearanceRuleResult)
                }

                clearanceCaseResult.exemptions?.each { Map clearanceRuleResult ->
                    addIceDensityToRuleResults(clearanceRuleResult)
                }
            }

            clearanceResults.violations?.each { Map clearanceRuleResult ->
                addIceDensityToRuleResults(clearanceRuleResult)
            }
        }

        designJSON.analysis?.each { Map analysisMap ->
            if(analysisMap.analysisCaseDetails != null) {
                addIceDensityToLoadCase(analysisMap.analysisCaseDetails as Map)
            }
        }
    }

    @Override
    boolean applyToResults(Map resultsJSON) throws ConversionException {
        boolean anyChanged = super.applyToResults(resultsJSON)

        resultsJSON.results?.each { Map resultMap ->
            if(resultMap.analysisCaseDetails != null) {
                anyChanged |= addIceDensityToLoadCase(resultMap.analysisCaseDetails as Map)
            }
        }

        return anyChanged
    }

    @Override
    boolean revertClientData(Map clientDataJSON) throws ConversionException {
        boolean anyChanged = false

        clientDataJSON.wireStates?.each { Map wireState ->
            anyChanged |= removeIceDensityFromWireState(wireState)
        }

        clientDataJSON.clearanceCases?.each { Map clearanceCase ->
            anyChanged |= removeIceDensityFromClearanceCases(clearanceCase)
        }

        clientDataJSON.analysisCases?.each { Map analysisCase ->
            anyChanged |= removeIceDensityFromLoadCase(analysisCase)
        }

        return anyChanged
    }

    @Override
    void revertProject(Map projectJSON) throws ConversionException {
        super.revertProject(projectJSON)

        projectJSON.defaultClearanceCases?.each { Map defaultClearanceCase ->
            removeIceDensityFromClearanceCases(defaultClearanceCase)
        }

        projectJSON.defaultLoadCases?.each { Map loadCaseJson ->
            removeIceDensityFromLoadCase(loadCaseJson)
        }
    }

    @Override
    void revertDesign(Map designJSON) throws ConversionException {
        super.revertDesign(designJSON)

        designJSON.clearanceCases?.each { Map clearanceCase ->
            removeIceDensityFromClearanceCases(clearanceCase)
        }

        if (designJSON.containsKey("clearanceResults")) {
            Map clearanceResults = designJSON.clearanceResults as Map
            clearanceResults.results?.each { Map clearanceCaseResult ->
                clearanceCaseResult.ruleResults?.each { Map clearanceRuleResult ->
                    removeIceDensityFromRuleResults(clearanceRuleResult)
                }

                clearanceCaseResult.exemptions?.each { Map clearanceRuleResult ->
                    removeIceDensityFromRuleResults(clearanceRuleResult)
                }
            }

            clearanceResults.violations?.each { Map clearanceRuleResult ->
                removeIceDensityFromRuleResults(clearanceRuleResult)
            }
        }

        designJSON.analysis?.each { Map analysisMap ->
            if(analysisMap.analysisCaseDetails != null) {
                removeIceDensityFromLoadCase(analysisMap.analysisCaseDetails as Map)
            }
        }
    }

    @Override
    boolean revertResults(Map resultsJSON) {
        boolean anyChanged = super.revertResults(resultsJSON)

        resultsJSON.results?.each { Map resultMap ->
            if(resultMap.analysisCaseDetails != null) {
                anyChanged |= removeIceDensityFromLoadCase(resultMap.analysisCaseDetails as Map)
            }
        }
    }

    boolean removeIceDensityFromWireState(Map wireStateJSON) {
        boolean anyChanged = false
        if (wireStateJSON.containsKey("designCondition")) {
            Map designCondition = wireStateJSON.designCondition as Map
            if (designCondition.remove("iceDensity") != null) {
                anyChanged = true
            }
        }

        if (wireStateJSON.containsKey("highestStressCondition")) {
            Map highestStressCondition = wireStateJSON.highestStressCondition as Map
            if (highestStressCondition.remove("iceDensity") != null) {
                anyChanged = true
            }
        }

        if (wireStateJSON.containsKey("creepCondition")) {
            Map creepCondition = wireStateJSON.creepCondition as Map
            if (creepCondition.remove("iceDensity") != null) {
                anyChanged = true
            }
        }
        return anyChanged
    }

    boolean removeIceDensityFromClearanceCases(Map clearanceCaseJSON) {
        boolean anyChanged = false

        if (clearanceCaseJSON.containsKey("upperThermalState")) {
            Map upperThermalState = clearanceCaseJSON.upperThermalState as Map
            if (upperThermalState.containsKey("wireState")) {
                anyChanged |= removeIceDensityFromWireState(upperThermalState.wireState as Map)
            }
        }

        if (clearanceCaseJSON.containsKey("lowerThermalState")) {
            Map lowerThermalState = clearanceCaseJSON.lowerThermalState as Map
            if (lowerThermalState.containsKey("wireState")) {
                anyChanged |= removeIceDensityFromWireState(lowerThermalState.wireState as Map)
            }
        }

        if (clearanceCaseJSON.containsKey("upperPhysicalState")) {
            Map upperPhysicalState = clearanceCaseJSON.upperPhysicalState as Map
            if (upperPhysicalState.containsKey("wireState")) {
                anyChanged |= removeIceDensityFromWireState(upperPhysicalState.wireState as Map)
            }
        }

        if (clearanceCaseJSON.containsKey("lowerPhysicalState")) {
            Map lowerPhysicalState = clearanceCaseJSON.lowerPhysicalState as Map
            if (lowerPhysicalState.containsKey("wireState")) {
                anyChanged |= removeIceDensityFromWireState(lowerPhysicalState.wireState as Map)
            }
        }
        return anyChanged
    }

    boolean removeIceDensityFromRuleResults(Map clearanceRuleResultJSON) {
        boolean anyChanged = false

        if (clearanceRuleResultJSON.containsKey("upperWireState")) {
            Map upperWireState = clearanceRuleResultJSON.upperWireState as Map
            if (upperWireState.containsKey("wireState")) {
                anyChanged |= removeIceDensityFromWireState(upperWireState.wireState as Map)
            }
        }

        if (clearanceRuleResultJSON.containsKey("lowerWireState")) {
            Map lowerWireState = clearanceRuleResultJSON.lowerWireState as Map
            if (lowerWireState.containsKey("wireState")) {
                anyChanged |= removeIceDensityFromWireState(lowerWireState.wireState as Map)
            }
        }
        return anyChanged
    }

    boolean addIceDensityToWireState(Map wireStateJSON) {
        boolean anyChanged = false
        Map defaultIceDensity = ["value": 917, "unit": "KILOGRAM_PER_CUBIC_METRE"]

        if (wireStateJSON.containsKey("designCondition")) {
            Map designCondition = wireStateJSON.designCondition as Map
            designCondition.put("iceDensity", defaultIceDensity)
            anyChanged = true
        }

        if (wireStateJSON.containsKey("highestStressCondition")) {
            Map highestStressCondition = wireStateJSON.highestStressCondition as Map
            highestStressCondition.put("iceDensity", defaultIceDensity)
            anyChanged = true
        }

        if (wireStateJSON.containsKey("creepCondition")) {
            Map creepCondition = wireStateJSON.creepCondition as Map
            creepCondition.put("iceDensity", defaultIceDensity)
            anyChanged = true
        }

        return anyChanged
    }

    boolean addIceDensityToClearanceCases(Map clearancesJSON) {
        boolean anyChanged = false

        if (clearancesJSON.containsKey("upperThermalState")) {
            Map upperThermalState = clearancesJSON.upperThermalState as Map
            if (upperThermalState.containsKey("wireState")) {
                anyChanged |= addIceDensityToWireState(upperThermalState.wireState as Map)
            }
        }

        if (clearancesJSON.containsKey("lowerThermalState")) {
            Map lowerThermalState = clearancesJSON.lowerThermalState as Map
            if (lowerThermalState.containsKey("wireState")) {
                anyChanged |= addIceDensityToWireState(lowerThermalState.wireState as Map)
            }
        }

        if (clearancesJSON.containsKey("upperPhysicalState")) {
            Map upperPhysicalState = clearancesJSON.upperPhysicalState as Map
            if (upperPhysicalState.containsKey("wireState")) {
                anyChanged |= addIceDensityToWireState(upperPhysicalState.wireState as Map)
            }
        }

        if (clearancesJSON.containsKey("LowerPhysicalState")) {
            Map LowerPhysicalState = clearancesJSON.LowerPhysicalState as Map
            if (LowerPhysicalState.containsKey("wireState")) {
                anyChanged |= addIceDensityToWireState(LowerPhysicalState.wireState as Map)
            }
        }
        return anyChanged
    }

    boolean addIceDensityToRuleResults(Map clearanceRuleResultJSON) {
        boolean anyChanged = false

        if (clearanceRuleResultJSON.containsKey("upperWireState")) {
            Map upperWireState = clearanceRuleResultJSON.upperWireState as Map
            if (upperWireState.containsKey("wireState")) {
                anyChanged |= addIceDensityToWireState(upperWireState.wireState as Map)
            }
        }

        if (clearanceRuleResultJSON.containsKey("lowerWireState")) {
            Map lowerWireState = clearanceRuleResultJSON.lowerWireState as Map
            if (lowerWireState.containsKey("wireState")) {
                anyChanged |= addIceDensityToWireState(lowerWireState.wireState as Map)
            }
        }
        return anyChanged
    }

    boolean addIceDensityToLoadCase(Map analysisCaseJSON) {
        if((analysisCaseJSON.type as String)?.startsWith("CSA ")) {
            (analysisCaseJSON.overrides as Map).put("iceDensity", ["unit": "KILOGRAM_PER_CUBIC_METRE", "value": 917])
            (analysisCaseJSON.valuesApplied as Map).put("iceDensity", ["unit": "KILOGRAM_PER_CUBIC_METRE", "value": 917])
            return true
        }
        return false
    }

    boolean removeIceDensityFromLoadCase(Map loadCaseJSON) {
        boolean changed = false

        if(loadCaseJSON.containsKey("creepWireTensionIceDensity") || loadCaseJSON.containsKey("highestWireTensionIceDensity")) {
            loadCaseJSON.remove("creepWireTensionIceDensity")
            loadCaseJSON.remove("highestWireTensionIceDensity")
            changed = true
        }
        if ((loadCaseJSON.overrides as Map)?.containsKey("iceDensity")) {
            (loadCaseJSON.overrides as Map)?.remove("iceDensity")
            // no need to remove iceDensity from valuesApplied, as extra values in that fields are ignored
            changed = true
        }

        return changed
    }

}
