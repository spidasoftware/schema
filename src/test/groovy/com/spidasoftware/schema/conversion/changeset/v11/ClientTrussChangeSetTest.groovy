/*
 * Copyright (c) 2024 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v11

import groovy.json.JsonSlurper
import spock.lang.Specification

class ClientTrussChangeSetTest extends Specification {

    static ClientTrussChangeSet clientTrussChangeSet

    def setupSpec() {
        clientTrussChangeSet = new ClientTrussChangeSet()
    }

    def "revert client data"() {
        setup:
            InputStream stream = ClientTrussChangeSetTest.getResourceAsStream("/conversions/v11/ClientPole-client-v11.json")
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
        expect:
            json.trusses
        when:
            clientTrussChangeSet.revertClientData(json)
        then:
            !json.trusses
    }
}
