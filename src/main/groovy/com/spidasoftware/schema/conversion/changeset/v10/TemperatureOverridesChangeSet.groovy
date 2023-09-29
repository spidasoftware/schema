/*
 * Copyright (c) 2023 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v10

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.client.AbstractClientDataChangeSet
import groovy.transform.CompileStatic

/**
 * ComponentGroupMeta.thermalTemperatureOverride can be set in the client editor's clearance case table.
 * In calc v8.0.0, the value set in the CE was only being set in clearanceCases.upper.thermalTemperatureOverride, but calc was looking for it in clearanceCases.clearances.upper.thermalTemperatureOverride
 *
 * When up converting, we make the values in both places the same
 */
@CompileStatic
class TemperatureOverridesChangeSet extends AbstractClientDataChangeSet {

    @Override
    boolean applyToClientData(Map clientDataJSON) throws ConversionException {
        boolean anyChanged = false

        clientDataJSON.clearanceCases?.each { Map clearanceCase ->
            anyChanged |= updateTempOverrides(clearanceCase)
        }

        return anyChanged
    }

    @Override
    boolean revertClientData(Map clientDataJSON) throws ConversionException {
        return false
    }

    @Override
    void applyToProject(Map projectJSON) throws ConversionException {
        super.applyToProject(projectJSON)

        projectJSON.defaultClearanceCases?.each { Map clearanceCase ->
            updateTempOverrides(clearanceCase)
        }
    }

    @Override
    void applyToDesign(Map designJSON) throws ConversionException {
        super.applyToDesign(designJSON)

        designJSON.clearanceCases?.each { Map clearanceCase ->
            updateTempOverrides(clearanceCase)
        }
    }

    boolean updateTempOverrides(Map clearanceCase) {
        if (clearanceCase.type == "AT_POLE") { // AT_POLE uppers are not ComponentGroupMeta
            return false
        }
        boolean anyChanged = false

        clearanceCase.upper.each { Map upper ->
            clearanceCase.clearances?.each { Map clearance ->
                Map clearanceUpper = clearance.upper as Map
                if ((upper.componentGroup as Map).name != (clearanceUpper.componentGroup as Map).name) {
                    return // from closure
                }

                if (upper.containsKey("thermalTemperatureOverride")) {
                    if (!clearanceUpper.containsKey("thermalTemperatureOverride")) {
                        clearanceUpper.thermalTemperatureOverride = upper.thermalTemperatureOverride
                        anyChanged = true
                    }

                    if (upper.thermalTemperatureOverride != clearanceUpper.thermalTemperatureOverride) {
                        clearanceUpper.thermalTemperatureOverride = upper.thermalTemperatureOverride
                        anyChanged = true
                    }
                } else {
                    if (clearanceUpper.remove("thermalTemperatureOverride") != null) {
                        anyChanged = true
                    }
                }
            }
        }

        return anyChanged
    }
}
