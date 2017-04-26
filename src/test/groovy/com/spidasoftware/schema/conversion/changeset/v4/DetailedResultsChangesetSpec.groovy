package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.ChangeSet
import groovy.json.JsonSlurper
import groovy.util.logging.Log4j
import spock.lang.Specification

@Log4j
class DetailedResultsChangesetSpec extends Specification {

    Map projectJSON
    Map locationJSON
    Map designJSON

    DetailedResultsChangeset changeset

    void setup() {
        changeset = new DetailedResultsChangeset()
        def leanStream = DetailedResultsChangesetSpec.getResourceAsStream("/conversions/v4/project-with-detailed-results.json")
        projectJSON = new JsonSlurper().parse(leanStream)
        locationJSON = ChangeSet.duplicateAsJson(projectJSON.leads[0].locations[0])
        designJSON = ChangeSet.duplicateAsJson(projectJSON.leads[0].locations[0].designs[0])
    }

    void "test revert"() {
        setup:

        when:
            changeset.revertProject(projectJSON)
            List analysis = projectJSON.leads[0].locations[0].designs[0].analysis
        then:
            analysis.size() == 1
            analysis.first().id == "Medium"
            analysis.first().results.size() == 10
            def result = analysis.first().results.find { it.component == component }
            Math.abs(result.actual - actual) < 0.01
            Math.abs(result.allowable - allowable) < 0.01
            result.unit == unit
            result.analysisDate == analysisDate
            result.component == component
            result.loadInfo == loadInfo
            result.passes == passes
            result.analysisType == analysisType
        when:
            changeset.revertLocation(locationJSON)
            analysis = locationJSON.designs[0].analysis
        then:
            analysis.size() == 1
            analysis.first().id == "Medium"
            analysis.first().results.size() == 10
            def resultFromLocation = analysis.first().results.find { it.component == component }
            Math.abs(resultFromLocation.actual - actual) < 0.01
            Math.abs(resultFromLocation.allowable - allowable) < 0.01
            resultFromLocation.unit == unit
            resultFromLocation.analysisDate == analysisDate
            resultFromLocation.component == component
            resultFromLocation.loadInfo == loadInfo
            resultFromLocation.passes == passes
            resultFromLocation.analysisType == analysisType
        when:
            changeset.revertDesign(designJSON)
            analysis = designJSON.analysis
        then:
            analysis.size() == 1
            analysis.first().id == "Medium"
            analysis.first().results.size() == 10
            def resultFromDesign = analysis.first().results.find { it.component == component }
            Math.abs(resultFromDesign.actual - actual) < 0.01
            Math.abs(resultFromDesign.allowable - allowable) < 0.01
            resultFromDesign.unit == unit
            resultFromDesign.analysisDate == analysisDate
            resultFromDesign.component == component
            resultFromDesign.loadInfo == loadInfo
            resultFromDesign.passes == passes
            resultFromDesign.analysisType == analysisType
        where:
            component     | actual | allowable | unit      | analysisDate  | loadInfo | passes | analysisType
            "Pole"        | 4.47   | 100.0     | "PERCENT" | 1484845969502 | "Medium" | true   | "STRESS"
            "CrossArm#1"  | 6.88   | 100.0     | "PERCENT" | 1484845969502 | "Medium" | true   | "STRESS"
            "Insulator#1" | 4.17   | 100.0     | "PERCENT" | 1484845969502 | "Medium" | true   | "FORCE"
            "PushBrace#1" | 0.16   | 100.0     | "PERCENT" | 1484845969502 | "Medium" | true   | "STRESS"
            "CrossArm#2"  | 6.88   | 100.0     | "PERCENT" | 1484845969502 | "Medium" | true   | "STRESS"
            "Insulator#2" | 4.17   | 100.0     | "PERCENT" | 1484845969502 | "Medium" | true   | "FORCE"
            "Anchor#1"    | 1.31   | 100.0     | "PERCENT" | 1484845969502 | "Medium" | true   | "FORCE"
            "Guy#1"       | 0.43   | 100.0     | "PERCENT" | 1484845969502 | "Medium" | true   | "FORCE"
            "Anchor#2"    | 1.45   | 100.0     | "PERCENT" | 1484845969502 | "Medium" | true   | "FORCE"
            "Guy#2"       | 0.48   | 100.0     | "PERCENT" | 1484845969502 | "Medium" | true   | "FORCE"
    }

    void "test revert when no analysis doesn't error"() {
        setup:
            projectJSON.leads[0].locations[0].designs[0].analysis = []
            locationJSON.designs[0].analysis = []
            designJSON.analysis = []
        when:
            changeset.revertProject(projectJSON)
        then:
            notThrown(Exception)
        when:
            changeset.revertLocation(locationJSON)
        then:
            notThrown(Exception)
        when:
            changeset.revertDesign(designJSON)
        then:
            notThrown(Exception)
    }
}
