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
        if (projectJSON.containsKey("clientData")) {
            Map clientDataJSON = projectJSON.clientData as Map
            revertClientData(clientDataJSON)
        }

        if (projectJSON.containsKey("defaultLoadCases")) {
            List<Map> defaultLoadCases = projectJSON.defaultLoadCases as List<Map>
            defaultLoadCases
                    .findAll { it.type == "NESC Extreme Wind 2007" || it.type == "NESC Extreme Wind 2012" }
                    .findAll { it.windSpeed == "MPH_85" }
                    .each { it.windSpeed = "MPH_90" }
        }

        if (projectJSON.containsKey("leads")) {
            List<Map> leadsList = projectJSON.leads as List<Map>
            leadsList.each { Map leadMap ->
                if (leadMap.containsKey("locations")) {
                    List<Map> locationsList = leadMap.locations as List<Map>
                    locationsList.each { Map locationMap ->
                        if (locationMap.containsKey("designs")) {
                            List<Map> designsList = locationMap.designs as List<Map>
                            designsList.each { Map designMap ->
                                revertDesign(designMap)
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    void revertDesign(Map designJSON) throws ConversionException {
        List<Map> analysisList = designJSON.analysis as List<Map>
        analysisList.each { Map analysisMap ->
            Map analysisCaseDetails = analysisMap.analysisCaseDetails as Map
            if (analysisCaseDetails.type == "NESC Extreme Wind 2007" || analysisCaseDetails.type == "NESC Extreme Wind 2012") {
                if (analysisCaseDetails.windSpeed == "MPH_85") {
                    analysisCaseDetails.windSpeed = "MPH_90"
                }
            }
        }
    }
}
