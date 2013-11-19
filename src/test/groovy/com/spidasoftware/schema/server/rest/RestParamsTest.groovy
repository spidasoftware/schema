package com.spidasoftware.schema.server.rest

import net.sf.json.*
import com.spidasoftware.schema.server.rest.ex.*

class RestParamsTest extends GroovyTestCase {
	def restParams
	def testApi

	void setUp() {
		def apiText = getClass().getResourceAsStream("/testAPI.json").getText()
		testApi = JSONObject.fromObject(apiText)
		restParams = new RestParams(testApi)
		
	}

	void testEmptyListParams(){
		def listResult = restParams.validateAndFormat("thing", "list", [:])
		assert listResult.query instanceof Map
		assert listResult.query.isEmpty()
		assert listResult.projection.skip == 0
		assert listResult.projection.limit == 50
	}

	void testListParams(){
		def params = [name:"testName", anotherProperty:"testProp", uuid:"testUuid", skip: 5, limit: 10]
		def result = restParams.validateAndFormat("thing", "list", params)

		assert result.query.name == "testName"
		assert result.query.uuid == "testUuid"
		assert result.projection.skip == 5
		assert result.projection.limit == 10

	}

	void testSaveParams(){
		restParams.metaClass.validateAgainstSchema = {schema, thing ->
			return true
		}
		def proj = new JSONObject()
		proj.put("prop1", "value1")
		proj.put("prop2", "value2")
		def startParams = [project: proj.toString()]
		def result = restParams.validateAndFormat("thing", "save", startParams)

		assert result.project.every{k, v->
			v == proj[k]
		}
		assert result.size() == 1
	}

	void testMissingParams(){
		restParams.metaClass.validateAgainstSchema = {schema, thing ->
			return true
		}
		shouldFail(MissingParamException){
			def proj = new JSONObject()
			proj.put("prop1", "value1")
			proj.put("prop2", "value2")
			def params = [project: proj.toString()]
			def result = restParams.validateAndFormat("thing", "update", params)
		}
	}

	void testInvalidParams(){
		restParams.metaClass.validateAgainstSchema = {schema, thing ->
			throw new InvalidParameterException("testParam")
		}
		shouldFail(InvalidParameterException){
			def proj = new JSONObject()
			proj.put("prop1", "value1")
			proj.put("prop2", "value2")
			def params = [project: proj.toString()]
			def result = restParams.validateAndFormat("thing", "save", params)
		}
	}


}