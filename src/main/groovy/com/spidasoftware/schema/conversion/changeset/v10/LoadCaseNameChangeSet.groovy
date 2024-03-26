/*
 * ©2009-2023 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v10

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.client.AbstractClientDataChangeSet
import groovy.transform.CompileStatic

/**
 * The 2015 CSA load case name was changed to say '2015-2020'
 *
 * All 2012 NESC load case names were changed to say '2012-2017'
 *
 * When up-converting, we will change '2015' to '2015-2020' for CSA load cases and '2012' to '2012-2017' for NESC
 * When down-converting, we will change '2015-2020' to '2015' for CSA and '2012-2017' to '2012' for NESC
 */
@CompileStatic
class LoadCaseNameChangeSet extends AbstractClientDataChangeSet {

    @Override
    boolean applyToClientData(Map clientDataJSON) throws ConversionException {
        boolean anyChanged = false

        clientDataJSON.analysisCases?.each { Map analysisCase ->
            anyChanged |= applyName(analysisCase)
        }
        return anyChanged
    }

    @Override
    boolean revertClientData(Map clientDataJSON) throws ConversionException {
        boolean anyChanged = false

        clientDataJSON.analysisCases?.each { Map analysisCase ->
            anyChanged |= revertName(analysisCase)
        }
        return anyChanged
    }

    @Override
    void applyToProject(Map projectJSON) throws ConversionException {
        super.applyToProject(projectJSON)

        projectJSON.defaultLoadCases?.each { Map loadCase ->
            applyName(loadCase)
        }
    }

    @Override
    void revertProject(Map projectJSON) throws ConversionException {
        super.revertProject(projectJSON)

        projectJSON.defaultLoadCases?.each { Map loadCase ->
            revertName(loadCase)
        }
    }

    @Override
    void applyToDesign(Map designJSON) throws ConversionException {
        super.applyToDesign(designJSON)

        designJSON.analysis?.each { Map analysis ->
            if (analysis.containsKey("analysisCaseDetails")) {
                applyName(analysis.analysisCaseDetails as Map)
            }
        }
    }

    @Override
    void revertDesign(Map designJSON) throws ConversionException {
        super.revertDesign(designJSON)

        designJSON.analysis?.each { Map analysis ->
            if (analysis.containsKey("analysisCaseDetails")) {
                revertName(analysis.analysisCaseDetails as Map)
            }
        }
    }

    @Override
    boolean applyToResults(Map resultsJSON) throws ConversionException {
        boolean anyChanged = super.applyToResults(resultsJSON)

        resultsJSON.results?.each { Map result ->
            if (result.containsKey("analysisCaseDetails")) {
                anyChanged |= applyName(result.analysisCaseDetails as Map)
            }

        }
        return anyChanged
    }

    @Override
    boolean revertResults(Map resultsJSON) throws ConversionException {
        boolean anyChanged = super.revertResults(resultsJSON)

        resultsJSON.results?.each { Map result ->
            if (result.containsKey("analysisCaseDetails")) {
                anyChanged |= revertName(result.analysisCaseDetails as Map)
            }
        }
        return anyChanged
    }

    boolean applyName(Map loadCase) {
        switch (loadCase.type) {
            case "CSA 2015":
                loadCase.type = "CSA 2015-2020"
                return true
            case "NESC 2012":
                loadCase.type = "NESC 2012-2017"
                return true
            case "NESC Extreme Wind 2012":
                loadCase.type = "NESC Extreme Wind 2012-2017"
                return true
            case "NESC Extreme Ice 2012":
                loadCase.type = "NESC Extreme Ice 2012-2017"
                return true
            default:
                return false
        }
    }

    boolean revertName(Map loadCase) {
        switch (loadCase.type) {
            case "CSA 2015-2020":
                loadCase.type = "CSA 2015"
                return true
            case "NESC 2012-2017":
                loadCase.type = "NESC 2012"
                return true
            case "NESC Extreme Wind 2012-2017":
                loadCase.type = "NESC Extreme Wind 2012"
                return true
            case "NESC Extreme Ice 2012-2017":
                loadCase.type = "NESC Extreme Ice 2012"
                return true
            default:
                return false
        }
    }
}