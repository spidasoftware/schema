/*
 * ©2009-2015 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v4

import net.sf.json.JSONObject
import net.sf.json.JSONSerializer
import spock.lang.Specification

class AssembliesChangeSetTest extends Specification {

	// TODO: Apply this when new assembly changes are complete.
//	def "Apply"() {
//		String jsonString = """{
//		"leads": [
//			{
//			"locations": [
//			{
//				"designs": [
//					{
//						"framingPlan": {
//						}
//					},
//					{
//						"framingPlan": {
//						}
//					}
//				]
//				}
//			]
//			}
//		]
//	}
//"""
//		def json = JSONSerializer.toJSON(jsonString)
//		AssembliesChangeSet changeSet = new AssembliesChangeSet()
//		changeSet.apply(json)
//		expect:
//			json.leads.first().locations.first().designs.every {it.containsKey("framingPlan") == false}
//	}

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
								"assemblies": [],
								"assemblyPlan": []
							}
						}
					]
					}
				]
				}
			]
		}
	"""
			JSONObject projectJSON = JSONSerializer.toJSON(jsonString)
			JSONObject locationJSON = JSONObject.fromObject(projectJSON.leads[0].locations[0])
			JSONObject designJSON = JSONObject.fromObject(projectJSON.leads[0].locations[0].designs[0])
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
