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

class ServiceSchemaTest extends GroovyTestCase { 


	def log = Logger.getLogger(this.class);
	def report
	final LoadingConfiguration cfg = LoadingConfiguration.newBuilder().setNamespace(new File("v2/schema").toURI().toString()).freeze();
  	final JsonSchemaFactory factory = JsonSchemaFactory.newBuilder().setLoadingConfiguration(cfg).freeze();
	def schema = factory.getJsonSchema("general/service_method.schema")
	def slurper = new JsonSlurper()

	void setUp() {
		// org.apache.log4j.BasicConfigurator.configure();
		// log.setLevel(Level.INFO);
	}

	void testAssetInterfaceMethodsAgainstServiceMethod(){
		def instance = slurper.parseText(new File("v2/schema/spidamin/asset/interfaces/asset.json").text)
		instance.each{k,v->
			if(k!="id" && k!="description"){
				report = schema.validate(JsonLoader.fromString(JsonOutput.toJson(v)))
				report.each{
					log.info "${this.class} using file: "+it.toString()
				}
				assertTrue "${k} should be true against a schema", report.isSuccess()		
			}
		}
	}

	void testAssetSearchInterfaceMethodsAgainstServiceMethod(){
		def instance = slurper.parseText(new File("v2/schema/spidamin/asset/interfaces/asset_search.json").text)
		instance.each{k,v->
			if(k!="id" && k!="description"){
				report = schema.validate(JsonLoader.fromString(JsonOutput.toJson(v)))
				report.each{
					log.info "${this.class} using file: "+it.toString()
				}
				assertTrue "${k} should be true against a schema", report.isSuccess()		
			}
		}
	}	
	void testGeoCoderInterfaceMethodsAgainstServiceMethod(){
		def instance = slurper.parseText(new File("v2/schema/spidamin/geo/interfaces/geo_coder.json").text)
		instance.each{k,v->
			if(k!="id" && k!="description"){
				report = schema.validate(JsonLoader.fromString(JsonOutput.toJson(v)))
				report.each{
					log.info "${this.class} using file: "+it.toString()
				}
				assertTrue "${k} should be true against a schema", report.isSuccess()		
			}
		}
	}
	void testUsersInterfaceMethodsAgainstServiceMethod(){
		def instance = slurper.parseText(new File("v2/schema/spidamin/user/interfaces/users.json").text)
		instance.each{k,v->
			if(k!="id" && k!="description"){
				report = schema.validate(JsonLoader.fromString(JsonOutput.toJson(v)))
				report.each{
					log.info "${this.class} using file: "+it.toString()
				}
				assertTrue "${k} should be true against a schema", report.isSuccess()		
			}
		}
	}
	void testUsersSecurityInterfaceMethodsAgainstServiceMethod(){
		def instance = slurper.parseText(new File("v2/schema/spidamin/user/interfaces/users_security.json").text)
		instance.each{k,v->
			if(k!="id" && k!="description"){
				report = schema.validate(JsonLoader.fromString(JsonOutput.toJson(v)))
				report.each{
					log.info "${this.class} using file: "+it.toString()
				}
				assertTrue "${k} should be true against a schema", report.isSuccess()		
			}
		}
	}	
	void testWireAnalysisInterfaceMethodsAgainstServiceMethod(){
		def instance = slurper.parseText(new File("v2/schema/spidacalc/analysis/interfaces/wire_analysis.json").text)
		instance.each{k,v->
			if(k!="id" && k!="description"){
				report = schema.validate(JsonLoader.fromString(JsonOutput.toJson(v)))
				report.each{
					log.info "${this.class} using file: "+it.toString()
				}
				assertTrue "${k} should be true against a schema", report.isSuccess()		
			}
		}
	}	
	void testLoadingAnalysisInterfaceMethodsAgainstServiceMethod(){
		def instance = slurper.parseText(new File("v2/schema/spidacalc/analysis/interfaces/loading_analysis.json").text)
		instance.each{k,v->
			if(k!="id" && k!="description"){
				report = schema.validate(JsonLoader.fromString(JsonOutput.toJson(v)))
				report.each{
					log.info "${this.class} using file: "+it.toString()
				}
				assertTrue "${k} should be true against a schema", report.isSuccess()		
			}
		}
	}
	void testClientDataInterfaceMethodsAgainstServiceMethod(){
		def instance = slurper.parseText(new File("v2/schema/spidacalc/client/interfaces/client_data.json").text)
		instance.each{k,v->
			if(k!="id" && k!="description"){
				report = schema.validate(JsonLoader.fromString(JsonOutput.toJson(v)))
				report.each{
					log.info "${this.class} using file: "+it.toString()
				}
				assertTrue "${k} should be true against a schema", report.isSuccess()		
			}
		}
	}
}

   