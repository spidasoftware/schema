SPIDA Software JSON Interfaces
==============================

[![Build Status](https://secure.travis-ci.org/spidasoftware/schema.png)](http://travis-ci.org/spidasoftware/schema)

General Overview of the Schema
--------------------------------

We will strive for simplicity, and it is still being working on.

There will be two types of services "remote procedures" and "stateful services".

To address these two types of services we will be implementing two different service types "RPC" and "REST" services.  These two types of services address different needs within the service environment.

Each of the services will have discriptors to allow for testing, and the objects that are passed will have schemas provided here for validating.


General proces for using a service definded here:
* Find the service interface you are implementing.
* Create tested input params for the service methods you are going to use
* Write integration tests if possible using example services.

General process for implementing a service:
* Find the service interface you are implementing.
* Write a test to make sure you implement all the methods.
* Wirte a test to make sure your method respond with correct responses.

### RPC

An example of the "RPC" type would be something that would in java have a service interface, a defined set of methods that can be replaced with any service that conforms to that interface.  This would be for example a "math" service with methods like "add" and "subtract", not that this example would have different implementations, a more complex math service certainly could.  We have based it on the example found [here](http://www.simple-is-better.org/json-rpc/jsonrpc20-schema-service-descriptor.html).

* Request: RPC type methods will be done agains a url that ends with the method name.  
* Params: parameters for the procedure are included in the http params list after the method name i.e. add?n1=1,n2=4
* Response: the response will always be formated in the generic "method_response", this allows for passing error codes and the result. Example: {"result":5}

### REST

The second type of services are "REST" services.  These services differ from the first in that they need to retain and store the state of some objects in the server.  A user service would be an example of this type of service, where you can create, modify, update and delete these users on the server.  With this type of service you generally won't need to define the methods to the same degree, but we will still use defined object within this service.

* Request: REST type methods will be done agains a url that ends with the id or object name.  
* Params: parameters for the procedure are included in the http params list after the method name i.e. project/1?
* Response: the response will always be formated in the generic "method_response", this allows for passing error codes and the result. Example: {"error":"Object not found"} 

### Schema Parts
1. [Asset](https://github.com/spidasoftware/schema/tree/master/v1/asset)
2. [General](https://github.com/spidasoftware/schema/tree/master/v1/general)

### Tools

#### Validation

1. Java - We have used the excellent library by [fge](https://github.com/fge/json-schema-validator) in our java environments.  It gives very good validation errors and also does all the references for you, so there is no need to load all the linked schemas.
2. javascript - [JSV](https://github.com/garycourt/JSV) is what we have used in our javascript tests here in this package.  The references are harder to handle, but still good.

#### Script Dereference

There is a coffee script in the utils/scripts that will combine all the schemas into a fewer number of really large files.  This is useful in test and validating entire project structures.

To Run:

	coffee utils/scripts/dereference.coffee

This is also run on each test from:

    npm test

#### Javascript Testing

We have included jasmine tests using nodejs.  

To Install:

	npm install jasmine-node -g

Run you tests:

	jasmine-node --coffee spec

Or a specific test:

  jasmine-node --coffee -m Calc spec/

For Debugging: [JSON Tools](https://github.com/ddopson/underscore-cli)
	
	echo "{'some':'json'}" | underscore pretty --color

#### Java Tools

In the utils/src/* folders are some java classes that can be helpful when integrating our stuff into a java world.  This includes a classes that produces a JSON descriptor of a class and validate a service against a JSON descriptor.

The jar can be compiled with:
    
    mvn install

***

SPIDA® is a registered trademark of SPIDAWeb LLC. Copyright © 2012 SPIDAWeb LLC. All rights reserved. All other brands or product names are the property of their respective holders.
[spidasoftware.com](http://www.spidasoftware.com/)
