package com.spidasoftware.schema.conversion.changeset.v5

import com.spidasoftware.schema.conversion.changeset.calc.CalcProjectChangeSet

//import com.spidasoftware.schema.conversion.changeset.calc.CalcProjectChangeSet
import com.spidasoftware.schema.conversion.changeset.v4.AnalysisTypeChangeSet
import com.spidasoftware.schema.validation.Validator
import groovy.json.JsonSlurper
import groovy.util.logging.Log4j
import spock.lang.Specification

@Log4j
class RemoveAdditionalPropertiesChangesetTest extends Specification {

    Map projectJSON,locationJSON, designJSON
    RemoveAdditionalPropertiesChangeset changeset = new RemoveAdditionalPropertiesChangeset()

    void setup() {
        def leanStream = AnalysisTypeChangeSet.getResourceAsStream("/conversions/v5/remove-additional-properties-project.json")
        projectJSON = new JsonSlurper().parse(leanStream)
        locationJSON = CalcProjectChangeSet.duplicateAsJson(projectJSON.leads[0].locations[0])
        designJSON = CalcProjectChangeSet.duplicateAsJson(projectJSON.leads[0].locations[0].designs[0])
    }

    void "test project"() {
        when:
            changeset.revertProject(projectJSON)
            log.info("projectJSON.leads[0].locations[0].designs[0].structure.spanPoints[0] = ${projectJSON.leads[0].locations[0].designs[0].analysis[0].results[0]}")
        then:
            new Validator().validateAndReport("/schema/spidacalc/calc/project-v4.schema", projectJSON).isSuccess()
    }

    void "test location"() {
        when:
            changeset.revertLocation(locationJSON)
            log.info("projectJSON.leads[0].locations[0].designs[0].structure.spanPoints[0] = ${projectJSON.leads[0].locations[0].designs[0].analysis[0].results[0]}")
        then:
            new Validator().validateAndReport("/schema/spidacalc/calc/project-v4.schema", projectJSON).isSuccess()
    }

    void "test design"() {
        when:
            changeset.revertDesign(designJSON)
            log.info("projectJSON.leads[0].locations[0].designs[0].structure.spanPoints[0] = ${projectJSON.leads[0].locations[0].designs[0].analysis[0].results[0]}")
        then:
            new Validator().validateAndReport("/schema/spidacalc/calc/project-v4.schema", projectJSON).isSuccess()
    }

}