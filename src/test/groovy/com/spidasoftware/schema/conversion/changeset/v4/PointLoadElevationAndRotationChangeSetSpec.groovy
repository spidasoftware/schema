package com.spidasoftware.schema.conversion.changeset.v4

import net.sf.json.JSONObject
import net.sf.json.groovy.JsonSlurper
import spock.lang.Specification

class PointLoadElevationAndRotationChangeSetSpec extends Specification {

    def "test revert"() {
        setup:
            def leanStream = PointLoadElevationAndRotationChangeSetSpec.getResourceAsStream("/conversions/v4/project-v4.json")
            JSONObject projectJSON = new JsonSlurper().parse(leanStream)
            JSONObject locationJSON = JSONObject.fromObject(projectJSON.leads[0].locations[0])
            JSONObject designJSON = JSONObject.fromObject(projectJSON.leads[0].locations[0].designs[0])

            PointLoadElevationAndRotationChangeSet pointLoadElevationAndRotationChangeSet = new PointLoadElevationAndRotationChangeSet()
        when: "revertProject"
            pointLoadElevationAndRotationChangeSet.revertProject(projectJSON)
            JSONObject structure = projectJSON.get("leads")[0].get("locations")[0].get("designs")[0].get("structure")
        then:
            Math.abs(structure.get("pointLoads")[0].get("elevation").getDouble("value") - 30.0D) < 0.0001
            structure.get("pointLoads")[0].get("elevation").get("unit") == 'DEGREE_ANGLE'
            Math.abs(structure.get("pointLoads")[0].get("rotation").getDouble("value") - 45.0D) < 0.0001
            structure.get("pointLoads")[0].get("rotation").get("unit") == 'DEGREE_ANGLE'
            structure.get("pointLoads")[1].get("elevation").getDouble("value") == 0.0D
            structure.get("pointLoads")[1].get("elevation").get("unit") == 'DEGREE_ANGLE'
            structure.get("pointLoads")[1].get("rotation").getDouble("value") == 0.0D
            structure.get("pointLoads")[1].get("rotation").get("unit") == 'DEGREE_ANGLE'
        when: "revertLocation"
            pointLoadElevationAndRotationChangeSet.revertLocation(locationJSON)
            structure = locationJSON.get("designs")[0].get("structure")
        then:
            Math.abs(structure.get("pointLoads")[0].get("elevation").getDouble("value") - 30.0D) < 0.0001
            structure.get("pointLoads")[0].get("elevation").get("unit") == 'DEGREE_ANGLE'
            Math.abs(structure.get("pointLoads")[0].get("rotation").getDouble("value") - 45.0D) < 0.0001
            structure.get("pointLoads")[0].get("rotation").get("unit") == 'DEGREE_ANGLE'
            structure.get("pointLoads")[1].get("elevation").getDouble("value") == 0.0D
            structure.get("pointLoads")[1].get("elevation").get("unit") == 'DEGREE_ANGLE'
            structure.get("pointLoads")[1].get("rotation").getDouble("value") == 0.0D
            structure.get("pointLoads")[1].get("rotation").get("unit") == 'DEGREE_ANGLE'
        when: "revertDesign"
            pointLoadElevationAndRotationChangeSet.revertDesign(designJSON)
            structure = designJSON.get("structure")
        then:
            Math.abs(structure.get("pointLoads")[0].get("elevation").getDouble("value") - 30.0D) < 0.0001
            structure.get("pointLoads")[0].get("elevation").get("unit") == 'DEGREE_ANGLE'
            Math.abs(structure.get("pointLoads")[0].get("rotation").getDouble("value") - 45.0D) < 0.0001
            structure.get("pointLoads")[0].get("rotation").get("unit") == 'DEGREE_ANGLE'
            structure.get("pointLoads")[1].get("elevation").getDouble("value") == 0.0D
            structure.get("pointLoads")[1].get("elevation").get("unit") == 'DEGREE_ANGLE'
            structure.get("pointLoads")[1].get("rotation").getDouble("value") == 0.0D
            structure.get("pointLoads")[1].get("rotation").get("unit") == 'DEGREE_ANGLE'
    }
}
