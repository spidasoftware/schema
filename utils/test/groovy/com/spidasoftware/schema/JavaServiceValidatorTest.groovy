package com.spidasoftware.schema

import groovy.json.*

@ServiceDescription(id="someService",  description="Test service")
class JavaServiceValidatorTest extends GroovyTestCase { 

  void testValidatorOutput(){
     println JavaServiceValidator.generateDescriptor(this.class)
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



}