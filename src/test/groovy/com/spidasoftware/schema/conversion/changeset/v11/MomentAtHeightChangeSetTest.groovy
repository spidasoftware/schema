/*
 * Copyright (c) 2024 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v11

import com.github.fge.jsonschema.core.report.ProcessingReport
import com.spidasoftware.schema.validation.Validator
import groovy.json.JsonSlurper
import spock.lang.Specification

class MomentAtHeightChangeSetTest extends Specification {

    static MomentAtHeightChangeSet changeSet

    def setupSpec() {
        changeSet = new MomentAtHeightChangeSet()
    }

    def "revert/apply client data json"() {
        setup:
            def stream = MomentAtHeightChangeSetTest.getResourceAsStream("/conversions/v11/ClientPole-client-v11.json")
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
        expect:
            json.poles[1].maximumGroundLineMoment
            !json.poles[1].momentAtHeights

            json.poles[2].maximumAllowableStress
            !json.poles[2].momentAtHeights

            json.poles[3].maximumAllowableStress
            json.poles[3].momentAtHeights.distanceFromTip
            json.poles[3].momentAtHeights.maximumAllowableMoment

            json.poles[4].maximumGroundLineMoment
            json.poles[4].momentAtHeights.distanceFromTip
            json.poles[4].momentAtHeights.maximumAllowableMoment

            json.poles[5].maximumAllowableStress
            json.poles[5].momentAtHeights[0].distanceFromTip
            json.poles[5].momentAtHeights[0].maximumAllowableMoment
        when: "down convert client data json"
            boolean reverted = changeSet.revertClientData(json)
        then:
            reverted
            json.poles[1].allowable.unit == "NEWTON_METRE"
            !json.poles[1].momentAtHeights

            json.poles[2].allowable.unit == "PASCAL"
            !json.poles[2].momentAtHeights

            json.poles[3].allowable.unit == "PASCAL"
            !json.poles[3].momentAtHeights

            json.poles[4].allowable.unit == "NEWTON_METRE"
            !json.poles[4].momentAtHeights

            json.poles[5].allowable.unit == "PASCAL"
            !json.poles[5].momentAtHeights
        when: "up convert client data json"
            boolean converted = changeSet.applyToClientData(json)
        then:
            converted
            json.poles[1].maximumGroundLineMoment
            !json.poles[1].maximumAllowableStress

            json.poles[2].maximumAllowableStress
            !json.poles[2].maximumGroundLineMoment

            json.poles[3].maximumAllowableStress
            !json.poles[3].maximumGroundLineMoment

            json.poles[4].maximumGroundLineMoment
            !json.poles[4].maximumAllowableStress

            json.poles[5].maximumAllowableStress
            !json.poles[5].maximumGroundLineMoment
    }

    def "validate client pole"() {
        setup:
            def stream = MomentAtHeightChangeSetTest.getResourceAsStream("/conversions/v11/ClientPole-client-v11.json")
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
            String schemaPath = "/schema/spidacalc/client/pole.schema"
            Validator validator = new Validator()
            ProcessingReport report
        when: "validate json"
            report = validator.validateAndReport(schemaPath, json.poles[0] as Map)
        then: "valid"
            report.success
        when: "max stress and max moment"
            json.poles[0].maximumAllowableStress = [value: 6.1, unit: "PASCAL"]
            json.poles[0].maximumGroundLineMoment = [value: 10.1, unit: "NEWTON_METRE"]
            report = validator.validateAndReport(schemaPath, json.poles[0] as Map)
        then: "invalid"
            !report.success
        when: "neither max stress or max moment"
            (json.poles[0] as Map).remove("maximumAllowableStress")
            (json.poles[0] as Map).remove("maximumGroundLineMoment")
            report = validator.validateAndReport(schemaPath, json.poles[0] as Map)
        then: "invalid"
            !report.success
        when: "max stress and momentAtHeights"
            json.poles[0].maximumAllowableStress = [value: 6.1, unit: "PASCAL"]
            Map momentAtHeights = [:]
            momentAtHeights.distanceFromTip = [value: 5.0, unit: "METRE"]
            momentAtHeights.maximumAllowableMoment = [value: 10.0, unit: "NEWTON_METRE"]
            json.poles[0].momentAtHeights = [momentAtHeights]
            report = validator.validateAndReport(schemaPath, json.poles[0] as Map)
        then: "valid"
            report.success
        when: "max moment and momentAtHeights"
            (json.poles[0] as Map).remove("maximumAllowableStress")
            json.poles[0].maximumGroundLineMoment = [value: 10.0, unit: "NEWTON_METRE"]
            report = validator.validateAndReport(schemaPath, json.poles[0] as Map)
        then: "valid"
            report.success
    }
}
