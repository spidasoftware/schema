package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.ChangeSet
import groovy.json.JsonSlurper
import groovy.util.logging.Log4j
import spock.lang.Specification

@Log4j
class PointLoadItemChangeSetSpec extends Specification {

    def "test revert"() {
        setup:
            def leanStream = PointLoadItemChangeSetSpec.getResourceAsStream("/conversions/v4/project-v4.json")
            JSONObject projectJSON = new JsonSlurper().parse(leanStream)
            JSONObject locationJSON = JSONObject.fromObject(projectJSON.leads[0].locations[0])
            JSONObject designJSON = JSONObject.fromObject(projectJSON.leads[0].locations[0].designs[0])

            PointLoadItemChangeSet changeSet = new PointLoadItemChangeSet()
        when:
            changeSet.revertProject(projectJSON)
            JSONObject structure = projectJSON.get("leads")[0].get("locations")[0].get("designs")[0].get("structure")
            JSONObject pointLoad = structure.get("pointLoads").find { it.id == "PointLoad#1" }
        then:
            Math.abs(pointLoad.getJSONObject("fx").getDouble("value") - 200.00000068613937D) < 0.000001
            pointLoad.getJSONObject("fx").get("unit") == 'POUND_FORCE'
            Math.abs(pointLoad.getJSONObject("fy").getDouble("value") - 200.00000068613937D) < 0.000001
            pointLoad.getJSONObject("fy").get("unit") == 'POUND_FORCE'
            Math.abs(pointLoad.getJSONObject("fz").getDouble("value") - (-29.05417390379112D)) < 0.000001
            pointLoad.getJSONObject("fz").get("unit") == 'POUND_FORCE'
            pointLoad.getJSONObject("mx").get("unit") == 'POUND_FORCE_FOOT'
            pointLoad.getJSONObject("my").get("unit") == 'POUND_FORCE_FOOT'
            pointLoad.getJSONObject("mz").get("unit") == 'POUND_FORCE_FOOT'
            structure.get("pointLoads")[0].get("elevation").get("unit") == 'DEGREE_ANGLE'
            structure.get("pointLoads")[0].get("rotation").get("unit") == 'DEGREE_ANGLE'
            structure.get("pointLoads")[1].get("elevation").getDouble("value") == 0.0D
            structure.get("pointLoads")[1].get("elevation").get("unit") == 'DEGREE_ANGLE'
            structure.get("pointLoads")[1].get("rotation").getDouble("value") == 0.0D
            structure.get("pointLoads")[1].get("rotation").get("unit") == 'DEGREE_ANGLE'
        when: "revertLocation"
            changeSet.revertLocation(locationJSON)
            structure = locationJSON.get("designs")[0].get("structure")
            pointLoad = structure.get("pointLoads").find { it.id == "PointLoad#1" }
        then:
            Math.abs(pointLoad.getJSONObject("fx").getDouble("value") - 200.00000068613937D) < 0.000001
            pointLoad.getJSONObject("fx").get("unit") == 'POUND_FORCE'
            Math.abs(pointLoad.getJSONObject("fy").getDouble("value") - 200.00000068613937D) < 0.000001
            pointLoad.getJSONObject("fy").get("unit") == 'POUND_FORCE'
            Math.abs(pointLoad.getJSONObject("fz").getDouble("value") - (-29.05417390379112D)) < 0.000001
            pointLoad.getJSONObject("fz").get("unit") == 'POUND_FORCE'
            pointLoad.getJSONObject("mx").get("unit") == 'POUND_FORCE_FOOT'
            pointLoad.getJSONObject("my").get("unit") == 'POUND_FORCE_FOOT'
            pointLoad.getJSONObject("mz").get("unit") == 'POUND_FORCE_FOOT'
            structure.get("pointLoads")[0].get("elevation").get("unit") == 'DEGREE_ANGLE'
            structure.get("pointLoads")[0].get("rotation").get("unit") == 'DEGREE_ANGLE'
            structure.get("pointLoads")[1].get("elevation").getDouble("value") == 0.0D
            structure.get("pointLoads")[1].get("elevation").get("unit") == 'DEGREE_ANGLE'
            structure.get("pointLoads")[1].get("rotation").getDouble("value") == 0.0D
            structure.get("pointLoads")[1].get("rotation").get("unit") == 'DEGREE_ANGLE'
        when: "revertDesign"
            changeSet.revertDesign(designJSON)
            structure = designJSON.get("structure")
            pointLoad = structure.get("pointLoads").find { it.id == "PointLoad#1" }
        then:
            Math.abs(pointLoad.getJSONObject("fx").getDouble("value") - 200.00000068613937D) < 0.000001
            pointLoad.getJSONObject("fx").get("unit") == 'POUND_FORCE'
            Math.abs(pointLoad.getJSONObject("fy").getDouble("value") - 200.00000068613937D) < 0.000001
            pointLoad.getJSONObject("fy").get("unit") == 'POUND_FORCE'
            Math.abs(pointLoad.getJSONObject("fz").getDouble("value") - (-29.05417390379112D)) < 0.000001
            pointLoad.getJSONObject("fz").get("unit") == 'POUND_FORCE'
            pointLoad.getJSONObject("mx").get("unit") == 'POUND_FORCE_FOOT'
            pointLoad.getJSONObject("my").get("unit") == 'POUND_FORCE_FOOT'
            pointLoad.getJSONObject("mz").get("unit") == 'POUND_FORCE_FOOT'
            structure.get("pointLoads")[0].get("elevation").get("unit") == 'DEGREE_ANGLE'
            structure.get("pointLoads")[0].get("rotation").get("unit") == 'DEGREE_ANGLE'
            structure.get("pointLoads")[1].get("elevation").getDouble("value") == 0.0D
            structure.get("pointLoads")[1].get("elevation").get("unit") == 'DEGREE_ANGLE'
            structure.get("pointLoads")[1].get("rotation").getDouble("value") == 0.0D
            structure.get("pointLoads")[1].get("rotation").get("unit") == 'DEGREE_ANGLE'
    }

    def "test apply"() {
        def leanStream = PointLoadItemChangeSetSpec.getResourceAsStream("/conversions/v4/designWithOldPointLoadItems.json")
        JSONObject designJSON = new JsonSlurper().parse(leanStream) as JSONObject
        def changeSet = new PointLoadItemChangeSet()

        when:
            def oldPointLoad = JSONObject.fromObject(designJSON.structure.pointLoads.first())
            changeSet.applyToDesign(designJSON)
            def pointLoad = designJSON.structure.pointLoads.first()
        then:
            !["elevation", "rotation", "x", "y", "z", "fx", "fy", "fz", "mx", "my", "mz"].any { pointLoad.containsKey(it) }
            pointLoad.attachHeight == oldPointLoad.attachmentHeight
            pointLoad.XForce == oldPointLoad.fx
            pointLoad.YForce == oldPointLoad.fy
            pointLoad.ZForce == oldPointLoad.fz
    }
}
