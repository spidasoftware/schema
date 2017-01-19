package com.spidasoftware.schema.conversion.changeset.v4

import groovy.util.logging.Log4j
import net.sf.json.JSONArray
import net.sf.json.JSONObject
import net.sf.json.groovy.JsonSlurper
import spock.lang.Specification

@Log4j
class DetailedResultsChangesetSpec extends Specification {

    void "test revert"() {
        setup:
            def leanStream = AnalysisTypeChangeSet.getResourceAsStream("/conversions/v4/ProjectWithDetailedResults.json")
            JSONObject projectJSON = new JsonSlurper().parse(leanStream)
            JSONObject locationJSON = JSONObject.fromObject(projectJSON.leads[0].locations[0])
            JSONObject designJSON = JSONObject.fromObject(projectJSON.leads[0].locations[0].designs[0])

            DetailedResultsChangeset changeset = new DetailedResultsChangeset()
        when:
            changeset.revertProject(projectJSON)
            JSONArray analysis = projectJSON.leads[0].locations[0].designs[0].analysis
            log.info("analysis = ${analysis.toString(2)}")
        then:
            analysis.size() == 1
            analysis.first().id == "Medium"
            analysis.first().results.size() == 1
            analysis.first().results.every { JSONObject newSummary ->
                JSONObject summaryResult = summaryAnalysis.first().results.find { it.component == newSummary }
                newSummary.actual == summaryResult.actual
                newSummary.actual == summaryResult.actual
            }


    }

    JSONArray summaryAnalysis = JSONArray.fromObject("""[        {
          "id": "Medium",
          "results":           [
                        {
              "actual": 4.473572344493995,
              "allowable": 100,
              "unit": "PERCENT",
              "analysisDate": 1484845969502,
              "component": "Pole",
              "loadInfo": "Medium",
              "passes": true,
              "analysisType": "STRESS"
            },
                        {
              "actual": 6.889231343242787,
              "allowable": 100,
              "unit": "PERCENT",
              "analysisDate": 1484845969502,
              "component": "CrossArm#1",
              "loadInfo": "Medium",
              "passes": true,
              "analysisType": "STRESS"
            },
                        {
              "actual": 4.17332194322955,
              "allowable": 100,
              "unit": "PERCENT",
              "analysisDate": 1484845982075,
              "component": "Insulator#1",
              "loadInfo": "Medium",
              "passes": true,
              "analysisType": "FORCE"
            },
                        {
              "actual": 0.16891023296250338,
              "allowable": 100,
              "unit": "PERCENT",
              "analysisDate": 1484845969502,
              "component": "PushBrace#1",
              "loadInfo": "Medium",
              "passes": true,
              "analysisType": "STRESS"
            },
                        {
              "actual": 6.889231344314371,
              "allowable": 100,
              "unit": "PERCENT",
              "analysisDate": 1484845969502,
              "component": "CrossArm#2",
              "loadInfo": "Medium",
              "passes": true,
              "analysisType": "STRESS"
            },
                        {
              "actual": 4.173321943191756,
              "allowable": 100,
              "unit": "PERCENT",
              "analysisDate": 1484845969502,
              "component": "Insulator#2",
              "loadInfo": "Medium",
              "passes": true,
              "analysisType": "FORCE"
            },
                        {
              "actual": 1.3114711790481888,
              "allowable": 100,
              "unit": "PERCENT",
              "analysisDate": 1484845969502,
              "component": "Anchor#1",
              "loadInfo": "Medium",
              "passes": true,
              "analysisType": "FORCE"
            },
                        {
              "actual": 0.4322765183465587,
              "allowable": 100,
              "unit": "PERCENT",
              "analysisDate": 1484845969502,
              "component": "Guy#1",
              "loadInfo": "Medium",
              "passes": true,
              "analysisType": "FORCE"
            },
                        {
              "actual": 1.459755589306103,
              "allowable": 100,
              "unit": "PERCENT",
              "analysisDate": 1484845969502,
              "component": "Anchor#2",
              "loadInfo": "Medium",
              "passes": true,
              "analysisType": "FORCE"
            },
                        {
              "actual": 0.48115282582125657,
              "allowable": 100,
              "unit": "PERCENT",
              "analysisDate": 1484845969502,
              "component": "Guy#2",
              "loadInfo": "Medium",
              "passes": true,
              "analysisType": "FORCE"
            }
          ]
        }]""")
}
