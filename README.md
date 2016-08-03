SPIDA Software JSON Interfaces
==============================

[![Build Status](https://travis-ci.org/spidasoftware/schema.svg?branch=master)](http://travis-ci.org/spidasoftware/schema)

* LAST RELEASED VERSION: 1.11/3.0.1

License
-------
By accessing, downloading or using the APIs defined here you are agreeing to these terms of use [here.](https://github.com/spidasoftware/schema/raw/master/2013_10_25%20SPIDA%20API%20Terms%20of%20Service.pdf)

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
4. The CalcDB api follows REpresentational State Transfer (REST) conventions, which makes it somewhat different from out other APIs, which use Remote Procedure Call (RPC) conventions. This document contains general information that is mostly only relevant to RPC, although some information is relevant to both. For information on using the CalcDB api, check out [this link.](http://github.com/spidasoftware/schema/blob/master/doc/apis/spidadbAPI.md)

Services
------------

Our services provide a set of methods that can be called for a specific product.  Our service definitions are in each folder in a separate folder called _interfaces_.  For example we have a _spidcalc_ folder that contains an _interfaces_ folder.  The SPIDACalc folder contains all the objects that relate to SPIDACalc.  The interfaces contains the service interfaces that relate to SPIDACalc.

### License Agreement (EULA)

All users must accept the License Agreement.  If this has not been accepted, all HTTP requests will be redirected to usersmaster/agreement.  Login to SPIDAMin and you will be redirected to the License Agreement.  Click the 'Accept' button at the bottom of the page.

Folder Structure
--------------------
1. [doc](doc) - documentation and overview of specific functionality that is available through all our API's.
1. [v1](v1) - the version 1 API
  1. [resources/schema/spidacalc](resources/schema/spidacalc) - schemas for communication with SPIDACalc version 4.4.2.0 and future versions
  1. [resources/schema/spidamin](resources/schema/spidamin) - schemas for communication with SPIDAMin 3.0
  1. [resources/examples](resources/examples) - used in tests, good example objects
1. [src](src) - some utilities that can be used in Java as well as the tests.

Versions
--------

Our schemas currently conform to the "json-schema-draft-03" version, but we are looking to always keep that current until the official release of that standard.

As of October 9, 2014, the v1 Schemas are considered stable, and will not have any more breaking changes. This of course means that no new features will be added to v1 either. All new development will be done under the 'v2' namespace. This will allow existing integrations to continue to work as changes are made to Schema v2. V2 is currently undergoing development, and will be considered unstable until its release. V1 may have minor bug fixes, but only as long as they do not break any existing integrations.

Development
-------------

Any parts of the exposed APIs should allow clients to continue to use the v1 schemas. In SPIDACalc and SPIDA DB, this can be specified simply as a property of project JSON, "version". The version property should hold an integer value that specifies which version of the schema to use, 1 or 2. This could also be accomplished by having a separate namespace in the URL for apis that use the v2 schemas, for example `/api/v2/project/createOrUpdate`. A way to specify schema version should be implemented as needed by each application and documented here.

Any changes to the v2 schema should be accompanied by a modification to a `v1 -> v2` changeset. This will allow us to simplify our handling of the JSON on the backend by allowing us to use the same classes to deal with all of the JSON. Checking for and running the changeset should be implemented in each project as it is required by changes to the schema.


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

SPIDA® is a registered trademark of SPIDAWeb LLC. Copyright © 2012 SPIDAWeb LLC. All rights reserved. All other brands or product names are the property of their respective holders.
[spidasoftware.com](http://www.spidasoftware.com/)
