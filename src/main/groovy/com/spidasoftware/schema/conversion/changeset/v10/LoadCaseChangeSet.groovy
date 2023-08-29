/*
 * ©2009-2023 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v10

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.client.AbstractClientDataChangeSet
import groovy.transform.CompileStatic

/**
 * New load cases were added after Calc v8.0
 *
 * When down converting from schema v10, we want to remove these load cases
 */
@CompileStatic
class LoadCaseChangeSet extends AbstractClientDataChangeSet {

    // list of load cases we want to remove
    private List<String> loadCases = ["CSA 2020 Maximum Wind", "NESC Extreme Wind 2023", "NESC Extreme Ice 2023"]

    @Override
    boolean applyToClientData(Map clientDataJSON) throws ConversionException {
        return false
    }

    @Override
    boolean revertClientData(Map clientDataJSON) throws ConversionException {
        boolean anyChanged = false

        if (clientDataJSON.containsKey("analysisCases")) {
            List<Map> analysisCases = clientDataJSON.analysisCases as List<Map>
            anyChanged = analysisCases.removeAll { loadCases.contains(it.type) }
        }

        return anyChanged
    }

    @Override
    void revertProject(Map projectJSON) throws ConversionException {
        super.revertProject(projectJSON)

        if (projectJSON.containsKey("defaultLoadCases")) {
            List<Map> defaultLoadCases = projectJSON.defaultLoadCases as List<Map>
            defaultLoadCases.removeAll { loadCases.contains(it.type) }
        }
    }

    @Override
    void revertDesign(Map designJSON) throws ConversionException {
        super.revertDesign(designJSON)

        if (designJSON.containsKey("analysis")) {
            List<Map> analysisList = designJSON.analysis as List<Map>
            analysisList.removeAll { loadCases.contains((it.analysisCaseDetails as Map).type) }
        }
    }

    @Override
    boolean revertResults(Map resultsJSON) throws ConversionException {
        boolean anyChanged = super.revertResults(resultsJSON)

        if (resultsJSON.containsKey("results")) {
            List<Map> resultsList = resultsJSON.results as List<Map>
            anyChanged = resultsList.removeAll { loadCases.contains((it.analysisCaseDetails as Map).type) }
        }
        return anyChanged
    }
}
