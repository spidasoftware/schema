/*
 * Copyright (c) 2025 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v12

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.client.AbstractClientDataChangeSet
import groovy.transform.CompileStatic

/**
 * In version 25.0.0, the CSA 2020 Maximum Wind load and strength factors were updated for anchors, guys, and foundations.
 *
 * When up/down converting, remove the load and strength factors for anchors, guys, and foundations from the overrides and valuesApplied.
 * This works because when deserializing load cases, we set the default values (of the specific calc version) on the load case.
 * If we remove the overrides, then the default values of that calc version will get set.
 */
@CompileStatic
class CSAMaxWindLoadFactorsChangeSet extends AbstractClientDataChangeSet {

    static List<String> loadFactorKeys = [
            "guyTensionMultiplier", "anchorTensionMultiplier", "foundationTensionMultiplier",
            "guyVerticalLoadMultiplier", "anchorVerticalLoadMultiplier", "foundationVerticalLoadMultiplier",
            "guyWindMultiplier", "anchorWindMultiplier", "foundationWindMultiplier",
            "guyStrengthFactor", "anchorStrengthFactor", "foundationStrengthFactor"
    ]

    @Override
    boolean applyToClientData(Map clientDataJSON) throws ConversionException {
        boolean changed = false
        clientDataJSON.analysisCases?.each { Map analysisCase ->
            changed |= removeCSALoadFactors(analysisCase)
        }
        return changed
    }

    @Override
    void applyToProject(Map projectJSON) throws ConversionException {
        super.applyToProject(projectJSON)
        projectJSON.defaultLoadCases?.each { Map loadCase ->
            removeCSALoadFactors(loadCase)
        }
    }

    @Override
    void applyToDesign(Map designJSON) throws ConversionException {
        super.applyToDesign(designJSON)
        designJSON.analysis?.each { Map analysis ->
            if (analysis.analysisCaseDetails != null) {
                removeCSALoadFactors(analysis.analysisCaseDetails as Map)
            }
        }
    }

    @Override
    boolean applyToResults(Map resultsJSON) throws ConversionException {
        boolean changed = super.applyToResults(resultsJSON)
        resultsJSON.results?.each { Map result ->
            if (result.analysisCaseDetails != null) {
                changed |= removeCSALoadFactors(result.analysisCaseDetails as Map)
            }
        }
        return changed
    }

    @Override
    boolean revertClientData(Map clientDataJSON) throws ConversionException {
        boolean changed = false
        clientDataJSON.analysisCases?.each { Map analysisCase ->
            changed |= removeCSALoadFactors(analysisCase)
        }
        return changed
    }

    @Override
    void revertProject(Map projectJSON) throws ConversionException {
        super.revertProject(projectJSON)
        projectJSON.defaultLoadCases?.each { Map loadCase ->
            removeCSALoadFactors(loadCase)
        }
    }

    @Override
    void revertDesign(Map designJSON) throws ConversionException {
        super.revertDesign(designJSON)
        designJSON.analysis?.each { Map analysis ->
            if (analysis.analysisCaseDetails != null) {
                removeCSALoadFactors(analysis.analysisCaseDetails as Map)
            }
        }
    }

    @Override
    boolean revertResults(Map resultsJSON) throws ConversionException {
        boolean changed = super.revertResults(resultsJSON)
        resultsJSON.results?.each { Map result ->
            if (result.analysisCaseDetails != null) {
                changed |= removeCSALoadFactors(result.analysisCaseDetails as Map)
            }
        }
        return changed
    }

    boolean removeCSALoadFactors(Map loadCaseJSON) {
        boolean changed = false
        if (loadCaseJSON.type == "CSA 2020 Maximum Wind") {
            Map overrides = loadCaseJSON.overrides as Map
            Map valuesApplied = loadCaseJSON.valuesApplied as Map
            loadFactorKeys.each { String loadFactor ->
                changed |= overrides.remove(loadFactor) != null
                changed |= valuesApplied.remove(loadFactor) != null
            }
        }
        return changed
    }
}
