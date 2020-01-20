/*
 * ©2009-2019 SPIDAWEB LLC
 */
package com.spidasoftware.schema.validation

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.networknt.schema.JsonSchema
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.SpecVersion
import com.networknt.schema.ValidationMessage
import spock.lang.Shared
import spock.lang.Specification

/**
 * This class is a failed attempt to reproduce the validation errors related to the order of items
 * in an array with item types defined by "oneOf".  Maybe the schemas need to be external/files instead of inline strings?
 */
class NetworkNtOneOfOrderTest extends Specification {

	@Shared
	JsonSchemaFactory jsonSchemaFactory

	def setupSpec() {
		jsonSchemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4)
	}

	def "oneOf test 1"() {
		setup:
			String schemaText = """{
  "type": "object",
  "oneOf": [
    {
      "type": "object",
      "required": ["name"],
      "properties": {
        "name": {
          "type": "string",
          "pattern": "^Name1\$"
        }
      }
    },
    {
      "type": "object",
      "required": ["name"],
      "properties": {
        "name": {
          "type": "string",
          "pattern": "^Name2\$"
        }
      }
    }
  ]
}"""

			String name1json = '{ "name": "Name1" }'
			String name2json = '{ "name": "Name2" }'

			JsonNode schemaNode = (new ObjectMapper()).readTree(schemaText)
			JsonSchema schema = jsonSchemaFactory.getSchema(schemaNode)

			Set<ValidationMessage> validationMessages

		when:
			validationMessages = schema.validate((new ObjectMapper()).readTree(name1json))
		then:
			validationMessages.empty

		when:
			validationMessages = schema.validate((new ObjectMapper()).readTree(name2json))
		then:
			validationMessages.empty
	}

	def "oneOf test 2"() {
		setup:
			String schemaText = """{
  "type": "object",
  "oneOf": [
    {
      "type": "object",
      "required": ["foo"],
      "properties": {
        "foo": {
          "type": "string"
        }
      }
    },
    {
      "type": "object",
      "required": ["bar"],
      "properties": {
        "bar": {
          "type": "string"
        }
      }
    }
  ]
}"""

			String name1json = '{ "foo": "Name1" }'
			String name2json = '{ "bar": "Name2" }'

			JsonNode schemaNode = (new ObjectMapper()).readTree(schemaText)
			JsonSchema schema = jsonSchemaFactory.getSchema(schemaNode)

			Set<ValidationMessage> validationMessages

		when:
			validationMessages = schema.validate((new ObjectMapper()).readTree(name1json))
		then:
			validationMessages.empty

		when:
			validationMessages = schema.validate((new ObjectMapper()).readTree(name2json))
		then:
			validationMessages.empty
	}

	def "oneOf test 3"() {
		setup:
			String schemaText = """{
  "type": "array",
  "items": {
	  "oneOf": [
		{
		  "type": "object",
		  "required": ["foo"],
		  "properties": {
			"foo": {
			  "type": "string"
			}
		  }
		},
		{
		  "type": "object",
		  "required": ["bar"],
		  "properties": {
			"bar": {
			  "type": "string"
			}
		  }
		}
	  ]
	}
}"""

			String name1json = '[{ "foo": "Name1" }, { "bar": "Name2" }]'
			String name2json = '[{ "bar": "Name2" }, { "foo": "Name1" }]'

			JsonNode schemaNode = (new ObjectMapper()).readTree(schemaText)
			JsonSchema schema = jsonSchemaFactory.getSchema(schemaNode)

			Set<ValidationMessage> validationMessages

		when:
			validationMessages = schema.validate((new ObjectMapper()).readTree(name1json))
		then:
			validationMessages.empty

		when:
			validationMessages = schema.validate((new ObjectMapper()).readTree(name2json))
		then:
			validationMessages.empty
	}

	def "oneOf test 4"() {
		setup:
			String schemaText = """{
  "type": "array",
  "items": {
	"type": "object", 
		"required": ["foobar"],
		"properties": {
			"foobar": {
				"oneOf": [
						{
							"type": "object",
							"required": ["foo"],
							"properties": {
								"foo": {
									"type": "string"
								}
							}
						},
						{
							"type": "object",
							"required": ["bar"],
							"properties": {
								"bar": {
									"type": "string"
								}
							}
						}
				]
			}
		}
	}
}"""

			String name1json = '[ { "foobar": { "foo": "Name1" } }, { "foobar": { "bar": "Name2" } }]'
			String name2json = '[ { "foobar": { "bar": "Name2" } }, { "foobar": { "foo": "Name1" } }]'

			JsonNode schemaNode = (new ObjectMapper()).readTree(schemaText)
			JsonSchema schema = jsonSchemaFactory.getSchema(schemaNode)

			Set<ValidationMessage> validationMessages

		when:
			validationMessages = schema.validate((new ObjectMapper()).readTree(name1json))
		then:
			validationMessages.empty

		when:
			validationMessages = schema.validate((new ObjectMapper()).readTree(name2json))
		then:
			validationMessages.empty
	}

	def "oneOf test 5"() {
		setup:
			String schemaText = """{
	  "definitions": {
		"fooType": {
		  "type": "object",
		  "required": ["foo"],
		  "properties": {
			"foo": {
			  "type": "string"
			}
		  }
		},
		"barType": {
		  "type": "object",
		  "required": ["bar"],
		  "properties": {
			"bar": {
			  "type": "string"
			}
		  }
		}
	  },
		
	  "type": "array",
	  "items": { 
		"type": "object",
		"required": ["foobar"],
		"properties": {
		  "foobar": {
			"oneOf": [
			  { "\$ref": "#/definitions/fooType" },
			  { "\$ref": "#/definitions/barType" }
			]
		  }
		}
	  }
	}"""

			String name1json = '[ { "foobar": { "foo": "Name1" } }, { "foobar": { "bar": "Name2" } }]'
			String name2json = '[ { "foobar": { "bar": "Name2" } }, { "foobar": { "foo": "Name1" } }]'

			JsonNode schemaNode = (new ObjectMapper()).readTree(schemaText)
			JsonSchema schema = jsonSchemaFactory.getSchema(schemaNode)

			Set<ValidationMessage> validationMessages

		when:
			validationMessages = schema.validate((new ObjectMapper()).readTree(name1json))
		then:
			validationMessages.empty

		when:
			validationMessages = schema.validate((new ObjectMapper()).readTree(name2json))
		then:
			validationMessages.empty
	}

	def "oneOf test 6"() {
		setup:
			String schemaText = """{
  "definitions": {
    "fooType": {
      "type": "object",
      "required": ["foo"],
      "properties": {
        "foo": {
          "type": "string"
        }
      }
    },
    "barType": {
      "type": "object",
      "required": ["bar"],
      "properties": {
        "bar": {
          "type": "string"
        }
      }
    }
  },
    
  "type": "array",
  "items": { 
        "oneOf": [
          { "\$ref": "#/definitions/fooType" },
          { "\$ref": "#/definitions/barType" }
        ]
      }
}"""

			String name1json = '[ { "foo": "Name1" }, { "bar": "Name2" } ]'
			String name2json = '[ { "bar": "Name2" }, { "foo": "Name1" } ]'

			JsonNode schemaNode = (new ObjectMapper()).readTree(schemaText)
			JsonSchema schema = jsonSchemaFactory.getSchema(schemaNode)

			Set<ValidationMessage> validationMessages

		when:
			validationMessages = schema.validate((new ObjectMapper()).readTree(name1json))
		then:
			validationMessages.empty

		when:
			validationMessages = schema.validate((new ObjectMapper()).readTree(name2json))
		then:
			validationMessages.empty
	}

	def "oneOf test 7"() {
		setup:
			String schemaText = """{
  "definitions": {
    "fooType": {
      "type": "object",
      "required": ["foo"],
      "properties": {
        "foo": {
          "type": "string"
        }
      }
    },
    "barType": {
      "type": "object",
      "required": ["bar"],
      "properties": {
        "bar": {
          "type": "string"
        }
      }
    }
  },
    
  "type": "object",
  "properties": {
    "name": {
      "type": "string"
    },
    "foobars": {
      "type": "array",
      "items": { 
        "oneOf": [
          { "\$ref": "#/definitions/fooType" },
          { "\$ref": "#/definitions/barType" }
        ]
      }
    }
  }
}"""

			String name1json = '{ "name": "asdf", "foobars": [ { "foo": "Name1" }, { "bar": "Name2" } ] }'
			String name2json = '{ "name": "asdf", "foobars": [ { "bar": "Name2" }, { "foo": "Name1" } ] }'

			JsonNode schemaNode = (new ObjectMapper()).readTree(schemaText)
			JsonSchema schema = jsonSchemaFactory.getSchema(schemaNode)

			Set<ValidationMessage> validationMessages

		when:
			validationMessages = schema.validate((new ObjectMapper()).readTree(name1json))
		then:
			validationMessages.empty

		when:
			validationMessages = schema.validate((new ObjectMapper()).readTree(name2json))
		then:
			validationMessages.empty
	}
}
