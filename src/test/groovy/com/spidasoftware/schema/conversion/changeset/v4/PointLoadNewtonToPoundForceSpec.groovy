package com.spidasoftware.schema.conversion.changeset.v4

import groovy.util.logging.Log4j
import net.sf.json.JSONObject
import net.sf.json.groovy.JsonSlurper
import spock.lang.Specification

@Log4j
class PointLoadNewtonToPoundForceSpec extends Specification {

    def "test revert"() {
        setup:
            def leanStream = PointLoadElevationAndRotationChangeSetSpec.getResourceAsStream("/conversions/v4/project-v4.json")
            JSONObject projectJSON = new JsonSlurper().parse(leanStream)
            JSONObject locationJSON = JSONObject.fromObject(projectJSON.leads[0].locations[0])
            JSONObject designJSON = JSONObject.fromObject(projectJSON.leads[0].locations[0].designs[0])

            PointLoadNewtonToPoundForceChangeset pointLoadNewtonToPoundForceChangeset = new PointLoadNewtonToPoundForceChangeset()
        when:
            pointLoadNewtonToPoundForceChangeset.revertProject(projectJSON)
            JSONObject structure = projectJSON.get("leads")[0].get("locations")[0].get("designs")[0].get("structure")
            JSONObject pointLoad = structure.get("pointLoads").find { it.id == "PointLoad#1" }
        then:
            Math.abs(pointLoad.getJSONObject("fx").getDouble("value") - 200.00000068613937D) < 0.000001
            pointLoad.getJSONObject("fx").get("unit") == 'POUND_FORCE'
            Math.abs(pointLoad.getJSONObject("fy").getDouble("value") - 200.00000068613937D) < 0.000001
            pointLoad.getJSONObject("fy").get("unit") == 'POUND_FORCE'
            Math.abs(pointLoad.getJSONObject("fz").getDouble("value") - (-29.05417390379112D)) < 0.000001
            pointLoad.getJSONObject("fz").get("unit") == 'POUND_FORCE'
            Math.abs(pointLoad.getJSONObject("mx").getDouble("value") - 75.45625066010481D) < 0.000001
            pointLoad.getJSONObject("mx").get("unit") == 'POUND_FORCE_FOOT'
            Math.abs(pointLoad.getJSONObject("my").getDouble("value") - (-22.418562485248607D)) < 0.000001
            pointLoad.getJSONObject("my").get("unit") == 'POUND_FORCE_FOOT'
            Math.abs(pointLoad.getJSONObject("mz").getDouble("value") - 0) < 0.000001
            pointLoad.getJSONObject("mz").get("unit") == 'POUND_FORCE_FOOT'
        when: "revertLocation"
            pointLoadNewtonToPoundForceChangeset.revertLocation(locationJSON)
            structure = locationJSON.get("designs")[0].get("structure")
            pointLoad = structure.get("pointLoads").find { it.id == "PointLoad#1" }
        then:
            Math.abs(pointLoad.getJSONObject("fx").getDouble("value") - 200.00000068613937D) < 0.000001
            pointLoad.getJSONObject("fx").get("unit") == 'POUND_FORCE'
            Math.abs(pointLoad.getJSONObject("fy").getDouble("value") - 200.00000068613937D) < 0.000001
            pointLoad.getJSONObject("fy").get("unit") == 'POUND_FORCE'
            Math.abs(pointLoad.getJSONObject("fz").getDouble("value") - (-29.05417390379112D)) < 0.000001
            pointLoad.getJSONObject("fz").get("unit") == 'POUND_FORCE'
            Math.abs(pointLoad.getJSONObject("mx").getDouble("value") - 75.45625066010481D) < 0.000001
            pointLoad.getJSONObject("mx").get("unit") == 'POUND_FORCE_FOOT'
            Math.abs(pointLoad.getJSONObject("my").getDouble("value") - (-22.418562485248607D)) < 0.000001
            pointLoad.getJSONObject("my").get("unit") == 'POUND_FORCE_FOOT'
            Math.abs(pointLoad.getJSONObject("mz").getDouble("value") - 0) < 0.000001
            pointLoad.getJSONObject("mz").get("unit") == 'POUND_FORCE_FOOT'
        when: "revertDesign"
            pointLoadNewtonToPoundForceChangeset.revertDesign(designJSON)
            structure = designJSON.get("structure")
            pointLoad = structure.get("pointLoads").find { it.id == "PointLoad#1" }
        then:
            Math.abs(pointLoad.getJSONObject("fx").getDouble("value") - 200.00000068613937D) < 0.000001
            pointLoad.getJSONObject("fx").get("unit") == 'POUND_FORCE'
            Math.abs(pointLoad.getJSONObject("fy").getDouble("value") - 200.00000068613937D) < 0.000001
            pointLoad.getJSONObject("fy").get("unit") == 'POUND_FORCE'
            Math.abs(pointLoad.getJSONObject("fz").getDouble("value") - (-29.05417390379112D)) < 0.000001
            pointLoad.getJSONObject("fz").get("unit") == 'POUND_FORCE'
            Math.abs(pointLoad.getJSONObject("mx").getDouble("value") - 75.45625066010481D) < 0.000001
            pointLoad.getJSONObject("mx").get("unit") == 'POUND_FORCE_FOOT'
            Math.abs(pointLoad.getJSONObject("my").getDouble("value") - (-22.418562485248607D)) < 0.000001
            pointLoad.getJSONObject("my").get("unit") == 'POUND_FORCE_FOOT'
            Math.abs(pointLoad.getJSONObject("mz").getDouble("value") - 0) < 0.000001
            pointLoad.getJSONObject("mz").get("unit") == 'POUND_FORCE_FOOT'
    }
}
