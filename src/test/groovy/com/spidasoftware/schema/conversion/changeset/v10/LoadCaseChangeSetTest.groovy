/*
 * ©2009-2023 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v10

import groovy.json.JsonSlurper
import spock.lang.Specification

class LoadCaseChangeSetTest extends Specification {

    static LoadCaseChangeSet changeSet

    def setupSpec() {
        changeSet = new LoadCaseChangeSet()
    }

    def "revert client data json"() {
        setup:
            def stream = LoadCaseChangeSetTest.getResourceAsStream("/conversions/v10/LoadCaseIceDensity-client.json".toString())
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
        expect:
            json.analysisCases.findAll { it.type == "CSA 2020 Maximum Wind" }.size() == 1
            json.analysisCases.findAll { it.type == "NESC Extreme Wind 2023" }.size() == 1
            json.analysisCases.findAll { it.type == "NESC Extreme Ice 2023" }.size() == 1
            json.analysisCases.findAll { it.type == "NESC 2023" }.size() == 1
            json.analysisCases.findAll { it.type == "GO95 01-2020" }.size() == 1
            json.analysisCases.size() == 21
        when: "apply changeset"
            boolean anyChanged = changeSet.revertClientData(json)
        then: "load cases get removed"
            anyChanged
            json.analysisCases.findAll { it.type == "CSA 2020 Maximum Wind" }.size() == 0
            json.analysisCases.findAll { it.type == "NESC Extreme Wind 2023" }.size() == 0
            json.analysisCases.findAll { it.type == "NESC Extreme Ice 2023" }.size() == 0
            json.analysisCases.findAll { it.type == "NESC 2023" }.size() == 0
            json.analysisCases.findAll { it.type == "GO95 01-2020" }.size() == 0
            json.analysisCases.size() == 16
    }

    def "revert project json"() {
        setup:
            def stream = LoadCaseChangeSetTest.getResourceAsStream("/conversions/v10/LoadCaseIceDensity-project.json".toString())
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
        expect:
            json.defaultLoadCases.findAll { it.type == "CSA 2020 Maximum Wind" }.size() == 1
            json.defaultLoadCases.findAll { it.type == "NESC Extreme Wind 2023" }.size() == 1
            json.defaultLoadCases.findAll { it.type == "NESC Extreme Ice 2023" }.size() == 1
            json.defaultLoadCases.findAll { it.type == "NESC 2023" }.size() == 1
            json.defaultLoadCases.findAll { it.type == "GO95 01-2020" }.size() == 1
            json.defaultLoadCases.size() == 18
        when: "apply changeset"
            changeSet.revertProject(json)
        then: "load cases get removed"
            json.defaultLoadCases.findAll { it.type == "CSA 2020 Maximum Wind" }.size() == 0
            json.defaultLoadCases.findAll { it.type == "NESC Extreme Wind 2023" }.size() == 0
            json.defaultLoadCases.findAll { it.type == "NESC Extreme Ice 2023" }.size() == 0
            json.defaultLoadCases.findAll { it.type == "NESC 2023" }.size() == 0
            json.defaultLoadCases.findAll { it.type == "GO95 01-2020" }.size() == 0
            json.defaultLoadCases.size() == 13
    }

    def "revert results json"() {
        setup:
            def stream = LoadCaseChangeSetTest.getResourceAsStream("/conversions/v10/LoadCaseIceDensity-results.json".toString())
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
        expect:
            json.results.findAll { it.analysisCaseDetails.type == "CSA 2020 Maximum Wind" }.size() == 1
            json.results.findAll { it.analysisCaseDetails.type == "NESC Extreme Wind 2023" }.size() == 1
            json.results.findAll { it.analysisCaseDetails.type == "NESC Extreme Ice 2023" }.size() == 1
            json.results.findAll { it.analysisCaseDetails.type == "NESC 2023" }.size() == 1
            json.results.findAll { it.analysisCaseDetails.type == "GO95 01-2020" }.size() == 1
            json.results.size() == 19
        when: "apply changeset"
            boolean anyChanged = changeSet.revertResults(json)
        then: "load cases get removed"
            anyChanged
            json.results.findAll { it.analysisCaseDetails.type == "CSA 2020 Maximum Wind" }.size() == 0
            json.results.findAll { it.analysisCaseDetails.type == "NESC Extreme Wind 2023" }.size() == 0
            json.results.findAll { it.analysisCaseDetails.type == "NESC Extreme Ice 2023" }.size() == 0
            json.results.findAll { it.analysisCaseDetails.type == "NESC 2023" }.size() == 0
            json.results.findAll { it.analysisCaseDetails.type == "GO95 01-2020" }.size() == 0
            json.results.size() == 14
    }

    def "revert design json when design.analysis have no analysisCaseDetails"() {
        setup:
            def stream = LoadCaseChangeSetTest.getResourceAsStream("/conversions/v10/StudioDesignWithoutAnalysisCaseDetails.json".toString())
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
        expect:
            json.calcDesign.analysis.size() == 2
            json.calcDesign.analysis.every { !it.containsKey("analysisCaseDetails") }
        when: "apply changeset"
            boolean anyChanged = changeSet.revertDesign(json.calcDesign)
        then: "no exception thrown"
            !anyChanged
    }
}
