package com.spidasoftware.schema

import org.codehaus.groovy.grails.web.json.*
import junit.framework.Assert
import junit.framework.TestCase
import org.apache.log4j.BasicConfigurator

public class JSONSchemaTest extends TestCase {
	
    JSONSchema schema
    String value, ref, json1, json2, json3
	
	public JSONSchemaTest(){
		BasicConfigurator.configure()
	}

    public void testFindReferenceSchema() throws Exception {
        String reference = JSONSchema.findReferenceSchema("project", "v1")
        JSONObject json = new JSONObject(reference)
        Assert.assertTrue(json.has("id"))
        Assert.assertTrue(json.get("id").equals("project"))

        reference = JSONSchema.findReferenceSchema("project.json", "v1")
        json = new JSONObject(reference)
        Assert.assertTrue(json.has("id"))
        Assert.assertTrue(json.get("id").equals("project"))

        reference = JSONSchema.findReferenceSchema("nonexistent-schema", "v1")
        Assert.assertNull(reference)
    }

    public void testSampleProjectValidation() throws Exception {
        schema = new JSONSchema(new JSONObject(JSONSchema.findReferenceSchema("project", "v1")))
    }

    // public void testBaseProjectValidation() throws Exception {
    //     schema = new JSONSchema(new JSONObject(JSONSchema.findReferenceSchema("project")))
    //     schema.addReferenceSchema("measurable",new JSONObject(JSONSchema.findReferenceSchema("measurable")))
    //     schema.addReferenceSchema("material",new JSONObject(JSONSchema.findReferenceSchema("pole_material")))
    //     schema.addReferenceSchema("shape",new JSONObject(JSONSchema.findReferenceSchema("client_item_shape")))
    //     schema.addReferenceSchema("owner",new JSONObject(JSONSchema.findReferenceSchema("owner")))
    //     json1 = "{'project': {                                       "
    //     json1+= "'name' : 'SampleProject1',                          "
    //     json1+= "'account':0,'leads' : [                             "
    //     json1+= "{'index' : 0,                                       "
    //     json1+= "'locations' : [                                     "
    //     json1+= "        {'pole_number' : 'XYZ1234C',                "
    //     json1+= "         'designs'     : [                          "
    //     json1+= "                    {'waypoint'     : {             "
    //     json1+= "                            'latitude'  : 84.02001, "
    //     json1+= "                            'longitude' : 87.076    "
    //     json1+= "                        }}]},                       "
    //     json1+= "        {'pole_number' : '1234322F',                "
    //     json1+= "                'designs' : [{                      "
    //     json1+= "                    'waypoint'     : {              "
    //     json1+= "                        'latitude'  : 87.00000,     "
    //     json1+= "                        'longitude' : 87.00000      "
    //     json1+= "                        }}]}]}]}}                   "
    //     Assert.assertTrue(schema.validate(new JSONObject(json1).getJSONObject("project")))
    // }

    // public void testValidateOptionalParam() {
    //     value = "{\"type\":\"object\",'properties':{'name':{'type':'string'},'count':{'type':'integer'},'someval':{'type':'string','optional':true}}}"
    //     json1 = "{'name':'test_object','count':8}"
    //     schema = new JSONSchema(new JSONObject(value))
    //     Assert.assertTrue(schema.validate(new JSONObject(json1)))
    // }

    // public void testMissingRequiredParam() {
    //     value = "{\"type\":\"object\",'properties':{'name':{'type':'string'},'count':{'type':'integer'},'someval':{'type':'string','optional':true}}}"
    //     json1 = "{'name':'test_object'}"
    //     schema = new JSONSchema(new JSONObject(value))
    //     Assert.assertFalse(schema.validate(new JSONObject(json1)))
    // }

    // public void testValidateJSONObject() {
    //     value = "{'type':'object',"                                      
    //     value+= "     'properties':{"                                    
    //     value+= "         'facts':{"                                     
    //     value+= "             'type':'object',"                          
    //     value+= "             'properties':{"                            
    //     value+= "                 'temp':{'type':'integer'},"            
    //     value+= "                 'height':{'type':'number'}"            
    //     value+= "                 }"                                     
    //     value+= "             },"                                        
    //     value+= "         'count':{'type':'integer'},"                   
    //     value+= "         'someval':{'type':'string','optional':true}"   
    //     value+= "     }"                                                 
    //     value+= " }"                                                     
    //     json1 = "{'facts':{'temp':85,'height':41.3},'count':9}"
    //     schema = new JSONSchema(new JSONObject(value))
    //     Assert.assertTrue(schema.validate(new JSONObject(json1)))
    //     json1 = "{'facts':{'temp':85},'count':9,'someval':'here'}"
    //     Assert.assertFalse(schema.validate(new JSONObject(json1)))
    // }

    // public void testValidateJSONArray() {
    //     value = "{                                            "
    //     value+= "    'type':'object',                         "
    //     value+= "    'properties': {                          "
    //     value+= "        'stuff': {                           "
    //     value+= "            'type':'array',                  "
    //     value+= "            'items': {                       "
    //     value+= "               'type':'object',              "
    //     value+= "               'properties': {               "
    //     value+= "                   'pop': {'type':'string'}, "
    //     value+= "                   'int': {'type':'integer'} "
    //     value+= "               }                             "
    //     value+= "            }                                "
    //     value+= "        }                                    "
    //     value+= "    }                                        "
    //     value+= "}                                            "
    //     schema = new JSONSchema(new JSONObject(value))
    //     json1 = "{'stuff':[{'pop':'pop1','int':31}]}"
    //     Assert.assertTrue(schema.validate(new JSONObject(json1)))
    //     json1 = "{'stuff':[{'pop':'pop1','int':31},{'pop':'pop5','int':9}]}"
    //     Assert.assertTrue(schema.validate(new JSONObject(json1)))
    // }

    // public void testValidateNumber() {
    //     value = "{'type':'object','properties':{'a_number':{'type':'number'}}}"
    //     schema = new JSONSchema(new JSONObject(value))
    //     json1 = "{'a_number':-9.8}"
    //     Assert.assertTrue(schema.validate(new JSONObject(json1)))
    //     json1 = "{'a_number':+9.8}"
    //     Assert.assertTrue(schema.validate(new JSONObject(json1)))
    //     json1 = "{'a_number':10000003}"
    //     Assert.assertTrue(schema.validate(new JSONObject(json1)))
    //     json1 = "{'a_number':9.8.3}"
    //     Assert.assertFalse(schema.validate(new JSONObject(json1)))
    // }

    // public void testValidateAny() {
    //     value = "{'type':'object','properties':{'anything':{'type':'any'}}}"
    //     schema = new JSONSchema(new JSONObject(value))
    //     json1 = "{'anything':9}"
    //     Assert.assertTrue(schema.validate(new JSONObject(json1)))
    //     json1 = "{'anything':'four'}"
    //     Assert.assertTrue(schema.validate(new JSONObject(json1)))
    //     json1 = "{'anything':null}"
    //     Assert.assertTrue(schema.validate(new JSONObject(json1)))
    //     json1 = "{'anything':true}"
    //     Assert.assertTrue(schema.validate(new JSONObject(json1)))
    // }

    // public void testMergeReferences() {
    //     value = "{'extends':{'\$ref':'some_ref'},'type':'object','properties':{'foo':{'type':'boolean'}}}"
    //     ref = "{'type':'object','properties':{'bar':{'type':'string'}}}"
    //     schema = new JSONSchema(new JSONObject(value))
    //     schema.addReferenceSchema("some_ref",new JSONObject(ref))
    //     JSONObject merge = schema.mergeReferences(new JSONObject(schema.toString()))

    //     Assert.assertTrue(merge.has("type") && merge.has("properties"))
    //     Assert.assertTrue(merge.getJSONObject("properties").has("foo"))
    //     Assert.assertTrue(merge.getJSONObject("properties").has("bar"))
    // }

    // public void testMergeAndOverrideReferences() {
    //     value = "{'extends':{'\$ref':'some_ref'},'type':'object','properties':{'foo':{'type':'boolean'}}}"
    //     ref = "{'type':'object','properties':{'foo':{'type':'string'}}}"
    //     schema = new JSONSchema(new JSONObject(value))
    //     schema.addReferenceSchema("some_ref",new JSONObject(ref))

    //     JSONObject merge = schema.mergeReferences(new JSONObject(schema.toString()))
    //     Assert.assertTrue(((JSONObject)merge.get("properties")).has("foo"))

    //     JSONObject foo = (JSONObject)((JSONObject)merge.get("properties")).get("foo")
    //     Assert.assertTrue(foo.get("type").equals("boolean"))
    // }

    // public void testValidateReferenceSchema() {
    //     value = "{'extends':{'\$ref':'some_ref'},'type':'object','properties':{'foo':{'type':'boolean'}}}"
    //     ref = "{'type':'object','properties':{'foo':{'type':'number'}}}"
    //     schema = new JSONSchema(new JSONObject(value))
    //     schema.addReferenceSchema("some_ref",new JSONObject(ref))
    //     JSONObject merge = schema.mergeReferences(new JSONObject(schema.toString()))

    //     json1 = "{'foo':true}"
    //     Assert.assertTrue(schema.validate(new JSONObject(json1)))
    //     json1 = "{'foo':999}"
    //     Assert.assertFalse(schema.validate(new JSONObject(json1)))
    // }

    // public void testValidateInteger() {
    //     value = "{\"type\":\"object\",'properties':{'name':{'type':'string'},'count':{'type':'integer'},'someval':{'type':'string','optional':true}}}"
    //     json1 = "{'name':'test_object','count':'8'}"
    //     json2 = "{'name':'test_object','count':'-1'}"
    //     json3 = "{'name':'test_object','count':'8.2'}"
    //     schema = new JSONSchema(new JSONObject(value))
    //     Assert.assertTrue(schema.validate(new JSONObject(json1)))
    //     Assert.assertTrue(schema.validate(new JSONObject(json2)))
    //     Assert.assertFalse(schema.validate(new JSONObject(json3)))
    // }

    // public void testValidateBoolean() {
    //     value = "{'type':'object','properties':{'check_this':{'type':'boolean'}}}"
    //     json1 = "{'check_this':true}"
    //     json2 = "{'check_this':'true'}"
    //     json3 = "{'check_this':null}"
    //     schema = new JSONSchema(new JSONObject(value))
    //     Assert.assertTrue(schema.validate(new JSONObject(json1)))
    //     Assert.assertTrue(schema.validate(new JSONObject(json2)))
    //     Assert.assertFalse(schema.validate(new JSONObject(json3)))
    // }

    // public void testValidateEmptyObject() {
    //     value = "{\"type\":\"object\",'properties':{'name':{'type':'string'},'count':{'type':'integer'},'someval':{'type':'string','optional':true}}}"
    //     json1 = "{}"
    //     schema = new JSONSchema(new JSONObject(value))
    //     Assert.assertFalse(schema.validate(new JSONObject(json1)))

    //     value = "{'type':'object','properties':{}}"
    //     schema = new JSONSchema(new JSONObject(value))
    //     Assert.assertTrue(schema.validate(new JSONObject(json1)))
    // }

    // //when a type is null, either the value is explicitly null, or
    // //the paramter isn't given
    // public void testValidateNull() {
    //     value = "{'type':'object','properties':{'cat':null}}"
    //     json1 = "{}"
    //     schema = new JSONSchema(new JSONObject(value))
    //     Assert.assertTrue(schema.validate(new JSONObject(json1)))

    //     json2 = "{'cat':null}"
    //     Assert.assertTrue(schema.validate(new JSONObject(json2)))

    //     json3 = "{'cat':'null'}"
    //     Assert.assertTrue(schema.validate(new JSONObject(json3)))

    //     json3 = "{'cat':'pod'}"
    //     Assert.assertFalse(schema.validate(new JSONObject(json3)))
    // }

    // public void testValidateStringChoices() throws Exception {
    //     value = "{'type':'object','properties':{'octocat':{'type':'string','choices':[{'value':'very cool'},{'value':'amazing'}]}}}"
    //     schema = new JSONSchema(new JSONObject(value))
    //     json1 = "{'octocat':'very cool'}"
    //     Assert.assertTrue(schema.validate(new JSONObject(json1)))
    //     json1 = "{'octocat':'not very cool at all, really'}"
    //     Assert.assertFalse(schema.validate(new JSONObject(json1)))
    // }

    // public void testMaxNumbers() throws Exception {
    //     value = "{                            "
    //     value+= "    'type':'object',         "
    //     value+= "    'properties': {          "
    //     value+= "        'top': {             "
    //     value+= "            'type':'number', "
    //     value+= "            'max':40         "
    //     value+= "        }                    "
    //     value+= "    }                        "
    //     value+= "}                            "
    //     schema = new JSONSchema(new JSONObject(value))
    //     json1 = "{'top':1}"
    //     Assert.assertTrue(schema.validate(new JSONObject(json1)))
    //     json1 = "{'top':41}"
    //     Assert.assertFalse(schema.validate(new JSONObject(json1)))
    //     json1 = "{'top':-9.6}"
    //     Assert.assertTrue(schema.validate(new JSONObject(json1)))

    //     value = "{                            "
    //     value+= "    'type':'object',         "
    //     value+= "    'properties': {          "
    //     value+= "        'top': {             "
    //     value+= "            'type':'integer',"
    //     value+= "            'max':92         "
    //     value+= "        }                    "
    //     value+= "    }                        "
    //     value+= "}                            "
    //     schema = new JSONSchema(new JSONObject(value))
    //     json1 = "{'top':1}"
    //     Assert.assertTrue(schema.validate(new JSONObject(json1)))
    //     json1 = "{'top':101}"
    //     Assert.assertFalse(schema.validate(new JSONObject(json1)))
    //     json1 = "{'top':-9}"
    //     Assert.assertTrue(schema.validate(new JSONObject(json1)))
    //     json1 = "{'top':-9.6}"
    //     Assert.assertFalse(schema.validate(new JSONObject(json1)))
    // }

    // public void testMinNumbers() throws Exception {
    //     value = "{                            "
    //     value+= "    'type':'object',         "
    //     value+= "    'properties': {          "
    //     value+= "        'top': {             "
    //     value+= "            'type':'number', "
    //     value+= "            'min':112.4      "
    //     value+= "        }                    "
    //     value+= "    }                        "
    //     value+= "}                            "
    //     schema = new JSONSchema(new JSONObject(value))
    //     json1 = "{'top':1}"
    //     Assert.assertFalse(schema.validate(new JSONObject(json1)))
    //     json1 = "{'top':112.31}"
    //     Assert.assertFalse(schema.validate(new JSONObject(json1)))
    //     json1 = "{'top':112.41}"
    //     Assert.assertTrue(schema.validate(new JSONObject(json1)))

    //     value = "{                            "
    //     value+= "    'type':'object',         "
    //     value+= "    'properties': {          "
    //     value+= "        'top': {             "
    //     value+= "            'type':'integer',"
    //     value+= "            'min':2          "
    //     value+= "        }                    "
    //     value+= "    }                        "
    //     value+= "}                            "
    //     schema = new JSONSchema(new JSONObject(value))
    //     json1 = "{'top':1}"
    //     Assert.assertFalse(schema.validate(new JSONObject(json1)))
    //     json1 = "{'top':-9}"
    //     Assert.assertFalse(schema.validate(new JSONObject(json1)))
    //     json1 = "{'top':101}"
    //     Assert.assertTrue(schema.validate(new JSONObject(json1)))
    // }

    // public void testNumberRange() throws Exception {
    //     value = "{                            "
    //     value+= "    'type':'object',         "
    //     value+= "    'properties': {          "
    //     value+= "        'top': {             "
    //     value+= "            'type':'number', "
    //     value+= "            'min':112.4,     "
    //     value+= "            'max':113.11     "
    //     value+= "        }                    "
    //     value+= "    }                        "
    //     value+= "}                            "
    //     schema = new JSONSchema(new JSONObject(value))
    //     json1 = "{'top':112.4}"
    //     Assert.assertTrue(schema.validate(new JSONObject(json1)))
    //     json1 = "{'top':113.31}"
    //     Assert.assertFalse(schema.validate(new JSONObject(json1)))
    //     json1 = "{'top':112.6}"
    //     Assert.assertTrue(schema.validate(new JSONObject(json1)))

    //     value = "{                            "
    //     value+= "    'type':'object',         "
    //     value+= "    'properties': {          "
    //     value+= "        'top': {             "
    //     value+= "            'type':'integer',"
    //     value+= "            'min':2,         "
    //     value+= "            'max':4          "
    //     value+= "        }                    "
    //     value+= "    }                        "
    //     value+= "}                            "
    //     schema = new JSONSchema(new JSONObject(value))
    //     json1 = "{'top':1}"
    //     Assert.assertFalse(schema.validate(new JSONObject(json1)))
    //     json1 = "{'top':2}"
    //     Assert.assertTrue(schema.validate(new JSONObject(json1)))
    //     json1 = "{'top':3}"
    //     Assert.assertTrue(schema.validate(new JSONObject(json1)))
    // }
}