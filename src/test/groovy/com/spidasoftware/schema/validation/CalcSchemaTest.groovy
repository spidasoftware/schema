package com.spidasoftware.schema.validation

import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.load.configuration.LoadingConfiguration
import com.github.fge.jsonschema.main.JsonSchemaFactory
import com.github.fge.jsonschema.uri.*
import net.sf.json.JSONObject
import org.apache.log4j.Logger;

class CalcSchemaTest extends GroovyTestCase { 

	def report
	def log = Logger.getLogger(this.class)
	final LoadingConfiguration cfg = LoadingConfiguration.newBuilder().setNamespace(new File("resources").toURI().toString()).freeze()
    final JsonSchemaFactory factory = JsonSchemaFactory.newBuilder().setLoadingConfiguration(cfg).freeze()

	void testProjectWithForms(){
		def schema = factory.getJsonSchema("v1/schema/spidacalc/calc/project.schema")
		report = schema.validate(JsonLoader.fromString(new File("resources/v1/examples/spidacalc/projects/minimal_project_with_forms.json").text))
		report.each{ log.info "testProjectWithGPSObject validation report "+it.toString() }
		println report.toString()
		assertTrue "this instance should be valid against the schema", report.isSuccess()
	}

	void testClientPoleObject(){
		def instance = '{"species":"pine", "height":{"unit":"FOOT", "value":100}, "classOfPole":"h2"}'				
		def schema = factory.getJsonSchema("v1/schema/spidacalc/calc/client_references/pole.schema")
		report = schema.validate(JsonLoader.fromString(instance))
		report.each{ log.info "testClientPoleObject validation report "+it.toString() }
		assertTrue "this instance should be valid against the schema", report.isSuccess()		
	}

	void testBasicDesignObject(){
		def schema = factory.getJsonSchema("v1/schema/spidacalc/calc/structure.schema")
		report = schema.validate(JsonLoader.fromString(new File("resources/v1/examples/spidacalc/designs/empty_pole.json").text))
		report.each{ log.info "testBasicDesignObject validation report "+it.toString() }
		assertTrue "this instance should be valid against the schema", report.isSuccess()		
	}

	void testOneOfEverythingObject(){
		def schema = factory.getJsonSchema("v1/schema/spidacalc/calc/structure.schema")
		report = schema.validate(JsonLoader.fromString(new File("resources/v1/examples/spidacalc/designs/one_of_everything.json").text))
		report.each{ log.info "testOneOfEverythingObject validation report "+it.toString() }
		assertTrue "this instance should be valid against the schema", report.isSuccess()
	}

	void testDamagesObject(){
		def schema = factory.getJsonSchema("v1/schema/spidacalc/calc/structure.schema")
		report = schema.validate(JsonLoader.fromString(new File("resources/v1/examples/spidacalc/designs/damages.json").text))
		report.each{ log.info "testOneOfEverythingObject validation report "+it.toString() }
		assertTrue "this instance should be valid against the schema", report.isSuccess()
	}

	void testBisectorObject(){
		def schema = factory.getJsonSchema("v1/schema/spidacalc/calc/structure.schema")
		report = schema.validate(JsonLoader.fromString(new File("resources/v1/examples/spidacalc/designs/bisector.json").text))
		report.each{ log.info "testBisectorObject validation report "+it.toString() }
		assertTrue "this instance should be valid against the schema", report.isSuccess()		
	}
	void testInsulatorObject(){
		def schema = factory.getJsonSchema("v1/schema/spidacalc/calc/structure.schema")
		report = schema.validate(JsonLoader.fromString(new File("resources/v1/examples/spidacalc/designs/insulator.json").text))
		report.each{ log.info "testInsulatorObject validation report "+it.toString() }
		assertTrue "this instance should be valid against the schema", report.isSuccess()		
	}	

	void testWireObject(){
		def schema = factory.getJsonSchema("v1/schema/spidacalc/calc/structure.schema")
		report = schema.validate(JsonLoader.fromString(new File("resources/v1/examples/spidacalc/designs/wire.json").text))
		report.each{ log.info "testWireObject validation report "+it.toString() }
		assertTrue "this instance should be valid against the schema", report.isSuccess()		
	}	

	void testXArmObject(){
		def schema = factory.getJsonSchema("v1/schema/spidacalc/calc/structure.schema")
		report = schema.validate(JsonLoader.fromString(new File("resources/v1/examples/spidacalc/designs/xarm.json").text))
		report.each{ log.info "testXArmObject validation report "+it.toString() }
		assertTrue "this instance should be valid against the schema", report.isSuccess()		
	}

	void testProjectWithGPSObject(){
		def schema = factory.getJsonSchema("v1/schema/spidacalc/calc/project.schema")
		report = schema.validate(JsonLoader.fromString(new File("resources/v1/examples/spidacalc/projects/minimal_project_with_gps.json").text))
		report.each{ log.info "testProjectWithGPSObject validation report "+it.toString() }
		assertTrue "this instance should be valid against the schema", report.isSuccess()		
	}

	void testFullProjectObject(){
		def schema = factory.getJsonSchema("v1/schema/spidacalc/calc/project.schema")
		report = schema.validate(JsonLoader.fromString(new File("resources/v1/examples/spidacalc/projects/full_project.json").text))
		report.each{ log.info "testFullProjectObject validation report "+it.toString() }
		assertTrue "this instance should be valid against the schema", report.isSuccess()	
	}
	
	void testMinimalProjectObject(){
		def schema = factory.getJsonSchema("v1/schema/spidacalc/calc/project.schema")
		report = schema.validate(JsonLoader.fromString(new File("resources/v1/examples/spidacalc/projects/minimal_project_no_designs.json").text))
		report.each{ log.info "testMinimalProjectObject validation report "+it.toString() }
		assertTrue "this instance should be valid against the schema", report.isSuccess()		
	}

	void testMultipleProjects(){
		def schema = factory.getJsonSchema("v1/schema/spidacalc/calc/projects.schema")
		report = schema.validate(JsonLoader.fromString(new File("resources/v1/examples/spidacalc/projects/multiple_projects.json").text))
		report.each{ log.info "testMultipleProjects validation report "+it.toString() }
		assertTrue "this instance should be valid against the schema", report.isSuccess()		
	}

	void testProjectAdditionalProperties() {
		def schema = factory.getJsonSchema("v1/schema/spidacalc/calc/project.schema")
		JSONObject json = JSONObject.fromObject(new File("resources/v1/examples/spidacalc/projects/full_project.json").text)
		json.put("an additional property", "shouldn't validate")
		report = schema.validate(JsonLoader.fromString(json.toString()))
		assertFalse "this instance should be valid against the schema", report.isSuccess()	

		json = JSONObject.fromObject(new File("resources/v1/examples/spidacalc/projects/full_project.json").text)
		json.getJSONArray("leads").getJSONObject(0).put("an additional property", "shouldn't validate")
		report = schema.validate(JsonLoader.fromString(json.toString()))
		assertFalse "this instance should be valid against the schema", report.isSuccess()	

		json = JSONObject.fromObject(new File("resources/v1/examples/spidacalc/projects/full_project.json").text)
		json.getJSONArray("leads").getJSONObject(0).getJSONArray("locations").getJSONObject(0).put("an additional property", "shouldn't validate")
		report = schema.validate(JsonLoader.fromString(json.toString()))
		assertFalse "this instance should be valid against the schema", report.isSuccess()	

		def testFailues = []
		def locationComponents = ["geographicCoordinate", "remedies", "poleTags", "images"]
		locationComponents.each { component ->
			json = JSONObject.fromObject(new File("resources/v1/examples/spidacalc/projects/full_project.json").text)
			if(component == "geographicCoordinate") {
				json.getJSONArray("leads").getJSONObject(0).getJSONArray("locations").getJSONObject(0).getJSONObject(component).put("an additional property", "shouldn't validate")
			} else {
				json.getJSONArray("leads").getJSONObject(0).getJSONArray("locations").getJSONObject(0).getJSONArray(component).getJSONObject(0).put("an additional property", "shouldn't validate")	
			}
			report = schema.validate(JsonLoader.fromString(json.toString()))
			if(report.isSuccess()) {
				testFailues << "added an additional property to ${component}, it should not validate but did."	
			}
		}
		assertEquals "The list should be empty, if it is not there are failures", [], testFailues

		json = JSONObject.fromObject(new File("resources/v1/examples/spidacalc/projects/full_project.json").text)
		json.getJSONArray("leads").getJSONObject(0).getJSONArray("locations").getJSONObject(0).getJSONArray("designs").getJSONObject(0).put("an additional property", "shouldn't validate")
		report = schema.validate(JsonLoader.fromString(json.toString()))
		assertFalse "this instance should be valid against the schema", report.isSuccess()	

		json = JSONObject.fromObject(new File("resources/v1/examples/spidacalc/projects/full_project.json").text)
		json.getJSONArray("leads").getJSONObject(0).getJSONArray("locations").getJSONObject(0).getJSONArray("designs").getJSONObject(0).getJSONArray("analysis").getJSONObject(0).put("an additional property", "shouldn't validate")
		report = schema.validate(JsonLoader.fromString(json.toString()))
		assertFalse "this instance should be valid against the schema", report.isSuccess()			
	}

	void testStructureAddidtionalProperties() {
		def testFailues = []
		def allStructureComponents = ["anchors", "wireEndPoints", "spanPoints", "notePoints", "pointLoads", "damages", "wires", "spanGuys", "guys",
				  					  "equipments", "crossArms", "insulators",  "pushBraces"]
	    def schema = factory.getJsonSchema("v1/schema/spidacalc/calc/structure.schema") 
	    def oneOfEverythingText = new File("resources/v1/examples/spidacalc/designs/one_of_everything.json").text
	    
	    // Test the structure object
	    JSONObject json = JSONObject.fromObject(oneOfEverythingText) 
	    json.put("an additional property", "shouldn't validate")
	    report = schema.validate(JsonLoader.fromString(json.toString()))
	    assertFalse "Added an additional property to the structure, it should not validate", report.isSuccess() 

	    // Test the pole object
	    json = JSONObject.fromObject(oneOfEverythingText)
		json.getJSONObject("pole").put("an additional property", "shouldn't validate")
		report = schema.validate(JsonLoader.fromString(json.toString()))
		assertFalse "Added an additional property to the pole, it should not validate", report.isSuccess() 

		// Test all of the other properties of the structure
	    allStructureComponents.each { item ->
			json = JSONObject.fromObject(oneOfEverythingText)
			json.getJSONArray(item).getJSONObject(0).put("an additional property", "shouldn't validate")
			report = schema.validate(JsonLoader.fromString(json.toString()))
			if(report.isSuccess()) {
				testFailues << "added an additional property to ${item}, it should not validate but did."
			}		
	    }
	    assertEquals "The list should be empty", [], testFailues
	}

	void testLocationImageURLAndLink() {
		def json = JSONObject.fromObject(new File("resources/v1/examples/spidacalc/projects/full_project.json").text)
		def location = json.getJSONArray("leads").getJSONObject(0).getJSONArray("locations").getJSONObject(0)
		location.getJSONArray("images").add(new JSONObject(["url":"/some/url"]))
		location.getJSONArray("images").add(new JSONObject(["url":"/some/url", "link":["source":"FFF","id":"3432432432"]]))	
		def schema = factory.getJsonSchema("v1/schema/spidacalc/calc/location.schema")
		report = schema.validate(JsonLoader.fromString(location.toString()))
		assertTrue "Valid images added, should validate", report.isSuccess() 

		location.getJSONArray("images").add(new JSONObject(["link":["source":"FFF","id":"3432432432"]]))
		report = schema.validate(JsonLoader.fromString(location.toString()))
		assertFalse "Invalid image added, url is required should not validate", report.isSuccess() 	
	}
}