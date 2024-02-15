/*
 * Copyright (c) 2024 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v11

import groovy.json.JsonSlurper
import spock.lang.Specification

class ClientPoleSettingTypeChangeSetTest extends Specification {

    static ClientPoleSettingTypeChangeSet clientPoleSettingTypeChangeSet

    def setupSpec() {
        clientPoleSettingTypeChangeSet = new ClientPoleSettingTypeChangeSet()
    }

    def "apply client data"() {
        setup:
            InputStream stream = ClientPoleSettingTypeChangeSetTest.getResourceAsStream("/conversions/v11/ClientPole-client-v10.json")
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
        expect:
            !json.poles[0].settingType
            !json.poles[0].customSettingDepth
            !json.poles[1].settingType
            !json.poles[1].customSettingDepth
        when: "up convert"
            clientPoleSettingTypeChangeSet.applyToClientData(json)
        then: "setting type is added to each client pole"
            json.poles[0].settingType == "ANSI"
            !json.poles[0].customSettingDepth
            json.poles[1].settingType == "ANSI"
            !json.poles[1].customSettingDepth
    }

    def "revert client data"() {
        setup:
            InputStream stream = ClientPoleSettingTypeChangeSetTest.getResourceAsStream("/conversions/v11/ClientPole-client-v11.json")
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
        expect:
            json.poles[0].settingType == "ANSI"
            !json.poles[0].customSettingDepth
            json.poles[1].settingType == "GO95"
            !json.poles[1].customSettingDepth
            json.poles[2].settingType == "CUSTOM"
            json.poles[2].customSettingDepth == [value: 1, unit: "METRE"]
        when: "revert client data"
            clientPoleSettingTypeChangeSet.revertClientData(json)
        then: "setting type and possibly custom setting depth are removed"
            !json.poles[0].settingType
            !json.poles[0].customSettingDepth
            !json.poles[1].settingType
            !json.poles[1].customSettingDepth
            !json.poles[2].settingType
            !json.poles[2].customSettingDepth
    }
}
