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

class AnalysisSchemaTest extends GroovyTestCase { 


	def log = Logger.getLogger(this.class);
	def report
	//private static final SyntaxValidator schemaValidator = new SyntaxValidator(ValidationConfiguration.byDefault());

	void setUp() {
		log.setLevel(Level.INFO);
	}

	void testBasicAnalysisDesignObject(){
		org.apache.log4j.BasicConfigurator.configure();
 		final LoadingConfiguration cfg = LoadingConfiguration.newBuilder().setNamespace(new File("v2/schema").toURI().toString()).freeze();
    	final JsonSchemaFactory factory = JsonSchemaFactory.newBuilder().setLoadingConfiguration(cfg).freeze();
		def schema = factory.getJsonSchema("spidacalc/analysis/structure.schema")
		report = schema.validate(JsonLoader.fromString(new File("v2/examples/spidacalc/analysis/empty_pole.json").text))
		report.each{
			log.info "${this.class} using file: "+it.toString()
		}
		assertTrue "the intance itself should be true against a schema", report.isSuccess()		
	}

//NEED TO FIX THIS ONE
// these are commented out until we decide how we want to handle duality of referenced and dereferenced client items.

//	void testAnalysisAnchorObject(){
//
//		def instance = '{"clientItem": {"size": "Big Anchor", "strength": {"unit": "POUND_FORCE", "value": 6000 } }, "direction": 81, "distance": {"unit": "FOOT", "value": 10 }, "guys": [], "height": {"unit": "FOOT", "value": 0 }, "id": "Anchor#1", "owner": {"industry": "UTILITY", "name": "Acme Power", "uuid": "43e130a1-3c21-41c9-b420-0e5b966eb2f2"}, "supportType": "Other", "uuid": "32850af8-5aa8-4292-a638-41406877609c"}'
// 		final LoadingConfiguration cfg = LoadingConfiguration.newBuilder().setNamespace(new File("v2/schema").toURI().toString()).freeze();
//    	final JsonSchemaFactory factory = JsonSchemaFactory.newBuilder().setLoadingConfiguration(cfg).freeze();
//		def schema = factory.getJsonSchema("spidacalc/analysis/anchor.schema")
//		report = schema.validate(JsonLoader.fromString(instance))
//		report.each{
//			log.info "${this.class} using file: "+it.toString()
//		}
//		assertTrue "the intance itself should be true against a schema", report.isSuccess()
//	}

//	void testAnalysisDesignWithAnchorObject(){
// 		final LoadingConfiguration cfg = LoadingConfiguration.newBuilder().setNamespace(new File("v2/schema").toURI().toString()).freeze();
//    	final JsonSchemaFactory factory = JsonSchemaFactory.newBuilder().setLoadingConfiguration(cfg).freeze();
//		def schema = factory.getJsonSchema("spidacalc/analysis/structure.schema")
//		report = schema.validate(JsonLoader.fromString(new File("v2/examples/spidacalc/analysis/pole_with_anchor.json").text))
//		report.each{
//			log.info "${this.class} using file: "+it.toString()
//		}
//		assertTrue "the intance itself should be true against a schema", report.isSuccess()
//	}
}