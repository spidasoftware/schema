/*
 * ©2009-2015 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v4

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
		def json = JSONSerializer.toJSON(jsonString)
		AssembliesChangeSet changeSet = new AssembliesChangeSet()
		changeSet.revert(json)
		expect:
			json.leads.first().locations.first().designs.every {design ->
				(design.structure.containsKey("assemblies") == false) &&
				(design.structure.containsKey("assemblyPlan") == false)
			}
	}
}
