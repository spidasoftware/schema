/*
 * ©2009-2023 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v10

import com.github.fge.jsonschema.core.report.ProcessingReport
import com.spidasoftware.schema.validation.Validator
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

    def "reverting design json that causes analysis case results to be empty also removes the detailed results"() {
        setup:
            def stream = LoadCaseChangeSetTest.getResourceAsStream("/conversions/v10/LoadCase-project.json".toString())
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
            Map design =  json.leads[0].locations[0].designs[0]
        expect: "design has an analysis with results that are not empty"
            design.analysis.size() == 2
            !design.analysis[0].results.isEmpty()
            design.analysis[1].results.isEmpty()
            design.analysisDetails
            design.analysisCurrent == true
        when: "revert design"
            changeSet.revertDesign(design)
        then: "design has no analysis with results"
            design.analysis.size() == 1
            design.analysis[0].results.isEmpty()
        and: "analysis details is also removed and analysis is not current"
            !design.analysisDetails
            design.analysisCurrent == false
    }

    def "reverting results json that ends with empty results list is invalid"() {
        setup:
            def stream = LoadCaseChangeSetTest.getResourceAsStream("/conversions/v10/LoadCase-results.json".toString())
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
            String schemaPath = "/schema/spidacalc/results/results.schema"
            Validator validator = new Validator()
            ProcessingReport report
        expect: "results not empty"
            validator.validateAndReport(schemaPath, json).isSuccess()
        when: "revert results"
            changeSet.revertResults(json)
        then: "results list is empty"
            json.results.isEmpty()
        and: "results is not valid"
            !validator.validateAndReport(schemaPath, json).isSuccess()
    }

    def "reverting design json with nonempty summary results does not remove the detailed results"() {
        setup:
            def stream = LoadCaseChangeSetTest.getResourceAsStream("/conversions/v10/LoadCase-project2.json".toString())
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
            Map design =  json.leads[0].locations[0].designs[0]
        expect: "design has an analysis with summary results and analysis details"
            design.analysis.size() == 1
            !design.analysis[0].results.isEmpty()
            design.analysisDetails
            design.analysisCurrent == true
        when: "revert design"
            changeSet.revertDesign(design)
        then: "analysis details is not removed and analysis is still current"
            design.analysisDetails
            design.analysisCurrent == true
    }
}
