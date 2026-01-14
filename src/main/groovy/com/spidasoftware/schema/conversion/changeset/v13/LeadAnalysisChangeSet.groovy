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
            analysisCase.remove("includeNeighborStructures")
            changed = true
        }
        return changed
    }

    @Override
    void revertProject(Map projectJSON) throws ConversionException {
        super.revertProject(projectJSON)
        projectJSON.defaultLoadCases?.each { Map loadCase ->
            loadCase.remove("includeNeighborStructures")
        }
    }

    @Override
    void revertDesign(Map designJSON) throws ConversionException {
        super.revertDesign(designJSON)
        designJSON.analysis?.each { Map analysis ->
            Map analysisCase = analysis.analysisCaseDetails as Map
            analysisCase?.remove("includeNeighborStructures")
        }
    }

    @Override
    boolean revertResults(Map resultsJSON) throws ConversionException {
        boolean changed = super.revertResults(resultsJSON)
        resultsJSON.results?.each { Map result ->
            Map analysisCase = result.analysisCaseDetails as Map
            if (analysisCase) {
                analysisCase.remove("includeNeighborStructures")
                changed = true
            }
        }
        return changed
    }
}
