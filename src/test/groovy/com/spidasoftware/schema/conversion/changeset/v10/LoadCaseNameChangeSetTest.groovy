/*
 * ©2009-2023 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v10

import groovy.json.JsonSlurper
import spock.lang.Specification

class LoadCaseNameChangeSetTest extends Specification {

    static LoadCaseNameChangeSet changeSet

    def setupSpec() {
        changeSet = new LoadCaseNameChangeSet()
    }

    def "apply/revert client data"() {
        setup:
            def stream = LoadCaseNameChangeSetTest.getResourceAsStream("/conversions/v10/LoadCaseIceDensity-client.json".toString())
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
            boolean anyChanged
        expect:
            json.analysisCases[9].type == "NESC 2012"
            json.analysisCases[16].type == "NESC Extreme Wind 2012"
            json.analysisCases[17].type == "NESC Extreme Ice 2012"
            json.analysisCases[18].type == "CSA 2015"
        when: "apply to client data"
            anyChanged = changeSet.applyToClientData(json)
        then: "types get updated"
            anyChanged
            json.analysisCases[9].type == "NESC 2012-2017"
            json.analysisCases[16].type == "NESC Extreme Wind 2012-2017"
            json.analysisCases[17].type == "NESC Extreme Ice 2012-2017"
            json.analysisCases[18].type == "CSA 2015-2020"
        when: "revert client data"
            anyChanged = changeSet.revertClientData(json)
        then: "types get updated"
            anyChanged
            json.analysisCases[9].type == "NESC 2012"
            json.analysisCases[16].type == "NESC Extreme Wind 2012"
            json.analysisCases[17].type == "NESC Extreme Ice 2012"
            json.analysisCases[18].type == "CSA 2015"
    }

    def "apply/revert project json"() {
        setup:
            def stream = LoadCaseNameChangeSetTest.getResourceAsStream("/conversions/v10/LoadCaseIceDensity-project.json".toString())
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
        expect:
            json.defaultLoadCases[6].type == "NESC 2012"
            json.defaultLoadCases[13].type == "NESC Extreme Wind 2012"
            json.defaultLoadCases[14].type == "NESC Extreme Ice 2012"
            json.defaultLoadCases[15].type == "CSA 2015"
            json.leads[0].locations[0].designs[0].analysis[6].analysisCaseDetails.type == "NESC 2012"
            json.leads[0].locations[0].designs[0].analysis[11].analysisCaseDetails.type == "CSA 2015"
            json.leads[0].locations[0].designs[0].analysis[12].analysisCaseDetails.type == "NESC Extreme Wind 2012"
            json.leads[0].locations[0].designs[0].analysis[13].analysisCaseDetails.type == "NESC Extreme Ice 2012"

        when:
            changeSet.applyToProject(json)
        then:
            json.defaultLoadCases[6].type == "NESC 2012-2017"
            json.defaultLoadCases[13].type == "NESC Extreme Wind 2012-2017"
            json.defaultLoadCases[14].type == "NESC Extreme Ice 2012-2017"
            json.defaultLoadCases[15].type == "CSA 2015-2020"
            json.leads[0].locations[0].designs[0].analysis[6].analysisCaseDetails.type == "NESC 2012-2017"
            json.leads[0].locations[0].designs[0].analysis[11].analysisCaseDetails.type == "CSA 2015-2020"
            json.leads[0].locations[0].designs[0].analysis[12].analysisCaseDetails.type == "NESC Extreme Wind 2012-2017"
            json.leads[0].locations[0].designs[0].analysis[13].analysisCaseDetails.type == "NESC Extreme Ice 2012-2017"
        when:
            changeSet.revertProject(json)
        then:
            json.defaultLoadCases[6].type == "NESC 2012"
            json.defaultLoadCases[13].type == "NESC Extreme Wind 2012"
            json.defaultLoadCases[14].type == "NESC Extreme Ice 2012"
            json.defaultLoadCases[15].type == "CSA 2015"
            json.leads[0].locations[0].designs[0].analysis[6].analysisCaseDetails.type == "NESC 2012"
            json.leads[0].locations[0].designs[0].analysis[11].analysisCaseDetails.type == "CSA 2015"
            json.leads[0].locations[0].designs[0].analysis[12].analysisCaseDetails.type == "NESC Extreme Wind 2012"
            json.leads[0].locations[0].designs[0].analysis[13].analysisCaseDetails.type == "NESC Extreme Ice 2012"
    }

    def "apply/revert results json"() {
        setup:
            def stream = LoadCaseNameChangeSetTest.getResourceAsStream("/conversions/v10/LoadCaseIceDensity-results.json".toString())
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
            boolean anyChanged
        expect:
            json.results[1].analysisCaseDetails.type == "CSA 2015"
            json.results[2].analysisCaseDetails.type == "NESC Extreme Wind 2012"
            json.results[3].analysisCaseDetails.type == "NESC Extreme Ice 2012"
            json.results[9].analysisCaseDetails.type == "NESC 2012"
        when:
            anyChanged = changeSet.applyToResults(json)
        then:
            anyChanged
            json.results[1].analysisCaseDetails.type == "CSA 2015-2020"
            json.results[2].analysisCaseDetails.type == "NESC Extreme Wind 2012-2017"
            json.results[3].analysisCaseDetails.type == "NESC Extreme Ice 2012-2017"
            json.results[9].analysisCaseDetails.type == "NESC 2012-2017"
        when:
            anyChanged = changeSet.revertResults(json)
        then:
            anyChanged
            json.results[1].analysisCaseDetails.type == "CSA 2015"
            json.results[2].analysisCaseDetails.type == "NESC Extreme Wind 2012"
            json.results[3].analysisCaseDetails.type == "NESC Extreme Ice 2012"
            json.results[9].analysisCaseDetails.type == "NESC 2012"
    }

}
