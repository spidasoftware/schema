package com.spidasoftware.schema.validation

import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.cfg.ValidationConfiguration
import com.github.fge.jsonschema.processors.syntax.SyntaxValidator
import com.github.fge.jsonschema.uri.*
import org.apache.log4j.Logger

class SchemaValidationTest extends GroovyTestCase {


	def log = Logger.getLogger(this.class)
	def report
	private static final SyntaxValidator schemaValidator = new SyntaxValidator(ValidationConfiguration.byDefault())

	void setUp() {
	}

  	void testSchemasAreValid(){
  		new File("resources/schema").eachFileRecurse(groovy.io.FileType.FILES){ schemaFile ->
  			if(schemaFile.absolutePath.endsWith(".schema")){
				log.info "Loading Json from ${schemaFile.absolutePath}"
				def schemaNode = JsonLoader.fromPath(schemaFile.absolutePath)
				report = schemaValidator.validateSchema(schemaNode)
				assertTrue "this schema should be valid \n${report.toString()}", report.isSuccess()
  			}
  		}
	}


}
