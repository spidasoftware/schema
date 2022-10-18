/*
 * ©2009-2022 SPIDAWEB LLC
 */

package com.spidasoftware.schema.conversion.changeset.v9

import groovy.json.JsonSlurper
import spock.lang.Specification

class ExtremeWindLoadCaseChangesetTest extends Specification {
    def "revert client data"() {
        setup:
            def changeSet = new ExtremeWindLoadCaseChangeset()
            def stream = ExtremeWindLoadCaseChangeset.getResourceAsStream("/conversions/v9/extremeWindLoadCase.v9.client.json".toString())
            Map json = new JsonSlurper().parse(stream)
            stream.close()
        when:
            boolean reverted = changeSet.revertClientData(json)
        then:
            json.analysisCases[0].windSpeed == "MPH_100"
            json.analysisCases[1].windSpeed == "MPH_90"
            reverted
    }

    def "revert project"() {
        setup:
            def changeSet = new ExtremeWindLoadCaseChangeset()
            def stream = ExtremeWindLoadCaseChangeset.getResourceAsStream("/conversions/v9/extremeWindLoadCase.v9.project.json".toString())
            Map json = new JsonSlurper().parse(stream)
            stream.close()
        when:
            changeSet.revertProject(json)
        then:
            json.clientData.analysisCases[0].windSpeed == "MPH_100"
            json.clientData.analysisCases[1].windSpeed == "MPH_90"
            json.defaultLoadCases[3].windSpeed == "MPH_90"
            json.leads[0].locations[0].designs[0].analysis[3].analysisCaseDetails.windSpeed == "MPH_90"
    }
}
