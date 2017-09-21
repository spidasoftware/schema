package com.spidasoftware.schema.validation

import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.load.configuration.LoadingConfiguration
import com.github.fge.jsonschema.main.JsonSchemaFactory
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.apache.log4j.Logger

class CalcSchemaTest extends GroovyTestCase {

	def report
	def log = Logger.getLogger(this.class)
	final LoadingConfiguration cfg = LoadingConfiguration.newBuilder().setNamespace(new File("resources").toURI().toString()).freeze()
    final JsonSchemaFactory factory = JsonSchemaFactory.newBuilder().setLoadingConfiguration(cfg).freeze()

	void testProjectWithForms(){
		def schema = factory.getJsonSchema("schema/spidacalc/calc/project.schema")
		report = schema.validate(JsonLoader.fromString(new File("resources/examples/spidacalc/projects/minimal_project_with_forms.json").text))
		report.each{ log.info "testProjectWithGPSObject validation report "+it.toString() }
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()
	}

	void testClientPoleObject(){
		def instance = '{"species":"pine", "height":{"unit":"FOOT", "value":100}, "classOfPole":"h2"}'
		def schema = factory.getJsonSchema("schema/spidacalc/calc/client_references/pole.schema")
		report = schema.validate(JsonLoader.fromString(instance))
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()
	}

	void testBasicDesignObject(){
		def schema = factory.getJsonSchema("schema/spidacalc/calc/structure.schema")
		report = schema.validate(JsonLoader.fromString(new File("resources/examples/spidacalc/designs/empty_pole.json").text))
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()
	}

	void testOneOfEverythingObject(){
		def schema = factory.getJsonSchema("schema/spidacalc/calc/structure.schema")
		report = schema.validate(JsonLoader.fromString(new File("resources/examples/spidacalc/designs/one_of_everything.json").text))
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()
	}

	void testDamagesObject(){
		def schema = factory.getJsonSchema("schema/spidacalc/calc/structure.schema")
		report = schema.validate(JsonLoader.fromString(new File("resources/examples/spidacalc/designs/damages.json").text))
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()
	}

	void testBisectorObject(){
		def schema = factory.getJsonSchema("schema/spidacalc/calc/structure.schema")
		report = schema.validate(JsonLoader.fromString(new File("resources/examples/spidacalc/designs/bisector.json").text))
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()
	}
	void testInsulatorObject(){
		def schema = factory.getJsonSchema("schema/spidacalc/calc/structure.schema")
		report = schema.validate(JsonLoader.fromString(new File("resources/examples/spidacalc/designs/insulator.json").text))
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()
	}

	void testWireObject(){
		def schema = factory.getJsonSchema("schema/spidacalc/calc/structure.schema")
		report = schema.validate(JsonLoader.fromString(new File("resources/examples/spidacalc/designs/wire.json").text))
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()
	}

	void testXArmObject(){
		def schema = factory.getJsonSchema("schema/spidacalc/calc/structure.schema")
		report = schema.validate(JsonLoader.fromString(new File("resources/examples/spidacalc/designs/xarm.json").text))
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()
	}


	void testProjectWithGPSObject(){
		def schema = factory.getJsonSchema("schema/spidacalc/calc/project.schema")
		report = schema.validate(JsonLoader.fromString(new File("resources/examples/spidacalc/projects/minimal_project_with_gps.json").text))
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()
	}

	void testFullProjectObject(){
		def schema = factory.getJsonSchema("schema/spidacalc/calc/project.schema")
		report = schema.validate(JsonLoader.fromString(new File("resources/examples/spidacalc/projects/full_project.json").text))
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()
	}

	void testMinimalProjectObject(){
		def schema = factory.getJsonSchema("schema/spidacalc/calc/project.schema")
		report = schema.validate(JsonLoader.fromString(new File("resources/examples/spidacalc/projects/minimal_project_no_designs.json").text))
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()
	}

	void testMultipleProjects(){
		def schema = factory.getJsonSchema("schema/spidacalc/calc/projects.schema")
		report = schema.validate(JsonLoader.fromString(new File("resources/examples/spidacalc/projects/multiple_projects.json").text))
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()
	}

	void testProjectAdditionalProperties() {
		def schema = factory.getJsonSchema("schema/spidacalc/calc/project.schema")
	    Map json = new JsonSlurper().parse(new File("resources/examples/spidacalc/projects/full_project.json"))
		json.put("an additional property", "shouldn't validate")
		report = schema.validate(JsonLoader.fromString(JsonOutput.toJson(json)))
		assertFalse "this instance should NOT be valid against the schema \n${report.toString()}", report.isSuccess()

		json = new JsonSlurper().parse(new File("resources/examples/spidacalc/projects/full_project.json"))
		json.get("leads").get(0).put("an additional property", "shouldn't validate")
		report = schema.validate(JsonLoader.fromString(JsonOutput.toJson(json)))
		assertFalse "this instance should NOT be valid against the schema \n${report.toString()}", report.isSuccess()

		json = new JsonSlurper().parse(new File("resources/examples/spidacalc/projects/full_project.json"))
		json.get("leads").get(0).get("locations").get(0).put("an additional property", "shouldn't validate")
		report = schema.validate(JsonLoader.fromString(JsonOutput.toJson(json)))
		assertFalse "this instance should NOT be valid against the schema \n${report.toString()}", report.isSuccess()

		def testFailues = []
		def locationComponents = ["geographicCoordinate", "remedies", "poleTags", "images"]
		locationComponents.each { component ->
			json = new JsonSlurper().parse(new File("resources/examples/spidacalc/projects/full_project.json"))
			if(component == "geographicCoordinate") {
				json.get("leads").get(0).get("locations").get(0).get(component).put("an additional property", "shouldn't validate")
			} else {
				json.get("leads").get(0).get("locations").get(0).get(component).get(0).put("an additional property", "shouldn't validate")
			}
			report = schema.validate(JsonLoader.fromString(JsonOutput.toJson(json)))
			if(report.isSuccess()) {
				testFailues << "added an additional property to ${component}, it should not validate but did."
			}
		}
		assertEquals "The list should be empty, if it is not there are failures", [], testFailues

		json = new JsonSlurper().parse(new File("resources/examples/spidacalc/projects/full_project.json"))
		json.get("leads").get(0).get("locations").get(0).get("designs").get(0).put("an additional property", "shouldn't validate")
		report = schema.validate(JsonLoader.fromString(JsonOutput.toJson(json)))
		assertFalse "this instance should NOT be valid against the schema \n${report.toString()}", report.isSuccess()

		json = new JsonSlurper().parse(new File("resources/examples/spidacalc/projects/full_project.json"))
		json.get("leads").get(0).get("locations").get(0).get("designs").get(0).get("analysis").get(0).put("an additional property", "shouldn't validate")
		report = schema.validate(JsonLoader.fromString(JsonOutput.toJson(json)))
		assertFalse "this instance should NOT be valid against the schema \n${report.toString()}", report.isSuccess()
	}

	void testStructureAddidtionalProperties() {
		def testFailues = []
		def allStructureComponents = ["anchors", "wireEndPoints", "spanPoints", "notePoints", "pointLoads", "damages", "wires", "spanGuys", "guys",
				  					  "equipments", "crossArms", "insulators",  "pushBraces"]
	    def schema = factory.getJsonSchema("schema/spidacalc/calc/structure.schema")
	    def oneOfEverythingFile = new File("resources/examples/spidacalc/designs/one_of_everything.json")

	    // Test the structure object
	    Map json = new JsonSlurper().parse(oneOfEverythingFile)
	    json.put("an additional property", "shouldn't validate")
	    report = schema.validate(JsonLoader.fromString(JsonOutput.toJson(json)))
		assertFalse "this instance should NOT be valid against the schema \n${report.toString()}", report.isSuccess()

	    // Test the pole object
	    json  = new JsonSlurper().parse(oneOfEverythingFile)
		json.get("pole").put("an additional property", "shouldn't validate")
		report = schema.validate(JsonLoader.fromString(JsonOutput.toJson(json)))
		assertFalse "this instance should NOT be valid against the schema \n${report.toString()}", report.isSuccess()

		// Test all of the other properties of the structure
	    allStructureComponents.each { item ->
			json = new JsonSlurper().parse(oneOfEverythingFile)
			json.get(item).get(0).put("an additional property", "shouldn't validate")
			report = schema.validate(JsonLoader.fromString(JsonOutput.toJson(json)))
			if(report.isSuccess()) {
				testFailues << "added an additional property to ${item}, it should not validate but did."
			}
	    }
	    assertEquals "The list should be empty", [], testFailues
	}

	void testLocationImageURLAndLink() {
		def json = new JsonSlurper().parse(new File("resources/examples/spidacalc/projects/full_project.json"))
		def location = json.get("leads").get(0).get("locations").get(0)
		location.get("images").add(["url":"/some/url"])
		location.get("images").add(["url":"/some/url", "link":["source":"FFF","id":"3432432432"]])
		def schema = factory.getJsonSchema("schema/spidacalc/calc/location.schema")
		report = schema.validate(JsonLoader.fromString(JsonOutput.toJson(location)))
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()

		location.get("images").add(["link":["source":"FFF","id":"3432432432"]])
		report = schema.validate(JsonLoader.fromString(JsonOutput.toJson(location)))
		assertFalse "this instance should NOT be valid against the schema \n${report.toString()}", report.isSuccess()
	}

	void testProjectWithInputAssemblies() {
		def schema = factory.getJsonSchema("schema/spidacalc/calc/project.schema")
		report = schema.validate(JsonLoader.fromString(new File("resources/examples/spidacalc/projects/project-with-input-assemblies.json").text))
		report.each{ log.info "testProjectWithGPSObject validation report "+it.toString() }
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()
	}

	void testProjectWithAssemblies() {
		def schema = factory.getJsonSchema("schema/spidacalc/calc/project.schema")
		report = schema.validate(JsonLoader.fromString(new File("resources/examples/spidacalc/projects/project-with-assemblies.json").text))
		report.each{ log.info "testProjectWithGPSObject validation report "+it.toString() }
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()
	}
}
