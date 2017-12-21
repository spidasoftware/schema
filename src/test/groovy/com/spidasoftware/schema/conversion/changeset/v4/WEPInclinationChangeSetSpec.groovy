package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.calc.CalcProjectChangeSet
import groovy.json.JsonSlurper
import spock.lang.Specification

class WEPInclinationChangeSetSpec extends Specification {

    def "test revert"() {
        setup:
            def leanStream = WEPInclinationChangeSetSpec.getResourceAsStream("/conversions/v4/project-v4.json")
            Map projectJSON = new JsonSlurper().parse(leanStream)
            Map locationJSON = CalcProjectChangeSet.duplicateAsJson(projectJSON.leads[0].locations[0])
            Map designJSON = CalcProjectChangeSet.duplicateAsJson(projectJSON.leads[0].locations[0].designs[0])

            WEPInclinationChangeSet wepInclinationChangeSet = new WEPInclinationChangeSet()
        when: "revertProject"
            wepInclinationChangeSet.revertProject(projectJSON)
            Map structure = projectJSON.get("leads")[0].get("locations")[0].get("designs")[0].get("structure")
        then:
            Math.abs(structure.get("wireEndPoints")[0].get("inclination").get("value") - 60.0D) < 0.0001
            structure.get("wireEndPoints")[0].get("inclination").get("unit") == 'DEGREE_ANGLE'
            structure.get("wireEndPoints")[1].get("inclination").get("value") == 0.0D
            structure.get("wireEndPoints")[1].get("inclination").get("unit") == 'DEGREE_ANGLE'
        when: "revertLocation"
            wepInclinationChangeSet.revertLocation(locationJSON)
            structure = locationJSON.get("designs")[0].get("structure")
        then:
            Math.abs(structure.get("wireEndPoints")[0].get("inclination").get("value") - 60.0D) < 0.0001
            structure.get("wireEndPoints")[0].get("inclination").get("unit") == 'DEGREE_ANGLE'
            structure.get("wireEndPoints")[1].get("inclination").get("value") == 0.0D
            structure.get("wireEndPoints")[1].get("inclination").get("unit") == 'DEGREE_ANGLE'
        when: "revertDesign"
            wepInclinationChangeSet.revertDesign(designJSON)
            structure = designJSON.get("structure")
        then:
            Math.abs(structure.get("wireEndPoints")[0].get("inclination").get("value") - 60.0D) < 0.0001
            structure.get("wireEndPoints")[0].get("inclination").get("unit") == 'DEGREE_ANGLE'
            structure.get("wireEndPoints")[1].get("inclination").get("value") == 0.0D
            structure.get("wireEndPoints")[1].get("inclination").get("unit") == 'DEGREE_ANGLE'

    }
}
