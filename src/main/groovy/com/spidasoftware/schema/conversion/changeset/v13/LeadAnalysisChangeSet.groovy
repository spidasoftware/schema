/*
 * Copyright (c) 2026 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v13

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.client.AbstractClientDataChangeSet
import groovy.transform.CompileStatic

@CompileStatic
class LeadAnalysisChangeSet extends AbstractClientDataChangeSet {

    @Override
    boolean applyToClientData(Map clientDataJSON) throws ConversionException {
        // do nothing
        return false
    }

    @Override
    boolean revertClientData(Map clientDataJSON) throws ConversionException {
        boolean changed = false
        clientDataJSON.analysisCases?.each { Map analysisCase ->
            changed |= revertLoadCase(analysisCase)
        }
        return changed
    }

    @Override
    void revertProject(Map projectJSON) throws ConversionException {
        super.revertProject(projectJSON)
        projectJSON.defaultLoadCases?.each { Map loadCase ->
            revertLoadCase(loadCase)
        }
    }

    @Override
    void revertDesign(Map designJSON) throws ConversionException {
        super.revertDesign(designJSON)
        eachDesignLoadCase(designJSON) { Map loadCase -> revertLoadCase(loadCase) }
        designJSON.remove("pendingAnalysis")
    }

    @Override
    boolean revertResults(Map resultsJSON) throws ConversionException {
        boolean changed = super.revertResults(resultsJSON)
        changed |= eachResultLoadCase(resultsJSON) { Map loadCase -> revertLoadCase(loadCase) }
        return changed
    }

    boolean revertLoadCase(Map loadCaseJSON) {
        boolean changed = (loadCaseJSON.remove("includeNeighborStructures") == true)
        changed |= (loadCaseJSON.remove("useSparseMatrices") == true)
        return changed
    }
}
