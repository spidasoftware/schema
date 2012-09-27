package com.spidasoftware.schema

import groovy.json.*

class JavaServiceValidator {

  def static ignoreMethods = ["equals", "__\$swapInit","assertEquals","assertFalse","assertNotNull","assertNotSame","assertNull","assertSame","assertTrue","countTestCases","fail","failNotEquals","failNotSame","failSame","format","getClass","getMetaClass","getMethodName","getName","getProperty","hashCode","invokeMethod","notYetImplemented","notify","notifyAll","run","runBare","setMetaClass","setName","setProperty","toString","wait"]


  // Pass in a class and it will generate the descriptor, this should not be used for validation right now.
  static String generateDescriptor(Class clazz){
    def builder = new JsonBuilder()
    def annotations = new JavaServiceValidatorTest().class.getAnnotation(ServiceDescription.class)
    def packageName = clazz.name.replace(clazz.getSimpleName(), "")
    def methods = clazz.metaClass.methods.findAll{!JavaServiceValidator.ignoreMethods.contains(it.name)}
    methods = methods.findAll{it.isPublic()}
    methods = methods.unique()*.name
    def methodMap = [:]
    for(m in methods){
      methodMap.put(m, clazz.methods.findAll{it.name.replace(packageName,"").trim()==m})
    }
    //Only public methods
    builder {
      id "${JavaServiceValidator.getId(clazz)}"
      description "${JavaServiceValidator.getId(clazz)}"
      methodMap.each {k,v -> 
         "${k}"(makeMethod(v))
      }
    }
    return builder.toPrettyString()
  } 

  //Pass in a service class and a descriptor to validate it against.
  static validateService(Class service, String jsonDescriptor){
      def serviceDescription = JavaServiceValidator.generateDescriptor(service)
      def slurper = new JsonSlurper()
      def serviceJSON = slurper.parseText(serviceDescription)
      def descriptorJSON = slurper.parseText(jsonDescriptor)
      def errorArray = []
      def infoArray = []
      def returnMap = [:]
      returnMap.error = errorArray
      returnMap.info = infoArray
      
      if(serviceJSON.id==null) errorArray.add "The service does not have an id annotation."
      if(serviceJSON.description==null) errorArray.add "The service does not have a description annotation."
      descriptorJSON.eachWithIndex{descriptorMethod, descriptorValue, methodIndex->
        if(descriptorValue instanceof Map){
          def serviceMethod = serviceJSON.find{k,v -> k==descriptorMethod}
          if(serviceMethod==null){
            infoArray.add "  Method: $descriptorMethod is in the descriptor but not in the class."
          }else{
            if(serviceMethod.value.returns!=descriptorValue.returns) errorArray.add "  Method: the returns value for $descriptorMethod does not match the descriptor."
            if(descriptorValue.params?.size()!=serviceMethod.value.params?.size()){
              errorArray.add "  Method: the parameter count for $descriptorMethod does not match the descriptor."
            }else{
              descriptorValue.params.eachWithIndex{descriptorParam, paramIndex->
                  def serviceParam = serviceMethod.value.params.get(paramIndex)
                  if(serviceParam.type!=descriptorParam.type) errorArray.add "  Method: the parameter at position $paramIndex for $descriptorMethod does not match the descriptor type."
                  if(serviceParam.required!=descriptorParam.required) errorArray.add "  Method: the parameter at position $paramIndex for $descriptorMethod does not match the descriptor requiredness."
              }
            } 
          }
        }
      }
      return returnMap
  }

  def static makeMethod(v){
    def map = [:]
    map.description="${JavaServiceValidator.getDescription(v.get(0))}"
    def returnType = JavaServiceValidator.getReturnType(v.get(0))
    if(returnType!="void") map.returns = returnType
    def params =JavaServiceValidator.getParams(v)
    if(params!=null) map.params = params
    return map
  }

  def static getParams(methods){
    if(methods==null || methods.size()==0) return
    def requiredParams = 1000
    def largestMethod = methods.get(0)
    for(m in methods){
      if(m.getGenericParameterTypes().size()<requiredParams) requiredParams=m.getGenericParameterTypes().size()
      if(m.getGenericParameterTypes().size()>largestMethod.getGenericParameterTypes().size()) largestMethod=m
    }
    if(largestMethod.getGenericParameterTypes().size()==0) return
    def params = []
    largestMethod.getGenericParameterTypes().eachWithIndex{p, i ->
      def map = [:]
      map.type = JavaServiceValidator.format(p)
      map.name = "param"+i
      if(i>requiredParams-1){
        map.required = false
      }else{
        map.required = true
      }
      params.add map
    }
    return params
  }

  def static getReturnType(m){
    return JavaServiceValidator.format(m.getReturnType())
  }
  def static format(m){
    def index = m.toString().lastIndexOf(".")
    def returnValue = "object"
    if(index>0){
      returnValue = m.toString().substring(index+1).trim()
    } else {
      returnValue = m.toString()
    } 
    returnValue = returnValue.toLowerCase()
    switch(returnValue) {
      case "list":
        returnValue = "array"
      case ["double", "integer", "float", "long", "short"]:
        returnValue = "number"
      break
    }
    return returnValue
  }

  def static getId(def object){
    def objectDescription = object.getAnnotation(ServiceDescription.class)
    if(objectDescription==null || objectDescription.id()=="[unassigned]"){
      return (object instanceof Class) ? object.getSimpleName().replaceAll(/\B[A-Z]/) { '_' + it }.toLowerCase() : object.name
    }else{
      return objectDescription.id()
    }
  }

  def static getDescription(def object){
    def objectDescription = object.getAnnotation(ServiceDescription.class)
    if(objectDescription==null){
      return "[unassigned]"
    }else{
      return objectDescription.description()
    }
  }

  
  
}