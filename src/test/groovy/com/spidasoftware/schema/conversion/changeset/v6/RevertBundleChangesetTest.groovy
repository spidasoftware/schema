/*
 * ©2009-2018 SPIDAWEB LLC
 */
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

        def wire = projectJSON.leads[0].locations[0].designs[0].structure.wires[0]
        def originalSize = wire.size
        def originalTensionGroup = wire.tensionGroup

        when:
            def changeset = new RevertBundleChangeset()
            changeset.revertProject(projectJSON)
            def wires = projectJSON.leads[0].locations[0].designs[0].structure.wires
        then:
            wires.every {
                it.usageGroup == "COMMUNICATION" &&
                        it.size == originalSize &&
                        it.tensionGroup == originalTensionGroup &&
                        it.clientItem.coreStrands == 1 &&
                        it.clientItem.conductorStrands == 0 &&
                        it.clientItemVersion == null &&
                        it.clientItem.bundleComponents == null &&
                        it.clientItem.messenger == null &&
                        it.clientItem.autoCalculateDiameter == null &&
                        it.clientItem.aliases == null &&
                        it.clientItem.diameter == null &&
                        it.clientItem.source == null
            }
            new Validator().validateAndReport("/schema/spidacalc/calc/project.schema", projectJSON).isSuccess()
    }
}
