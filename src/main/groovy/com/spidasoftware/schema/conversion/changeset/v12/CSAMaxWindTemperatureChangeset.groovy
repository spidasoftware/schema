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
 * When up-converting, we override the default temperature to be 15 °C if there was not already a temperature override.
 * When down-converting, we remove the temperature override if we added it while up-converting.
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
            boolean tempOverridesPresent = (loadCaseJSON.overrides as Map).containsKey("temperature")
            if (!tempOverridesPresent) {
                (loadCaseJSON.overrides as Map).temperature = [value: 15.0, unit: "CELSIUS"]
                (loadCaseJSON.valuesApplied as Map).temperature = [value: 15.0, unit: "CELSIUS"]
                return true
            }
        }
        return false
    }

    boolean revertTemperatureFromLoadCase(Map loadCaseJSON) {
        if (loadCaseJSON.type == "CSA 2020 Maximum Wind") {
            boolean shouldRemoveTempOverride = (loadCaseJSON.overrides as Map).temperature == [unit: "CELSIUS", value: 15.0]
            if (shouldRemoveTempOverride) {
                (loadCaseJSON.overrides as Map).remove("temperature")
                return true
            }
        }
        return false
    }
}
