package com.spidasoftware.schema.conversion.changeset.v4

import net.sf.json.JSONObject
import spock.lang.Specification
import groovy.util.logging.Log4j
import net.sf.json.groovy.JsonSlurper

@Log4j
class AnalysisTypeChangeSetSpec extends Specification {

    def "apply and revert"() {
        setup:
            def leanStream = AnalysisTypeChangeSet.getResourceAsStream("/conversions/v4/analysis-type.json")
            JSONObject projectJSON = new JsonSlurper().parse(leanStream)
            JSONObject locationJSON = JSONObject.fromObject(projectJSON.leads[0].locations[0])
            JSONObject designJSON = JSONObject.fromObject(projectJSON.leads[0].locations[0].designs[0])

            AnalysisTypeChangeSet analysisTypeChangeSet = new AnalysisTypeChangeSet()
        when: "applyToProject"
            analysisTypeChangeSet.applyToProject(projectJSON)
        then:
            projectJSON.get("leads")[0].get("locations")[0].get("designs")[0].get("analysis")[0].get("results")[0].analysisType=="STRESS"
        when: "revertProject"
            analysisTypeChangeSet.revertProject(projectJSON)
        then:
            projectJSON.get("leads")[0].get("locations")[0].get("designs")[0].get("analysis")[0].get("results")[0].analysisType==null
        when: "applyToLocation"
            analysisTypeChangeSet.applyToLocation(locationJSON)
        then:
            locationJSON.get("designs")[0].get("analysis")[0].get("results")[0].analysisType=="STRESS"
        when: "revertLocation"
            analysisTypeChangeSet.revertLocation(locationJSON)
        then:
            locationJSON.get("designs")[0].get("analysis")[0].get("results")[0].analysisType==null
        when: "applyToDesign"
            analysisTypeChangeSet.applyToDesign(designJSON)
        then:
            designJSON.get("analysis")[0].get("results")[0].analysisType=="STRESS"
        when: "revertDesign"
            analysisTypeChangeSet.revertDesign(designJSON)
        then:
            designJSON.get("analysis")[0].get("results")[0].analysisType==null
    }


}
