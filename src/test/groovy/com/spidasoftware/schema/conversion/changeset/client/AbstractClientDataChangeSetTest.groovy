/*
 * Copyright (c) 2026 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.client

import spock.lang.Specification
import spock.lang.Unroll

class AbstractClientDataChangeSetTest extends Specification {

	static class TestChangeSet extends AbstractClientDataChangeSet {
		@Override
		boolean applyToClientData(Map clientDataJSON) { return false }

		@Override
		boolean revertClientData(Map clientDataJSON) { return false }

		boolean design(Map json, Closure closure) { return eachDesignLoadCase(json, closure) }

		boolean result(Map json, Closure closure) { return eachResultLoadCase(json, closure) }
	}

	TestChangeSet changeSet = new TestChangeSet()

	@Unroll
	def "eachDesignLoadCase skips missing or null entries: #description"() {
		setup:
			List visited = []
		when:
			boolean changed = changeSet.design(json, { Map lc -> visited << lc; return true })
		then:
			notThrown(NullPointerException)
			!changed
			visited.isEmpty()
		where:
			description                   | json
			"null design"                 | null
			"no analysis key"             | [:]
			"null analysis list"          | [analysis: null]
			"empty analysis list"         | [analysis: []]
			"null analysis entry"         | [analysis: [null]]
			"null analysisCaseDetails"    | [analysis: [[analysisCaseDetails: null]]]
			"missing analysisCaseDetails" | [analysis: [[id: "x"]]]
	}

	@Unroll
	def "eachResultLoadCase skips missing or null entries: #description"() {
		setup:
			List visited = []
		when:
			boolean changed = changeSet.result(json, { Map lc -> visited << lc; return true })
		then:
			notThrown(NullPointerException)
			!changed
			visited.isEmpty()
		where:
			description                   | json
			"null results"                | null
			"no results key"              | [:]
			"null results list"           | [results: null]
			"empty results list"          | [results: []]
			"null result entry"           | [results: [null]]
			"null analysisCaseDetails"    | [results: [[analysisCaseDetails: null]]]
			"missing analysisCaseDetails" | [results: [[id: "x"]]]
	}

	def "eachDesignLoadCase visits every non-null load case, including empty maps"() {
		setup:
			Map first = [type: "A"]
			Map empty = [:]
			Map last = [type: "B"]
			Map json = [analysis: [[analysisCaseDetails: first], [analysisCaseDetails: null], [analysisCaseDetails: empty], [analysisCaseDetails: last]]]
			List visited = []
		when:
			boolean changed = changeSet.design(json, { Map lc -> visited << lc; return false })
		then:
			!changed
			visited == [first, empty, last]
	}

	def "eachResultLoadCase continues after closure returns true and reports change"() {
		setup:
			Map json = [results: [[analysisCaseDetails: [type: "A"]], [analysisCaseDetails: [type: "B"]], [analysisCaseDetails: [type: "C"]]]]
			List visited = []
		when:
			boolean changed = changeSet.result(json, { Map lc -> visited << lc.type; return lc.type == "A" })
		then:
			changed
			visited == ["A", "B", "C"]
	}

	def "non-boolean closure results are treated as unchanged"() {
		setup:
			Map json = [results: [[analysisCaseDetails: [type: "A"]]]]
		when:
			boolean changed = changeSet.result(json, { Map lc -> lc.remove("type") })
		then:
			!changed
			json.results[0].analysisCaseDetails.isEmpty()
	}
}
