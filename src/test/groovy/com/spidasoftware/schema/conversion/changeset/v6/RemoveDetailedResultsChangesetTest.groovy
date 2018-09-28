/*
 * ©2009-2018 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v6

import com.spidasoftware.schema.conversion.changeset.calc.CalcProjectChangeSet
import com.spidasoftware.schema.validation.Validator
import groovy.json.JsonSlurper
import groovy.util.logging.Log4j
import spock.lang.Specification
import spock.lang.Unroll

@Log4j
class RemoveDetailedResultsChangesetTest extends Specification {

    @Unroll
    void "test revert fileName=#fileName"() {
        setup:
            RemoveDetailedResultsChangeset changeset = new RemoveDetailedResultsChangeset()
            def leanStream = RemoveDetailedResultsChangesetTest.getResourceAsStream("/conversions/v6/${fileName}".toString())
            Map projectJSON = new JsonSlurper().parse(leanStream)
            Map locationJSON = CalcProjectChangeSet.duplicateAsJson(projectJSON.leads[0].locations[0])
            Map designJSON = CalcProjectChangeSet.duplicateAsJson(projectJSON.leads[0].locations[0].designs[0])
        when: "revertProject"
            changeset.revertProject(projectJSON)
            Map loadCaseResult = projectJSON.leads[0].locations[0].designs[0].analysis.find { it.id == "New, Light, 8 lb, Grade A" }
        then:
            projectJSON.leads[0].locations[0].designs[0].analysis.size() == 2
            projectJSON.leads[0].locations[0].designs[0].analysis.find { it.id == "GO95" } == [id: "GO95"]
            loadCaseResult.results.size() == 1
            new Validator().validateAndReport("/schema/spidamin/asset/standard_details/analysis_asset.schema", loadCaseResult.results.first()).isSuccess()
        when: "revertLocation"
            changeset.revertLocation(locationJSON)
            loadCaseResult = locationJSON.designs[0].analysis.find { it.id == "New, Light, 8 lb, Grade A" }
        then:
            locationJSON.designs[0].analysis.size() == 2
            locationJSON.designs[0].analysis.find { it.id == "GO95" } == [id: "GO95"]
            loadCaseResult.results.size() == 1
            new Validator().validateAndReport("/schema/spidamin/asset/standard_details/analysis_asset.schema", loadCaseResult.results.first()).isSuccess()
        when: "revertDesign"
            changeset.revertDesign(designJSON)
            loadCaseResult = designJSON.analysis.find { it.id == "New, Light, 8 lb, Grade A" }
        then:
            designJSON.analysis.size() == 2
            designJSON.analysis.find { it.id == "GO95" } == [id: "GO95"]
            loadCaseResult.results.size() == 1
            new Validator().validateAndReport("/schema/spidamin/asset/standard_details/analysis_asset.schema", loadCaseResult.results.first()).isSuccess()
        where:
            fileName << ["project-detailed-and-summary-results.json", "project-resultId-and-summary-results.json"]
    }
}
