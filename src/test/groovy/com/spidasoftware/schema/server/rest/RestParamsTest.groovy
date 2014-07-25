package com.spidasoftware.schema.server.rest

import net.sf.json.*
import com.spidasoftware.schema.server.rest.ex.*

class RestParamsTest extends GroovyTestCase {
	def restParams
	def testApi

	void setUp() {
		def apiText = getClass().getResourceAsStream("/rest/testAPI.json").getText()
		testApi = JSONObject.fromObject(apiText)
		restParams = new RestParams(testApi)
		
	}

	void testEmptyListParams(){
		def listResult = restParams.validateAndFormat("thing", "list", [:])
		assert listResult.query instanceof Map
		assert listResult.query.isEmpty()
		assert listResult.projection.skip == 0
		assert listResult.projection.limit == 1
	}

	void testListParams(){
		def params = [name:"testName", anotherProperty:"testProp", uuid:"testUuid", skip: "5", limit: "10", format: "referenced"]
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


	void testProjectionParamTypes(){
		def api2 = JSONObject.fromObject(getClass().getResourceAsStream("/rest/testParamTypes.json").getText())
		def restParams2 = new RestParams(api2)

		def params = [name:"testName", anotherProperty:"testProp", uuid:"testUuid", skip: "5", limit: "10", someString:"stringy", someBoolean:"true"]

		def result = restParams2.validateAndFormat("thing", "list", params)

		assert result.query.name == "testName"
		assert result.query.uuid == "testUuid"
		assert result.projection.someBoolean == true
		assert result.projection.someString == "stringy"
		assert result.projection.skip == 5
		assert result.projection.limit == 10
	}

    void testNoFormatInParams() {
        def listResult = restParams.validateAndFormat("thing", "list", [:])
        assert listResult.format == "calc"
    }

    void testFormatInParams() {
        def listResult = restParams.validateAndFormat("thing", "list", [format: "referenced"])
        assert listResult.format == "referenced"
    }

    void testInvalidFormatInParams() {
        shouldFail(InvalidParameterException) {
            restParams.validateAndFormat("thing", "list", [format:"exchange"])
        }
    }

    void testAtLeastOneParamRequiredMissing() {
    	shouldFail(InvalidParameterException) {
    		restParams.validateAndFormat("thing", "saveMultipleParams", [:])	
    	}
    }

    void testAtLeastOneParamRequired() {
    	def uuid = UUID.randomUUID()
    	def listResult = restParams.validateAndFormat("thing", "saveMultipleParams", [filefortUuid:uuid])
    	assert uuid == listResult.filefortUuid
    }

    void testMaxAllowedByType() {
        def listResult = restParams.validateAndFormat("thing", "list", [limit:1000, format:"calc"])
        assert listResult.projection.limit == 1 // Should return the max for calc type which is 1
    }

    void testZeroLimitParam() {
        def listResult = restParams.validateAndFormat("thing", "list", [limit:0, format:"referenced"])
        assert listResult.projection.limit == 50 // Should return the defaultValue for limit
    }
}