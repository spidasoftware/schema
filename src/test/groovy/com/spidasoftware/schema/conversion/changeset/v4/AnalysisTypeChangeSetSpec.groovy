package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.calc.CalcProjectChangeSet
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import spock.lang.Specification

@Slf4j
class AnalysisTypeChangeSetSpec extends Specification {

    def "apply and revert"() {
        setup:
            def leanStream = AnalysisTypeChangeSet.getResourceAsStream("/conversions/v4/analysis-type.json")
            Map projectJSON = new JsonSlurper().parse(leanStream)
            Map locationJSON = CalcProjectChangeSet.duplicateAsJson(projectJSON.leads[0].locations[0])
            Map designJSON = CalcProjectChangeSet.duplicateAsJson(projectJSON.leads[0].locations[0].designs[0])

            AnalysisTypeChangeSet analysisTypeChangeSet = new AnalysisTypeChangeSet()
        when: "applyToProject"
            analysisTypeChangeSet.applyToProject(projectJSON)
        then:
            projectJSON.get("leads")[0].get("locations")[0].get("designs")[0].get("analysis")[0].get("results")[0].analysisType == "STRESS"
            projectJSON.get("leads")[0].get("locations")[0].get("designs")[0].get("analysis")[0].get("results")[0].component == "Pole"
            projectJSON.get("leads")[0].get("locations")[0].get("designs")[0].get("analysis")[0].get("results")[1].analysisType == "BUCKLING"
            projectJSON.get("leads")[0].get("locations")[0].get("designs")[0].get("analysis")[0].get("results")[1].component == "Pole"
            projectJSON.get("leads")[0].get("locations")[0].get("designs")[0].get("analysis")[0].get("results")[2].analysisType == "STRENGTH"
            projectJSON.get("leads")[0].get("locations")[0].get("designs")[0].get("analysis")[0].get("results")[2].component == "Pole"
            projectJSON.get("leads")[0].get("locations")[0].get("designs")[0].get("analysis")[0].get("results")[3].analysisType == "DEFLECTION"
            projectJSON.get("leads")[0].get("locations")[0].get("designs")[0].get("analysis")[0].get("results")[3].component == "Pole"
            projectJSON.get("leads")[0].get("locations")[0].get("designs")[0].get("analysis")[0].get("results")[4].analysisType == "FORCE"
            projectJSON.get("leads")[0].get("locations")[0].get("designs")[0].get("analysis")[0].get("results")[4].component == "Insulator#1"
            projectJSON.get("leads")[0].get("locations")[0].get("designs")[0].get("analysis")[0].get("results")[5].analysisType == "FORCE"
            projectJSON.get("leads")[0].get("locations")[0].get("designs")[0].get("analysis")[0].get("results")[5].component == "SidewalkBrace#1"
            projectJSON.get("leads")[0].get("locations")[0].get("designs")[0].get("analysis")[0].get("results")[6].analysisType == "FORCE"
            projectJSON.get("leads")[0].get("locations")[0].get("designs")[0].get("analysis")[0].get("results")[6].component == "Anchor#1"
            projectJSON.get("leads")[0].get("locations")[0].get("designs")[0].get("analysis")[0].get("results")[7].analysisType == "FORCE"
            projectJSON.get("leads")[0].get("locations")[0].get("designs")[0].get("analysis")[0].get("results")[7].component == "Guy#1"
            projectJSON.get("leads")[0].get("locations")[0].get("designs")[0].get("analysis")[0].get("results")[8].analysisType == "MOMENT"
            projectJSON.get("leads")[0].get("locations")[0].get("designs")[0].get("analysis")[0].get("results")[8].component == "Foundation"

        when: "revertProject"
            analysisTypeChangeSet.revertProject(projectJSON)
        then:
            projectJSON.get("leads")[0].get("locations")[0].get("designs")[0].get("analysis")[0].get("results")[0].analysisType == null
            projectJSON.get("leads")[0].get("locations")[0].get("designs")[0].get("analysis")[0].get("results")[0].component == "Pole"
            projectJSON.get("leads")[0].get("locations")[0].get("designs")[0].get("analysis")[0].get("results")[1].analysisType == null
            projectJSON.get("leads")[0].get("locations")[0].get("designs")[0].get("analysis")[0].get("results")[1].component == "Pole-Buckling"
            projectJSON.get("leads")[0].get("locations")[0].get("designs")[0].get("analysis")[0].get("results")[2].analysisType == null
            projectJSON.get("leads")[0].get("locations")[0].get("designs")[0].get("analysis")[0].get("results")[2].component == "Pole-Strength"
            projectJSON.get("leads")[0].get("locations")[0].get("designs")[0].get("analysis")[0].get("results")[3].analysisType == null
            projectJSON.get("leads")[0].get("locations")[0].get("designs")[0].get("analysis")[0].get("results")[3].analysisType == null
            projectJSON.get("leads")[0].get("locations")[0].get("designs")[0].get("analysis")[0].get("results")[3].component == "Pole-Deflection"
            projectJSON.get("leads")[0].get("locations")[0].get("designs")[0].get("analysis")[0].get("results")[4].analysisType == null
            projectJSON.get("leads")[0].get("locations")[0].get("designs")[0].get("analysis")[0].get("results")[4].component == "Insulator#1"
            projectJSON.get("leads")[0].get("locations")[0].get("designs")[0].get("analysis")[0].get("results")[5].analysisType == null
            projectJSON.get("leads")[0].get("locations")[0].get("designs")[0].get("analysis")[0].get("results")[5].component == "SidewalkBrace#1"
            projectJSON.get("leads")[0].get("locations")[0].get("designs")[0].get("analysis")[0].get("results")[6].analysisType == null
            projectJSON.get("leads")[0].get("locations")[0].get("designs")[0].get("analysis")[0].get("results")[6].component == "Anchor#1"
            projectJSON.get("leads")[0].get("locations")[0].get("designs")[0].get("analysis")[0].get("results")[7].analysisType == null
            projectJSON.get("leads")[0].get("locations")[0].get("designs")[0].get("analysis")[0].get("results")[7].component == "Guy#1"
            projectJSON.get("leads")[0].get("locations")[0].get("designs")[0].get("analysis")[0].get("results")[8].analysisType == null
            projectJSON.get("leads")[0].get("locations")[0].get("designs")[0].get("analysis")[0].get("results")[8].component == "Foundation"
        when: "applyToLocation"
            analysisTypeChangeSet.applyToLocation(locationJSON)
        then:
            locationJSON.get("designs")[0].get("analysis")[0].get("results")[0].analysisType == "STRESS"
            locationJSON.get("designs")[0].get("analysis")[0].get("results")[0].component == "Pole"
            locationJSON.get("designs")[0].get("analysis")[0].get("results")[1].analysisType == "BUCKLING"
            locationJSON.get("designs")[0].get("analysis")[0].get("results")[1].component == "Pole"
            locationJSON.get("designs")[0].get("analysis")[0].get("results")[2].analysisType == "STRENGTH"
            locationJSON.get("designs")[0].get("analysis")[0].get("results")[2].component == "Pole"
            locationJSON.get("designs")[0].get("analysis")[0].get("results")[3].analysisType == "DEFLECTION"
            locationJSON.get("designs")[0].get("analysis")[0].get("results")[3].component == "Pole"
            locationJSON.get("designs")[0].get("analysis")[0].get("results")[4].analysisType == "FORCE"
            locationJSON.get("designs")[0].get("analysis")[0].get("results")[4].component == "Insulator#1"
            locationJSON.get("designs")[0].get("analysis")[0].get("results")[5].analysisType == "FORCE"
            locationJSON.get("designs")[0].get("analysis")[0].get("results")[5].component == "SidewalkBrace#1"
            locationJSON.get("designs")[0].get("analysis")[0].get("results")[6].analysisType == "FORCE"
            locationJSON.get("designs")[0].get("analysis")[0].get("results")[6].component == "Anchor#1"
            locationJSON.get("designs")[0].get("analysis")[0].get("results")[7].analysisType == "FORCE"
            locationJSON.get("designs")[0].get("analysis")[0].get("results")[7].component == "Guy#1"
            locationJSON.get("designs")[0].get("analysis")[0].get("results")[8].analysisType == "MOMENT"
            locationJSON.get("designs")[0].get("analysis")[0].get("results")[8].component == "Foundation"
        when: "revertLocation"
            analysisTypeChangeSet.revertLocation(locationJSON)
        then:
            locationJSON.get("designs")[0].get("analysis")[0].get("results")[0].analysisType == null
            locationJSON.get("designs")[0].get("analysis")[0].get("results")[0].component == "Pole"
            locationJSON.get("designs")[0].get("analysis")[0].get("results")[1].analysisType == null
            locationJSON.get("designs")[0].get("analysis")[0].get("results")[1].component == "Pole-Buckling"
            locationJSON.get("designs")[0].get("analysis")[0].get("results")[2].analysisType == null
            locationJSON.get("designs")[0].get("analysis")[0].get("results")[2].component == "Pole-Strength"
            locationJSON.get("designs")[0].get("analysis")[0].get("results")[3].analysisType == null
            locationJSON.get("designs")[0].get("analysis")[0].get("results")[3].analysisType == null
            locationJSON.get("designs")[0].get("analysis")[0].get("results")[3].component == "Pole-Deflection"
            locationJSON.get("designs")[0].get("analysis")[0].get("results")[4].analysisType == null
            locationJSON.get("designs")[0].get("analysis")[0].get("results")[4].component == "Insulator#1"
            locationJSON.get("designs")[0].get("analysis")[0].get("results")[5].analysisType == null
            locationJSON.get("designs")[0].get("analysis")[0].get("results")[5].component == "SidewalkBrace#1"
            locationJSON.get("designs")[0].get("analysis")[0].get("results")[6].analysisType == null
            locationJSON.get("designs")[0].get("analysis")[0].get("results")[6].component == "Anchor#1"
            locationJSON.get("designs")[0].get("analysis")[0].get("results")[7].analysisType == null
            locationJSON.get("designs")[0].get("analysis")[0].get("results")[7].component == "Guy#1"
            locationJSON.get("designs")[0].get("analysis")[0].get("results")[8].analysisType == null
            locationJSON.get("designs")[0].get("analysis")[0].get("results")[8].component == "Foundation"
        when: "applyToDesign"
            analysisTypeChangeSet.applyToDesign(designJSON)
        then:
            designJSON.get("analysis")[0].get("results")[0].analysisType == "STRESS"
            designJSON.get("analysis")[0].get("results")[0].component == "Pole"
            designJSON.get("analysis")[0].get("results")[1].analysisType == "BUCKLING"
            designJSON.get("analysis")[0].get("results")[1].component == "Pole"
            designJSON.get("analysis")[0].get("results")[2].analysisType == "STRENGTH"
            designJSON.get("analysis")[0].get("results")[2].component == "Pole"
            designJSON.get("analysis")[0].get("results")[3].analysisType == "DEFLECTION"
            designJSON.get("analysis")[0].get("results")[3].component == "Pole"
            designJSON.get("analysis")[0].get("results")[4].analysisType == "FORCE"
            designJSON.get("analysis")[0].get("results")[4].component == "Insulator#1"
            designJSON.get("analysis")[0].get("results")[5].analysisType == "FORCE"
            designJSON.get("analysis")[0].get("results")[5].component == "SidewalkBrace#1"
            designJSON.get("analysis")[0].get("results")[6].analysisType == "FORCE"
            designJSON.get("analysis")[0].get("results")[6].component == "Anchor#1"
            designJSON.get("analysis")[0].get("results")[7].analysisType == "FORCE"
            designJSON.get("analysis")[0].get("results")[7].component == "Guy#1"
            designJSON.get("analysis")[0].get("results")[8].analysisType == "MOMENT"
            designJSON.get("analysis")[0].get("results")[8].component == "Foundation"
        when: "revertDesign"
            analysisTypeChangeSet.revertDesign(designJSON)
        then:
            designJSON.get("analysis")[0].get("results")[0].analysisType == null
            designJSON.get("analysis")[0].get("results")[0].component == "Pole"
            designJSON.get("analysis")[0].get("results")[1].analysisType == null
            designJSON.get("analysis")[0].get("results")[1].component == "Pole-Buckling"
            designJSON.get("analysis")[0].get("results")[2].analysisType == null
            designJSON.get("analysis")[0].get("results")[2].component == "Pole-Strength"
            designJSON.get("analysis")[0].get("results")[3].analysisType == null
            designJSON.get("analysis")[0].get("results")[3].analysisType == null
            designJSON.get("analysis")[0].get("results")[3].component == "Pole-Deflection"
            designJSON.get("analysis")[0].get("results")[4].analysisType == null
            designJSON.get("analysis")[0].get("results")[4].component == "Insulator#1"
            designJSON.get("analysis")[0].get("results")[5].analysisType == null
            designJSON.get("analysis")[0].get("results")[5].component == "SidewalkBrace#1"
            designJSON.get("analysis")[0].get("results")[6].analysisType == null
            designJSON.get("analysis")[0].get("results")[6].component == "Anchor#1"
            designJSON.get("analysis")[0].get("results")[7].analysisType == null
            designJSON.get("analysis")[0].get("results")[7].component == "Guy#1"
            designJSON.get("analysis")[0].get("results")[8].analysisType == null
            designJSON.get("analysis")[0].get("results")[8].component == "Foundation"
    }


}
