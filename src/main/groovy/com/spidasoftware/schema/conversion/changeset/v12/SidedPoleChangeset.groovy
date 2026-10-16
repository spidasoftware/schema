/*
 * Copyright (c) 2025 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v12

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.client.AbstractClientDataChangeSet
import groovy.transform.CompileStatic

/**
 * Throws a ConversionException if a non-round pole is present in the client data.
 */
@CompileStatic
class SidedPoleChangeset extends AbstractClientDataChangeSet {

    @Override
    void revertProject(Map projectJSON) throws ConversionException {
        super.revertProject(projectJSON)

        projectJSON.defaultLoadCases?.each { Map loadCaseJSON ->
            revertLoadCase(loadCaseJSON)
        }
    }

    @Override
    void revertDesign(Map designJSON) throws ConversionException {
        super.revertDesign(designJSON)

        designJSON.analysis?.each { Map analysisMap ->
            if (analysisMap.analysisCaseDetails != null) {
                revertLoadCase(analysisMap.analysisCaseDetails as Map)
            }
        }
    }

    @Override
    boolean revertResults(Map resultsJSON) throws ConversionException {
        boolean changed = super.revertResults(resultsJSON)

        resultsJSON?.results?.each { Map result ->
            if (result.analysisCaseDetails != null) {
                changed |= revertLoadCase(result.analysisCaseDetails as Map)
            }
        }

        return changed
    }

    @Override
    boolean applyToClientData(Map clientDataJSON) throws ConversionException {
        // do nothing
        return false
    }

    @Override
    boolean revertClientData(Map clientDataJSON) throws ConversionException {
        // if non-round pole is present throw ConversionException
        clientDataJSON.poles?.each { Map pole ->
            if (pole.shape != "ROUND") {
                throw new ConversionException("Cannot downconvert a ${pole.shape} pole.")
            }
        }

        boolean anyChanged = false

        clientDataJSON.analysisCases?.each { Map analysisCase ->
            anyChanged |= revertLoadCase(analysisCase)
        }

        return anyChanged
    }

    boolean revertLoadCase(Map loadCaseJSON) {
        Map overrideMultipliers = (loadCaseJSON.overrides as Map)?.areaMultipliers as Map
        boolean hasRectangle = (overrideMultipliers?.remove("RECTANGLE") != null)
        boolean hasPolygon = (overrideMultipliers?.remove("POLYGON") != null)

        // valuesApplied is not deserialized, but better safe than sorry
        ((loadCaseJSON.valuesApplied as Map)?.areaMultipliers as Map)?.remove("RECTANGLE")
        ((loadCaseJSON.valuesApplied as Map)?.areaMultipliers as Map)?.remove("POLYGON")

        return hasRectangle || hasPolygon
    }
}
