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
 * When up-converting, we override the default temperature to be 15 °C.
 * When down-converting, we remove the override.
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
        projectJSON.defaultLoadCases?.each { Map loadCase ->
            applyTemperatureToLoadCase(loadCase)
        }
    }

    @Override
    void applyToDesign(Map designJSON) throws ConversionException {
        designJSON.analysis?.each { Map analysis ->
            if (analysis.analysisCaseDetails != null) {
                applyTemperatureToLoadCase(analysis.analysisCaseDetails as Map)
            }
        }
    }

    @Override
    boolean applyToResults(Map resultsJSON) throws ConversionException {
        boolean changed = false
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
        projectJSON.defaultLoadCases?.each { Map loadCase ->
            revertTemperatureFromLoadCase(loadCase)
        }
    }

    @Override
    void revertDesign(Map designJSON) throws ConversionException {
        designJSON.analysis?.each { Map analysis ->
            if (analysis.analysisCaseDetails != null) {
                revertTemperatureFromLoadCase(analysis.analysisCaseDetails as Map)
            }
        }
    }

    @Override
    boolean revertResults(Map resultsJSON) throws ConversionException {
        boolean changed = false
        resultsJSON.results?.each { Map result ->
            if (result.analysisCaseDetails != null) {
                changed |= revertTemperatureFromLoadCase(result.analysisCaseDetails as Map)
            }
        }
        return changed
    }

    boolean applyTemperatureToLoadCase(Map loadCaseJSON) {
        if (loadCaseJSON.type == "CSA 2020 Maximum Wind") {
            (loadCaseJSON.overrides as Map).temperature = [value: 15, unit: "CELSIUS"]
            (loadCaseJSON.valuesApplied as Map).temperature = [value: 15, unit: "CELSIUS"]
            return true
        }
        return false
    }

    boolean revertTemperatureFromLoadCase(Map loadCaseJSON) {
        return (loadCaseJSON.overrides as Map)?.remove("temperature") != null
    }
}
