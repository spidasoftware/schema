SPIDA Software JSON Interfaces
==============================

[![Build Status](https://secure.travis-ci.org/spidasoftware/schema.png)](http://travis-ci.org/spidasoftware/schema)

* LAST RELEASED VERSION: 1.0.0

License
-------
This repository is governed by the terms of use located [here.](https://github.com/spidasoftware/schema/raw/master/2013_10_25%20SPIDA%20API%20Terms%20of%20Service.pdf)

General Overview of the Schema
--------------------------------

This project contains information about the API's for the SPIDA products.  These API's allow specific actions against our products to take place from
external products or integrations.  This repository contains example information about how to implement those external integrations.  Please read this introductory page, it
will give you a good overview of how our API's function.

### Concepts

There are few concepts to keep in mind:

1. Our services rely heavily on the JSON format.  If you are not familiar with JSON, you will need to be to understand what it is your are submitting to the service.  Try this short [tutorial](http://www.w3schools.com/json/default.asp).
2. We have two main areas for our services. SPIDACalc and SPIDAMin, don't confuse the two.  They each have some overlap in naming, but fundamentally different structures.  SPIDAMin is an online application that is primarily used to manage assets.  SPIDACalc is a client application for analyzing utility pole structures.  Both have a project, but each is unique and different.  Make sure you are using the correct one.
3. Schema files end in .schema, and interface definitions end in .json.  The .schema files are used to validate the objects passed. The .json files describe the services to which you pass those objects.
4. The CalcDB api follows REpresentational Sate Transfer (REST) conventions, which makes it somewhat different from out other APIs, which use Remote Procedure Call (RPC) conventions. This document contains general information that is mostly only relevant to RPC, although some information is relevant to both. For information on using the CalcDB api, check out [this link.](http://github.com/spidasoftware/schema/blob/master/resources/v1/schema/calcdb/interfaces)

Services
------------

Our services provide a set of methods that can be called for a specific product.  Our service definitions are in each folder in a separate folder called _interfaces_.  For example we have a _spidcalc_ folder that contains an _interfaces_ folder.  The spidacalc folder contains all the objects that relate to spidacalc.  The interfaces contains the service interfaces that relate to spidacalc.

We based our services on the json-rpc that can be found [here](http://www.simple-is-better.org/json-rpc/jsonrpc20-schema-service-descriptor.html).  

Each service call has three main pieces, the request url, the request parameters, and the response.

### Request URL

Methods will be done against a url that ends with the method name.  The method names are defined in the interfaces.  Example: 

    .../projectmanager/projectAPI/getProjects

would be the _getProjects_ method from the spidamin/project/interfaces/pm.json.  The url before the _getProjects_ would be server specific.

### Request Parameters

Parameters for the procedure are included in the http params list after the method name i.e. 

    .../getProjects?projectCodeValues=["value"]

would contain the parameter of projectCodeValues.

If you are using POST, the parameters must still be in the POST params if they are too large for the params list in the request string. You will need to set the request content type to:

     application/x-www-form-urlencoded

And the charset to `UTF-8`

### Response 

The response body will always be formatted in the generic [_method\_response_](resources/v1/general/method_response.schema), this allows for passing error codes and the result. 

    {"result":5}

Would be a valid response object, that might be returned.

### Allowed Methods

HTTP says that GET calls should not modify data.  So if you are making a call with our API and it is a modification or creation of the object stored in the application, you will need to do a POST. 

### License Agreement (EULA)

All users must accept the License Agreement.  If this has not been accepted, all HTTP requests will be redirected to usersmaster/agreement.  Login to SPIDAMin and you will be redirected to the License Agreeement.  Click the 'Accept' button at the bottom of the page.

Folder Structure
--------------------

1. [v1](v1) - the version 1 API
  1. [resources/v1/schema/spidacalc](resources/v1/schema/spidacalc) - schemas for communication with spidacalc version 4.4.2.0 and future versions
  1. [resources/v1/schema/spidamin](resources/v1/schema/spidamin) - schemas for communication with spidamin 3.0
  1. [resources/v1/examples](resources/v1/examples) - used in tests, good example objects
1. [src](src) - some utilities that can be used in Java as well as the tests.

General Process
------------------

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

Versions
--------

The V1 of the schema is currently in a "alpha" state it is complete in some areas, please contact us about which pieces are stable enough to use.  

*We of course will be working on this further in the future and until we reach V1, there WILL be breaking changes.*

Our schema's currently conform to the "json-schema-draft-03" version, but we are looking to always keep that current until the official release of that standard.

Testing
-------

The tests are run using the gradle task:

```
gradlew test
```

Tools
-----

#### Utilities

We have included some utilities in the [utils](src/main/groovy/com/spidasoftware/schema/utils) package, if we needed them maybe they can be of use to you.

#### Schema Validation

If you are in need of actually validating some JSON data against the schema there several options depending on your language.  The one we use in our tests is the excellent library by [fge](https://github.com/fge/json-schema-validator).  It gives very good validation errors and also does all the references for you, so there is no need to load all the linked schema.  You will also notice in our tests we use a namespace of a file system.  This could be any location you put the file, you could even use a "resource:/" uri for referencing in a jar.

##### Command line validator

We include a command line validator to validate against any of our included schemas. To run the command, use the validateJson gradle task

    gradlew :validateJson -Pschema=/path/to/schema -PjsonFile=/path/to/json
    schema - path to schema starting from resources. eg. /v1/schema/spidacalc/calc/structure.schema
    json - json file to be validated.

For example, to validate the "one of everything" structure example, from the schema directory you would type:

    gradlew :validateJson -Pschema=/v1/schema/spidacalc/calc/structure.schema -PjsonFile=resources/v1/examples/spidacalc/designs/one_of_everything.json

The tool uses our included Validator java class.

#### Java Tools

In the src/* folders are some java classes that can be helpful when integrating our stuff into a java world.  This includes a classes that produces a JSON descriptor of a class and validate a service against a JSON descriptor.

The jar can be compiled with:
    
    gradlew install

***

SPIDA® is a registered trademark of SPIDAWeb LLC. Copyright © 2012 SPIDAWeb LLC. All rights reserved. All other brands or product names are the property of their respective holders.
[spidasoftware.com](http://www.spidasoftware.com/)
