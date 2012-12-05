package com.spidasoftware.schema.utils

import groovy.json.*

@ServiceDescription(id="someService",  description="Test service")
class JavaServiceValidatorTest extends GroovyTestCase { 

  void testValidatorOutput(){
     assert ServiceDescriptorGenerator.generateDescriptor(this.class)
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