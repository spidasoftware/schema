SPIDA Software JSON Interfaces
==============================

[![Build Status](https://travis-ci.org/spidasoftware/schema.svg?branch=master)](http://travis-ci.org/spidasoftware/schema)

* LAST RELEASED VERSION: 1.11/3.0.1

Look at the [Release](https://github.com/spidasoftware/schema/releases) page for your specific product version and the API documentation for it. 

License
-------
By accessing, downloading or using the APIs defined here you are agreeing to these terms of use [here.](https://github.com/spidasoftware/schema/raw/master/SPIDAWeb%20API%20Terms%20of%20Service%20(Updated%204%20October%202017).pdf)

General Overview of the Schema
--------------------------------

This project contains information about the API's for the SPIDA products.  These API's allow specific actions against our products to take place from
external products or integrations.  This repository contains example information about how to implement those external integrations.  Please read this introductory page, it
will give you a good overview of how our API's function.

### Concepts

There are few concepts to keep in mind:

1. Our services rely heavily on the JSON format.  If you are not familiar with JSON, you will need to be to understand what it is your are submitting to the service.  Try this short [tutorial](http://www.w3schools.com/json/default.asp).
2. We have two main areas for our services. SPIDACalc and SPIDAMin, don't confuse the two.  They each have some overlap in naming, but fundamentally different structures.  SPIDAMin is an online application that is primarily used to manage assets.  SPIDACalc is a client application for analyzing utility pole structures.  Both have a project, but each is unique and different.  Make sure you are using the correct one.
3. Schema files end in .schema. The .schema files are used to validate the objects passed into specific API endpoints. The [Doc](doc) folder has descriptions of the services to which you pass those objects.
4. The SPIDAdb api follows REpresentational State Transfer (REST) conventions, which makes it somewhat different from out other APIs, which use Remote Procedure Call (RPC) conventions. This document contains general information that is mostly only relevant to RPC, although some information is relevant to both. For information on using the SPIDAdb api, check out [this link.](http://github.com/spidasoftware/schema/blob/master/doc/apis/spidadbAPI.md)

Calc Quick Start Guide
---------------
[Calc Integration Guide](doc/calc.md)

Services
------------

Our services provide a set of methods that can be called for a specific product.  Our service definitions are in each folder in a separate folder called _interfaces_.  For example we have a _spidcalc_ folder that contains an _interfaces_ folder.  The SPIDACalc folder contains all the objects that relate to SPIDACalc.  The interfaces contains the service interfaces that relate to SPIDACalc.

### License Agreement (EULA)

All users must accept the License Agreement.  If this has not been accepted, all HTTP requests will be redirected to usersmaster/agreement.  Login to SPIDAMin and you will be redirected to the License Agreement.  Click the 'Accept' button at the bottom of the page.

Folder Structure
--------------------
1. [doc](doc) - documentation and overview of specific functionality that is available through all our API's.
1. [resources](resources) - location of the schema and example files.
  1. [schema](resources/schema) - all the schemas are located here.
    1. [spidacalc](resources/schema/spidacalc) - schemas for communication with SPIDACalc 4.4.2.0 and future versions
    1. [spidamin](resources/schema/spidamin) - schemas for communication with SPIDAMin 15.0 and future versions
  1. [examples](resources/examples) - used in tests, good example objects.
1. [src](src) - some utilities that can be used in Java as well as the tests.

Standards
--------

Our schemas currently conform to the "json-schema-draft-03" version, but we are looking to always keep that current until the official release of that standard.

Testing
-------

The tests are run using the gradle task:

```
gradlew test
```

Tools
-----

#### Schema Validation

If you are in need of actually validating some JSON data against the schema there several options depending on your language.  The one we use in our tests is the excellent library by [fge](https://github.com/fge/json-schema-validator).  It gives very good validation errors and also does all the references for you, so there is no need to load all the linked schema.  You will also notice in our tests we use a namespace of a file system.  This could be any location you put the file, you could even use a "resource:/" uri for referencing in a jar.

##### Command line validator

We include a command line validator to validate against any of our included schemas. To run the command, use the validateJson gradle task

    gradlew :validateJson -Pschema=/path/to/schema -PjsonFile=/path/to/json
    schema - path to schema starting from resources. eg. /v1/schema/spidacalc/calc/structure.schema
    json - json file to be validated.

For example, to validate the "one of everything" structure example, from the schema directory you would type:

    gradlew :validateJson -Pschema=/v1/schema/spidacalc/calc/structure.schema -PjsonFile=resources/examples/spidacalc/designs/one_of_everything.json

The tool uses our included Validator java class.

#### Java Tools

In the src/* folders are some java classes that can be helpful when integrating our stuff into a java world.  This includes a classes that produces a JSON descriptor of a class and validate a service against a JSON descriptor.

The jar can be compiled with:

    gradlew install

***

SPIDA® is a registered trademark of SPIDAWeb LLC. Copyright © 2017 SPIDAWeb LLC. All rights reserved. All other brands or product names are the property of their respective holders.
[spidasoftware.com](http://www.spidasoftware.com/)
