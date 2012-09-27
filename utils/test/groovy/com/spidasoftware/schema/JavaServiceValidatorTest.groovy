package com.spidasoftware.schema

import groovy.json.*

@ServiceDescription(id="someService",  description="Test service")
class JavaServiceValidatorTest extends GroovyTestCase { 

  void testValidatorOutput(){
     assert JavaServiceValidator.generateDescriptor(this.class)
  }

  void testValidateService(){
    def validateResponse = JavaServiceValidator.validateService(this.class, exampleServiceJSON)
    
    validateResponse.error.each{line->
      println line
    }    
    validateResponse.info.each{line->
      println line
    }
  }
  
  @ServiceDescription(id="someMethod",  description="Test Method")
  def methodOne(String one, String two="value"){
    //This is here for testing
  }

  @ServiceDescription(description="Test Method")
  String methodTwo(){
    //This is here for testing
  }
  
  @ServiceDescription(id="ids are ignored on methods")
  List methodThree(){
    //This is here for testing
  }  

  @ServiceDescription(id="ids are ignored on methods")
  double methodFour(){
    //This is here for testing
  }

def exampleServiceJSON = """
{
    "id": "someService",
    "description": "someService",
    "methodFour": {
        "description": "[unassigned]",
        "returns": "number"
    },
    "methodOne": {
        "description": "Test Method",
        "returns": "object",
        "params": [
            {
                "type": "string",
                "name": "param0",
                "required": true
            },
            {
                "type": "number",
                "name": "param1",
                "required": true
            }
        ]
    },
    "methodThree": {
        "description": "[unassigned]",
        "returns": "number"
    },
    "methodTwo": {
        "description": "Test Method",
        "returns": "number"
    },
    "methodDoesNotExist": {
        "description": "Test Method",
        "returns": "string"
    },
    "testValidatorOutput": {
        "description": "[unassigned]"
    }
}
"""

}