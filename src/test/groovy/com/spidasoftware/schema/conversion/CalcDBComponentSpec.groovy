package com.spidasoftware.schema.conversion

import net.sf.json.JSONArray
import net.sf.json.JSONObject
import spock.lang.Specification

class CalcDBComponentSpec extends Specification{


	CalcDBProject project
	CalcDBLocation location
	CalcDBDesign design

	private CalcDBLocation loadLocation(String resourceString) {
		JSONObject json = getResource(resourceString)
		return new CalcDBLocation(json.getJSONArray("locations").getJSONObject(0))
	}

	private CalcDBDesign loadDesign(String resourceString) {
		JSONObject json = getResource(resourceString)
		return new CalcDBDesign(json.getJSONArray('designs').getJSONObject(0))
	}

	private CalcDBProject loadProject(String resourceString) {
		JSONObject json = getResource(resourceString)
		JSONArray ray = json.getJSONArray("projects")
		JSONObject projectJson = ray.getJSONObject(0)
		return new CalcDBProject(projectJson)
	}

	private JSONObject getResource(String resourceString) {
		String text = getClass().getResourceAsStream(resourceString).text
		return JSONObject.fromObject(text)
	}

    def setup() throws Exception {
		project = loadProject("/formats/calcdb/minimal-project-valid/projects.json")
		location = loadLocation("/formats/calcdb/minimal-project-valid/locations.json")
		design = loadDesign("/formats/calcdb/minimal-project-valid/designs.json")
	}

	def "replace locations ids on project"(){
		when:
			project.updateLocationIds(["53e3906d44ae3953e03b39fd":"replaced"])
		then:
			project.calcJSON.leads*.locations*.id.flatten()==["replaced"]
	}

	def "replace locations ids on location"(){
		when:
			location.updateLocationIds(["53e3906d44ae3953e03b39fd":"replaced"])
		then:
			location.getCalcDBId()=="replaced"
			location.getCalcJSON().id=="replaced"
	}

	def "replace locations ids on design"(){
		when:
			design.updateLocationIds(["53e3906d44ae3953e03b39fd":"replaced"])
		then:
			design.getJSON().locationId=="replaced"
	}
}
