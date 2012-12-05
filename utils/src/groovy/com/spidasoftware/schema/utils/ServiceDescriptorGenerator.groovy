package com.spidasoftware.schema.utils

import groovy.json.*

class ServiceDescriptorGenerator {

  def static ignoreMethods = ["equals", "__\$swapInit","assertEquals","assertFalse","assertNotNull","assertNotSame","assertNull","assertSame","assertTrue","countTestCases","fail","failNotEquals","failNotSame","failSame","format","getClass","getMetaClass","getMethodName","getName","getProperty","hashCode","invokeMethod","notYetImplemented","notify","notifyAll","run","runBare","setMetaClass","setName","setProperty","toString","wait"]


  // Pass in a class and it will generate the descriptor, this should not be used for validation right now.
  static String generateDescriptor(Class clazz){
    def builder = new JsonBuilder()
    def annotations = new JavaServiceValidatorTest().class.getAnnotation(ServiceDescription.class)
    def packageName = clazz.name.replace(clazz.getSimpleName(), "")
    def methods = clazz.metaClass.methods.findAll{!ServiceDescriptorGenerator.ignoreMethods.contains(it.name)}
    methods = methods.findAll{it.isPublic()}
    methods = methods.unique()*.name
    def methodMap = [:]
    for(m in methods){
      methodMap.put(m, clazz.methods.findAll{it.name.replace(packageName,"").trim()==m})
    }
    //Only public methods
    builder {
      id "${ServiceDescriptorGenerator.getId(clazz)}"
      description "${ServiceDescriptorGenerator.getId(clazz)}"
      methodMap.each {k,v -> 
         "${k}"(makeMethod(v))
      }
    }
    return builder.toPrettyString()
  } 

  def static makeMethod(v){
    def map = [:]
    map.description="${ServiceDescriptorGenerator.getDescription(v.get(0))}"
    def returnType = ServiceDescriptorGenerator.getReturnType(v.get(0))
    if(returnType!="void") map.returns = returnType
    def params =ServiceDescriptorGenerator.getParams(v)
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
      map.type = ServiceDescriptorGenerator.format(p)
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
    return ServiceDescriptorGenerator.format(m.getReturnType())
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