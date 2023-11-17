/*
 * ©2009-2022 SPIDAWEB LLC
 */

package com.spidasoftware.schema.conversion.changeset.v9

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.client.AbstractClientDataChangeSet
import groovy.transform.CompileStatic

@CompileStatic
class ExtremeWindLoadCaseChangeset extends AbstractClientDataChangeSet{
    @Override
    boolean applyToClientData(Map clientDataJSON) throws ConversionException {
        return false
    }

    @Override
    boolean revertClientData(Map clientDataJSON) throws ConversionException {
        boolean anyChanged = false

        if (clientDataJSON.containsKey("analysisCases")) {
            List<Map> analysisCases = clientDataJSON.analysisCases as List<Map>
            analysisCases
                    .findAll { it.type == "NESC Extreme Wind 2007" || it.type == "NESC Extreme Wind 2012" }
                    .findAll { it.windSpeed == "MPH_85" }
                    .each {
                        it.windSpeed = "MPH_90"
                        anyChanged = true
                    }
        }
        return anyChanged
    }

    @Override
    void revertProject(Map projectJSON) throws ConversionException {
        super.revertProject(projectJSON)

        if (projectJSON.containsKey("defaultLoadCases")) {
            List<Map> defaultLoadCases = projectJSON.defaultLoadCases as List<Map>
            defaultLoadCases
                    .findAll { it.type == "NESC Extreme Wind 2007" || it.type == "NESC Extreme Wind 2012" }
                    .findAll { it.windSpeed == "MPH_85" }
                    .each { it.windSpeed = "MPH_90" }
        }
    }

    @Override
    void revertDesign(Map designJSON) throws ConversionException {
        super.revertDesign(designJSON)

        if (designJSON.containsKey("analysis")) {
            List<Map> analysisList = designJSON.analysis as List<Map>
            analysisList.each { Map analysisMap ->
                if(analysisMap.analysisCaseDetails != null) {
                    Map analysisCaseDetails = analysisMap.analysisCaseDetails as Map
                    if (analysisCaseDetails.type == "NESC Extreme Wind 2007" || analysisCaseDetails.type == "NESC Extreme Wind 2012") {
                        if (analysisCaseDetails.windSpeed == "MPH_85") {
                            analysisCaseDetails.windSpeed = "MPH_90"
                        }
                    }
                }
            }
        }
    }

    @Override
    boolean revertResults(Map resultsJSON) throws ConversionException {
        boolean anyChanged = false
        if (resultsJSON.containsKey("results")) {
            List<Map> resultsList = resultsJSON.results as List<Map>
            resultsList.each { Map resultMap ->
                if (resultMap.containsKey("analysisCaseDetails")) {
                    Map analysisCaseDetailsMap = resultMap.analysisCaseDetails as Map
                    if (analysisCaseDetailsMap.containsKey("windSpeed")) {
                        if (analysisCaseDetailsMap.windSpeed == "MPH_85") {
                            analysisCaseDetailsMap.windSpeed = "MPH_90"
                            anyChanged = true
                        }
                    }
                }
            }
        }
        return anyChanged
    }
}
