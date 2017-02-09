package com.spidasoftware.schema.conversion.changeset.v4

import net.sf.json.JSONObject
import net.sf.json.groovy.JsonSlurper
import spock.lang.Specification

class PoleTemperatureChangesetSpec extends Specification {

    def "test revert"() {
        setup:
            def leanStream = PoleTemperatureChangesetSpec.getResourceAsStream("/conversions/v4/project-v4.json")
            JSONObject projectJSON = new JsonSlurper().parse(leanStream)
            JSONObject locationJSON = JSONObject.fromObject(projectJSON.leads[0].locations[0])
            JSONObject designJSON = JSONObject.fromObject(projectJSON.leads[0].locations[0].designs[0])

            PoleTemperatureChangeset poleTemperatureChangeset = new PoleTemperatureChangeset()
        when: "revertProject"
            poleTemperatureChangeset.revertProject(projectJSON)
            JSONObject pole = projectJSON.get("leads")[0].get("locations")[0].get("designs")[0].get("structure").get("pole")
        then:
            Math.abs(pole.get("temperature").getDouble("value") - 60.0D) < 0.0001
            pole.get("temperature").get("unit") == "FAHRENHEIT"
        when: "revertLocation"
            poleTemperatureChangeset.revertLocation(locationJSON)
            pole = locationJSON.get("designs")[0].get("structure").get("pole")
        then:
            Math.abs(pole.get("temperature").getDouble("value") - 60.0D) < 0.0001
            pole.get("temperature").get("unit") == "FAHRENHEIT"
        when: "revertDesign"
            poleTemperatureChangeset.revertDesign(designJSON)
            pole = designJSON.get("structure").get("pole")
        then:
            Math.abs(pole.get("temperature").getDouble("value") - 60.0D) < 0.0001
            pole.get("temperature").get("unit") == "FAHRENHEIT"
    }
}
