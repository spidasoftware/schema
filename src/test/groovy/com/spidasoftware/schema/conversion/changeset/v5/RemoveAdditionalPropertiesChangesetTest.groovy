package com.spidasoftware.schema.conversion.changeset.v5

import com.spidasoftware.schema.conversion.changeset.calc.CalcProjectChangeSet

import com.spidasoftware.schema.conversion.changeset.v4.AnalysisTypeChangeSet
import com.spidasoftware.schema.validation.Validator
import groovy.json.JsonSlurper
import groovy.util.logging.Log4j
import spock.lang.Shared
import spock.lang.Specification

@Log4j
class RemoveAdditionalPropertiesChangesetTest extends Specification {

    @Shared Map projectJSON, locationJSON, designJSON
    RemoveAdditionalPropertiesChangeset changeset = new RemoveAdditionalPropertiesChangeset()

    void setupSpec() {
        def leanStream = AnalysisTypeChangeSet.getResourceAsStream("/conversions/v5/remove-additional-properties-project.json")
        projectJSON = new JsonSlurper().parse(leanStream)
        locationJSON = CalcProjectChangeSet.duplicateAsJson(projectJSON.leads[0].locations[0])
        designJSON = CalcProjectChangeSet.duplicateAsJson(projectJSON.leads[0].locations[0].designs[0])

        assert projectJSON.address.projectAddressAdditionalProperty != null
        assert projectJSON.leads[0].leadAdditionalProperty != null
        assert projectJSON.leads[0].locations[0].locationAdditionalProperty != null
        assert projectJSON.leads[0].locations[0].designs[0].designAdditionalProperty != null
    }

    void testRevertProject() {
        when:
            projectJSON.put("strict", true)
        then:
            !new Validator().validateAndReport("/schema/spidacalc/calc/project-v4.schema", projectJSON).isSuccess()
        when:
            changeset.revertProject(projectJSON)
            projectJSON.put("strict", true)
        then:
            new Validator().validateAndReport("/schema/spidacalc/calc/project-v4.schema", projectJSON).isSuccess()
    }

    void testRevertLocation() {
        when:
            locationJSON.put("strict", true)
        then:
            !new Validator().validateAndReport("/schema/spidacalc/calc/location-v4.schema", locationJSON).isSuccess()
        when:
            changeset.revertLocation(locationJSON)
            locationJSON.put("strict", true)
        then:
            new Validator().validateAndReport("/schema/spidacalc/calc/location-v4.schema", locationJSON).isSuccess()
    }

    void testRevertDesign() {
        when:
            designJSON.put("strict", true)
        then:
            !new Validator().validateAndReport("/schema/spidacalc/calc/design-v4.schema", designJSON).isSuccess()
        when:
            changeset.revertDesign(designJSON)
            designJSON.put("strict", true)
        then:
            new Validator().validateAndReport("/schema/spidacalc/calc/design-v4.schema", designJSON).isSuccess()
    }

}