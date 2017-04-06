package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.ChangeSet
import groovy.json.JsonSlurper
import groovy.util.logging.Log4j
import spock.lang.Specification

@Log4j
class PointLoadNewtonToPoundForceSpec extends Specification {

    def "test revert"() {
        setup:
            def leanStream = PointLoadElevationAndRotationChangeSetSpec.getResourceAsStream("/conversions/v4/project-v4.json")
            Map projectJSON = new JsonSlurper().parse(leanStream)
            Map locationJSON = ChangeSet.duplicateAsJson(projectJSON.leads[0].locations[0])
            Map designJSON = ChangeSet.duplicateAsJson(projectJSON.leads[0].locations[0].designs[0])

            PointLoadNewtonToPoundForceChangeset pointLoadNewtonToPoundForceChangeset = new PointLoadNewtonToPoundForceChangeset()
        when:
            pointLoadNewtonToPoundForceChangeset.revertProject(projectJSON)
            Map structure = projectJSON.get("leads")[0].get("locations")[0].get("designs")[0].get("structure")
            Map pointLoad = structure.get("pointLoads").find { it.id == "PointLoad#1" }
        then:
            Math.abs(pointLoad.get("fx").get("value") - 200.00000068613937D) < 0.000001
            pointLoad.get("fx").get("unit") == 'POUND_FORCE'
            Math.abs(pointLoad.get("fy").get("value") - 200.00000068613937D) < 0.000001
            pointLoad.get("fy").get("unit") == 'POUND_FORCE'
            Math.abs(pointLoad.get("fz").get("value") - (-29.05417390379112D)) < 0.000001
            pointLoad.get("fz").get("unit") == 'POUND_FORCE'
            Math.abs(pointLoad.get("mx").get("value") - 75.45625066010481D) < 0.000001
            pointLoad.get("mx").get("unit") == 'POUND_FORCE_FOOT'
            Math.abs(pointLoad.get("my").get("value") - (-22.418562485248607D)) < 0.000001
            pointLoad.get("my").get("unit") == 'POUND_FORCE_FOOT'
            Math.abs(pointLoad.get("mz").get("value") - 0) < 0.000001
            pointLoad.get("mz").get("unit") == 'POUND_FORCE_FOOT'
        when: "revertLocation"
            pointLoadNewtonToPoundForceChangeset.revertLocation(locationJSON)
            structure = locationJSON.get("designs")[0].get("structure")
            pointLoad = structure.get("pointLoads").find { it.id == "PointLoad#1" }
        then:
            Math.abs(pointLoad.get("fx").get("value") - 200.00000068613937D) < 0.000001
            pointLoad.get("fx").get("unit") == 'POUND_FORCE'
            Math.abs(pointLoad.get("fy").get("value") - 200.00000068613937D) < 0.000001
            pointLoad.get("fy").get("unit") == 'POUND_FORCE'
            Math.abs(pointLoad.get("fz").get("value") - (-29.05417390379112D)) < 0.000001
            pointLoad.get("fz").get("unit") == 'POUND_FORCE'
            Math.abs(pointLoad.get("mx").get("value") - 75.45625066010481D) < 0.000001
            pointLoad.get("mx").get("unit") == 'POUND_FORCE_FOOT'
            Math.abs(pointLoad.get("my").get("value") - (-22.418562485248607D)) < 0.000001
            pointLoad.get("my").get("unit") == 'POUND_FORCE_FOOT'
            Math.abs(pointLoad.get("mz").get("value") - 0) < 0.000001
            pointLoad.get("mz").get("unit") == 'POUND_FORCE_FOOT'
        when: "revertDesign"
            pointLoadNewtonToPoundForceChangeset.revertDesign(designJSON)
            structure = designJSON.get("structure")
            pointLoad = structure.get("pointLoads").find { it.id == "PointLoad#1" }
        then:
            Math.abs(pointLoad.get("fx").get("value") - 200.00000068613937D) < 0.000001
            pointLoad.get("fx").get("unit") == 'POUND_FORCE'
            Math.abs(pointLoad.get("fy").get("value") - 200.00000068613937D) < 0.000001
            pointLoad.get("fy").get("unit") == 'POUND_FORCE'
            Math.abs(pointLoad.get("fz").get("value") - (-29.05417390379112D)) < 0.000001
            pointLoad.get("fz").get("unit") == 'POUND_FORCE'
            Math.abs(pointLoad.get("mx").get("value") - 75.45625066010481D) < 0.000001
            pointLoad.get("mx").get("unit") == 'POUND_FORCE_FOOT'
            Math.abs(pointLoad.get("my").get("value") - (-22.418562485248607D)) < 0.000001
            pointLoad.get("my").get("unit") == 'POUND_FORCE_FOOT'
            Math.abs(pointLoad.get("mz").get("value") - 0) < 0.000001
            pointLoad.get("mz").get("unit") == 'POUND_FORCE_FOOT'
    }
}
