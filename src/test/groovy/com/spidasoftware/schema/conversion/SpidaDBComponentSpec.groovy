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

	private SpidaDBResult loadResult(String resourceString) {
        Map json = getResource(resourceString)
    	return new SpidaDBResult(json.get('results').get(0))
    }

	private Map getResource(String resourceString) {
		String text = getClass().getResourceAsStream(resourceString).text
		return new JsonSlurper().parseText(text)
	}

    def setup() throws Exception {
		project = loadProject("/formats/spidadb/minimal-project-valid/projects.json")
		location = loadLocation("/formats/spidadb/minimal-project-valid/locations.json")
		design = loadDesign("/formats/spidadb/minimal-project-valid/designs.json")
		result = loadResult("/formats/spidadb/minimal-project-valid/results.json")
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
	        location.updateCalcJSONSchema("added")
        then:
            location.getMap().calcLocation.schema=="added"
	}

	def "set schema in design"() {
	    when:
	        design.updateCalcJSONSchema("added")
        then:
            design.getMap().calcDesign.schema=="added"
	}

	def "set schema in result"() {
	    when:
	        result.updateCalcJSONSchema("added")
        then:
            result.getMap().calcResult.schema=="added"
	}

	def "set version in location"() {
	    when:
	        location.updateCalcJSONVersion("11")
        then:
            location.getMap().calcLocation.version=="11"
	}

	def "set version in design"() {
	    when:
	        design.updateCalcJSONVersion("11")
        then:
            design.getMap().calcDesign.version=="11"
	}

	def "set version in result"() {
	    when:
	        result.updateCalcJSONVersion("11")
        then:
            result.getMap().calcResult.version=="11"
	}

	def "set client file id in project"() {
	    when:
	        project.updateClientFileId("123456")
        then:
            project.getMap().clientFileId=="123456"
	}

	def "set client file id in location"() {
	    when:
	        location.updateClientFileId("123456")
        then:
            location.getMap().clientFileId=="123456"
	}

	def "set client file id in design"() {
	    when:
	        design.updateClientFileId("123456")
        then:
            design.getMap().clientFileId=="123456"
	}

	def "set client file id in result"() {
	    when:
	        result.updateClientFileId("123456")
        then:
            result.getMap().clientFileId=="123456"
    }

	def "set analysis summary in design"() {
	    when:
	        design.updateAnalysisSummary([key1:"value1", key2:"value2", key3: "value3"])
        then:
            design.getMap().calcDesign.analysisSummary==[key1:"value1", key2:"value2", key3: "value3"]
	}

 	def "set id in project"() {
 	    when:
 	        project.updateId("123")
         then:
             project.getMap().id=="123"
 	}

 	def "set id in location"() {
 	    when:
 	        location.updateId("123")
         then:
             location.getMap().id=="123"
 	}

 	def "set id in design"() {
 	    when:
 	        design.updateId("123")
         then:
             design.getMap().id=="123"
 	}

 	def "set id in result"() {
 	    when:
 	        result.updateId("123")
         then:
             result.getMap().id=="123"
     }

 	def "set id in calcJSON project"() {
 	    when:
 	        project.updateCalcJSONId("123")
         then:
             project.getMap().calcProject.id=="123"
 	}

 	def "set id in calcJSON location"() {
 	    when:
 	        location.updateCalcJSONId("123")
         then:
             location.getMap().calcLocation.id=="123"
 	}

 	def "set id in calcJSON design"() {
 	    when:
 	        design.updateCalcJSONId("123")
         then:
             design.getMap().calcDesign.id=="123"
 	}

 	def "set id in calcJSON result"() {
 	    when:
 	        result.updateCalcJSONId("123")
         then:
             result.getMap().calcResult.id=="123"
     }

   	def "test calc json is immutable"() {
   	    when:
   	        project.getCalcJSON().put("id","123")
        then:
            thrown(UnsupportedOperationException)
   	    when:
   	        location.getCalcJSON().put("id","123")
        then:
            thrown(UnsupportedOperationException)
   	    when:
   	        design.getCalcJSON().put("id","123")
        then:
            thrown(UnsupportedOperationException)
   	    when:
   	        result.getCalcJSON().put("id","123")
        then:
            thrown(UnsupportedOperationException)

    }
}
