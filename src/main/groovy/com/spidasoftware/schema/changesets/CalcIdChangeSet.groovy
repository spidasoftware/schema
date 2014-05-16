package com.spidasoftware.schema.changesets
import groovy.util.logging.Log4j
import net.sf.json.JSON
import net.sf.json.JSONArray
import net.sf.json.JSONObject
/**
 * Converts ids, names, and UUIDs to match the new schema
 * User: mford
 * Date: 5/13/14
 * Time: 4:02 PM
 * Copyright SPIDAWeb
 */
@Log4j
class CalcIdChangeSet {
	String schemaVersion = "0.8"
	String schemaPath = "/v1/schema/spidacalc/calc/project.schema"

	@Override
	void convert(JSON calcProject) {
		replaceProject(calcProject)
		calcProject.leads?.each { lead ->
			replaceProjectComponent(lead)
			lead.locations?.each { loc ->
				replaceProjectComponent(loc)
				loc.designs?.each { design ->
					replaceProjectComponent(design)
					def structure = design.structure
					if (structure) {
						structure.each { key, value ->
							if (value instanceof JSONArray) {
								value.each {poleComponent ->
									replacePoleComponent(poleComponent)
								}
							}
						}
						replacePoleComponent(structure)
						if (structure.pole) {
							removePoleId(structure.pole)
						}
					}
				}
			}
		}
	}

	void replaceProjectComponent(JSONObject component) {
		component.label = component.id
		component.id = component?.uuid?:component.id

		if (component.uuid) {
			component.remove("uuid")
		}
	}

	void replacePoleComponent(JSONObject component) {
		component.externalId = component.uuid

		if (component.uuid) {
			component.remove("uuid")
		}
		replaceOwner(component)
	}

	void replaceOwner(JSONObject component) {
		if (component.owner) {
			component.owner.id = component.owner.name
			component.owner.externalId = component.owner.uuid
			if (component.owner.uuid) {
				component.owner.remove("uuid")
			}
			component.owner.remove("name")
		}
	}

	void replaceProject(JSONObject project) {
		project.id = project.name
		project.label = project.name
		project.remove("name")
	}

	void removePoleId(JSONObject pole) {
		if (pole.uuid) {
			pole.remove("uuid")
		}
		replaceOwner(pole)
	}
}
