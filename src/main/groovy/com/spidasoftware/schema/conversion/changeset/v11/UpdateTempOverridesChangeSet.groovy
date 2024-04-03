/*
 * Copyright (c) 2024 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v11

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.client.AbstractClientDataChangeSet
import groovy.transform.CompileStatic

@CompileStatic
class UpdateTempOverridesChangeSet extends AbstractClientDataChangeSet {

    @Override
    boolean applyToClientData(Map clientDataJSON) throws ConversionException {
        boolean anyChanged = false

        clientDataJSON.clearanceCases?.each { Map clearanceCase ->
            applyTempOverrides(clearanceCase)
            anyChanged = true
        }
        return anyChanged
    }

    @Override
    boolean revertClientData(Map clientDataJSON) throws ConversionException {
        boolean anyChanged = false

        clientDataJSON.clearanceCases?.each { Map clearanceCase ->
            revertTempOverrides(clearanceCase)
            anyChanged = true
        }
        return anyChanged
    }

    @Override
    void applyToProject(Map projectJSON) throws ConversionException {
        super.applyToProject(projectJSON)

        projectJSON.defaultClearanceCases?.each { Map clearanceCase ->
            applyTempOverrides(clearanceCase)
        }
    }

    @Override
    void revertProject(Map projectJSON) throws ConversionException {
        super.revertProject(projectJSON)

        projectJSON.defaultClearanceCases?.each { Map clearanceCase ->
            revertTempOverrides(clearanceCase)
        }
    }

    @Override
    void applyToDesign(Map designJSON) throws ConversionException {
        super.applyToDesign(designJSON)

        designJSON.clearanceCases?.each { Map clearanceCase ->
            applyTempOverrides(clearanceCase)
        }
    }

    @Override
    void revertDesign(Map designJSON) throws ConversionException {
        super.revertDesign(designJSON)

        if (designJSON.containsKey("clearanceResults")) {
            boolean shouldClearResults = false
            Map clearanceResults = designJSON.clearanceResults as Map
            clearanceResults.results.each { Map clearanceCaseResult ->
                clearanceCaseResult.ruleResults.each { Map ruleResult ->
                    Map clearanceCase = designJSON.clearanceCases.find { Map clearanceCase ->
                        clearanceCase.name == ruleResult.clearanceCaseType
                    } as Map
                    shouldClearResults = (clearanceCase.checkThermalTemperatureOverride == false) || (clearanceCase.checkPhysicalTemperatureOverride == true)
                }
            }
            if (shouldClearResults) {
                designJSON.remove("clearanceResults")
            }
        }

        designJSON.clearanceCases?.each { Map clearanceCase ->
            revertTempOverrides(clearanceCase)
        }
    }

    void applyTempOverrides(Map clearanceCase) {
        if (clearanceCase.type == "At Pole") {
            return
        }

        clearanceCase.checkThermalTemperatureOverride = true
        clearanceCase.checkPhysicalTemperatureOverride = false

        clearanceCase.upper?.each { Map componentGroupMeta ->
            if (componentGroupMeta.containsKey("thermalTemperatureOverride")) {
                componentGroupMeta.temperatureOverride = componentGroupMeta.thermalTemperatureOverride
                componentGroupMeta.remove("thermalTemperatureOverride")
            }
        }

        clearanceCase.clearances?.each { Map clearance ->
            Map componentGroupMeta = clearance.upper as Map
            if (componentGroupMeta?.containsKey("thermalTemperatureOverride")) {
                componentGroupMeta.temperatureOverride = componentGroupMeta.thermalTemperatureOverride
                componentGroupMeta.remove("thermalTemperatureOverride")
            }
        }
    }

    void revertTempOverrides(Map clearanceCase) {
        if (clearanceCase.type == "At Pole") {
            return
        }

        clearanceCase.remove("checkThermalTemperatureOverride")
        clearanceCase.remove("checkPhysicalTemperatureOverride")

        clearanceCase.upper?.each { Map componentGroupMeta ->
            if (componentGroupMeta.containsKey("temperatureOverride")) {
                componentGroupMeta.thermalTemperatureOverride = componentGroupMeta.temperatureOverride
                componentGroupMeta.remove("temperatureOverride")
            }
        }

        clearanceCase.clearances?.each { Map clearance ->
            Map componentGroupMeta = clearance.upper as Map
            if (componentGroupMeta?.containsKey("temperatureOverride")) {
                componentGroupMeta.thermalTemperatureOverride = componentGroupMeta.temperatureOverride
                componentGroupMeta.remove("temperatureOverride")
            }
        }
    }
}
