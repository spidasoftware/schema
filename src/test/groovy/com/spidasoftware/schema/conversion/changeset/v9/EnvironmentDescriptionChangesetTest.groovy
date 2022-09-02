/*
 * ©2009-2022 SPIDAWEB LLC
 */

package com.spidasoftware.schema.conversion.changeset.v9

import spock.lang.Specification
import groovy.json.JsonSlurper

class EnvironmentDescriptionChangesetTest extends Specification {


    def "revert client data"() {
        setup:
            def changeSet = new EnvironmentDescriptionChangeset()
            def stream = EnvironmentDescriptionChangeset.getResourceAsStream("/conversions/v9/descriptionlessEnvironment.v9.client.json".toString())
            Map json = new JsonSlurper().parse(stream)
            stream.close()
        when:
            boolean reverted = changeSet.revertClientData(json)
        then:
            json.environments.size() == 6
            json.environments[0].name == "Street"
            json.environments[0].description == "N/A"
            json.environments[1].name == "Parking Lot"
            json.environments[1].description == "Parking Lot"
            json.environments[2].name == "Wet Foundation"
            json.environments[2].description == "Foundation is damp"
            json.environments[3].name == "Pedestrian"
            json.environments[3].description == "N/A"
            json.environments[4].name == "Farm"
            json.environments[4].description == "test"
            json.environments[5].name == "Test"
            json.environments[5].description == "I am writing a really long description for a test. This note does not make any sense."
            reverted
    }

    def "revert project"() {
        setup:
            def changeSet = new EnvironmentDescriptionChangeset()
            def stream = EnvironmentDescriptionChangeset.getResourceAsStream("/conversions/v9/descriptionlessEnvironment.v9.project.json".toString())
            Map json = new JsonSlurper().parse(stream)
            stream.close()
        when:
            changeSet.revertProject(json)
        then:
            json.clientData.environments.size() == 6
            json.clientData.environments[0].name == "Street"
            json.clientData.environments[0].description == "Street"
            json.clientData.environments[1].name == "Parking Lot"
            json.clientData.environments[1].description == "Parking Lot"
            json.clientData.environments[2].name == "Wet Foundation"
            json.clientData.environments[2].description == "Foundation is damp"
            json.clientData.environments[3].name == "Pedestrian"
            json.clientData.environments[3].description == "Pedestrian"
            json.clientData.environments[4].name == "Farm"
            json.clientData.environments[4].description == "test"
            json.clientData.environments[5].name == "Test"
            json.clientData.environments[5].description == "I am writing a really long description for a test. This note does not make any sense."
    }
}
