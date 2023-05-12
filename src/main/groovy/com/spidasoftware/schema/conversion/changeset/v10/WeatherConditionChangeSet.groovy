/*
 * ©2009-2023 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v10

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.client.AbstractClientDataChangeSet
import groovy.transform.CompileStatic

/**
 * Ice density was added to Weather Condition in Calc v8.0.1
 *
 * When down converting, remove ice density
 *
 * When up converting, add ice density with a default value of 917.0 kg/m^3
 */
@CompileStatic
class WeatherConditionChangeSet extends AbstractClientDataChangeSet {

    @Override
    boolean applyToClientData(Map clientDataJSON) throws ConversionException {
        boolean anyChanged = false

        clientDataJSON.wireStates?.each { Map wireState ->
            anyChanged |= addIceDensityToWireState(wireState)
        }

        clientDataJSON.clearanceCases?.each { Map clearanceCase ->
            anyChanged |= addIceDensityToClearances(clearanceCase, true)
        }
        return anyChanged
    }

    @Override
    void applyToProject(Map projectJSON) throws ConversionException {
        super.applyToProject(projectJSON)

        projectJSON.defaultClearanceCases?.each { Map defaultClearanceCase ->
            addIceDensityToClearances(defaultClearanceCase, true)
        }
    }

    @Override
    void applyToDesign(Map designJSON) throws ConversionException {
        designJSON.clearanceCases?.each { Map clearanceCase ->
            addIceDensityToClearances(clearanceCase, true)
        }

        if (designJSON.containsKey("clearanceResults")) {
            Map clearanceResults = designJSON.clearanceResults as Map
            clearanceResults.results?.each { Map clearanceCaseResult ->
                clearanceCaseResult.ruleResults?.each { Map clearanceRuleResult ->
                    addIceDensityToClearances(clearanceRuleResult, false)
                }

                clearanceCaseResult.exemptions?.each { Map clearanceRuleResult ->
                    addIceDensityToClearances(clearanceRuleResult, false)
                }
            }

            clearanceResults.violations?.each { Map clearanceRuleResult ->
                addIceDensityToClearances(clearanceRuleResult, false)
            }
        }
    }

    @Override
    boolean revertClientData(Map clientDataJSON) throws ConversionException {
        boolean anyChanged = false

        clientDataJSON.wireStates?.each { Map wireState ->
            anyChanged |= removeIceDensityFromWireState(wireState)
        }

        clientDataJSON.clearanceCases?.each { Map clearanceCase ->
            anyChanged |= removeIceDensityFromClearances(clearanceCase, true)
        }
        return anyChanged
    }

    @Override
    void revertProject(Map projectJSON) throws ConversionException {
        super.revertProject(projectJSON)

        if (projectJSON.containsKey("clientData")) {
            Map clientData = projectJSON.clientData as Map
            revertClientData(clientData)
        }

        projectJSON.defaultClearanceCases?.each { Map defaultClearanceCase ->
            removeIceDensityFromClearances(defaultClearanceCase, true)
        }
    }

    @Override
    void revertDesign(Map designJSON) throws ConversionException {
        designJSON.clearanceCases?.each { Map clearanceCase ->
            removeIceDensityFromClearances(clearanceCase, true)
        }

        if (designJSON.containsKey("clearanceResults")) {
            Map clearanceResults = designJSON.clearanceResults as Map
            clearanceResults.results?.each { Map clearanceCaseResult ->
                clearanceCaseResult.ruleResults?.each { Map clearanceRuleResult ->
                    removeIceDensityFromClearances(clearanceRuleResult, false)
                }

                clearanceCaseResult.exemptions?.each { Map clearanceRuleResult ->
                    removeIceDensityFromClearances(clearanceRuleResult, false)
                }
            }

            clearanceResults.violations?.each { Map clearanceRuleResult ->
                removeIceDensityFromClearances(clearanceRuleResult, false)
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

    boolean removeIceDensityFromClearances(Map clearancesJSON, boolean clearanceCase) {
        boolean anyChanged = false

        if (clearanceCase) { // clearance case json
            if (clearancesJSON.containsKey("upperThermalState")) {
                Map upperThermalState = clearancesJSON.upperThermalState as Map
                if (upperThermalState.containsKey("wireState")) {
                    anyChanged |= removeIceDensityFromWireState(upperThermalState.wireState as Map)
                }
            }

            if (clearancesJSON.containsKey("lowerThermalState")) {
                Map lowerThermalState = clearancesJSON.lowerThermalState as Map
                if (lowerThermalState.containsKey("wireState")) {
                    anyChanged |= removeIceDensityFromWireState(lowerThermalState.wireState as Map)
                }
            }

            if (clearancesJSON.containsKey("upperPhysicalState")) {
                Map upperPhysicalState = clearancesJSON.upperPhysicalState as Map
                if (upperPhysicalState.containsKey("wireState")) {
                    anyChanged |= removeIceDensityFromWireState(upperPhysicalState.wireState as Map)
                }
            }

            if (clearancesJSON.containsKey("lowerPhysicalState")) {
                Map lowerPhysicalState = clearancesJSON.lowerPhysicalState as Map
                if (lowerPhysicalState.containsKey("wireState")) {
                    anyChanged |= removeIceDensityFromWireState(lowerPhysicalState.wireState as Map)
                }
            }
        } else { // clearance rule result json
            if (clearancesJSON.containsKey("upperWireState")) {
                Map upperWireState = clearancesJSON.upperWireState as Map
                if (upperWireState.containsKey("wireState")) {
                    anyChanged |= removeIceDensityFromWireState(upperWireState.wireState as Map)
                }
            }

            if (clearancesJSON.containsKey("lowerWireState")) {
                Map lowerWireState = clearancesJSON.lowerWireState as Map
                if (lowerWireState.containsKey("wireState")) {
                    anyChanged |= removeIceDensityFromWireState(lowerWireState.wireState as Map)
                }
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

    boolean addIceDensityToClearances(Map clearancesJSON, boolean clearanceCase) {
        boolean anyChanged = false

        if (clearanceCase) { // clearance case json
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
        } else { // clearance rule result json
            if (clearancesJSON.containsKey("upperWireState")) {
                Map upperWireState = clearancesJSON.upperWireState as Map
                if (upperWireState.containsKey("wireState")) {
                    anyChanged |= addIceDensityToWireState(upperWireState.wireState as Map)
                }
            }

            if (clearancesJSON.containsKey("lowerWireState")) {
                Map lowerWireState = clearancesJSON.lowerWireState as Map
                if (lowerWireState.containsKey("wireState")) {
                    anyChanged |= addIceDensityToWireState(lowerWireState.wireState as Map)
                }
            }
        }
        return anyChanged
    }

}
