SPIDA Software JSON Interfaces
==============================

[![Build Status](https://secure.travis-ci.org/spidasoftware/schema.png)](http://travis-ci.org/spidasoftware/schema)

General Overview of the Schema
--------------------------------

We will strive for simplicity, and it is still being working on.

### Services

Our service definitions are in each folder in a separate folder called _interfaces_.  For example we have a _spidcalc_ folder that contains an _interfaces_ folder.  The spidacalc folder contains all the objects that relate to spidacalc.  The interfaces contains the service interfaces that relate to spidacalc.

We based our services on the json-rpc that can be found [here](http://www.simple-is-better.org/json-rpc/jsonrpc20-schema-service-descriptor.html).  

Each service call has three main pieces, the request url, the request parameters, and the response.

#### Request URL

Methods will be done against a url that ends with the method name.  The method names are defined in the interfaces.  Example: 

    .../projectmanager/projectAPI/getProjects

would be the _getProjects_ method from the spidamin/project/interfaces/pm.json.  The url before the _getProjects_ would be server specific.

#### Request Parameters

Parameters for the procedure are included in the http params list after the method name i.e. 

    .../getProjects?projectCodeValues=["value"]

would contain the parameter of projectCodeValues.

#### Response 

The response body will always be formatted in the generic [_method\_response_](https://github.com/spidasoftware/schema/tree/master/v1/general/method_response.schema), this allows for passing error codes and the result. 

    {"result":5}

Would be a valid response object, that might be returned.

### Main Schema Parts
2. [v1](https://github.com/spidasoftware/schema/tree/master/v1) - the version 1 API main folder.
2. [spec](https://github.com/spidasoftware/schema/tree/master/spec) - the folder containing our tests against the schema itself, with some test fixtures.
2. [utils](https://github.com/spidasoftware/schema/tree/master/util) - some utilities that can be used in Java.

### Consume a Service

General process for using a service defined here:
* Find the service interface you are implementing.
* Create tested input params for the service methods you are going to use
* Write integration tests if possible using example services.

### Implement a Service

General process for implementing a service:
* Find the service interface you are implementing.
* Write a test to make sure you implement all the methods.
* Write a test to make sure your method respond with correct responses.

Concepts
--------
1. Our services rely heavily on the JSON format.  If you are not familiar with JSON, you will need to be to understand what it is your are submitting to the service.  Try this short [tutorial](http://www.w3schools.com/json/default.asp).
2. We have two main areas for our services. SPIDACalc and SPIDAMin, don't confuse the two.  They each have some overlap in naming, but fundamentally different structures.  SPIDAMin is an online application that is primarily used to manage assets.  SPIDACalc is a client application for analyzing utility pole structures.  Both have a project, but each is unique and different.  Make sure you are using the correct one.
3. Schema files end in .schema, and interface definitions end in .json.  The .schema files are used to validate the objects passed. The .json files describe the services to which you pass those objects.

Versions
--------

The V1 of the schema is currently in a "alpha" state it is complete in some areas, please contact us about which pieces are stable enough to use.  

*We of course will be working on this further in the future and until we reach V1, there WILL be breaking changes.*

Our schema's currently conform to the "json-schema-draft-03" version, but we are looking to always keep that current until the official release of that standard.

Testing
-------

#### Javascript Testing

The general testing of the schema itself is written in coffeescript and uses nodejs and jasmine to run the test suite. 

1. spec - contains all the jasmine tests.  
1. spec/fixtures - contains the test fixtures used in the tests.

To be able to run tests:

1. Install nodejs
2. Install npm for node.
3. Install jasmine node ```npm install jasmine-node -g```

To run test suite:

``` npm test ```

Or a specific test:

``` jasmine-node --coffee -m Calc spec/ ```

Tools
-----

#### Validation

If you are in need of actually validating some JSON data against the schema there are two options that we have used and found to be pretty good for Java and javascript.

1. Java - We have used the excellent library by [fge](https://github.com/fge/json-schema-validator) in our java environments.  It gives very good validation errors and also does all the references for you, so there is no need to load all the linked schema.
2. javascript - [JSV](https://github.com/garycourt/JSV) is what we have used in our javascript tests here in this package.  The references are harder to handle, but still good.

For Debugging: [JSON Tools](https://github.com/ddopson/underscore-cli)
	
	echo "{'some':'json'}" | underscore pretty --color

#### Java Tools

In the utils/src/* folders are some java classes that can be helpful when integrating our stuff into a java world.  This includes a classes that produces a JSON descriptor of a class and validate a service against a JSON descriptor.

The jar can be compiled with:
    
    mvn install

***

SPIDA® is a registered trademark of SPIDAWeb LLC. Copyright © 2012 SPIDAWeb LLC. All rights reserved. All other brands or product names are the property of their respective holders.
[spidasoftware.com](http://www.spidasoftware.com/)
