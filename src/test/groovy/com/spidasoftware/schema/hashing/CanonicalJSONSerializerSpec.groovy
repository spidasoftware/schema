package com.spidasoftware.schema.hashing

import net.sf.json.JSONObject
import net.sf.json.JSONArray
import net.sf.json.JSONSerializer
import net.sf.json.JSONNull
import spock.lang.Specification

class CanonicalJSONSerializerSpec extends Specification{

	def "serializer should produce the same value for equal but mixed objects"() {

		given:
			def json = '{ "level": 1, "child": { "array": [5,4,3,42,1], "arrayOfObjects":[ {"name":"short", "age":12, "location":"someplace"}, {"name":"long", "age":66, "location":"nowhere"} ], "sibling":"sister" }, "otherProp":"something" }'
			def jsonMixed = '{ "child": { "sibling": "sister", "array": [5,4,3,42,1], "arrayOfObjects": [{ "name": "long", "location": "nowhere", "age": 66 }, { "age": 12, "name": "short", "location": "someplace" }], }, "otherProp": "something", "level": 1 }'
			def jsonObject = JSONSerializer.toJSON(json)
			def jsonMixedObject = JSONSerializer.toJSON(jsonMixed)

		when:
			def serialized = new CanonicalJSONSerializer().serialize(jsonObject)
			def serializedMixed = new CanonicalJSONSerializer().serialize(jsonMixedObject)

		then: "all the json should be equal"
			serialized == serializedMixed
			//Serialized JSON is valid
			JSONSerializer.toJSON(serialized)
			JSONSerializer.toJSON(serializedMixed)

	}

	def "numbers formatted to 12 decimal places"() {
	
		when:
			def json = JSONObject.fromObject(
				a: 1,
				b: 0.0001234567890123
			)

		then:
			new CanonicalJSONSerializer().serialize(json) == '{"a":1E0,"b":1.234567890123E-4}'

	}

	def "sorts objects by id"() {
		when:
			def json = JSONArray.fromObject([
				JSONObject.fromObject(id: 'def'),
				JSONObject.fromObject(id: 'abc'),
				JSONObject.fromObject(id: 'zzz', plus: 'more', a: "nother field")
			])

		then:
			new CanonicalJSONSerializer().serialize(json) == '[{"id":"abc"},{"id":"def"},{"a":"nother field","id":"zzz","plus":"more"}]'
	}

	def "Handles special chars"() {
		when:
			def json = JSONArray.fromObject( val: '\n\t"\'\\' )
		
		then:
			JSONSerializer.toJSON(new CanonicalJSONSerializer().serialize(json)).val == json.val
	}

	def "Handles nulls"() {
		when:
			def json = JSONObject.fromObject(val: null)

		then:
			JSONSerializer.toJSON(new CanonicalJSONSerializer().serialize(json)).val instanceof JSONNull
	}




}
