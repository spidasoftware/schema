package com.spidasoftware.schema.validation

import groovy.json.*
import com.github.fge.jsonschema.util.*
import com.github.fge.jsonschema.main.*
import com.github.fge.jsonschema.uri.*
import com.github.fge.jsonschema.cfg.*
import org.apache.log4j.*
import com.fasterxml.jackson.databind.JsonNode
import com.github.fge.jackson.*
import com.github.fge.jsonschema.exceptions.ProcessingException
import com.github.fge.jsonschema.load.SchemaLoader
import com.github.fge.jsonschema.load.configuration.LoadingConfiguration
import com.github.fge.jsonschema.load.configuration.LoadingConfigurationBuilder
import com.github.fge.jsonschema.main.JsonSchema
import com.github.fge.jsonschema.main.JsonSchemaFactory
import com.github.fge.jsonschema.main.JsonSchemaFactoryBuilder
import com.github.fge.jsonschema.report.ProcessingReport

class CalcSchemaTest extends GroovyTestCase { 


	def report
	def log = Logger.getLogger(this.class)
	final LoadingConfiguration cfg = LoadingConfiguration.newBuilder().setNamespace(new File("resources").toURI().toString()).freeze()
    final JsonSchemaFactory factory = JsonSchemaFactory.newBuilder().setLoadingConfiguration(cfg).freeze()

	void setUp() {
		// org.apache.log4j.BasicConfigurator.configure();
		// log.setLevel(Level.INFO);
	}

	void testBasicPointObject(){
		def instance = '{"id":"uuid", "distance":{"unit":"FOOT", "value":10}, "direction":0}'				
		def schema = factory.getJsonSchema("v1/schema/spidacalc/calc/point.schema")
		report = schema.validate(JsonLoader.fromString(instance))
		report.each{ log.info "validation report "+it.toString() }
		assertTrue "this instance should be valid against the schema", report.isSuccess()		
	}
	
	void testBasicAttachmentObject(){
		def instance = '{"id":"uuid", "attachmentHeight":{"unit":"FOOT", "value":10}, "owner":{"name":"company", "industry":"COMMUNICATION"}}'				
		def schema = factory.getJsonSchema("v1/schema/spidacalc/calc/attachment.schema")
		report = schema.validate(JsonLoader.fromString(instance))
		report.each{ log.info "validation report "+it.toString() }
		assertTrue "this instance should be valid against the schema", report.isSuccess()		
	}

	void testClientPoleObject(){
		def instance = '{"species":"pine", "height":{"unit":"FOOT", "value":100}, "classOfPole":"h2"}'				
		def schema = factory.getJsonSchema("v1/schema/spidacalc/calc/client_references/pole.schema")
		report = schema.validate(JsonLoader.fromString(instance))
		report.each{ log.info "validation report "+it.toString() }
		assertTrue "this instance should be valid against the schema", report.isSuccess()		
	}

	void testBasicDesignObject(){
		def schema = factory.getJsonSchema("v1/schema/spidacalc/calc/structure.schema")
		report = schema.validate(JsonLoader.fromString(new File("resources/v1/examples/spidacalc/designs/empty_pole.json").text))
		report.each{ log.info "validation report "+it.toString() }
		assertTrue "this instance should be valid against the schema", report.isSuccess()		
	}

	void testOneOfEverythingObject(){
		def schema = factory.getJsonSchema("v1/schema/spidacalc/calc/structure.schema")
		report = schema.validate(JsonLoader.fromString(new File("resources/v1/examples/spidacalc/designs/one_of_everything.json").text))
		report.each{ log.info "validation report "+it.toString() }
		assertTrue "this instance should be valid against the schema", report.isSuccess()		
	}	

	void testBisectorObject(){
		def schema = factory.getJsonSchema("v1/schema/spidacalc/calc/structure.schema")
		report = schema.validate(JsonLoader.fromString(new File("resources/v1/examples/spidacalc/designs/bisector.json").text))
		report.each{ log.info "validation report "+it.toString() }
		assertTrue "this instance should be valid against the schema", report.isSuccess()		
	}
	void testInsulatorObject(){
		def schema = factory.getJsonSchema("v1/schema/spidacalc/calc/structure.schema")
		report = schema.validate(JsonLoader.fromString(new File("resources/v1/examples/spidacalc/designs/insulator.json").text))
		report.each{ log.info "validation report "+it.toString() }
		assertTrue "this instance should be valid against the schema", report.isSuccess()		
	}	

	void testWireObject(){
		def schema = factory.getJsonSchema("v1/schema/spidacalc/calc/structure.schema")
		report = schema.validate(JsonLoader.fromString(new File("resources/v1/examples/spidacalc/designs/wire.json").text))
		report.each{ log.info "validation report "+it.toString() }
		assertTrue "this instance should be valid against the schema", report.isSuccess()		
	}	

	void testXArmObject(){
		def schema = factory.getJsonSchema("v1/schema/spidacalc/calc/structure.schema")
		report = schema.validate(JsonLoader.fromString(new File("resources/v1/examples/spidacalc/designs/xarm.json").text))
		report.each{ log.info "validation report "+it.toString() }
		assertTrue "this instance should be valid against the schema", report.isSuccess()		
	}

	void testProjectWithGPSObject(){
		def schema = factory.getJsonSchema("v1/schema/spidacalc/calc/project.schema")
		report = schema.validate(JsonLoader.fromString(new File("resources/v1/examples/spidacalc/projects/minimal_project_with_gps.json").text))
		report.each{ log.info "validation report "+it.toString() }
		assertTrue "this instance should be valid against the schema", report.isSuccess()		
	}

	void testFullProjectObject(){
		def schema = factory.getJsonSchema("v1/schema/spidacalc/calc/project.schema")
		report = schema.validate(JsonLoader.fromString(new File("resources/v1/examples/spidacalc/projects/full_project.json").text))
		report.each{ log.info "validation report "+it.toString() }
		assertTrue "this instance should be valid against the schema", report.isSuccess()		
	}
	
	void testMinimalProjectObject(){
		def schema = factory.getJsonSchema("v1/schema/spidacalc/calc/project.schema")
		report = schema.validate(JsonLoader.fromString(new File("resources/v1/examples/spidacalc/projects/minimal_project_no_designs.json").text))
		report.each{ log.info "validation report "+it.toString() }
		assertTrue "this instance should be valid against the schema", report.isSuccess()		
	}

	void testMultipleProjects(){
		def schema = factory.getJsonSchema("v1/schema/spidacalc/calc/projects.schema")
		report = schema.validate(JsonLoader.fromString(new File("resources/v1/examples/spidacalc/projects/multiple_projects.json").text))
		report.each{ log.info "validation report "+it.toString() }
		assertTrue "this instance should be valid against the schema", report.isSuccess()		
	}

}