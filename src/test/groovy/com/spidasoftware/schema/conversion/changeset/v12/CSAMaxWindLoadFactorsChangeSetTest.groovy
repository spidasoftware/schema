/*
 * Copyright (c) 2025 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v12

import groovy.json.JsonSlurper
import spock.lang.Specification

class CSAMaxWindLoadFactorsChangeSetTest extends Specification {

    def "clientData"() {
        setup:
            CSAMaxWindLoadFactorsChangeSet changeSet = new CSAMaxWindLoadFactorsChangeSet()
            InputStream stream = CSAMaxWindLoadFactorsChangeSetTest.getResourceAsStream("/conversions/v12/csaMaxWindLoadFactors-v11.json")
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
            Map overrides = json.overrides as Map
            Map valuesApplied = json.valuesApplied as Map
        expect:
            CSAMaxWindLoadFactorsChangeSet.loadFactorKeys.every { String loadFactor ->
                overrides.containsKey(loadFactor) && valuesApplied.containsKey(loadFactor)
            }
        when: "remove load factors"
            changeSet.removeCSALoadFactors(json)
        then:
            CSAMaxWindLoadFactorsChangeSet.loadFactorKeys.every { String loadFactor ->
                !overrides.containsKey(loadFactor) && !valuesApplied.containsKey(loadFactor)
            }
    }
}
