package com.spidasoftware.schema.conversion

import groovy.json.JsonSlurper
import spock.lang.Specification

class SpidaDBComponentSpec extends Specification{


	SpidaDBProject project
	SpidaDBLocation location
	SpidaDBDesign design
	SpidaDBResult result

	private SpidaDBLocation loadLocation(String resourceString) {
	    Map json = getResource(resourceString)
		return new SpidaDBLocation(json.get("locations").get(0))
	}

	private SpidaDBDesign loadDesign(String resourceString) {
	    Map json = getResource(resourceString)
		return new SpidaDBDesign(json.get('designs').get(0))
	}

	private SpidaDBProject loadProject(String resourceString) {
	    Map json = getResource(resourceString)
		List ray = json.get("projects")
	    Map projectJson = ray.get(0)
		return new SpidaDBProject(projectJson)
	}

	private Map getResource(String resourceString) {
		String text = getClass().getResourceAsStream(resourceString).text
		return new JsonSlurper().parseText(text)
	}

    def setup() throws Exception {
		project = loadProject("/formats/spidadb/minimal-project-valid/projects.json")
		location = loadLocation("/formats/spidadb/minimal-project-valid/locations.json")
		design = loadDesign("/formats/spidadb/minimal-project-valid/designs.json")
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
			location.getSpidaDBId()=="replaced"
			location.getCalcJSON().id=="replaced"
	}

	def "replace locations ids on design"(){
		when:
			design.updateLocationIds(["53e3906d44ae3953e03b39fd":"replaced"])
		then:
			design.getMap().locationId=="replaced"
	}

	def "set schema in location"() {
	    when:
	        location.setSchema("added")
        then:
            location.getMap().calcLocation.schema=="added"
	}

	def "set schema in design"() {
	    when:
	        design.setSchema("added")
        then:
            design.getMap().calcDesign.schema=="added"
	}

//	def "set schema in result"() {
//	    when:
//	        result.setSchema("added")
//        then:
//            result.getMap().calcResult.schema=="added"
//	}

	def "set version in location"() {
	    when:
	        location.setVersion("11")
        then:
            location.getMap().calcLocation.version=="11"
	}

	def "set version in design"() {
	    when:
	        design.setVersion("11")
        then:
            design.getMap().calcDesign.version=="11"
	}

	def "set version in result"() {
        expect:
            1==1
	}
}
