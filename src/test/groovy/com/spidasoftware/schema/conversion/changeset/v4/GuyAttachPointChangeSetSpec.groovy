package com.spidasoftware.schema.conversion.changeset.v4

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import spock.lang.Specification

@Slf4j
class GuyAttachPointChangeSetSpec extends Specification {

    def "test revert"() {
        setup:
            def leanStream = GuyAttachPointChangeSetSpec.getResourceAsStream("/conversions/v4/project-v4.json")
            def projectJSON = new JsonSlurper().parse(leanStream)
            def designJSON = new JsonSlurper().parseText(JsonOutput.toJson(projectJSON.leads[0].locations[0].designs[0]))

            GuyAttachPointChangeSet changeSet = new GuyAttachPointChangeSet()
        when: "revertDesign"
            changeSet.revertDesign(designJSON)
            def structure = designJSON.get("structure")
        then:
            structure.get("guyAttachPoints") == null
    }
}
