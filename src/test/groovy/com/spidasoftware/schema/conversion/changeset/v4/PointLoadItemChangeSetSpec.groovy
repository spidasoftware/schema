package com.spidasoftware.schema.conversion.changeset.v4

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import spock.lang.Specification

@Slf4j
class PointLoadItemChangeSetSpec extends Specification {

    def "test revert"() {
        setup:
            def leanStream = PointLoadItemChangeSetSpec.getResourceAsStream("/conversions/v4/project-v4.json")
            def projectJSON = new JsonSlurper().parse(leanStream)
            def locationJSON = new JsonSlurper().parseText(JsonOutput.toJson(projectJSON.leads[0].locations[0]))
            def designJSON = new JsonSlurper().parseText(JsonOutput.toJson(projectJSON.leads[0].locations[0].designs[0]))

            PointLoadItemChangeSet changeSet = new PointLoadItemChangeSet()
        when:
            changeSet.revertProject(projectJSON)
            def structure = projectJSON.get("leads")[0].get("locations")[0].get("designs")[0].get("structure")
            def pointLoad = structure.get("pointLoads").find { it.id == "PointLoad#1" }
        then:
            Math.abs(pointLoad.get("fx").get("value") - 200.00000068613937D) < 0.000001
            pointLoad.get("fx").get("unit") == 'POUND_FORCE'
            Math.abs(pointLoad.get("fy").get("value") - 200.00000068613937D) < 0.000001
            pointLoad.get("fy").get("unit") == 'POUND_FORCE'
            Math.abs(pointLoad.get("fz").get("value") - (-29.05417390379112D)) < 0.000001
            pointLoad.get("fz").get("unit") == 'POUND_FORCE'
            pointLoad.get("mx").get("unit") == 'POUND_FORCE_FOOT'
            pointLoad.get("my").get("unit") == 'POUND_FORCE_FOOT'
            pointLoad.get("mz").get("unit") == 'POUND_FORCE_FOOT'
            pointLoad.z == pointLoad.attachmentHeight
            structure.get("pointLoads")[0].get("elevation").get("unit") == 'DEGREE_ANGLE'
            structure.get("pointLoads")[0].get("rotation").get("unit") == 'DEGREE_ANGLE'
            structure.get("pointLoads")[1].get("elevation").get("value") == 0
            structure.get("pointLoads")[1].get("elevation").get("unit") == 'DEGREE_ANGLE'
            structure.get("pointLoads")[1].get("rotation").get("value") == 0
            structure.get("pointLoads")[1].get("rotation").get("unit") == 'DEGREE_ANGLE'
        when: "revertLocation"
            changeSet.revertLocation(locationJSON)
            structure = locationJSON.get("designs")[0].get("structure")
            pointLoad = structure.get("pointLoads").find { it.id == "PointLoad#1" }
        then:
            Math.abs(pointLoad.get("fx").get("value") - 200.00000068613937D) < 0.000001
            pointLoad.get("fx").get("unit") == 'POUND_FORCE'
            Math.abs(pointLoad.get("fy").get("value") - 200.00000068613937D) < 0.000001
            pointLoad.get("fy").get("unit") == 'POUND_FORCE'
            Math.abs(pointLoad.get("fz").get("value") - (-29.05417390379112D)) < 0.000001
            pointLoad.z == pointLoad.attachmentHeight
            pointLoad.get("fz").get("unit") == 'POUND_FORCE'
            pointLoad.get("mx").get("unit") == 'POUND_FORCE_FOOT'
            pointLoad.get("my").get("unit") == 'POUND_FORCE_FOOT'
            pointLoad.get("mz").get("unit") == 'POUND_FORCE_FOOT'
            structure.get("pointLoads")[0].get("elevation").get("unit") == 'DEGREE_ANGLE'
            structure.get("pointLoads")[0].get("rotation").get("unit") == 'DEGREE_ANGLE'
            structure.get("pointLoads")[1].get("elevation").get("value") == 0
            structure.get("pointLoads")[1].get("elevation").get("unit") == 'DEGREE_ANGLE'
            structure.get("pointLoads")[1].get("rotation").get("value") == 0
            structure.get("pointLoads")[1].get("rotation").get("unit") == 'DEGREE_ANGLE'
        when: "revertDesign"
            changeSet.revertDesign(designJSON)
            structure = designJSON.get("structure")
            pointLoad = structure.get("pointLoads").find { it.id == "PointLoad#1" }
        then:
            Math.abs(pointLoad.get("fx").get("value") - 200.00000068613937D) < 0.000001
            pointLoad.get("fx").get("unit") == 'POUND_FORCE'
            Math.abs(pointLoad.get("fy").get("value") - 200.00000068613937D) < 0.000001
            pointLoad.get("fy").get("unit") == 'POUND_FORCE'
            Math.abs(pointLoad.get("fz").get("value") - (-29.05417390379112D)) < 0.000001
            pointLoad.z == pointLoad.attachmentHeight
            pointLoad.get("fz").get("unit") == 'POUND_FORCE'
            pointLoad.get("mx").get("unit") == 'POUND_FORCE_FOOT'
            pointLoad.get("my").get("unit") == 'POUND_FORCE_FOOT'
            pointLoad.get("mz").get("unit") == 'POUND_FORCE_FOOT'
            structure.get("pointLoads")[0].get("elevation").get("unit") == 'DEGREE_ANGLE'
            structure.get("pointLoads")[0].get("rotation").get("unit") == 'DEGREE_ANGLE'
            structure.get("pointLoads")[1].get("elevation").get("value") == 0.0D
            structure.get("pointLoads")[1].get("elevation").get("unit") == 'DEGREE_ANGLE'
            structure.get("pointLoads")[1].get("rotation").get("value") == 0.0D
            structure.get("pointLoads")[1].get("rotation").get("unit") == 'DEGREE_ANGLE'
    }

    def "test apply"() {
        def leanStream = PointLoadItemChangeSetSpec.getResourceAsStream("/conversions/v4/designWithOldPointLoadItems.json")
        def designJSON = new JsonSlurper().parse(leanStream)
        def changeSet = new PointLoadItemChangeSet()

        when:
            def oldPointLoad = new JsonSlurper().parseText(JsonOutput.toJson(designJSON.structure.pointLoads.first()))
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
