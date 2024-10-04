/*
 * Copyright (c) 2024 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v12

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.client.AbstractClientDataChangeSet

/**
 * In version 24.1, the default temperature for CSA Maximum Wind load cases was -20 °C.
 * CSA 60826 code updates the default temperature to 15 °C.
 *
 * When up-converting load cases created in older version:
 *      - add a temperature override to keep the value the same.
 * When up-converting load cases created in current version:
 *      - remove temperature override if it's equal to default value.
 *
 * When down-converting load cases created in older version:
 *      - remove temperature override if it's equal to old default value.
 * When down-converting load cases created in current version:
 *      - add a temperature override to keep the value the same.
 */
class CSAMaxWindTemperatureChangeset extends AbstractClientDataChangeSet {

    @Override
    boolean applyToClientData(Map clientDataJSON) throws ConversionException {
        boolean changed = false
        clientDataJSON.analysisCases?.each { Map analysisCase ->
            changed |= applyTemperatureToLoadCase(analysisCase)
        }
        return changed
    }

    @Override
    void applyToProject(Map projectJSON) throws ConversionException {
        super.applyToProject(projectJSON)
        projectJSON.defaultLoadCases?.each { Map loadCase ->
            applyTemperatureToLoadCase(loadCase)
        }
    }

    @Override
    void applyToDesign(Map designJSON) throws ConversionException {
        super.applyToDesign(designJSON)
        designJSON.analysis?.each { Map analysis ->
            if (analysis.analysisCaseDetails != null) {
                applyTemperatureToLoadCase(analysis.analysisCaseDetails as Map)
            }
        }
    }

    @Override
    boolean applyToResults(Map resultsJSON) throws ConversionException {
        boolean changed = super.applyToResults(resultsJSON)
        resultsJSON.results?.each { Map result ->
            if (result.analysisCaseDetails != null) {
                changed |= applyTemperatureToLoadCase(result.analysisCaseDetails as Map)
            }
        }
        return changed
    }

    @Override
    boolean revertClientData(Map clientDataJSON) throws ConversionException {
        boolean changed = false
        clientDataJSON.analysisCases?.each { Map analysisCase ->
            changed |= revertTemperatureFromLoadCase(analysisCase)
        }
        return changed
    }

    @Override
    void revertProject(Map projectJSON) throws ConversionException {
        super.revertProject(projectJSON)
        projectJSON.defaultLoadCases?.each { Map loadCase ->
            revertTemperatureFromLoadCase(loadCase)
        }
    }

    @Override
    void revertDesign(Map designJSON) throws ConversionException {
        super.revertDesign(designJSON)
        designJSON.analysis?.each { Map analysis ->
            if (analysis.analysisCaseDetails != null) {
                revertTemperatureFromLoadCase(analysis.analysisCaseDetails as Map)
            }
        }
    }

    @Override
    boolean revertResults(Map resultsJSON) throws ConversionException {
        boolean changed = super.revertResults(resultsJSON)
        resultsJSON.results?.each { Map result ->
            if (result.analysisCaseDetails != null) {
                changed |= revertTemperatureFromLoadCase(result.analysisCaseDetails as Map)
            }
        }
        return changed
    }

    boolean applyTemperatureToLoadCase(Map loadCaseJSON) {
        if (loadCaseJSON.type == "CSA 2020 Maximum Wind") {
            Map currTempOverride = (loadCaseJSON.overrides as Map).temperature as Map
            if (currTempOverride == null) { // up-converting a load case that was created in an old version
                (loadCaseJSON.overrides as Map).temperature = [value: -20.0, unit: "CELSIUS"]
                return true
            } else if (currTempOverride.value == 15.0) { // up-converting a load case that was created in current version
                (loadCaseJSON.overrides as Map).remove("temperature")
                return true
            }
        }
        return false
    }

    boolean revertTemperatureFromLoadCase(Map loadCaseJSON) {
        if (loadCaseJSON.type == "CSA 2020 Maximum Wind") {
            Map currTempOverride = (loadCaseJSON.overrides as Map).temperature as Map
            if (currTempOverride == null) { // down-converting a load case that was created in the current version
                (loadCaseJSON.overrides as Map).temperature = [value: 15.0, unit: "CELSIUS"]
                return true
            } else if (currTempOverride.value == -20.0) { // down-converting a load case that was created in old version
                (loadCaseJSON.overrides as Map).remove("temperature")
                return true
            }
        }
        return false
    }
}
