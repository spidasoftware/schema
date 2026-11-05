/*
 * Copyright (c) 2026 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v14

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.calc.AbstractResultsChangeSet
import groovy.transform.CompileStatic

@CompileStatic
class AnalyzedStructureGroupChangeset extends AbstractResultsChangeSet {

    @Override
    boolean applyToResults(Map resultsJSON) {
        if (resultsJSON.containsKey("analyzedStructure")) {
            Map analyzedStructure = resultsJSON.remove("analyzedStructure") as Map
            resultsJSON.analyzedStructureGroup = [
                primaryStructureId: resultsJSON.id,
                primaryStructure: analyzedStructure,
                neighbors: [],
                wireConnections: [],
                spanGuyConnections: []
            ]
        }
        return false
    }

    @Override
    boolean revertResults(Map resultsJSON) {
        if (resultsJSON.containsKey("analyzedStructureGroup")) {
            Map structureGroup = resultsJSON.remove("analyzedStructureGroup") as Map
            if (structureGroup?.primaryStructure != null) {
                resultsJSON.analyzedStructure = structureGroup.primaryStructure
            }
        }
        return false
    }

    @Override
    void applyToDesign(Map designJSON) throws ConversionException {
        Map detailedResults = inlineDetailedResults(designJSON)
        if (detailedResults != null) {
            applyToResults(detailedResults)
        }
    }

    @Override
    void revertDesign(Map designJSON) throws ConversionException {
        Map detailedResults = inlineDetailedResults(designJSON)
        if (detailedResults != null) {
            revertResults(detailedResults)
        }
    }

    private static Map inlineDetailedResults(Map designJSON) {
        Map analysisDetails = designJSON.analysisDetails as Map
        return analysisDetails?.detailedResults as Map
    }
}
