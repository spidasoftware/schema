/*
 * ©2009-2023 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v10

import groovy.json.JsonSlurper
import spock.lang.Specification

class CSAMaxWindLoadCaseChangeSetTest extends Specification {

    static CSAMaxWindLoadCaseChangeSet changeSet

    def setupSpec() {
        changeSet = new CSAMaxWindLoadCaseChangeSet()
    }

    def "revert client data json"() {
        setup:
            def stream = CSAMaxWindLoadCaseChangeSetTest.getResourceAsStream("/conversions/v10/LoadCaseIceDensity-client.json".toString())
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
        expect:
            json.analysisCases.findAll { it.type == "CSA 2020 Maximum Wind" }.size() == 1
            json.analysisCases.size() == 14
        when: "apply changeset"
            boolean anyChanged = changeSet.revertClientData(json)
        then: "csa max wind case gets removed"
            anyChanged
            json.analysisCases.findAll { it.type == "CSA 2020 Maximum Wind" }.size() == 0
            json.analysisCases.size() == 13
    }

    def "revert project json"() {
        setup:
            def stream = CSAMaxWindLoadCaseChangeSetTest.getResourceAsStream("/conversions/v10/LoadCaseIceDensity-project.json".toString())
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
        expect:
            json.defaultLoadCases.findAll { it.type == "CSA 2020 Maximum Wind" }.size() == 1
            json.defaultLoadCases.size() == 11
        when: "apply changeset"
            changeSet.revertProject(json)
        then: "csa max wind case gets removed"
            json.defaultLoadCases.findAll { it.type == "CSA 2020 Maximum Wind" }.size() == 0
            json.defaultLoadCases.size() == 10
    }

    def "revert results json"() {
        setup:
            def stream = CSAMaxWindLoadCaseChangeSetTest.getResourceAsStream("/conversions/v10/LoadCaseIceDensity-results.json".toString())
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
        expect:
            json.results.findAll { it.analysisCaseDetails.type == "CSA 2020 Maximum Wind" }.size() == 1
            json.results.size() == 12
        when: "apply changeset"
            boolean anyChanged = changeSet.revertResults(json)
        then: "csa max wind case gets removed"
            anyChanged
            json.results.findAll { it.analysisCaseDetails.type == "CSA 2020 Maximum Wind" }.size() == 0
            json.results.size() == 11
    }
}
