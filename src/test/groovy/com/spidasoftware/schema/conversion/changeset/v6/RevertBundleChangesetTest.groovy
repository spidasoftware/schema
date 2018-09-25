package com.spidasoftware.schema.conversion.changeset.v6


import com.spidasoftware.schema.validation.Validator
import groovy.json.JsonSlurper
import groovy.util.logging.Log4j
import spock.lang.Specification

@Log4j
class RevertBundleChangesetTest extends Specification {

    void "test revert file with bundles"() {
        def leanStream = RevertBundleChangesetTest.getResourceAsStream("/conversions/v6/project-bundles.json".toString())
        Map projectJSON = new JsonSlurper().parse(leanStream)

        def wire1 = projectJSON.leads[0].locations[0].designs[0].structure.wires[0]
        def wire2 = projectJSON.leads[0].locations[0].designs[0].structure.wires[1]
        def originalTensionGroup = wire1.tensionGroup
        def originalSize = wire1.size

        when:
            def changeset = new RevertBundleChangeset()
            changeset.revertProject(projectJSON)
            def newWire1 = projectJSON.leads[0].locations[0].designs[0].structure.wires[0]
            def newWire2 = projectJSON.leads[0].locations[0].designs[0].structure.wires[1]
        then:
            newWire1.usageGroup == "COMMUNICATION"
            newWire2.usageGroup == "COMMUNICATION"
            newWire1.clientItem.coreStrands == 1
            newWire2.clientItem.coreStrands == 1
            newWire1.clientItem.conductorStrands == 0
            newWire2.clientItem.conductorStrands == 0
            newWire1.tensionGroup == originalTensionGroup
            newWire1.size == originalSize

            [newWire1, newWire2].each {
                it.clientItem.bundleComponents == null
                it.clientItem.messenger == null
                it.clientItem.autoCalculateDiameter == null
                it.clientItem.aliases == null
                it.clientItem.diameter == null
                it.clientItem.source == null
            }
            new Validator().validateAndReport("/schema/spidacalc/calc/project.schema", projectJSON).isSuccess()
    }
}
