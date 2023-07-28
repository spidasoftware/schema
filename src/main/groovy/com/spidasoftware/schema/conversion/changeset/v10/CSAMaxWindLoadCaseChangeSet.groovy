/*
 * ©2009-2023 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v10

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.client.AbstractClientDataChangeSet
import groovy.transform.CompileStatic

/**
 * New CSA Max wind load case was added after Calc v8.0
 *
 * When down converting from schema v10, we want to remove the entire load case
 */
@CompileStatic
class CSAMaxWindLoadCaseChangeSet extends AbstractClientDataChangeSet {
    @Override
    boolean applyToClientData(Map clientDataJSON) throws ConversionException {
        return false
    }

    @Override
    boolean revertClientData(Map clientDataJSON) throws ConversionException {
        boolean anyChanged = false

        if (clientDataJSON.containsKey("analysisCases")) {
            List<Map> analysisCases = clientDataJSON.analysisCases as List<Map>
            anyChanged = analysisCases.removeAll { it.type == "CSA 2020 Maximum Wind" }
        }

        return anyChanged
    }

    @Override
    void revertProject(Map projectJSON) throws ConversionException {
        super.revertProject(projectJSON)

        if (projectJSON.containsKey("defaultLoadCases")) {
            List<Map> defaultLoadCases = projectJSON.defaultLoadCases as List<Map>
            defaultLoadCases.removeAll { it.type == "CSA 2020 Maximum Wind" }
        }
    }

    @Override
    void revertDesign(Map designJSON) throws ConversionException {
        super.revertDesign(designJSON)

        if (designJSON.containsKey("analysis")) {
            List<Map> analysisList = designJSON.analysis as List<Map>
            analysisList.removeAll { (it.analysisCaseDetails as Map).type == "CSA 2020 Maximum Wind" }
        }
    }

    @Override
    boolean revertResults(Map resultsJSON) throws ConversionException {
        boolean anyChanged = super.revertResults(resultsJSON)

        if (resultsJSON.containsKey("results")) {
            List<Map> resultsList = resultsJSON.results as List<Map>
            anyChanged = resultsList.removeAll { (it.analysisCaseDetails as Map).type == "CSA 2020 Maximum Wind" }
        }
        return anyChanged
    }
}
