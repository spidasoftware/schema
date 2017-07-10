/*
 * ©2009-2015 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.ChangeSet
import groovy.json.JsonSlurper
import spock.lang.Specification

class AssembliesChangeSetTest extends Specification {

	def "apply"() {
		String jsonString = """{
		"leads": [
			{
			"locations": [
			{
				"designs": [
					{
						"framingPlan": {
						}
					},
					{
						"framingPlan": {
						}
					}
				]
				}
			]
			}
		]
	}
"""
		def json = new JsonSlurper().parseText(jsonString)
		AssembliesChangeSet changeSet = new AssembliesChangeSet()
		changeSet.applyToProject(json)
		expect:
			json.leads.first().locations.first().designs.every {it.containsKey("framingPlan") == false}
	}

	def "revert"() {
		setup:
			String jsonString = """{
			"leads": [
				{
				"locations": [
				{
					"designs": [
						{
							"structure": {
								"assemblies": []
							}
						}
					]
					}
				]
				}
			]
		}
	"""
		    Map projectJSON = new JsonSlurper().parseText(jsonString)
		    Map locationJSON = projectJSON.leads[0].locations[0]
		    Map designJSON = ChangeSet.duplicateAsJson(projectJSON.leads[0].locations[0].designs[0])
			AssembliesChangeSet changeSet = new AssembliesChangeSet()
		when: "revertProject"
			changeSet.revertProject(projectJSON)
		then:
			projectJSON.leads.first().locations.first().designs.every { design ->
				(design.structure.containsKey("assemblies") == false) &&
				(design.structure.containsKey("assemblyPlan") == false)
			}
		when: "revertLocation"
			changeSet.revertLocation(locationJSON)
		then:
			locationJSON.designs.every { design ->
				(design.structure.containsKey("assemblies") == false) &&
						(design.structure.containsKey("assemblyPlan") == false)
			}
		when: "revertDesign"
			changeSet.revertDesign(designJSON)
		then:
			designJSON.structure.containsKey("assemblies") == false
			designJSON.structure.containsKey("assemblyPlan") == false
	}
}
