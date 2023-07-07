package com.spidasoftware.schema.conversion.changeset.v10

import groovy.json.JsonSlurper
import spock.lang.Specification

class TerrainLayerChangeSetTest extends Specification {

    def "revert project"() {
        setup:
            def changeSet = new TerrainLayerChangeSet()
            def stream = TerrainLayerChangeSetTest.getResourceAsStream("/conversions/v10/terrainLayer.v10.project.json")
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
        expect:
            json.leads[0].locations[0].designs[1].terrainLayer
        when:
            changeSet.revertProject(json)
        then:
            !json.leads[0].locations[0].designs[1].terrainLayer
    }

}

