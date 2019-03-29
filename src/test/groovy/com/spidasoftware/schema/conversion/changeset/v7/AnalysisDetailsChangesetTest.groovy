/*
 * ©2009-2019 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v7

import com.spidasoftware.schema.conversion.ExchangeFile
import spock.lang.Specification

class AnalysisDetailsChangesetTest extends Specification {

	def "convert"() {
		setup:
		AnalysisDetailsChangeset changeset = new AnalysisDetailsChangeset()

		when:
			changeset.applyToDesign(design)
		then:
			design.containsKey("analysis")
			design.containsKey("analysisDetails")
			design.analysisDetails.containsKey("resultId")
			!design.analysisDetails.resultId.endsWith(".json")
			!design.analysisDetails.containsKey("detailedResults")
			!design.analysisDetails.containsKey("analyzedStructure")
		where:
			design << getDesigns("/conversions/v7/connectedAnalyzed.exchange.spida")
	}

	private List<Map> getDesigns(String exchangeFileName) {
		def exchangeFile = new File(AnalysisDetailsChangesetTest.getResource(exchangeFileName).toURI())
		ExchangeFile exchange = ExchangeFile.createFromZipFile(exchangeFile)
		Map project = exchange.projectJSON

		List<Map> designs = []
		project.leads.each { Map lead ->
			lead.locations.each { Map location ->
				location.designs.each { Map design ->
					designs.add(design)
				}
			}
		}
		return designs
	}
}
