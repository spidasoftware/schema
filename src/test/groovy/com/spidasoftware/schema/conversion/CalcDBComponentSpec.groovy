package com.spidasoftware.schema.conversion

import groovy.json.JsonSlurper
import spock.lang.Specification

class CalcDBComponentSpec extends Specification{


	CalcDBProject project
	CalcDBLocation location
	CalcDBDesign design

	private CalcDBLocation loadLocation(String resourceString) {
	    Map json = getResource(resourceString)
		return new CalcDBLocation(json.get("locations").get(0))
	}

	private CalcDBDesign loadDesign(String resourceString) {
	    Map json = getResource(resourceString)
		return new CalcDBDesign(json.get('designs').get(0))
	}

	private CalcDBProject loadProject(String resourceString) {
	    Map json = getResource(resourceString)
		List ray = json.get("projects")
	    Map projectJson = ray.get(0)
		return new CalcDBProject(projectJson)
	}

	private Map getResource(String resourceString) {
		String text = getClass().getResourceAsStream(resourceString).text
		return new JsonSlurper().parseText(text)
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
			design.getMap().locationId=="replaced"
	}
}
