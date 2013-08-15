package com.spidasoftware.schema.validation

import groovy.json.*
import com.github.fge.jsonschema.util.*
import com.github.fge.jsonschema.main.*
import com.github.fge.jsonschema.uri.*
import com.github.fge.jsonschema.cfg.*
import org.apache.log4j.*
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.*
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.load.SchemaLoader;
import com.github.fge.jsonschema.load.configuration.LoadingConfiguration;
import com.github.fge.jsonschema.load.configuration.LoadingConfigurationBuilder;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.main.JsonSchemaFactoryBuilder;
import com.github.fge.jsonschema.report.ProcessingReport;

class CalcSchemaTest extends GroovyTestCase { 


	def log = Logger.getLogger(this.class);
	def report
	final LoadingConfiguration cfg = LoadingConfiguration.newBuilder().setNamespace(new File("v2/schema").toURI().toString()).freeze();
    final JsonSchemaFactory factory = JsonSchemaFactory.newBuilder().setLoadingConfiguration(cfg).freeze();

	void setUp() {
		// org.apache.log4j.BasicConfigurator.configure();
		// log.setLevel(Level.INFO);
	}

	void testBasicPointObject(){
		def instance = '{"id":"uuid", "distance":{"unit":"FOOT", "value":10}, "direction":0}'				
		def schema = factory.getJsonSchema("spidacalc/calc/point.schema")
		report = schema.validate(JsonLoader.fromString(instance))
		report.each{
			log.info "point using url: "+it.toString()
		}
		assertTrue "the intance itself should be true against a file namespace", report.isSuccess()
	}
	
	void testBasicAttachmentObject(){
		def instance = '{"id":"uuid", "attachmentHeight":{"unit":"FOOT", "value":10}, "owner":{"name":"company", "industry":"COMMUNICATION"}}'				
		def schema = factory.getJsonSchema("spidacalc/calc/attachment.schema")
		report = schema.validate(JsonLoader.fromString(instance))
		report.each{
			log.info "${this.class} using file: "+it.toString()
		}
		assertTrue "the intance itself should be true against a schema", report.isSuccess()		
	}

	void testClientPoleObject(){
		def instance = '{"species":"pine", "height":{"unit":"FOOT", "value":100}, "classOfPole":"h2"}'				
		def schema = factory.getJsonSchema("spidacalc/calc/client_references/pole.schema")
		report = schema.validate(JsonLoader.fromString(instance))
		report.each{
			log.info "${this.class} using file: "+it.toString()
		}
		assertTrue "the intance itself should be true against a schema", report.isSuccess()		
	}

	void testBasicDesignObject(){
		def schema = factory.getJsonSchema("spidacalc/calc/structure.schema")
		report = schema.validate(JsonLoader.fromString(new File("v2/examples/spidacalc/designs/empty_pole.json").text))
		report.each{
			log.info "${this.class} using file: "+it.toString()
		}
		assertTrue "the intance itself should be true against a schema", report.isSuccess()		
	}

	void testOneOfEverythingObject(){
		def schema = factory.getJsonSchema("spidacalc/calc/structure.schema")
		report = schema.validate(JsonLoader.fromString(new File("v2/examples/spidacalc/designs/one_of_everything.json").text))
		report.each{
			log.info "${this.class} using file: "+it.toString()
		}
		assertTrue "the intance itself should be true against a schema", report.isSuccess()		
	}	

	void testBisectorObject(){
		def schema = factory.getJsonSchema("spidacalc/calc/structure.schema")
		report = schema.validate(JsonLoader.fromString(new File("v2/examples/spidacalc/designs/bisector.json").text))
		report.each{
			log.info "${this.class} using file: "+it.toString()
		}
		assertTrue "the intance itself should be true against a schema", report.isSuccess()		
	}
	void testInsulatorObject(){
		def schema = factory.getJsonSchema("spidacalc/calc/structure.schema")
		report = schema.validate(JsonLoader.fromString(new File("v2/examples/spidacalc/designs/insulator.json").text))
		report.each{
			log.info "${this.class} using file: "+it.toString()
		}
		assertTrue "the intance itself should be true against a schema", report.isSuccess()		
	}	

	void testWireObject(){
		def schema = factory.getJsonSchema("spidacalc/calc/structure.schema")
		report = schema.validate(JsonLoader.fromString(new File("v2/examples/spidacalc/designs/wire.json").text))
		report.each{
			log.info "${this.class} using file: "+it.toString()
		}
		assertTrue "the intance itself should be true against a schema", report.isSuccess()		
	}	

	void testXArmObject(){
		def schema = factory.getJsonSchema("spidacalc/calc/structure.schema")
		report = schema.validate(JsonLoader.fromString(new File("v2/examples/spidacalc/designs/xarm.json").text))
		report.each{
			log.info "${this.class} using file: "+it.toString()
		}
		assertTrue "the intance itself should be true against a schema", report.isSuccess()		
	}

	void testProjectWithGPSObject(){
		def schema = factory.getJsonSchema("spidacalc/calc/project.schema")
		report = schema.validate(JsonLoader.fromString(new File("v2/examples/spidacalc/projects/minimal_project_with_gps.json").text))
		report.each{
			log.info "${this.class} using file: "+it.toString()
		}
		assertTrue "the intance itself should be true against a schema", report.isSuccess()		
	}

	void testFullProjectObject(){
		def schema = factory.getJsonSchema("spidacalc/calc/project.schema")
		report = schema.validate(JsonLoader.fromString(new File("v2/examples/spidacalc/projects/full_project.json").text))
		report.each{
			log.info "${this.class} using file: "+it.toString()
		}
		assertTrue "the intance itself should be true against a schema", report.isSuccess()		
	}
	
	void testMinimalProjectObject(){
		def schema = factory.getJsonSchema("spidacalc/calc/project.schema")
		report = schema.validate(JsonLoader.fromString(new File("v2/examples/spidacalc/projects/minimal_project_no_designs.json").text))
		report.each{
			log.info "${this.class} using file: "+it.toString()
		}
		assertTrue "the intance itself should be true against a schema", report.isSuccess()		
	}

}