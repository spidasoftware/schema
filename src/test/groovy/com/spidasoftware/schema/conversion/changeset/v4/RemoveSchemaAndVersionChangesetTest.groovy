package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.calc.CalcProjectChangeSet
import groovy.json.JsonSlurper
import spock.lang.Shared
import spock.lang.Specification


class RemoveSchemaAndVersionChangesetTest extends Specification {

    @Shared String jsonString = """{
		"leads": [
			{
			"locations": [
			{
			    "schema": "/schema/spidacalc/calc/location.schema",
			    "version": 4,
				"designs": [
					{
						"schema": "/schema/spidacalc/calc/design.schema",
                        "version": 4,
					}
				]
				}
			]
			}
		]
	}
"""
    @Shared Map projectJSON, locationJSON, designJSON
    RemoveSchemaAndVersionChangeSet changeSet = new RemoveSchemaAndVersionChangeSet()

    void setupSpec() {
        projectJSON = new JsonSlurper().parseText(jsonString)
        locationJSON = CalcProjectChangeSet.duplicateAsJson(projectJSON.leads[0].locations[0])
        designJSON = CalcProjectChangeSet.duplicateAsJson(projectJSON.leads[0].locations[0].designs[0])
    }

    void testRevertProject() {
        when:
            changeSet.revertProject(projectJSON)
        then:
            !projectJSON.leads[0].locations[0].containsKey("schema")
            !projectJSON.leads[0].locations[0].containsKey("version")
            !projectJSON.leads[0].locations[0].designs[0].containsKey("schema")
            !projectJSON.leads[0].locations[0].designs[0].containsKey("version")
    }

    void testRevertLocation() {
        when:
            changeSet.revertLocation(locationJSON)
        then:
            !locationJSON.containsKey("schema")
            !locationJSON.containsKey("version")
            !locationJSON.designs[0].containsKey("schema")
            !locationJSON.designs[0].containsKey("version")
    }

    void testRevertDesign() {
        when:
            changeSet.revertDesign(designJSON)
        then:
            !designJSON.containsKey("schema")
            !designJSON.containsKey("version")
    }
}
