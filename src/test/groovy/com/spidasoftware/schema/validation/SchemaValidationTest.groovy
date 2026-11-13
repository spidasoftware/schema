/*
 * Copyright (c) 2026 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.validation

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.networknt.schema.JsonMetaSchema
import com.networknt.schema.JsonSchema
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.SchemaLocation
import com.networknt.schema.SpecVersion

class SchemaValidationTest extends GroovyTestCase {

	def report
	final JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4)
	JsonSchema metaSchema = factory.getSchema(SchemaLocation.of(JsonMetaSchema.v4.getIri()))

	static JsonNode fromFile(File path) {
		return new ObjectMapper().readValue(path, JsonNode)
	}
	void setUp() {
	}

  	void testSchemasAreValid(){
  		new File("resources/schema").eachFileRecurse(groovy.io.FileType.FILES){ schemaFile ->
  			if(schemaFile.absolutePath.endsWith(".schema")){
				log.info "Loading Json from ${schemaFile.absolutePath}"
				def schemaNode = fromFile(schemaFile)
				report = metaSchema.validate(schemaNode)
				assertTrue "this schema should be valid \n${report.toListString()}", report.isEmpty()
  			}
  		}
	}
}
