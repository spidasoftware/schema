/*
 * Copyright (c) 2024 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v11

import com.github.fge.jsonschema.core.report.ProcessingReport
import com.spidasoftware.schema.validation.Validator
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
            boolean changes = clientPoleSettingTypeChangeSet.applyToClientData(json)
        then:
            changes
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
            boolean changes = clientPoleSettingTypeChangeSet.revertClientData(json)
        then: "setting type and possibly custom setting depth are removed"
            changes
            !json.poles[0].settingType
            !json.poles[0].customSettingDepth
            !json.poles[1].settingType
            !json.poles[1].customSettingDepth
            !json.poles[2].settingType
            !json.poles[2].customSettingDepth
    }

    def "test schema validation"() {
        setup:
            InputStream stream = ClientPoleSettingTypeChangeSetTest.getResourceAsStream("/conversions/v11/ClientPole-client-v11.json")
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
            Validator validator = new Validator()
            ProcessingReport report
        expect:
            json.poles[0].settingType == "ANSI"
            !json.poles[0].customSettingDepth
            json.poles[1].settingType == "GO95"
            !json.poles[1].customSettingDepth
            json.poles[2].settingType == "CUSTOM"
            json.poles[2].customSettingDepth == [value: 1, unit: "METRE"]
        when: "validate pole with ANSI setting type and no customSettingDepth"
            report = validator.validateAndReport("/schema/spidacalc/client/pole.schema", json.poles[0] as Map)
        then: "valid"
            report.success
        when: "validate pole with ANSI setting type and customSettingDepth"
            json.poles[0].customSettingDepth = [value: 1, unit: "METRE"]
            report = validator.validateAndReport("/schema/spidacalc/client/pole.schema", json.poles[0] as Map)
        then: "invalid"
            !report.success
        when: "validate pole with GO95 setting type and no customSettingDepth"
            report = validator.validateAndReport("/schema/spidacalc/client/pole.schema", json.poles[1] as Map)
        then: "valid"
            report.success
        when: "validate pole with GO95 setting type and customSettingDepth"
            json.poles[1].customSettingDepth = [value: 1, unit: "METRE"]
            report = validator.validateAndReport("/schema/spidacalc/client/pole.schema", json.poles[1] as Map)
        then: "invalid"
            !report.success
        when: "validate pole with CUSTOM setting type and customSettingDepth"
            report = validator.validateAndReport("/schema/spidacalc/client/pole.schema", json.poles[2] as Map)
        then: "valid"
            report.success
        when: "validate pole with CUSTOM setting type and no customSettingDepth"
            json.poles[2].remove("customSettingDepth")
            report = validator.validateAndReport("/schema/spidacalc/client/pole.schema", json.poles[2] as Map)
        then: "invalid"
            !report.success
    }
}
