/*
 * ©2009-2018 SPIDAWEB LLC
 */

package com.spidasoftware.schema.conversion.changeset.v6

import com.spidasoftware.schema.validation.Validator
import groovy.json.JsonSlurper
import spock.lang.Specification

class RemoveTensionResultsChangesetTest extends Specification {

	void "test revert file with wire tension"() {
		def leanStream = RevertBundleChangesetTest.getResourceAsStream("/conversions/v6/wire-tension.json".toString())
		Map projectJSON = new JsonSlurper().parse(leanStream)
		leanStream.close()


		def designs = projectJSON.leads[0].locations*.designs.flatten()
		expect:
			designs[0].analysis[0].results.size() == 12
			designs[0].analysis[0].results[11].analysisType == "TENSION"

		when:
			def changeset = new RemoveTensionResultsChangeset()
			changeset.revertProject(projectJSON)
			def firstResults = projectJSON.leads[0].locations[0].designs[0].analysis[0].results
		then:
			!designs*.analysis.flatten().any { analysis ->
				analysis.results?.any { result ->
					result.analysisType == "TENSION"
				}
			}

			firstResults.size() == 11
	}
}
