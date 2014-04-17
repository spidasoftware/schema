package com.spidasoftware.schema.validation

import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.load.configuration.LoadingConfiguration
import com.github.fge.jsonschema.main.JsonSchemaFactory
import com.github.fge.jsonschema.uri.*
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.apache.log4j.Logger;

class ServiceSchemaTest extends GroovyTestCase { 


	def log = Logger.getLogger(this.class);
	def report
	final LoadingConfiguration cfg = LoadingConfiguration.newBuilder().setNamespace(new File("resources/v1/schema").toURI().toString()).freeze();
  	final JsonSchemaFactory factory = JsonSchemaFactory.newBuilder().setLoadingConfiguration(cfg).freeze();
	def schema = factory.getJsonSchema("general/service_method.schema")
	def slurper = new JsonSlurper()

	void setUp() {
	}

	void testAssetInterfaceMethodsAgainstServiceMethod(){
		def instance = slurper.parseText(new File("resources/v1/schema/spidamin/asset/interfaces/asset.json").text)
		instance.each{k,v->
			if(k!="id" && k!="description"){
				report = schema.validate(JsonLoader.fromString(JsonOutput.toJson(v)))
				report.each{
					log.info "${this.class} using file: "+it.toString()
				}
				assertTrue "${k} should be valid against a schema", report.isSuccess()		
			}
		}
	}

	void testAssetSearchInterfaceMethodsAgainstServiceMethod(){
		def instance = slurper.parseText(new File("resources/v1/schema/spidamin/asset/interfaces/asset_search.json").text)
		instance.each{k,v->
			if(k!="id" && k!="description"){
				report = schema.validate(JsonLoader.fromString(JsonOutput.toJson(v)))
				report.each{
					log.info "${this.class} using file: "+it.toString()
				}
				assertTrue "${k} should be valid against a schema", report.isSuccess()		
			}
		}
	}	
	
	void testGeoCoderInterfaceMethodsAgainstServiceMethod(){
		def instance = slurper.parseText(new File("resources/v1/schema/spidamin/geo/interfaces/geo_coder.json").text)
		instance.each{k,v->
			if(k!="id" && k!="description"){
				report = schema.validate(JsonLoader.fromString(JsonOutput.toJson(v)))
				report.each{
					log.info "${this.class} using file: "+it.toString()
				}
				assertTrue "${k} should be valid against a schema", report.isSuccess()		
			}
		}
	}

	void testUsersInterfaceMethodsAgainstServiceMethod(){
		def instance = slurper.parseText(new File("resources/v1/schema/spidamin/user/interfaces/users.json").text)
		instance.each{k,v->
			if(k!="id" && k!="description"){
				report = schema.validate(JsonLoader.fromString(JsonOutput.toJson(v)))
				report.each{
					log.info "${this.class} using file: "+it.toString()
				}
				assertTrue "${k} should be valid against a schema", report.isSuccess()		
			}
		}
	}

	void testClientDataInterfaceMethodsAgainstServiceMethod(){
		def instance = slurper.parseText(new File("resources/v1/schema/spidacalc/client/interfaces/client_data.json").text)
		instance.each{k,v->
			if(k!="id" && k!="description"){
				report = schema.validate(JsonLoader.fromString(JsonOutput.toJson(v)))
				report.each{
					log.info "${this.class} using file: "+it.toString()
				}
				assertTrue "${k} should be valid against a schema", report.isSuccess()		
			}
		}
	}

	void testAssetCreationMethodsAgainstServiceMethod() {
		def instance = slurper.parseText(new File("resources/v1/schema/spidamin/asset/interfaces/asset_creation.json").text)
		instance.each{k,v->
			if(k!="id" && k!="description"){
				report = schema.validate(JsonLoader.fromString(JsonOutput.toJson(v)))
				report.each{
					log.info "${this.class} using file: "+it.toString()
				}
				assertTrue "${k} should be valid against a schema", report.isSuccess()		
			}
		}
	}
	
	void testAssetFileMethodsAgainstServiceMethod() {
		def instance = slurper.parseText(new File("resources/v1/schema/spidamin/asset/interfaces/asset_file.json").text)
		instance.each{k,v->
			if(k!="id" && k!="description"){
				report = schema.validate(JsonLoader.fromString(JsonOutput.toJson(v)))
				report.each{
					log.info "${this.class} using file: "+it.toString()
				}
				assertTrue "${k} should be valid against a schema", report.isSuccess()		
			}
		}
	}
}
