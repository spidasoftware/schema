/*
 * Copyright (c) 2024 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v11

import groovy.json.JsonSlurper
import spock.lang.Specification

class TrussChangeSetTest extends Specification {

    static TrussChangeSet trussChangeSet

    def setupSpec() {
        trussChangeSet = new TrussChangeSet()
    }

    def "revert client data"() {
        setup:
            InputStream stream = TrussChangeSetTest.getResourceAsStream("/conversions/v11/ClientPole-client-v11.json")
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
        expect:
            json.trusses
        when:
            trussChangeSet.revertClientData(json)
        then:
            !json.trusses
    }
}
