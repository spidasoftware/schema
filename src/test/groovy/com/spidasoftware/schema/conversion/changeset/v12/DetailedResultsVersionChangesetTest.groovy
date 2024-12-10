/*
 * Copyright (c) 2024 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v12

import spock.lang.Specification

class DetailedResultsVersionChangesetTest extends Specification {

    def "convert results"() {
        setup:
            DetailedResultsVersionChangeset changeset = new DetailedResultsVersionChangeset()
            Map resultsJson = [id: "test", results: []]
            boolean changed
        when: "up-convert"
            changed = changeset.applyToResults(resultsJson)
        then:
            !changed
            resultsJson.version == 12
        when: "down-convert"
            changed = changeset.revertResults(resultsJson)
        then:
            !changed
            resultsJson.version == null
    }
}
